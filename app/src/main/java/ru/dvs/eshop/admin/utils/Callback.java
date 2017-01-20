package ru.dvs.eshop.admin.utils;

public class Callback {
    public interface ISimple {
        void run();
    }

    public interface ISuccessError {
        void onSuccess();

        void onError();
    }
}
