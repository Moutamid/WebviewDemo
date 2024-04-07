package com.moutamid.webviewdemo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.fxn.stash.Stash;
import com.moutamid.webviewdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding b;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideFullScreen(getWindow());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.webView.setWebViewClient(new WebViewClient());
        b.webView.loadUrl("www.google.com");

        b.disableButton.setOnClickListener(v -> {
            count++;

            if (count == 10) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_pin);
                dialog.setCancelable(false);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.findViewById(R.id.submitBtn).setOnClickListener(view -> {
                    EditText pinEditText = dialog.findViewById(R.id.pinEditText);

                    if (pinEditText.getText().toString().equals("1234")) {
                        Stash.put("unlocked", true);
                        Toast.makeText(this, "unlocked", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "pin wrong", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                });
                dialog.findViewById(R.id.cancelBtn).setOnClickListener(v2 -> {
                    count = 0;
                    dialog.dismiss();
                });
                dialog.show();
                dialog.getWindow().setAttributes(layoutParams);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isMyLauncherDefault()) {
            openDefaultAppsSettings();
            return;
        }

        if (!isAccessibilityServiceEnabled()) {
            launchAccessibilitySettings();
        }

        Stash.put("unlocked", false);
    }

    public static void hideFullScreen(Window window) {
        boolean isFullScreen = true;
        if (window != null) {
            WindowCompat.setDecorFitsSystemWindows(window, !isFullScreen);
            WindowInsetsControllerCompat controllerCompat = WindowCompat.getInsetsController(window, window.getDecorView());

            if (controllerCompat != null) {
                controllerCompat.setSystemBarsBehavior(
                        isFullScreen
                                ? WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                                : WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
                );

                if (isFullScreen) {
                    controllerCompat.hide(WindowInsetsCompat.Type.systemBars());
                } else {
                    controllerCompat.show(WindowInsetsCompat.Type.systemBars());
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.layoutInDisplayCutoutMode = isFullScreen
                        ? WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                        : WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
                window.setAttributes(layoutParams);
            }
        }
    }

    //check if the accessibility service permission is granted or not
    private boolean isAccessibilityServiceEnabled() {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + MotionClass.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not enabled: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }

    // check if app is home launcher or not
    private boolean isMyLauncherDefault() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String currentHomePackage = resolveInfo.activityInfo.packageName;
        return currentHomePackage.equals(getPackageName());
    }

    // launch accessibility settings to enable the service
    private void launchAccessibilitySettings() {
        Toast.makeText(this, "Please allow accessibility permissions", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    // open default home app chooser dialog
    /*private void openHomeAppChooser() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }*/

    // open default apps settings screen to set the app as default launcher
    private void openDefaultAppsSettings() {
        Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
        startActivity(intent);
    }

}
