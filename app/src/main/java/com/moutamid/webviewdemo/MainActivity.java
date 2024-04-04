package com.moutamid.webviewdemo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.fxn.stash.Stash;
import com.moutamid.webviewdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideFullScreen(getWindow());
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

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
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
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

}
