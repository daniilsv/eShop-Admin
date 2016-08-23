package ru.dvs.eshop.admin.data.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private String mAppId;

    public POSTQuery(String site, String token) {
        mSite = site;
        mAppId = Utils.getUniqueID(Core.getInstance().context);
        mToken = token;
        mJsonObj = new JSONObject();
        put("app_id", mAppId);
        put("token", mToken);
    }

    public POSTQuery(String site) {
        mSite = site;
        mAppId = Utils.getUniqueID(Core.getInstance().context);
        mToken = null;
        mJsonObj = new JSONObject();
        put("app_id", mAppId);
    }

    public void put(String a, String b) {
        try {
            mJsonObj.put(a, b);
        } catch (JSONException ignored) {
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (!Utils.hasConnection(Core.getInstance().context)) {
            status = 1;
            response = "10";
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
            response = "11";
        } catch (JSONException e) {
            e.printStackTrace();
            status = 1;
            response = "12";
        }
        Log.e("POSTQuery", "mStatus = " + status);
        Log.e("POSTQuery", "mResponse = " + response);
        return null;
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