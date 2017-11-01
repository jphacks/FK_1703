package com.example.haruyai.brainstorming3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by h_sato on 2017/10/24.
 */

public class SplashActivity extends Activity{
    private final int REQUEST_PERMISSION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // タイトルを非表示にします。
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // splash.xmlをViewに指定します。
        setContentView(R.layout.splash);

        // Android 6, API 23以上でパーミッシンの確認
        if(Build.VERSION.SDK_INT >= 23){
            checkMikePermission();
        } else {
            postprocessing();
        }
    }

    // カメラのアクセスの許可を確認
    public void checkMikePermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED){
            postprocessing();
        }
        // 拒否していた場合
        else{
            requestMikePermission();
        }
    }

    // 許可を求める
    private void requestMikePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(SplashActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION);
        } else {
            Toast toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,}, REQUEST_PERMISSION);

        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                postprocessing();
                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this, "許可されなかったためアプリを終了します", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }
    }

    //パーミッション取得後の処理
    private void postprocessing(){
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
        // 1000ms遅延させてsplashHandlerを実行します。
        hdl.postDelayed(new splashHandler(), 1000);
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
