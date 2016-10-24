package ru.dvs.eshop.admin.data.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.utils.Utils;

//Удобный класс скачивания файла с сайта на устройство
public class FileSendQuery extends AsyncTask<Void, Void, Void> {
    protected String response = "";
    private String mSite;
    private String mController;
    private String mSourcePath;
    private String mType;
    private int mId;
    private String mField;

    public FileSendQuery(String site, String controller, String sourcePath, String type, int id, String field) {
        mSite = site;
        mController = controller;
        mSourcePath = sourcePath;
        mType = type;
        mId = id;
        mField = field;
    }

    private void showErrorMsg() {
    }

    @Override
    protected Void doInBackground(Void... params) {

        if (!Utils.hasConnection(Core.getInstance().context)) {
            response = "-1";//Ошибка. Нет подключения
            showErrorMsg();
            Log.e("PostQuery", "mResponse = " + response);
            return null;
        }
        response = uploadFile(mSite + "/" + mController + "/upload_file", mSourcePath);
        return null;
    }

    public String uploadFile(String url, String sourcePath) {

        HttpURLConnection conn;
        DataOutputStream dos;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File sourceFile = new File(sourcePath);

        if (!sourceFile.isFile())
//TODO:ERROR File not found
            return "File Does not exists";
        try {

            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            url = (url.contains("https://") ? "https://" : "http://") + url.replaceAll("\\s+|http://|https://", "");

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-For-Type", mType);
            conn.setRequestProperty("X-For-Id", mId + "");
            conn.setRequestProperty("X-For-Field", mField);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", sourcePath);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                    + sourcePath + "\"" + lineEnd);

            dos.writeBytes(lineEnd);

            //returns no. of bytes present in fileInputStream
            bytesAvailable = fileInputStream.available();
            //selecting the buffer size as minimum of available bytes or 1 MB
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            //setting the buffer as byte array of size of bufferSize
            buffer = new byte[bufferSize];

            //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            //loop repeats till bytesRead = -1, i.e., no bytes are left to read
            while (bytesRead > 0) {
                //write the bytes read from inputstream
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder resp = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    resp.append(inputLine);
                }
                conn.disconnect();
                return resp.toString();
            }

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
//TODO:ERROR ex.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
//TODO:ERROR ex.getMessage();
        }
        return "Failed";
    }
}