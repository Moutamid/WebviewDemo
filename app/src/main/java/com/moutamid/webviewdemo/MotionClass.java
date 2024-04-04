package com.moutamid.webviewdemo;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.fxn.stash.Stash;

public class MotionClass extends AccessibilityService {
    private static final String TAG = "MotionClass";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getSource() == null) {
            return;
        }
        Log.d(TAG, "onAccessibilityEvent/14| started: ");

        if (Stash.getBoolean("unlocked", false)){
            return;
        }

        if (!event.getPackageName().equals(getPackageName())) {
            Intent homeScreenIntent = new Intent(Intent.ACTION_MAIN);
            homeScreenIntent.addCategory(Intent.CATEGORY_HOME);
            homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeScreenIntent);
        }
    }

    @Override
    public void onInterrupt() {

    }
}
