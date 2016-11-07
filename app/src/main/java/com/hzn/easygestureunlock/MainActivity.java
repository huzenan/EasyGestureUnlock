package com.hzn.easygestureunlock;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Integer> pd;
    private GestureUnlockView unlockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWindow();

        // password
        pd = new ArrayList<>();
        pd.add(2);
        pd.add(1);
        pd.add(3);
        pd.add(7);
        pd.add(8);
        pd.add(5);
        pd.add(4);

        unlockView = (GestureUnlockView) findViewById(R.id.lock_view);
        unlockView.setOnUnlockListener(new GestureUnlockView.OnUnlockListener() {
            @Override
            public boolean onUnlockFinished(ArrayList<Integer> password) {
                boolean success = pd.equals(password);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                unlockView.reset();
                            }
                        });
                    }
                }).start();
                return success;
            }
        });
    }

    @TargetApi(19)
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(android.R.color.transparent));
            tintManager.setStatusBarTintEnabled(true);
        }
    }
}
