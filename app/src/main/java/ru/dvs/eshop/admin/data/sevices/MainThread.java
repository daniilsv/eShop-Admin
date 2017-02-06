package ru.dvs.eshop.admin.data.sevices;

import java.util.ArrayList;

import ru.dvs.eshop.admin.data.network.PostQuery;

public class MainThread extends Thread {
    private static MainThread mInstance = null;
    private PostQuery mCurQuery = null;
    private ArrayList<PostQuery> mQueue = new ArrayList<>();
    private boolean working = true;

    private MainThread() {
        super();
        start();
    }

    public static MainThread getInstance() {
        if (mInstance == null)
            mInstance = new MainThread();
        return mInstance;
    }

    public static void addQuery(PostQuery query) {
        getInstance().mQueue.add(query);
    }

    @Override
    public void run() {
        while (working) {
            if (mCurQuery != null && !mCurQuery.isEnded()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                continue;
            }
            if (mQueue.size() == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                continue;
            }
            mCurQuery = mQueue.remove(0);
            mCurQuery.start();
        }
    }

    @Override
    public void interrupt() {
        working = false;
        if (mCurQuery != null && !mCurQuery.isEnded()) {
            mCurQuery.interrupt();
        }
        super.interrupt();
    }
}
