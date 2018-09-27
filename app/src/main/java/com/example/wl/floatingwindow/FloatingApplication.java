package com.example.wl.floatingwindow;

import android.app.Application;

import com.example.windows.WindowsManagerPicker;

public class FloatingApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        WindowsManagerPicker.init(this);
    }
}
