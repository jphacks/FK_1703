package com.example.haruyai.brainstorming3;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by haruya.i on 2017/10/29.
 */

public class SpeechRecognition extends AppCompatActivity  {
    String url;
    boolean stopClickedorNot = false;

    static final int RESULT_SPEECH = 1;  // The request code
    private SpeechRecognizer sr;

    private AudioManager mAudioManager;
    private int mStreamVolume = 0;

    //TextView textView;
    TextView hintview;

    String hint = "";
    String resultsString = "";

    private String[] summary = new String[3];
    private String[] keywords = new String[3];

    Button startstop_Button;

    //ここでactionbar_menu.xmlをActionBar上に表示している
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu_speech, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //ActionBar上のアイコン（Item）が押されたことを検知するための関数
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //IDごとにボタンを押した際の処理を記述
        switch (item.getItemId()) {
            //戻るアイコンが押された時
            case android.R.id.home:
                //Toast.makeText(this, "back clicked", Toast.LENGTH_SHORT).show();
                finish();
                return true;

            //バブルアイコンが押された時
            case R.id.node_icon:
                //Toast.makeText(this, "バブル押されましたよ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplication(), MainActivity.class);

                //変数(全ノードのリスト)を渡すとこ
                //intent.putExtra("originalNodes", originalWords_List);

                startActivity(intent);
                break;

        }
        return true;
    }


    //Activityが表示された時 → 音声認識を開始する
    @Override
    protected void onResume() {
        super.onResume();
        startListening();
    }
    //Activityが一時停止した場合
    @Override
    protected void onPause() {
        stopListening();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speeching_layout);

        //STARTボタンが押されるまでは音声認識を止めておく
        stopListening();

        //textView = (TextView) this.findViewById(R.id.summary);
        hintview = (TextView) this.findViewById(R.id.hint);
        startstop_Button = (Button)this.findViewById(R.id.ssButton);

        startstop_Button.setOnClickListener(new View.OnClickListener(){ public void onClick(View v){
            ClickedssButton(startstop_Button.getText().toString());}});


        // ActionBarの設定
        if (savedInstanceState == null) {
            // customActionBarの取得
            View customActionBarView = this.getActionBarView("Hoge", "表示する画像のURL");
            // ActionBarの取得
            android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
            // 戻るボタンを表示するかどうか('<' <- こんなやつ)
            actionBar.setDisplayHomeAsUpEnabled(true);
            // タイトルを表示するか（もちろん表示しない）
            actionBar.setDisplayShowTitleEnabled(false);
            // iconを表示するか（もちろん表示しない）
            actionBar.setDisplayShowHomeEnabled(false);
            // ActionBarにcustomViewを設定する
            actionBar.setCustomView(customActionBarView);
            // CustomViewを表示するか
            actionBar.setDisplayShowCustomEnabled(true);
        }
    }


    // 音声認識を開始する
    protected void startListening() {
        try {
            if (sr == null) {
                sr = SpeechRecognizer.createSpeechRecognizer(this);
                if (!SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "音声認識が使えません",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                sr.setRecognitionListener(new Listener());
            }
            // インテントの作成
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            // 言語モデル指定
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
            sr.startListening(intent);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "startListening()でエラーが起こりました",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // 音声認識を終了する
    protected void stopListening() {
        if (sr != null) sr.destroy();
        sr = null;
    }

    // 音声認識を再開する
    public void restartListeningService() {
        //ピコンっていう音がいちいちしたらうるさいから消しておく
        //mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

        stopListening();
        startListening();
    }

    //音声認識して取得した文字列からHTTPでキーワードを抽出する関数
    class SendVoice extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer[] params) {
            try {
                hint = "";

                HttpClient httpclient = new DefaultHttpClient(null);
                HttpPost httppost = new HttpPost(url);

                // Request parameters and other properties.
                List<NameValuePair> param = new ArrayList<>(1);

                param.add(new BasicNameValuePair("sentence", resultsString));
                httppost.setEntity(new UrlEncodedFormEntity(param, "UTF-8"));

                //Execute and get the response.
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
                //STOPボタンが押された時
                if(stopClickedorNot == true){
                    System.out.println(resultsString);
                    JSONArray jsonArray = jsonObject.getJSONArray("summary");
                    JSONArray jsonArray2 = jsonObject.getJSONArray("keywords");
                    for(int i=0; i<3; i++){
                        summary[i] = jsonArray.get(i).toString();
                        keywords[i] = jsonArray2.get(i).toString();
                    }
                }else{
                    JSONArray jsonArray = jsonObject.getJSONArray("relation_keyword");
                    for(int i=0; i<3; i++){
                        hint = hint + " " + jsonArray.get(i).toString();
                    }
                    if(!jsonArray.get(0).toString().equals("null") || !jsonArray.get(0).toString().equals(null)){
                        //hint += "\nといったワードが面白そうです";
                    }else{
                        hint = "STOPボタンを押すことで録音を停止します";
                    }

                }

                //System.out.println("-------" + hint+ "---------");

            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }
            return "test";
        }
    }

    // RecognitionListenerの定義
    // 中が空でも全てのメソッドを書く必要がある
    class Listener implements RecognitionListener {
        SpeechRecognition speechRecognition = new SpeechRecognition();

        // 話し始めたときに呼ばれる
        public void onBeginningOfSpeech() {
            /*Toast.makeText(getApplicationContext(), "onBeginningofSpeech",
                    Toast.LENGTH_SHORT).show();*/
        }
        public void onRmsChanged(float f){

        }

        // 結果に対する反応などで追加の音声が来たとき呼ばれる
        // しかし呼ばれる保証はないらしい
        public void onBufferReceived(byte[] buffer) {
        }

        // 話し終わった時に呼ばれる
        public void onEndOfSpeech() {
            /*Toast.makeText(getApplicationContext(), "onEndofSpeech",
                    Toast.LENGTH_SHORT).show();*/
        }

        // ネットワークエラーか認識エラーが起きた時に呼ばれる
        public void onError(int error) {
            String reason = "";
            switch (error) {
                // Audio recording error
                case SpeechRecognizer.ERROR_AUDIO:
                    reason = "ERROR_AUDIO";
                    break;
                // Other client side errors
                case SpeechRecognizer.ERROR_CLIENT:
                    reason = "ERROR_CLIENT";
                    break;
                // Insufficient permissions
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    reason = "ERROR_INSUFFICIENT_PERMISSIONS";
                    break;
                // 	Other network related errors
                case SpeechRecognizer.ERROR_NETWORK:
                    reason = "ERROR_NETWORK";
                    /* ネットワーク接続をチェックする処理をここに入れる */
                    break;
                // Network operation timed out
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    reason = "ERROR_NETWORK_TIMEOUT";
                    break;
                // No recognition result matched
                case SpeechRecognizer.ERROR_NO_MATCH:
                    reason = "ERROR_NO_MATCH";
                    break;
                // RecognitionService busy
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    reason = "ERROR_RECOGNIZER_BUSY";
                    break;
                // Server sends error status
                case SpeechRecognizer.ERROR_SERVER:
                    reason = "ERROR_SERVER";
                    /* ネットワーク接続をチェックをする処理をここに入れる */
                    break;
                // No speech input
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    reason = "ERROR_SPEECH_TIMEOUT";
                    break;
            }
            //Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_SHORT).show();
            restartListeningService();
        }
        // 将来の使用のために予約されている
        public void onEvent(int eventType, Bundle params) {
        }

        // 部分的な認識結果が利用出来るときに呼ばれる
        // 利用するにはインテントでEXTRA_PARTIAL_RESULTSを指定する必要がある
        public void onPartialResults(Bundle partialResults) {
        }

        // 音声認識の準備ができた時に呼ばれる
        public void onReadyForSpeech(Bundle params) {
            //Toast.makeText(getApplicationContext(), "話してください",
            //      Toast.LENGTH_SHORT).show();
        }

        // 認識結果が準備できた時に呼ばれる
        public void onResults(Bundle results) {
            // 結果をArrayListとして取得
            ArrayList results_array = results.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION);
            // 取得した文字列を結合
            for (int i=0; i<1; i++) {
                resultsString += results_array.get(i) + "\n";
            }

            //textView.setText(resultsString);

            url = "http://fullfill.sakura.ne.jp/JPHACKS/relation_char.php";

            SendVoice job = new SendVoice();
            job.execute();


            //アイデアのヒントとして、議事録から抽出したキーワードを定時する
            hintview.setVisibility(View.VISIBLE);
            hintview.setText(hint);

            //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, 0);

            restartListeningService();
        }
    }

    private void ClickedssButton(String textOnButton){
        if(textOnButton.equals("START")){
            startstop_Button.setBackgroundResource(R.drawable.summary_stop_button);
            startstop_Button.setText("STOP");
            hintview.setText("STOPボタンを押すことで録音を停止します");
            stopClickedorNot = false;
            startListening();
        }else if(textOnButton.equals(("STOP"))){
            stopListening();
            url = "http://fullfill.sakura.ne.jp/JPHACKS/summary.php";
            stopClickedorNot = true;

            SendVoice job = new SendVoice();
            //job.execute();

            try {
                Object result = job.execute().get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(getApplication(), Summary.class);
            //変数(全ノードのリスト)を渡すとこ
            intent.putExtra("resultString", resultsString);
            intent.putExtra("summary", summary);
            intent.putExtra("keywords", keywords);
            startActivity(intent);

            startstop_Button.setBackgroundResource(R.drawable.summary_start_button);
            startstop_Button.setText("START");
            hintview.setText("STARTボタンを押すことで録音が開始されます");
        }
    }

    public View getActionBarView(String title, String imageURL) {

        // 表示するlayoutファイルの取得
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.custom_action_bar, null);

        // CustomViewにクリックイベントを登録する
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return view;

    }
}


