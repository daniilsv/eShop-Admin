package ru.dvs.eshop.admin.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.Preferences;
import ru.dvs.eshop.admin.data.network.POSTQuery;
import ru.dvs.eshop.admin.utils.Encode;
import ru.dvs.eshop.admin.utils.Utils;

//TODO: Переделать функционал кнопки назад
//TODO: Сделать дизайн и "перетекание частей"

/**
 * Активность аутентификации.
 * Пинг, логин, получение и проверка статуса токена.
 */
public class LoginActivity extends AppCompatActivity {
    private LinearLayout siteLayout, userLayout, responseLayout;
    private int curAction;
    private ImageView responseImage;
    private TextView responseText;
    private String site = null;
    private String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        siteLayout = (LinearLayout) findViewById(R.id.site_layout);
        userLayout = (LinearLayout) findViewById(R.id.user_layout);
        responseLayout = (LinearLayout) findViewById(R.id.response_layout);
        responseImage = (ImageView) findViewById(R.id.response_image);
        responseText = (TextView) findViewById(R.id.response_text);
        ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        Button next1 = (Button) findViewById(R.id.start_next1_button);
        Button next2 = (Button) findViewById(R.id.start_next2_button);
        Button next3 = (Button) findViewById(R.id.start_next3_button);
        if (next1 != null)
            next1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pingAndSwipeNext();
                }
            });
        if (next2 != null)
            next2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginAndSwipeNext();
                }
            });
        if (next3 != null)
            next3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    responseAndSwipeNext();
                }
            });
        if (backButton != null)
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });


        Core.getInstance().setActivity(this);

        site = Preferences.getString("host");
        token = Preferences.getString("token");
        switch (Preferences.getInt("login_status")) {
            case 0:
                siteLayout.setVisibility(View.VISIBLE);
                curAction = 0;
                break;
            case 1:
                curAction = 2;
                setResponseData(R.drawable.wait, R.string.start_wait_for_token, true);
                responseLayout.setVisibility(View.VISIBLE);
                break;
            case 2:
                curAction = 3;
                setResponseData(R.drawable.accept, R.string.start_success, true);
                responseLayout.setVisibility(View.VISIBLE);
                break;
        }

    }

    private void pingAndSwipeNext() {
        EditText siteEditText = (EditText) findViewById(R.id.site);
        if (siteEditText != null) {
            site = siteEditText.getText().toString();
            POSTQuery task = new POSTQuery(site) {
                @Override
                protected void onPostExecute(Void voids) {
                    if (status == 0) {
                        siteLayout.setVisibility(View.GONE);
                        userLayout.setVisibility(View.VISIBLE);
                        curAction = 1;
                    } else {
                        siteLayout.setVisibility(View.GONE);
                        responseLayout.setVisibility(View.VISIBLE);
                        setResponseData(R.drawable.reject, R.string.start_ping_failed, false);
                    }
                }
            };
            task.put("controller", "api");
            task.put("method", "ping");
            task.execute();
        }
    }

    private void loginAndSwipeNext() {
        EditText emailEditText = (EditText) findViewById(R.id.email);
        EditText passEditText = (EditText) findViewById(R.id.password);
        if (emailEditText != null && passEditText != null) {
            String email = emailEditText.getText().toString();
            String pass_md5 = Encode.MD5(passEditText.getText().toString());

            POSTQuery task = new POSTQuery(site, "login") {
                @Override
                protected void onPostExecute(Void voids) {
                    if (status == 0 && Encode.isValidMD5(response)) {
                        token = response;
                        Preferences.setString("host", site);
                        Preferences.setString("token", token);
                        Preferences.setInt("login_status", 1);
                        curAction = 2;
                        setResponseData(R.drawable.wait, R.string.start_wait_for_token, true);
                        userLayout.setVisibility(View.GONE);
                        responseLayout.setVisibility(View.VISIBLE);
                    } else {
                        userLayout.setVisibility(View.GONE);
                        responseLayout.setVisibility(View.VISIBLE);
                        setResponseData(R.drawable.reject, R.string.start_login_failed, false);
                    }
                }
            };
            task.put("controller", "users");
            task.put("method", "login_api");
            task.put("app_id", Utils.getUniqueID(this));
            task.put("email", email);
            task.put("pass", pass_md5);
            task.execute();
        }
    }

    private void setResponseData(int img_id, int str_id, boolean is_next_visible) {
        if (Build.VERSION.SDK_INT >= 23)
            responseImage.setImageDrawable(this.getResources().getDrawable(img_id, null));
        else
            responseImage.setImageDrawable(this.getResources().getDrawable(img_id));
        responseText.setText(str_id);
        View sn3b = findViewById(R.id.start_next3_button);
        if (sn3b != null)
            sn3b.setVisibility(is_next_visible ? View.VISIBLE : View.GONE);
    }

    private void responseAndSwipeNext() {
        if (curAction == 3) {
            this.startActivity(new Intent(this, MainActivity.class));
            this.finish();
            return;
        }
        POSTQuery task = new POSTQuery(site, token) {
            @Override
            protected void onPostExecute(Void voids) {
                if (status == 0) {
                    curAction = 2;

                    try {
                        JSONObject node = new JSONObject(response);
                        int token_status = node.getInt("status");
                        if (token_status == 1) {
                            Preferences.setInt("login_status", 2);
                            curAction = 3;
                            setResponseData(R.drawable.accept, R.string.start_success, true);
                        } else
                            setResponseData(R.drawable.wait, R.string.start_wait_for_token, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    userLayout.setVisibility(View.GONE);
                    responseLayout.setVisibility(View.VISIBLE);
                    setResponseData(R.drawable.reject, R.string.start_login_failed, false);
                }
            }
        };
        task.put("controller", "api");
        task.put("method", "get_token_info");
        task.execute();
    }

    //При нажатии кнопки назад
    @Override
    public void onBackPressed() {
        responseLayout.setVisibility(View.GONE);
        switch (curAction) {
            case 0:
                siteLayout.setVisibility(View.VISIBLE);
                break;
            case 1:
                userLayout.setVisibility(View.VISIBLE);
                break;
            case 2:
            case 3:
                siteLayout.setVisibility(View.VISIBLE);
                curAction = 1;
                break;
        }
        curAction--;
        if (curAction == -1)
            super.onBackPressed();
    }

}
