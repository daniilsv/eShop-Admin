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
import java.net.URLDecoder;
import java.net.URLEncoder;

import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.utils.Encode;
import ru.dvs.eshop.admin.utils.Utils;

//TODO: Переделать систему шифрования
//TODO: Добавить систему сжатия данных

/**
 * Асинхронное подключение к API сайта
 */
public class POSTQuery extends AsyncTask<Void, Void, Void> {
    private static final String POST_XOR_KEY = "cebce3d1a8b98c7cdfcb88e10ebdd096";
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
            Log.e("AsyncPOST", "mStatus = " + status);
            Log.e("AsyncPOST", "mResponse = " + response);
            return null;
        }
        String result;
        try {
            result = sendPOST(mSite + "/api", mJsonObj);
            JSONObject node = new JSONObject(result);
            status = node.getInt("status");
            response = node.getString("response");
        } catch (IOException e) {
            status = 1;
            response = "11";
        } catch (JSONException e) {
            status = 1;
            response = "12";
        }
        Log.e("AsyncPOST", "mStatus = " + status);
        Log.e("AsyncPOST", "mResponse = " + response);
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
        String str = Encode.compress(Encode.xorIt(URLEncoder.encode(params.toString(), "UTF-8"), POST_XOR_KEY));
        OutputStream os = con.getOutputStream();
        os.write(("data=" + str).getBytes());
        os.flush();
        os.close();

        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuilder response = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            con.disconnect();
            return URLDecoder.decode(Encode.xorIt(Encode.decompress(response.toString()), POST_XOR_KEY), "UTF-8");
        }
        con.disconnect();
        return "RC: " + responseCode;
    }
}