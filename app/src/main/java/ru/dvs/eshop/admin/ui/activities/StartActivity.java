package ru.dvs.eshop.admin.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.Utils;

public class StartActivity extends AppCompatActivity {
    private LinearLayout siteLayout, userLayout, responseLayout;
    private String site, email, pass_md5;
    private int curAction;
    private ImageView responseImage;
    private TextView responseText;
    private Button responseNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        siteLayout = (LinearLayout) findViewById(R.id.site_layout);
        userLayout = (LinearLayout) findViewById(R.id.user_layout);
        responseLayout = (LinearLayout) findViewById(R.id.response_layout);
        responseImage = (ImageView) findViewById(R.id.response_image);
        responseText = (TextView) findViewById(R.id.response_text);
        responseNext = (Button) findViewById(R.id.back_button);
        siteLayout.setVisibility(View.VISIBLE);
        curAction = 0;
        Button next1 = (Button) findViewById(R.id.start_next1_button);
        Button next2 = (Button) findViewById(R.id.start_next2_button);
        Button next3 = (Button) findViewById(R.id.start_next3_button);
        if (next1 != null)
            next1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkSiteAndSwipeNext();
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

    }

    private void checkSiteAndSwipeNext() {
        EditText siteEditText = (EditText) findViewById(R.id.site);
        if (siteEditText != null) {
            site = siteEditText.getText().toString();
            if (Core.apiPingSite(site)) {
                siteLayout.setVisibility(View.GONE);
                userLayout.setVisibility(View.VISIBLE);
                curAction = 1;
            } else {
                siteLayout.setVisibility(View.GONE);
                responseLayout.setVisibility(View.VISIBLE);
                setResponseData(R.drawable.reject, R.string.start_ping_failed);
            }
        }
    }

    private void loginAndSwipeNext() {
        EditText emailEditText = (EditText) findViewById(R.id.email);
        EditText passEditText = (EditText) findViewById(R.id.password);
        if (emailEditText != null && passEditText != null) {
            email = emailEditText.getText().toString();
            pass_md5 = passEditText.getText().toString();
            String response = Core.apiLoginSite(site, email, pass_md5);
            if (Utils.isValidMD5(response)) {
                userLayout.setVisibility(View.GONE);
                responseLayout.setVisibility(View.VISIBLE);
                curAction = 2;
                setResponseData(R.drawable.wait, R.string.start_wait_for_token);
            } else {
                userLayout.setVisibility(View.GONE);
                responseLayout.setVisibility(View.VISIBLE);
                setResponseData(R.drawable.reject, R.string.start_login_failed);
            }
        }
    }

    private void responseAndSwipeNext() {
        if ()
    }

    private void setResponseData(int img_id, int str_id, boolean is_next_visible) {
        responseImage.setImageDrawable(this.getResources().getDrawable(img_id));
        responseText.setText(str_id);
        responseNext.setVisibility(is_next_visible ? View.VISIBLE : View.GONE);
    }
}
