package ru.dvs.eshop.admin;

/**
 * Created by DVS and copy-past by GICHA on 10.06.2016.
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class Connection {
    private static Connection ourInstance = null;

    private Connection() {

    }

    static Connection getInstance() {
        if (ourInstance == null)
            ourInstance = new Connection();
        return ourInstance;
    }

    //Посылаем POST запрос на сайт в текущем потоке
    private static String sendPOST(String url, String paramArray[]) throws IOException {
        url = (url.contains("https://") ? "https://" : "http://") + url.replaceAll("\\s+|http://|https://", "");
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        String params = "";
        for (String param : paramArray) {
            params += param + "&";
        }
        os.write(params.getBytes());
        os.flush();
        os.close();

        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuilder response = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            con.disconnect();
            return response.toString();
        }
        con.disconnect();
        return "RC: " + responseCode;
    }

    //Удобный класс запросов и ответов от сайта в отдельном потоке
    static class POSTQuery extends Thread {
        private JSONObject json_ooj;
        private String action;
        private String site;
        private String result;

        POSTQuery(String _site, String _action) {
            site = _site;
            action = _action;
            result = null;
            json_ooj = new JSONObject();
        }

        public void run() {
            String s = "";
            if (json_ooj.length() > 0)
                s = "data=" + json_ooj.toString();
            String params[] = {
                    "aid=" + Utils.getUniqueID(Core.getInstance().context), "action=" + action, s
            };
            try {
                result = sendPOST(site + "/api/post", params);
            } catch (IOException e) {
                result = "RC: 1";
            }

        }

        void send() {
            if (Utils.hasConnection())
                start();
        }

        String getResult() {
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }

        void put(String a, String b) {
            try {
                json_ooj.put(a, b);
            } catch (JSONException e) {
            }
        }
    }

    //Удобный класс скачивания файла с сайта на устройство
    static class FILEQuery extends Thread {
        File result;
        String url;

        FILEQuery(String _url, String destination) {
            url = _url;
            result = new File(destination);
        }

        public void run() {
            try {
                url = (url.contains("https://") ? "https://" : "http://") + url.replaceAll("\\s+|http://|https://", "");
                BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                FileOutputStream out = new FileOutputStream(result);
                byte data[] = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, count);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void get() {
            if (Utils.hasConnection())
                start();
        }

        File getResult() {
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}