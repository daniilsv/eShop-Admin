package ru.dvs.eshop.admin.data.network;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.utils.Utils;
//TODO: Переделать на подобие

//Удобный класс скачивания файла с сайта на устройство
public class FILEQuery extends AsyncTask<Void, Void, Void> {
    public File result;
    private String mUrl;

    public FILEQuery(String _url, String destination) {
        mUrl = _url;
        String r = "";
        String t[] = destination.split("/");

        for (int i = 0; i < t.length - 1; i++) {
            r += t[i] + "/";
        }
        new File(r).mkdirs();
        result = new File(destination);
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (!Utils.hasConnection(Core.getInstance().context))
            return null;
        try {
            mUrl = (mUrl.contains("https://") ? "https://" : "http://") + mUrl.replaceAll("\\s+|http://|https://", "");
            BufferedInputStream in = new BufferedInputStream(new URL(mUrl).openStream());
            FileOutputStream out = new FileOutputStream(result);
            byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}