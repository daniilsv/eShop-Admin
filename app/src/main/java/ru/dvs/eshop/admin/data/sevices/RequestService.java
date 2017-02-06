package ru.dvs.eshop.admin.data.sevices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class RequestService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MainThread.getInstance();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThread.getInstance().interrupt();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
