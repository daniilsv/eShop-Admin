package ru.dvs.eshop.admin.data.network;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.utils.Utils;

/**
 * Асинхронное подключение к API сайта
 */
public class POSTQuery extends AsyncTask<Void, Void, Void> {
    protected int status = 0;
    protected String response = "";
    private String mToken;
    private JSONObject mJsonObj;
    private String mSite;

    public POSTQuery(String site, String token, String controller, String method) {
        mSite = site;
        mToken = token;
        mJsonObj = new JSONObject();
        put("token", mToken);
        put("controller", controller);
        put("method", method);
    }

    public POSTQuery(String site, String controller, String method) {
        mSite = site;
        mToken = null;
        mJsonObj = new JSONObject();
        put("controller", controller);
        put("method", method);
    }

    public void put(String a, String b) {
        try {
            mJsonObj.put(a, b);
        } catch (JSONException ignored) {
        }
    }

    public void put(String a, Map b) {
        try {
            mJsonObj.put(a, new JSONObject(b));
        } catch (JSONException ignored) {
        }
    }

    public void put(String a, List b) {
        try {
            mJsonObj.put(a, new JSONArray(b));
        } catch (JSONException ignored) {
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (!Utils.hasConnection(Core.getInstance().context)) {
            status = 1;
            response = "-1";//Ошибка. Нет подключения
            showErrorMsg();
            Log.e("POSTQuery", "mStatus = " + status);
            Log.e("POSTQuery", "mResponse = " + response);
            return null;
        }
        String result;
        try {
            result = sendPOST(mSite + "/api", mJsonObj);
            JSONObject node = new JSONObject(result);
            status = node.getInt("status");
            response = node.getString("response");
        } catch (IOException e) {
            e.printStackTrace();
            status = 1;
            response = "-2";//Ошибка подключения
        } catch (JSONException e) {
            e.printStackTrace();
            status = 1;
            response = "-3";//Ошибка первичного парсинга ответа
        }
        if (response == "null") {
            status = 1;
        }
        if (status != 0) {
            showErrorMsg();
        }
        Log.e("POSTQuery", "mStatus = " + status);
        Log.e("POSTQuery", "mResponse = " + response);
        response = response.replace(":null", ":\"\"");
        return null;
    }

    private void makeErrorToast(final int res_id) {
        Core.getInstance().activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Core.getInstance().context, res_id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showErrorMsg() {
        switch (response) {
            case "-1":
                makeErrorToast(R.string.query_error_m1);
                break;
            case "-2":
                makeErrorToast(R.string.query_error_m2);
                break;
            case "-3":
                makeErrorToast(R.string.query_error_m3);
                break;

            case "null":
                makeErrorToast(R.string.query_error_null);
                break;

            case "1":
                makeErrorToast(R.string.query_error_1);
                break;
            case "2":
            case "3":
                makeErrorToast(R.string.query_error_2);
                break;
            case "4":
                makeErrorToast(R.string.query_error_4);
                break;
            case "5":
                makeErrorToast(R.string.query_error_5);
                break;
            case "6":
                makeErrorToast(R.string.query_error_6);
                break;
            case "7":
                makeErrorToast(R.string.query_error_7);
                break;
            case "8":
                makeErrorToast(R.string.query_error_8);
                break;
            case "9":
                makeErrorToast(R.string.query_error_9);
                break;
        }
    }

    //Посылаем POST запрос на сайт в текущем потоке
    private String sendPOST(String url, JSONObject params) throws IOException {
        url = (url.contains("https://") ? "https://" : "http://") + url.replaceAll("\\s+|http://|https://", "");
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        con.setDoOutput(true);
        String str = params.toString();
        OutputStream os = con.getOutputStream();
        os.write(("data=" + str).getBytes());
        os.flush();

        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuilder resp = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                resp.append(inputLine);
            }
            con.disconnect();
            String ret = resp.toString();
            return ret;
        }
        con.disconnect();
        return "RC: " + responseCode;
    }
}