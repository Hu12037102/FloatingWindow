package com.example.windows;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class WindowsManagerPicker {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private View mRecoveryView;
    private ImageView mFloatingImage;
    private WindowManager.LayoutParams mRecoveryParams;
    private static final int RECOVERY_Y = 1500;
    private static final int RECOVERY_X = 750;
    private boolean canVibratio = true;
    private Activity mActivity;
    private static final int UP_MESSAGE_WHAT = 100;
    private static final int DOWN_MESSAGE_WHAT = 200;



    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WindowsManagerPicker.UP_MESSAGE_WHAT:
                    if (DispalyUtils.isShowDeviceHasNavigationBar(mContext)) {
                        DispalyUtils.showTransparentNavigation(mActivity);
                    }
                    break;
                case WindowsManagerPicker.DOWN_MESSAGE_WHAT:
                    if (DispalyUtils.isShowDeviceHasNavigationBar(mContext)) {
                        DispalyUtils.hideTransparentNavigation(mActivity);
                    }
                    break;
            }

            return true;
        }
    });

    public static void init(@NonNull Context context) {
        mContext = context;
    }

    private static WindowsManagerPicker mPicker;

    private WindowsManagerPicker(Activity activity) {
        this.mActivity = activity;
    }

    public static WindowsManagerPicker newInstances(Activity activity) {
        synchronized (WindowsManagerPicker.class) {
            if (mPicker == null) {
                synchronized (WindowsManagerPicker.class) {
                    mPicker = new WindowsManagerPicker(activity);
                }
            }
        }
        return mPicker;
    }

    /**
     * 创建悬浮球
     */
    @SuppressLint("ClickableViewAccessibility")
    public void createFloatingWindows() {

        if (mFloatingImage == null) {
            final WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager == null)
                return;
            createRecoverWindows(false);
            mFloatingImage = new ImageView(mContext);
            mFloatingImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.a));
            final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE |//低版本默认的type类型
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//设置该窗口的type类型为顶层
            }
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.x = 0;
            layoutParams.y = 0;
            windowManager.addView(mFloatingImage, layoutParams);

            mFloatingImage.setOnTouchListener(new View.OnTouchListener() {
                int x, y = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x = (int) event.getRawX();
                            y = (int) event.getRawY();
                            mHandler.sendEmptyMessage(WindowsManagerPicker.DOWN_MESSAGE_WHAT);
                            break;
                        case MotionEvent.ACTION_MOVE:

                            Log.w("DispalyUtils--", DispalyUtils.isShowDeviceHasNavigationBar(mContext) + "--"
                                    + DispalyUtils.getNavigationHeight(mContext));
                            createRecoverWindows(true);
                            int nowX = (int) event.getRawX();
                            int nowY = (int) event.getRawY();
                            int movedX = nowX - x;
                            int movedY = nowY - y;
                            x = nowX;
                            y = nowY;
                            layoutParams.x = layoutParams.x + movedX;
                            layoutParams.y = layoutParams.y + movedY;

                            if (mRecoveryView != null) {
                                if (nowY >= RECOVERY_Y && nowX >= RECOVERY_X) {
                                    mRecoveryParams.x = 450;
                                    mRecoveryParams.y = 850;
                                    windowManager.updateViewLayout(mRecoveryView, mRecoveryParams);
                                    if (canVibratio) {
                                        vibratePhone();
                                        canVibratio = false;
                                    }

                                } else {
                                    mRecoveryParams.x = 500;
                                    mRecoveryParams.y = 900;
                                    windowManager.updateViewLayout(mRecoveryView, mRecoveryParams);
                                    canVibratio = true;
                                }
                            }

                            if (mFloatingImage != null) {
                                windowManager.updateViewLayout(mFloatingImage, layoutParams);
                            }

                            break;
                        case MotionEvent.ACTION_UP:
                            int nowUpX = (int) event.getRawX();
                            int nowUpY = (int) event.getRawY();
                            if (nowUpY >= RECOVERY_Y && nowUpX >= RECOVERY_X) {
                                removeFloatingView();
                                removeRecoveryView();
                            } else {
                                createRecoverWindows(false);
                            }
                            mHandler.sendEmptyMessageDelayed(WindowsManagerPicker.UP_MESSAGE_WHAT, 50);

                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }

    }

    /**
     * 创建回收View
     */
    private void createRecoverWindows(boolean isShow) {
        final WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null)
            return;
        if (mRecoveryView == null) {
            mRecoveryView = LayoutInflater.from(mContext).inflate(R.layout.recovery_view, null);
            mRecoveryParams = new WindowManager.LayoutParams();
            mRecoveryParams.format = PixelFormat.RGBA_8888;
            mRecoveryParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;//这个flag的意思此View可以随意移动（可以超出屏幕）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mRecoveryParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;//Android8.0适配浮动窗口
            } else {
                mRecoveryParams.type = WindowManager.LayoutParams.TYPE_PHONE;//低版本默认浮动窗口type
            }
            mRecoveryParams.width = 800;
            mRecoveryParams.height = 800;

            mRecoveryParams.x = 500;
            mRecoveryParams.y = 900;
            windowManager.addView(mRecoveryView, mRecoveryParams);
        }
        if (isShow) {
            mRecoveryView.setVisibility(View.VISIBLE);
        } else {
            mRecoveryView.setVisibility(View.GONE);
        }
    }


    /**
     * 回收悬浮球View
     */
    private void removeRecoveryView() {
        final WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null)
            return;
        if (mRecoveryView != null) {
            windowManager.removeView(mRecoveryView);
            mRecoveryView = null;
        }
    }


    private void removeFloatingView() {
        final WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null)
            return;
        if (mFloatingImage != null) {
            windowManager.removeView(mFloatingImage);
            mFloatingImage = null;
        }
    }

    /**
     * 振动功能
     */
    private void vibratePhone() {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(100);
        }
    }

    public void onDestroy() {
        removeFloatingView();
        removeRecoveryView();
        mHandler.removeMessages(WindowsManagerPicker.DOWN_MESSAGE_WHAT);
        mHandler.removeMessages(WindowsManagerPicker.UP_MESSAGE_WHAT);
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mPicker = null;
    }


}
