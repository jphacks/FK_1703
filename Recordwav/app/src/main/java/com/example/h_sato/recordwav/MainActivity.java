package com.example.h_sato.recordwav;

import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Intent;
import android.speech.RecognizerIntent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int SAMPLING = 8000;
    private SpeechRecognizer sr;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream(SAMPLING * 2 * 80);
    int silence_count = 0;


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
                sr.setRecognitionListener(new listener());
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
        // record();
    }

    // 音声認識を再開する
    public void restartListeningService() {
        stopListening();
        startListening();
    }

    //音声をwavに書き出し
    private void record() {

        OutputStream os = null;

        try {
            byte[] b = baos.toByteArray();
            int len = b.length;
/*
            if (len <= 0) {
                return;
            }
*/
            short type = 1;
            short channel = 1;
            short perSampling = 16;
            String filename = System.currentTimeMillis() + ".wav";

            os = new FileOutputStream(
                    new File(getExternalCacheDir(), filename)
            );

            // WAVEヘッダーの書き出し?
            write(os, "RIFF");
            writeInt(os, 36 + len);
            write(os, "WAVE");
            write(os, "fmt ");
            writeInt(os, 16);
            writeShort(os, type);
            writeShort(os, channel);
            writeInt(os, SAMPLING);
            writeInt(os, channel * SAMPLING * perSampling);
            writeShort(os, (short) (channel * perSampling / 8));
            writeShort(os, perSampling);
            write(os, "data");
            writeInt(os, len);

            // WAVEボディ(PCM?)の書き出し
            os.write(b);

            Toast.makeText(getApplicationContext(), filename + "に保存しました",
                    Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startListening();
    }

    @Override
    protected void onPause() {
        stopListening();
        super.onPause();
    }

    private void write(OutputStream out, String value) throws IOException {
        for(int i = 0; i < value.length(); i++) {
            out.write(value.charAt(i));
        }
    }

    private void writeShort(OutputStream out, short value) throws IOException {
        out.write(value >> 0);
        out.write(value >> 8);
    }

    private void writeInt(OutputStream out, int value) throws IOException {
        out.write(value >> 0);
        out.write(value >> 8);
        out.write(value >> 16);
        out.write(value >> 24);
    }

    // RecognitionListenerの定義
    // 中が空でも全てのメソッドを書く必要がある
    class listener implements RecognitionListener {
        // 話し始めたときに呼ばれる
        public void onBeginningOfSpeech() {
            /*Toast.makeText(getApplicationContext(), "onBeginningofSpeech",
                    Toast.LENGTH_SHORT).show();*/
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
            String resultName = "";
            // 結果をArrayListとして取得
            ArrayList results_array = results.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION);
            // 取得した文字列を結合
            String resultsString = "";

            for (int i = 0; i < 1; i++) {
                resultsString += results_array.get(i);
            }

            // resultName = PatternMatching(resultsString );

            //results_arrayの中身がないことが、100回あれば一度録音する
            if(resultsString==""){
                silence_count++;
                if(silence_count == 100){
                    Toast.makeText(getApplicationContext(), System.currentTimeMillis() + "何か喋って！" , Toast.LENGTH_LONG).show();
                    silence_count = 0;
                    restartListeningService();
                    record();
                    finish();
                }
            } else {
                silence_count = 0;
            }

            // トーストを使って結果表示
            //Toast.makeText(getApplicationContext(), resultName, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), resultsString, Toast.LENGTH_LONG).show();
            restartListeningService();
        }

        public String PatternMatching(String inputString) {
            double match = 1.0;
            double distance;
            String name = "";

            String[] tsubasa = {"本田翼","ほんだ","ホンダ","つばさ","ツバサ"};
            String[] maki = {"堀北真希","ほりきた","ホリキタ","まき","マキ"};
            String[] haruka = {"兒玉遥","こだま","コダマ","はるか","ハルカ"};
            String[] suzu = {"広瀬すず","ひろせ","ヒロセ","すず","スズ"};
            String[] sakura = {"宮脇咲良","みやわき","ミヤワキ","さくら","サクラ"};
            String[] madoka = {"千代田まどか","ちよだ","チヨダ","まどか","マドカ","ちょまど","チョマド"};
            String[] kanna = {"橋本環奈","はしもと","ハシモト","かんな","カンナ"};

            Levenstein_distance LD = new Levenstein_distance();

            for(int i = 0; i < tsubasa.length; i++) {
                distance = LD.LevensteinDistance(tsubasa[i],inputString);
                if(match >= distance){
                    match = distance;
                    name = "本田翼";
                }
            }
            for(int i = 0; i < maki.length; i++) {
                distance = LD.LevensteinDistance(maki[i],inputString);
                if(match >= distance){
                    match = distance;
                    name = "堀北真希";
                }
            }
            for(int i = 0; i < haruka.length; i++) {
                distance = LD.LevensteinDistance(haruka[i],inputString);
                if(match >= distance){
                    match = distance;
                    name = "兒玉遥";
                }
            }
            for(int i = 0; i < suzu.length; i++) {
                distance = LD.LevensteinDistance(suzu[i],inputString);
                if(match >= distance){
                    match = distance;
                    name = "広瀬すず";
                }
            }
            for(int i = 0; i < sakura.length; i++) {
                distance = LD.LevensteinDistance(sakura[i],inputString);
                if(match >= distance){
                    match = distance;
                    name = "宮脇咲良";
                }
            }
            for(int i = 0; i < madoka.length; i++) {
                distance = LD.LevensteinDistance(madoka[i],inputString);
                if(match >= distance){
                    match = distance;
                    name = "千代田まどか";
                }
            }
            for(int i = 0; i < kanna.length; i++) {
                distance = LD.LevensteinDistance(kanna[i],inputString);
                if(match >= distance){
                    match = distance;
                    name = "橋本環奈";
                }
            }

            return name;
        }

        // サウンドレベルが変わったときに呼ばれる
        // 呼ばれる保証はない
        public void onRmsChanged(float rmsdB) {
        }
    }
}
