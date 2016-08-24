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

    public POSTQuery(String site, String token) {
        mSite = site;
        mToken = token;
        mJsonObj = new JSONObject();
        put("token", mToken);
    }

    public POSTQuery(String site) {
        mSite = site;
        mToken = null;
        mJsonObj = new JSONObject();
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
            response = "-1";//Ошибка. Нет подключения
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
        if (status != 0) {
            showErrorMsg();
        }
        Log.e("POSTQuery", "mStatus = " + status);
        Log.e("POSTQuery", "mResponse = " + response);
        return null;
    }

    private void showErrorMsg() {
        switch (response) {
            //TODO: Сделать вывод ошибки. (Например Тостом)
/*
//Такие ошибки сейчас доступны на сервере.
define('CMS_API_ERROR_CONTROLLER_NOT_FOUND', 1);
define('CMS_API_ERROR_METHOD_NOT_FOUND', 2);
define('CMS_API_ERROR_LOGIN_INCORRECT', 3);
define('CMS_API_ERROR_TOKEN_STATUS_IS_0', 4);
define('CMS_API_ERROR_TOKEN_STATUS_IS_1', 5);
define('CMS_API_ERROR_TOKEN_STATUS_IS_2', 6);

//Вот расшифровка:
define('LANG_API_ERROR_CONTROLLER_NOT_FOUND', "Контроллер не найден");
define('LANG_API_ERROR_METHOD_NOT_FOUND', "Метод не найден");
define('CMS_API_ERROR_LOGIN_INCORRECT', "Логин или пароль не верный");
define('LANG_API_ERROR_TOKEN_STATUS_IS_0', "Токен еще не активирован");
define('LANG_API_ERROR_TOKEN_STATUS_IS_1', "Токен активирован");
define('LANG_API_ERROR_TOKEN_STATUS_IS_2', "Токен заблокирован");

//Все серверные ошибки больше нуля.
//Все клиентские (ошибки приложения) меньше нуля.
*/
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