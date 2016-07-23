package ru.dvs.eshop.admin.data.network;

import android.content.Context;
import android.content.Intent;
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

import ru.dvs.eshop.admin.utils.Encode;
import ru.dvs.eshop.admin.utils.Utils;

public class POSTQuery extends AsyncTask<Void, Void, Void> {
    private static final String POST_XOR_KEY = "cebce3d1a8b98c7cdfcb88e10ebdd096";

    private Context mContext;
    private String mActivityAction;
    private String mToken;
    private JSONObject mJsonObj;
    private String mSite;
    private String mAppId;

    private int mStatus = 0;
    private String mResponse = "";

    public POSTQuery(Context context, String site, String activityAction, String token) {
        mContext = context;
        mSite = site;
        mActivityAction = activityAction;
        mAppId = Utils.getUniqueID();
        mToken = token;
        mJsonObj = new JSONObject();
        put("app_id", mAppId);
        put("token", mToken);
    }

    public POSTQuery(Context context, String site, String activityAction) {
        mContext = context;
        mSite = site;
        mActivityAction = activityAction;
        mAppId = Utils.getUniqueID();
        mToken = null;
        mJsonObj = new JSONObject();
        put("app_id", mAppId);
    }

    public void put(String a, String b) {
        try {
            mJsonObj.put(a, b);
        } catch (JSONException e) {
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (!Utils.hasConnection()) {
            mStatus = 1;
            mResponse = "10";
            Log.e("AsyncPOST", "mStatus = " + mStatus);
            Log.e("AsyncPOST", "mResponse = " + mResponse);
            return null;
        }
        String result;
        try {
            result = sendPOST(mSite + "/api", mJsonObj);
            JSONObject node = new JSONObject(result);
            mStatus = node.getInt("status");
            mResponse = node.getString("response");
        } catch (IOException e) {
            mStatus = 1;
            mResponse = "11";
        } catch (JSONException e) {
            mStatus = 1;
            mResponse = "12";
        }
        Log.e("AsyncPOST", "mStatus = " + mStatus);
        Log.e("AsyncPOST", "mResponse = " + mResponse);
        return null;
    }

    @Override
    protected void onPostExecute(Void voids) {
        Intent intent = new Intent(mActivityAction);
        intent.putExtra("status", mStatus);
        intent.putExtra("response", mResponse);
        mContext.sendBroadcast(intent);
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