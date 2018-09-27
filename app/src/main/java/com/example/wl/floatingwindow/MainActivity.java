package com.example.wl.floatingwindow;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.windows.WindowsManagerPicker;

public class MainActivity extends AppCompatActivity {

    private AlertDialog mOpenPermission;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void openFloatingWindows(View view){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
            if (mOpenPermission == null) {
                mOpenPermission = new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage("请前往设置中心打开浮窗权限！")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOpenPermission.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQUEST_CODE);
                        mOpenPermission.dismiss();
                    }
                }).create();
            }
            if (!mOpenPermission.isShowing()) {
                mOpenPermission.show();
            }
        } else {
            WindowsManagerPicker.newInstances(this).createFloatingWindows();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            switch (requestCode) {
                case REQUEST_CODE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                        Toast.makeText(this, "授权失败！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        WindowsManagerPicker.newInstances(this).onDestroy();
        super.onDestroy();

    }
}
