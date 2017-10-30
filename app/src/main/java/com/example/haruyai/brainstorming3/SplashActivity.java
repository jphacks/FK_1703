package com.example.haruyai.brainstorming3;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by h_sato on 2017/10/24.
 */

public class SplashActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // タイトルを非表示にします。
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // splash.xmlをViewに指定します。
        setContentView(R.layout.splash);
        PackageInfo packageInfo = null;
        TextView tv = (TextView)findViewById(R.id.versionName);
        String versionName;
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "unknown";
        }
        tv.setText("version: " + versionName);

        Handler hdl = new Handler();
        // 1200ms遅延させてsplashHandlerを実行します。
        //hdl.postDelayed(new splashHandler(), 1200);
        hdl.postDelayed(new splashHandler(), 2000);
    }
    class splashHandler implements Runnable {
        public void run() {
            // スプラッシュ完了後に実行するActivityを指定します。
            //Intent intent = new Intent(getApplication(), ListActivity.class); //本来はこれでいく
            //Intent intent = new Intent(getApplication(), MainActivity.class);
            Intent intent = new Intent(getApplicationContext(), SpeechRecognition.class);// アップロードはこっち
            startActivity(intent);
            // SplashActivityを終了させます。
            SplashActivity.this.finish();
        }
    }
}
