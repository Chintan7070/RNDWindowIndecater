package com.rndwindowindecater.services;

import android.accessibilityservice.AccessibilityService;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import androidx.core.app.NotificationCompat;

import com.rndwindowindecater.R;
import com.rndwindowindecater.utils.ConstantVal;

public class IndecaterService extends AccessibilityService implements View.OnTouchListener, View.OnClickListener {

    private WindowManager windowManager;
    private View floatingIndicator;
    private View anotherView;
    private View vIndecater;
    private View llMainLayout;

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = buildNotification();
        startForeground(ConstantVal.NOTIFICATION_ID, notification);

    }


    @Override
    public void onServiceConnected() {
        super.onServiceConnected();

        // Initialize WindowManager
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Initialize and add the floating indicator
        floatingIndicator = LayoutInflater.from(this).inflate(R.layout.floating_indicator, null);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        vIndecater = floatingIndicator.findViewById(R.id.vIndecater);

        // Set position and other parameters for your indicator
        params.gravity = Gravity.CENTER | Gravity.RIGHT;
       /* params.x = 0;
        params.y = 100;*/
        // Add the view to the window
        windowManager.addView(floatingIndicator, params);
        vIndecater.setOnTouchListener(this);

        //View 02
        /*anotherView = LayoutInflater.from(this).inflate(R.layout.another_view, null);
        llMainLayout = anotherView.findViewById(R.id.llMainLayout);
        llMainLayout.setOnClickListener(this);
        WindowManager.LayoutParams anotherParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);*/

        anotherView = LayoutInflater.from(this).inflate(R.layout.another_view, null);
        llMainLayout = anotherView.findViewById(R.id.llMainLayout);
        llMainLayout.setOnClickListener(this);
        WindowManager.LayoutParams anotherParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                PixelFormat.TRANSLUCENT);

        // Set position off-screen
        anotherParams.gravity = Gravity.CENTER | Gravity.RIGHT;
        anotherParams.x = -anotherParams.width;  // Off-screen initially

        // Add the view to the window
        anotherView.setVisibility(View.GONE);
        windowManager.addView(anotherView, anotherParams);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }


    @Override
    public void onInterrupt() {
        // Handle interruption
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the floating indicator when the service is destroyed
        if (windowManager != null && floatingIndicator != null) {
            windowManager.removeView(floatingIndicator);
        }
    }

    private Notification buildNotification() {
        // Create a notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    ConstantVal.NOTIFICATION_IDNEW,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Build your notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ConstantVal.NOTIFICATION_IDNEW)
                .setContentTitle("Foreground Service Title")
                .setContentText("Foreground Service Text")
                .setSmallIcon(R.drawable.ic_launcher_background);

        return builder.build();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float startX = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                return true; // Consumes the touch event

            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float deltaX = endX - startX;

                if (Math.abs(deltaX) > ConstantVal.SWIPE_THRESHOLD) {
                    // Proper swipe detected
                    if (deltaX > 0) {
                        // Swipe from left to right (show another view)
//                        animateView(anotherView, 0, 500);  // Animate to the visible position
                        showControlerLayout();
                    } else {
                        // Swipe from right to left (hide another view with fade animation)
//                        animateFadeOut(anotherView);  // Animate fade-out
                    }
                }
                return true; // Consumes the touch event
        }
        return false;
    }

    private void showControlerLayout() {
        // Define the animation
        anotherView.setVisibility(View.VISIBLE);
        anotherView.setAlpha(0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(anotherView, "alpha", 0f, 1f);
        alphaAnimator.setDuration(500); // Set the duration of the animation in milliseconds
        animateView(anotherView, 0, 500);  // Animate to the visible position
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                llMainLayout.setEnabled(true);
                llMainLayout.setClickable(true);
            }
        });
        alphaAnimator.start();
    }




    private void animateView(final View view, final int targetX, long duration) {
        if (view == null) return;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "x", targetX);
        animator.setDuration(duration);
        animator.start();
    }

    private void animateFadeOut(final View view) {
        if (view == null) return;
        anotherView.setVisibility(View.VISIBLE);
        anotherView.setAlpha(0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(anotherView, "alpha", 1f, 0f);
        alphaAnimator.setDuration(500); // Set the duration of the animation in milliseconds
        animateView(anotherView, 0, 500);  // Animate to the visible position
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                llMainLayout.setEnabled(false);
                llMainLayout.setClickable(false);
                anotherView.setVisibility(View.GONE);
            }
        });
        alphaAnimator.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.llMainLayout) {
            animateFadeOut(v);
            Log.e("checkclickApply", "onClick: check fadeou click applay" );
        }
    }
}

