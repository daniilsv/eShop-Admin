package ru.dvs.eshop.admin.data.network;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.utils.Utils;

/**
 * Асинхронное подключение к API сайта
 */
public class PostQuery extends AsyncTask<Void, Void, Void> {
    protected int status = 0;
    protected String response = "";
    private String mToken;
    private HashMap mJsonObj;
    private String mSite;
    private String mController;
    private String mMethod;

    public PostQuery(String site, String token, String controller, String method) {
        mSite = site;
        mToken = token;
        mController = controller;
        mMethod = method;
        mJsonObj = new HashMap();
        put("api_key", mToken);
    }

    public PostQuery(String site, String controller, String method) {
        mSite = site;
        mToken = null;
        mController = controller;
        mMethod = method;
        mJsonObj = new HashMap();
    }

    public void put(String a, String b) {
        mJsonObj.put(a, b);
    }

    public void put(String a, Map b) {
        mJsonObj.put(a, new JSONObject(b));
    }

    public void put(String a, List b) {
        mJsonObj.put(a, new JSONArray(b));
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (!Utils.hasConnection(Core.getInstance().context)) {
            status = 1;
            response = "-1";//Ошибка. Нет подключения
            showErrorMsg();
            Log.e("PostQuery", "mStatus = " + status);
            Log.e("PostQuery", "mResponse = " + response);
            return null;
        }
        String result;
        try {
            result = sendPOST(mSite + "/api/method/" + mController + "." + mMethod, mJsonObj);
            if (result.charAt(2) == 'r') {
                status = 0;
                response = result.subSequence(12, result.length() - 1).toString();
            } else if (result.charAt(2) == 'e') {
                status = 1;
                response = result.subSequence(9, result.length() - 1).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            status = 1;
            response = "-2";//Ошибка подключения
        }
        if (response.equals("null")) {
            status = 1;
        }
        if (status != 0) {
            showErrorMsg();
        }
        Log.e("PostQuery", "mStatus = " + status);
        Log.e("PostQuery", "mResponse = " + response);
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

    //TODO: Reset this
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
                makeErrorToast(R.string.query_error_2);
                break;
            case "3":
                makeErrorToast(R.string.query_error_3);
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
                makeErrorToast(R.string.query_error_8);
                break;
        }
    }

    //Посылаем POST запрос на сайт в текущем потоке
    private String sendPOST(String url, HashMap<String, Object> params) throws IOException {
        url = (url.contains("https://") ? "https://" : "http://") + url.replaceAll("\\s+|http://|https://", "");
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        con.setDoOutput(true);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s", URLEncoder.encode(entry.getKey().toString(), "UTF-8"),
                    URLEncoder.encode(entry.getValue().toString(), "UTF-8")));
        }
        OutputStream os = con.getOutputStream();
        os.write(sb.toString().getBytes());

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
            return resp.toString();
        }
        con.disconnect();
        return "RC: " + responseCode;
    }
}