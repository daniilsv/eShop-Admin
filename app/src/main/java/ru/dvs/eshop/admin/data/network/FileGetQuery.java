package ru.dvs.eshop.admin.data.network;

import android.content.Context;
import android.os.AsyncTask;
import ru.dvs.eshop.admin.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class FileGetQuery extends AsyncTask<Void, Void, Void> {
    public File result;
    private String mUrl;
    private Context mContext;

    public FileGetQuery(Context context, String url, String destination) {
        mContext = context;
        mUrl = url;
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
        if (!Utils.hasConnection(mContext))
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