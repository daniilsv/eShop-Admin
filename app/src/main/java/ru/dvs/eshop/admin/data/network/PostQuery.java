package ru.dvs.eshop.admin.data.network;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.dvs.eshop.admin.data.Site;
import ru.dvs.eshop.admin.utils.Callback;
import ru.dvs.eshop.admin.utils.Utils;

/**
 * Асинхронное подключение к API сайта
 */
public class PostQuery extends Thread implements Callback.ISuccessError {
    private final Site mSite;
    private final String mController;
    private final String mMethod;
    private final Context mContext;
    protected JSONObject mResponse = null;
    protected boolean mIsError = false;
    protected int mErrorCode = 0;
    protected String mErrorMessage = "";
    protected HashMap<String, Object> mJsonObj;
    private Callback.ISuccessError mCallback = null;

    public PostQuery(Context context, Site site, String controller, String method) {
        mContext = context;
        mSite = site;
        mController = controller;
        mMethod = method;
        mJsonObj = new HashMap<>();
        if (site.token != null)
            put("api_key", site.token);
    }

    public boolean isEnded() {
        return mResponse != null;
    }

    public final void put(String a, String b) {
        mJsonObj.put(a, b);
    }

    public final void put(String a, Map b) {
        mJsonObj.put(a, new JSONObject(b));
    }

    public final void put(String a, List b) {
        mJsonObj.put(a, new JSONArray(b));
    }

    public void run() {
        doInBackground();
        onPostExecute();
    }

    private Void doInBackground() {
        String result;
        if (!Utils.hasConnection(mContext))
            result = "{\"error\":{\"error_code\":-1,\"error_msg\":\"Connection error: No signal\"}}";
        else
            result = sendPOST(mSite.host + "/api/method/" + mController + "." + mMethod, mJsonObj);

        parseResult(result);
        if (mIsError) {
            onError();
        } else {
            onSuccess();
        }
        return null;
    }


    private void onPostExecute() {
        if (mCallback == null)
            return;
        if (mIsError) {
            mCallback.onError();
        } else {
            mCallback.onSuccess();
        }
    }

    public void onError() {
    }

    public void onSuccess() {
    }

    public final void setCallback(Callback.ISuccessError callback) {
        mCallback = callback;
    }


    private String sendPOST(String url, HashMap<String, Object> params) {
        String conerr;
        try {
            url = (url.contains("https://") ? "https://" : "http://") + url.replaceAll("\\s+|http://|https://", "");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            con.setDoOutput(true);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                try {
                    sb.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=").append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                } catch (UnsupportedEncodingException ignored) {
                }
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
                String ret = resp.toString();
                ret = ret.replace(":null", ":\"\"");
                return ret;
            }
            con.disconnect();
            conerr = responseCode + "";
        } catch (IOException e) {
            conerr = e.getMessage();
        }
        return "{\"error\":{\"error_code\":-2,\"error_msg\":\"Connection error:" + conerr + "\"}}";
    }

    private void parseResult(String result) {
        try {
            mResponse = new JSONObject(result);
            if (mResponse.opt("error") != null) {
                mIsError = true;
                mResponse = mResponse.getJSONObject("error");
                mErrorCode = mResponse.getInt("error_code");
                mErrorMessage = mResponse.getString("error_msg");
            } else {
                mResponse = mResponse.getJSONObject("response");
            }
        } catch (JSONException ignored) {
        }
    }
}