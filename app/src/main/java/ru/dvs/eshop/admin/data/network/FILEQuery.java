package ru.dvs.eshop.admin.data.network;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
//TODO: Переделать на подобие

/**
 * @see POSTQuery
 */
//Удобный класс скачивания файла с сайта на устройство
public class FILEQuery extends Thread {
    private File result;
    private String url;

    public FILEQuery(String _url, String destination) {
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
        //if (Utils.hasConnection())
        start();
    }

    public File getResult() {
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}