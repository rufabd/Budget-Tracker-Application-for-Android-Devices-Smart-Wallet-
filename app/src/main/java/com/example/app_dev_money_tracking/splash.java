package com.example.app_dev_money_tracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class splash extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        SplashLauncher launcher = new SplashLauncher(Home_activity.class, 2000);
        TextView splash_text = findViewById(R.id.TXT_splash);
        ImageView spalsh_image = findViewById(R.id.Img_splash);
        splash_text.animate().translationY(1600).setDuration(2000).setStartDelay(1000);
        spalsh_image.animate().translationY(-1600).setDuration(2000).setStartDelay(1000);
        launcher.start();
    }

    private class SplashLauncher extends Thread
    {
        public Class toRun;
        int duration;

        public SplashLauncher(Class a, int _duration)
        {
            toRun = a;
            duration = _duration;
        }
        public void run()
        {
            try {
                sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent i = new Intent(getApplicationContext(), toRun);
            startActivity(i);
            finish();
        }
    }


}
