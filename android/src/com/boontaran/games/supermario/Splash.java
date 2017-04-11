package com.boontaran.games.supermario;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**g
 * Created by real on 6/5/2016.
 */
public class Splash extends Activity {
    SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_splash);

        manager=new SessionManager();
        // METHOD 1

        /****** Create Thread that will sleep for 3 seconds *************/
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 3 seconds
                    sleep(3 * 1000);

                    // After 5 seconds redirect to another intent
                    String status = manager.getPreferences(Splash.this, "status");
                    Log.d("status", status);

                    Intent i = new Intent(Splash.this, AndroidLauncher.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();

//METHOD 2

        /*
        new Handler().postDelayed(new Runnable() {

            // Using handler with postDelayed called runnable run method

            @Override
            public void run() {
              if (status=="1"){
                        Intent i=new Intent(Splash.this,MainActivity.class);
                        startActivity(i);
                    }else{
                        Intent i=new Intent(Splash.this,Login.class);
                        startActivity(i);
                    }

                // close this activity
                finish();
            }
        }, 3*1000); // wait for 3 seconds
        */
    }

    @Override
    public void onBackPressed() {
    //    super.onBackPressed();
    }
}
