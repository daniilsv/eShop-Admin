package ru.dvs.eshop.admin.data.network;

import android.content.Context;
import ru.dvs.eshop.admin.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class FileGetQuery extends Thread {
    public File result = null;
    private String mUrl;
    private Context mContext;

    public FileGetQuery(Context context, String url, String destination) {
        mContext = context;
        mUrl = url;
        String r = "";
        File appFolder = mContext.getExternalFilesDir(null);
        if (appFolder == null)
            return;
        destination = appFolder.getPath() + "/" + destination;
        String t[] = destination.split("/");
        for (int i = 0; i < t.length - 1; i++) {
            r += t[i] + "/";
        }
        new File(r).mkdirs();
        result = new File(destination);
    }

    @Override
    public void run() {
        if (result == null)
            return;
        if (!Utils.hasConnection(mContext))
            return;
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
        return;
    }
}