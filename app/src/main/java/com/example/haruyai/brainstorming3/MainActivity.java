package com.example.haruyai.brainstorming3;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.HttpClients;


import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {
    String uRL;
    String key;


    //最初の文字入力用
    private EditText editText;
    private String first_Text;
    //文字を移動する時の文字列
    private String string2swap1, string2swap2;
    //デフォルトの7つのボタン
    private ImageButton button1;
    //オリジナルで追加されるノードを追加する用のボタン
    private ImageButton addButton;
    //文字の受け渡し用
    private String text2pass;
    //中心のノード上の文字
    private TextView textOnbutton1;
    //アニメーション用の文字
    private TextView text4animation;

    //デフォルトのノード数
    private int perNode = 3;

    //デフォルトの3つのノード
    private ImageButton[] buttons_array = new ImageButton[perNode];
    //隅に表示される6つのノードの画像
    private ImageButton[] imageButtons_array = new ImageButton[6];
    //隅のノードに表示される文字
    private TextView[] texts_array = new TextView[6];
    //オリジナルで追加される3つのノード
    private ImageButton[] oriButtons_array = new ImageButton[perNode];
    //文字編集中のオリジナルで追加される3つのノード
    private EditText[] oriETs_array = new EditText[perNode];
    //デフォルトノード上の文字
    private TextView[] textsOnbuttons_array = new TextView[perNode];
    //オリジナルノード上の文字
    private TextView[] textsOnoributtons_array = new TextView[perNode];


    //デフォルトのノードのID
    private int[] buttons_id = {
            R.id.button2,
            R.id.button3,
            R.id.button4
    };
    //隅に表示されるノードの画像のID
    private int[] imageButtons_id = {
            R.id.imagebutton22,
            R.id.imagebutton33,
            R.id.imagebutton44,
            R.id.imagebutton55,
            R.id.imagebutton66,
            R.id.imagebutton77
    };
    //隅に表示されるノードの文字のID
    private int[] texts_id = {
            R.id.button22_text,
            R.id.button33_text,
            R.id.button44_text,
            R.id.button55_text,
            R.id.button66_text,
            R.id.button77_text
    };
    //オリジナルで追加されるノードのID
    private int[] oriButtons_id = {
            R.id.originalButton2,
            R.id.originalButton3,
            R.id.originalButton4
    };
    //文字編集中のオリジナルで追加されるノードのID
    private int[] oriETs_id = {
            R.id.originalET2,
            R.id.originalET3,
            R.id.originalET4
    };
    //デフォルトノード上の文字のID
    private int[] textsOnbuttons_id = {
            R.id.textOfbutton2,
            R.id.textOfbutton3,
            R.id.textOfbutton4
    };
    //オリジナルノード状の文字のID
    private int[] textsOnoributtons_id = {
            R.id.textOfoributton2,
            R.id.textOfoributton3,
            R.id.textOfoributton4
    };

    //ノードの軌跡(path)を追うためのフォロワを設定。初期値は"a"
    private String follower = "a";

    /*デフォルトノードとオリジナルノードをまとめてpathを定義してる
        "b"
    "g"     "c"
        "a"
    "f"     "d"
        "e"
     */

    //デフォルトノードが作成された際のノードのパス(軌跡)を格納するリスト → デフォルトノードのもつ文字を参照するため
    private List<String> pathDefault_List = new ArrayList<String>();
    //オリジナルノードが作成された際のノードのパス(軌跡)を格納するリスト → オリジナルノードのもつ文字を参照するため
    private List<String> pathOriginal_List = new ArrayList<String>();
    //ノードのパス(軌跡)とそこで幾つオリジナルノードが生成されたかを保持するMap
    //<"path", オリジナルノードの数> pathはabc
    private HashMap<String, Integer> pathNhowmanyOriginals_Map = new HashMap<String, Integer>();
    //デフォルトノードのもつ文字を格納するリスト（現状3つずつ）
    public static List<String> defaultWords_List = new ArrayList<String>();
    //オリジナルノードのもつ文字を格納するリスト（個数は可変なのでpathNhowmanyOriginals_Mapで参照するが、作成されていない箇所は空文字で対応する）
    public static List<String> originalWords_List = new ArrayList<String>();
    //隅のノードの文字をパスから判断するためのMap
    private HashMap<String, String> pathNText_Map = new HashMap<String, String>();

    private int count, pathDefaultIndex, pathOriginalIndex, oriIndex, oriInt;

    //アニメーション用
    private TranslateAnimation translate;
    private ScaleAnimation scale;
    private AlphaAnimation alpha;
    private AnimationSet animationSet;

    //[0~5]:デフォルトノードとオリジナルノードを出すときのアニメーション6パターン
    //[6~47]:デフォルトノードかオリジナルノードを押したときのアニメーションを7×6(42)パターン
    //[48~89]:隅のノードを押した時のアニメーションを7×6(42)パターン
    //[90~131]:デフォルトノードかオリジナルノードを押した時の文字のアニメーションを7×6(42)パターン
    //[132~173]:隅のノードを押した時の文字のアニメーションを7×6(42)パターン
    //[174~179]:デフォルトノードとオリジナルノードを出す時の文字のアニメーション6パターン
    //[180]:ノード間を移動する時の隅のノードと、オリジナルノードを押した時に中心ボタンを黄色くさせないために透明にしておくアニメーション
    //[181~186]:真ん中の文字が隅に行く時のアニメーション6パターン
    //[187~192]:隅の文字が真ん中に行く時のアニメーション6パターン
    //二次元配列を使って順次入れる
    private List<AnimationSet> animationSet_array = new ArrayList<AnimationSet>();

    //サーバ上のスクリプトからデフォルトノードの文字列3つを格納、受け渡しするための変数
    private String[] defaultNodesishere = new String[3];
    private String whatsDefault = "";
    private ProgressBar progressBar;

    static final int RESULT_SPEECH = 1;  // The request code


    //ここでactionbar_menu.xmlをActionBar上に表示している
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
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
            case R.id.bubble_icon:
                //Toast.makeText(this, "バブル押されましたよ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplication(), Mapping.class);

                //変数(全ノードのリスト)を渡すとこ
                //intent.putExtra("originalNodes", originalWords_List);

                startActivity(intent);
                break;

        }
        return true;
    }

    //真ん中のノードが押された際にデフォルトノードの文字列を取得するためのクラス
    private class GetDefaultNodesWords extends AsyncTask<Integer, Integer, String>{
        int c;


        @Override
        protected String doInBackground(Integer[] params){
            try {
                HttpClient httpclient = new DefaultHttpClient(null);
                HttpPost httppost = new HttpPost(uRL);

                // Request parameters and other properties.
                List<NameValuePair> param = new ArrayList<>(1);
                param.add(new BasicNameValuePair(key, whatsDefault));
                httppost.setEntity(new UrlEncodedFormEntity(param, "UTF-8"));

                //Execute and get the response.
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
                JSONArray jsonArray = jsonObject.getJSONArray("relation_keyword");
                for (int i=0; i < jsonArray.length(); i++) {
                    defaultNodesishere[i] = jsonArray.get(i).toString();
                }
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }
            return "test";
        }

        /*
        @Override
        protected void onProgressUpdate(Integer... values){
            progressBar.setProgress(values[0]);
        }


        @Override
        protected void onPostExecute(String result){
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
        }
        */

    }

    private class SpeechRecognite extends AsyncTask<Integer, Integer, String>{
        boolean flag = true;

        @Override
        protected String doInBackground(Integer[] params){
            while(flag == true){
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "jp-JA");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",Toast.LENGTH_SHORT).show();
                }
            }
            return "hi";
        }
    }

    //SpeechRecognizerが終わった時に呼ばれる
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> text = null;
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                }
                break;
            }
        }
        MainActivity maina = new MainActivity();
        maina.uRL = "http://fullfill.sakura.ne.jp/JPHACKS/relation_char.php";
        maina.key = "sentence";
        GetDefaultNodesWords get = new GetDefaultNodesWords();
        get.execute();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //横画面にならないように縦画面で固定
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*
        //中心のノードからデフォルトノードの文字列を取得するクラスを呼ぶ
        SpeechRecognite job = new SpeechRecognite();
        job.execute();
        */

        // ActionBarの設定
        if (savedInstanceState == null) {
            // customActionBarの取得
            View customActionBarView = this.getActionBarView("Hoge", "表示する画像のURL");
            // ActionBarの取得
            android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();

            //ActionBar actionBar = getActionBar();

            // 戻るボタンを表示するかどうか('<' <- こんなやつ)
            //actionBar.setDisplayHomeAsUpEnabled(true);

            // ActionBarの左に「<」を表示するように設定
            //ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            // タイトルを表示するか（もちろん表示しない）
            actionBar.setDisplayShowTitleEnabled(false);
            // iconを表示するか（もちろん表示しない）
            actionBar.setDisplayShowHomeEnabled(false);
            // ActionBarにcustomViewを設定する
            actionBar.setCustomView(customActionBarView);
            // CutomViewを表示するか
            actionBar.setDisplayShowCustomEnabled(true);
        }

        //中心ノードの呼び出し
        button1 = (ImageButton) this.findViewById(R.id.button1);
        //中心ノード上の文字の呼び出し
        textOnbutton1 = (TextView)this.findViewById(R.id.textOfbutton1);

        //アニメーション用のテキスト
        text4animation = (TextView)this.findViewById(R.id.text4animation);

        progressBar = (ProgressBar)this.findViewById(R.id.progressbar_horizontal);

        //デフォルトのノード、オリジナルのノードのそれぞれを定義
        for(int a=0; a<perNode; a++){
            buttons_array[a] = new ImageButton(this);
            buttons_array[a] = (ImageButton) this.findViewById(buttons_id[a]);
            oriButtons_array[a] = new ImageButton(this);
            oriButtons_array[a] = (ImageButton) this.findViewById(oriButtons_id[a]);
            oriETs_array[a] = new EditText(this);
            oriETs_array[a] = (EditText)this.findViewById(oriETs_id[a]);
            textsOnbuttons_array[a] = new TextView(this);
            textsOnbuttons_array[a] = (TextView)this.findViewById(textsOnbuttons_id[a]);
            textsOnoributtons_array[a] = new TextView(this);
            textsOnoributtons_array[a] = (TextView)this.findViewById(textsOnoributtons_id[a]);
        }
        //隅のノードとその文字列、基本の6つのノード上のテキストを定義
        for(int b=0; b<6; b++){
            imageButtons_array[b] = new ImageButton(this);
            imageButtons_array[b] = (ImageButton)this.findViewById(imageButtons_id[b]);
            texts_array[b] = new TextView(this);
            texts_array[b] = (TextView)this.findViewById(texts_id[b]);
        }

        //オリジナルで追加されるボタンを追加する用のボタンの呼び出し
        addButton = (ImageButton)this.findViewById(R.id.addOriginals);

        //初っ端表示されるEditText
        editText = (EditText)this.findViewById(R.id.first_text);

        showSoftInput(editText);

        //最初のノードのEditTextにリスナーを付けて、Enterが押されたら、キーボードを閉じて、文字列をButtonに渡して表示を入れ替える
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                //EditTextの文字入力が完了した時
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //EditTextにある文字を取得する
                    first_Text = editText.getText().toString();
                    //ソフトキーボードを下げる
                    InputMethodManager inputMethodMgr = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodMgr.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //Button1に文字を渡して表示に、EditTextを非表示に
                    button1.setVisibility(View.VISIBLE);
                    textOnbutton1.setText(first_Text);
                    textOnbutton1.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.INVISIBLE);
                    //アニメーションで使用するため
                    editText.setText("");

                    handled = true;
                }
                return handled; // このメソッド中でアクションを消化したら true を返す。
            }
        });


        //Button1(中央ボタン)をクリックしたら周りの6つのButtonを表示する
        button1.setOnClickListener(new View.OnClickListener() {public void onClick(View v) {centerButtonClicked();}});


        //デフォルトの各ノードにリスナーを付けて、押された際に表示を適宜変える
        buttons_array[0].setOnClickListener(new View.OnClickListener(){ public void onClick(View v){eachButtonClicked(0);}});
        buttons_array[1].setOnClickListener(new View.OnClickListener(){ public void onClick(View v){eachButtonClicked(1);}});
        buttons_array[2].setOnClickListener(new View.OnClickListener(){ public void onClick(View v){eachButtonClicked(2);}});


        //addButtonをクリックした時、オリジナルのノードを追加しEditTextの入力画面へ
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addButtonClicked();
            }
        });


        //各oriButtonでEnterを押した時にキーボードを閉じて、Viewを切り替える
        oriETs_array[0].setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {boolean handled = false;if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager inputMethodMgr = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodMgr.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                oriButtonsClicked(0);
                handled = true;
            }
                return handled; //このメソッド中でアクションを消化したら true を返す
            }
        });
        oriETs_array[1].setOnEditorActionListener(new TextView.OnEditorActionListener() {@Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {boolean handled = false;if (actionId == EditorInfo.IME_ACTION_DONE) {InputMethodManager inputMethodMgr = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);inputMethodMgr.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);oriButtonsClicked(1);handled = true;}return handled; }});
        oriETs_array[2].setOnEditorActionListener(new TextView.OnEditorActionListener() {@Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {boolean handled = false;if (actionId == EditorInfo.IME_ACTION_DONE) {InputMethodManager inputMethodMgr = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);inputMethodMgr.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);oriButtonsClicked(2);handled = true;}return handled; }});

        //ImageButtonにリスナーをつける
        imageButtons_array[0].setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) {eachImageButtonClicked(0);}});
        imageButtons_array[1].setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) {eachImageButtonClicked(1);}});
        imageButtons_array[2].setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) {eachImageButtonClicked(2);}});
        imageButtons_array[3].setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) {eachImageButtonClicked(2);}});
        imageButtons_array[4].setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) {eachImageButtonClicked(2);}});
        imageButtons_array[5].setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) {eachImageButtonClicked(2);}});

        //オリジナルの各ノードにリスナーを付けて、押された際に表示を適宜変える
        oriButtons_array[0].setOnClickListener(new View.OnClickListener(){ public void onClick(View v){eachOriButtonClicked(0);}});
        oriButtons_array[1].setOnClickListener(new View.OnClickListener(){ public void onClick(View v){eachOriButtonClicked(1);}});
        oriButtons_array[2].setOnClickListener(new View.OnClickListener(){ public void onClick(View v){eachOriButtonClicked(2);}});

        //アニメーション------------------------------------------------------------------------------------
        //Duration(時間設定)
        int translateDuration = 500;
        int scaleDuration = 300;
        int alphaDuration = 500;

        //アニメーション用のパラメータ
        float straightY = 0.4f;
        float halfY = straightY / 2f;
        float sideX = straightY * 0.866f;

        //TranslateAnimation, ScaleAnimationで使うそれぞれの開始座標(x, y)
        //b~gの時計回り
        float[] fromX_t = {0, -sideX, -sideX, 0, sideX, sideX};
        float[] fromY_t = {straightY, halfY, -halfY, -straightY, -halfY, halfY};

        //デフォルトノードとオリジナルノードが出てくるときのアニメーションの定義部分。移動とサイズ
        for(int x=0; x<6; x++){
            //移動の定義部分
            translate = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, fromX_t[x],
                    TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, fromY_t[x],
                    TranslateAnimation.RELATIVE_TO_SELF, 0);
            //サイズの定義部分
            scale = new ScaleAnimation(0, 1, 0, 1,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            //アニメーションに要する時間の設定
            translate.setDuration(translateDuration);
            scale.setDuration(scaleDuration);
            //一定の速度ではなく自然な動きにする
            translate.setInterpolator(new FastOutSlowInInterpolator());
            scale.setInterpolator(new FastOutSlowInInterpolator());

            //アニメーションを結合する
            animationSet = new AnimationSet(false);
            animationSet.addAnimation(translate);
            animationSet.addAnimation(scale);

            animationSet_array.add(animationSet);
        }

        //頑張って調整したそれぞれの値
        float throwAwayX = 5f;
        float side2centerX = 0.733f;
        float side2edgeX = 1.25f;
        float throwAwayY = throwAwayX * 2f;
        float straight2centerY = 0.847f;
        float toedgeY = 1.75f;
        float half2centerY = 0.449f;

        //b~gの6つの配列にそれぞれ7つ要素の配列
        //{0~5}:デフォルト・オリジナルノードのアニメーション
        //{6}:真ん中ノードのアニメーション
        //いったん隅のノードは置いとく
        float[][] toX_t = {{0, 0, 0, 0, 0, 0, 0},
                {-throwAwayX, -side2centerX, -throwAwayX, -throwAwayX, -throwAwayX, -throwAwayX, -side2edgeX},
                {-throwAwayX, -throwAwayX, -side2centerX, -throwAwayX, -throwAwayX, -throwAwayX, -side2edgeX},
                {0, 0, 0, 0, 0, 0, 0},
                {throwAwayX, throwAwayX, throwAwayX, throwAwayX, side2centerX, throwAwayX, side2edgeX},
                {throwAwayX, throwAwayX, throwAwayX, throwAwayX, throwAwayX, side2centerX, side2edgeX}};

        float[][] toY_t = {{straight2centerY, throwAwayY, throwAwayY, throwAwayY, throwAwayY, throwAwayY, toedgeY+0.11f},
                {throwAwayY, half2centerY, throwAwayY, throwAwayY, throwAwayY, throwAwayY, toedgeY},
                {-throwAwayY, -throwAwayY, -half2centerY, -throwAwayY, -throwAwayY, -throwAwayY, -toedgeY},
                {-throwAwayY, -throwAwayY, -throwAwayY, -straight2centerY, -throwAwayY, -throwAwayY, -toedgeY-0.1f},
                {-throwAwayY, -throwAwayY, -throwAwayY, -throwAwayY, -half2centerY, -throwAwayY, -toedgeY},
                {throwAwayY, throwAwayY, throwAwayY, throwAwayY, throwAwayY, half2centerY, toedgeY}};

        float switchTfromX, switchTtoX, switchTfromY, switchTtoY,switchSfrom1, switchSfrom2, switchSto1, switchSto2;

        // 上の二次元配列を回して順次animationSet_arrayに格納していく
        for(int w=0; w<2; w++){
            for(int y=0; y<6; y++){
                for(int z=0; z<7; z++){
                    //デフォルト・オリジナルノードが押されたとき用
                    if(w == 0){
                        switchTfromX = 0;
                        switchTtoX = toX_t[y][z];
                        switchTfromY = 0;
                        switchTtoY = toY_t[y][z];
                        switchSfrom1 = 1;
                        switchSto1 = 1.6f;
                        switchSfrom2 = 1;
                        switchSto2 = 0.9f;
                    }
                    //隅のノードが押されたとき用
                    else {
                        switchTfromX = toX_t[y][z];
                        switchTtoX = 0;
                        switchTfromY = toY_t[y][z];
                        switchTtoY = 0;
                        switchSfrom1 = 1.6f;
                        switchSto1 = 1;
                        switchSfrom2 = 0.9f;
                        switchSto2 = 1;
                    }

                    //移動の定義部分
                    translate = new TranslateAnimation(
                            TranslateAnimation.RELATIVE_TO_SELF, switchTfromX,
                            TranslateAnimation.RELATIVE_TO_SELF, switchTtoX,
                            TranslateAnimation.RELATIVE_TO_SELF, switchTfromY,
                            TranslateAnimation.RELATIVE_TO_SELF, switchTtoY);
                    //サイズの定義部分
                    //押されたノードのとき
                    if(toY_t[y][z] == straight2centerY || toY_t[y][z] == -straight2centerY || toY_t[y][z] == half2centerY || toY_t[y][z] == -half2centerY){
                        scale = new ScaleAnimation(switchSfrom1, switchSto1, switchSfrom1, switchSto1,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    }else{
                        scale = new ScaleAnimation(switchSfrom2, switchSto2, switchSfrom2, switchSto2,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    }

                    //アニメーションに要する時間の設定
                    translate.setDuration(translateDuration);
                    scale.setDuration(scaleDuration);
                    //一定の速度ではなく自然な動きにする
                    translate.setInterpolator(new FastOutSlowInInterpolator());
                    scale.setInterpolator(new FastOutSlowInInterpolator());
                    //アニメーションを結合する
                    animationSet = new AnimationSet(false);
                    animationSet.addAnimation(translate);
                    animationSet.addAnimation(scale);

                    animationSet_array.add(animationSet);
                }
            }
        }

        //ノードと文字の誤差の微調整用
        float multi_X = 2.15f;
        float multi_Y = 3.2f;

        float throwAwayX_text = throwAwayX * multi_X;
        float side2centerX_text = side2centerX * multi_X;
        float side2edgeX_text = side2edgeX * multi_X;
        float throwAwayY_text = throwAwayX * 2f * multi_Y;
        float straight2centerY_text = straight2centerY * multi_Y;
        float toedgeY_text = toedgeY * multi_Y;
        float half2centerY_text = half2centerY * multi_Y;

        //b~gの6つの配列にそれぞれ7つ要素の配列
        //{0~5}:デフォルト・オリジナルノードの文字のアニメーション
        //{6}:真ん中ノードのアニメーション
        //いったん隅のノードは置いとく
        float[][] toX_t_text = {{0, 0, 0, 0, 0, 0, 0},
                {-throwAwayX_text, -side2centerX_text, -throwAwayX_text, -throwAwayX_text, -throwAwayX_text, -throwAwayX_text, -side2edgeX_text},
                {-throwAwayX_text, -throwAwayX_text, -side2centerX_text, -throwAwayX_text, -throwAwayX_text, -throwAwayX_text, -side2edgeX_text},
                {0, 0, 0, 0, 0, 0, 0},
                {throwAwayX_text, throwAwayX_text, throwAwayX_text, throwAwayX_text, side2centerX_text, throwAwayX_text, side2edgeX_text},
                {throwAwayX_text, throwAwayX_text, throwAwayX_text, throwAwayX_text, throwAwayX_text, side2centerX_text, side2edgeX_text}};

        float[][] toY_t_text = {{straight2centerY_text, throwAwayY_text, throwAwayY_text, throwAwayY_text, throwAwayY_text, throwAwayY_text, toedgeY_text },
                {throwAwayY_text, half2centerY_text, throwAwayY_text, throwAwayY_text, throwAwayY_text, throwAwayY_text, toedgeY_text },
                {-throwAwayY_text, -throwAwayY_text, -half2centerY_text, -throwAwayY_text, -throwAwayY_text, -throwAwayY_text, -toedgeY_text },
                {-throwAwayY_text, -throwAwayY_text, -throwAwayY_text, -straight2centerY_text, -throwAwayY_text, -throwAwayY_text, -toedgeY_text },
                {-throwAwayY_text, -throwAwayY_text, -throwAwayY_text, -throwAwayY_text, -half2centerY_text, -throwAwayY_text, -toedgeY_text },
                {throwAwayY_text, throwAwayY_text, throwAwayY_text, throwAwayY_text, throwAwayY_text, half2centerY_text, toedgeY_text }};

        float switchTfromX_text, switchTtoX_text, switchTfromY_text, switchTtoY_text;

        // 上の二次元配列を回して順次animationSet_arrayに格納していく
        for(int w_t=0; w_t<2; w_t++){
            for(int y_t=0; y_t<6; y_t++){
                for(int z_t=0; z_t<7; z_t++){
                    //デフォルト・オリジナルノードが押されたとき用
                    if(w_t == 0){
                        switchTfromX_text = 0;
                        switchTtoX_text = toX_t_text[y_t][z_t];
                        switchTfromY_text = 0;
                        switchTtoY_text = toY_t_text[y_t][z_t];
                    }
                    //隅のノードが押されたとき用
                    else {
                        switchTfromX_text = toX_t_text[y_t][z_t];
                        switchTtoX_text = 0;
                        switchTfromY_text = toY_t_text[y_t][z_t];
                        switchTtoY_text = 0;
                    }

                    //移動の定義部分
                    translate = new TranslateAnimation(
                            TranslateAnimation.RELATIVE_TO_SELF, switchTfromX_text,
                            TranslateAnimation.RELATIVE_TO_SELF, switchTtoX_text,
                            TranslateAnimation.RELATIVE_TO_SELF, switchTfromY_text,
                            TranslateAnimation.RELATIVE_TO_SELF, switchTtoY_text
                    );

                    //アニメーションに要する時間の設定
                    translate.setDuration(translateDuration);

                    //一定の速度ではなく自然な動きにする
                    translate.setInterpolator(new FastOutSlowInInterpolator());
                    //アニメーションを結合する
                    animationSet = new AnimationSet(false);
                    animationSet.addAnimation(translate);

                    //進む際の（戻る時でない）中心のノードのみ
                    if(w_t==0 && z_t == 6){
                        //透明度の定義部分（スイッチ済みの文字列を見せにくくするため）
                        alpha = new AlphaAnimation(0, 0);
                        alpha.setDuration(alphaDuration);
                        alpha.setInterpolator(new FastOutSlowInInterpolator());
                        animationSet.addAnimation(alpha);
                    }

                    animationSet_array.add(animationSet);
                }
            }
        }

        //testing
        float straightY_text = straightY * multi_X;
        float halfY_text = halfY * multi_Y;
        float sideX_text = sideX * multi_X;

        //TranslateAnimation, ScaleAnimationで使うそれぞれの開始座標(x, y)
        //b~gの時計回り
        float[] fromX_t_text = {0, -sideX_text, -sideX_text, 0, sideX_text, sideX_text};
        float[] fromY_t_text = {straightY_text, halfY_text, -halfY_text, -straightY_text, -halfY_text, halfY_text};

        //デフォルトノードとオリジナルノードの文字が出てくるときのアニメーションの定義部分。移動とサイズ
        for(int x_t=0; x_t<6; x_t++){
            //移動の定義部分
            translate = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, fromX_t_text[x_t],
                    TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, fromY_t_text[x_t],
                    TranslateAnimation.RELATIVE_TO_SELF, 0);

            //サイズの定義部分
            scale = new ScaleAnimation(0, 1, 0, 1,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            //アニメーションに要する時間の設定
            translate.setDuration(translateDuration);
            scale.setDuration(scaleDuration);
            //一定の速度ではなく自然な動きにする
            translate.setInterpolator(new FastOutSlowInInterpolator());
            scale.setInterpolator(new FastOutSlowInInterpolator());

            //アニメーションを結合する
            animationSet = new AnimationSet(false);
            animationSet.addAnimation(translate);
            animationSet.addAnimation(scale);

            animationSet_array.add(animationSet);
        }

        //透明度の定義部分（スイッチ済みの文字列を見せにくくするため）
        alpha = new AlphaAnimation(0, 0);
        alpha.setDuration(alphaDuration);
        //alpha.setInterpolator(new FastOutSlowInInterpolator());
        animationSet.addAnimation(alpha);
        animationSet_array.add(animationSet);


        //真ん中のテキスト用
        float multi_X2 = 1f;
        float multi_Y2 = 0;
        float sub_multi_Y21 = 1.52f;
        float sub_multi_Y22 = 1.7f;

        for(int t4a=0; t4a<6; t4a++){
            if(t4a==0 || t4a==3){
                multi_Y2 = sub_multi_Y21;
            }else{
                multi_Y2 = sub_multi_Y22;
            }
            translate = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, toX_t[t4a][6] * multi_X2,
                    TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, toY_t[t4a][6] * multi_Y2);
            translate.setDuration(translateDuration);
            translate.setDuration(translateDuration);
            translate.setInterpolator(new FastOutSlowInInterpolator());

            animationSet = new AnimationSet(false);
            animationSet.addAnimation(translate);
            animationSet_array.add(animationSet);
        }
        multi_X2 = -multi_X2;
        multi_Y2 = -multi_Y2;

        for(int t4a=0; t4a<6; t4a++){
            translate = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, toX_t[t4a][6] * multi_X2,
                    TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, toY_t[t4a][6] * multi_Y2,
                    TranslateAnimation.RELATIVE_TO_SELF, 0);

            translate.setInterpolator(new FastOutSlowInInterpolator());

            animationSet = new AnimationSet(false);
            animationSet.addAnimation(translate);
            animationSet_array.add(animationSet);
        }


    }

    //真ん中のノードが押された時、周りのデフォルトのノードを表示する
    public void centerButtonClicked(){
        uRL = "http://fullfill.sakura.ne.jp/JPHACKS/action_keyword.php";
        key = "keyword";

        //pathDefault_Listを調べて新しいpathの場合表示（更新）
        if(pathDefault_List.indexOf(follower) == -1) {

            //クラスに中心のノードの文字を渡すための文字列
            whatsDefault = textOnbutton1.getText().toString();
            //中心のノードからデフォルトノードの文字列を取得するクラスを呼ぶ
            GetDefaultNodesWords job = new GetDefaultNodesWords();


            try {
                Object result = job.execute().get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            //job.execute();

            //各種Buttonを表示
            for (int j=0; j<perNode; j++) {
                buttons_array[j].startAnimation(animationSet_array.get(2*j));
                buttons_array[j].setVisibility(View.VISIBLE);

                textsOnbuttons_array[j].startAnimation(animationSet_array.get(174 + 2*j));
                textsOnbuttons_array[j].setText(defaultNodesishere[j]);
                //textsOnbuttons_array[j].setText("hey");
                //デフォルトノードの文字列として格納
                defaultWords_List.add(defaultNodesishere[j]);

            }

            //今のpathをリストに追加
            pathDefault_List.add(follower);
        }
    }

    //デフォルトのノードが押された時の関数(indexは0~2)
    public void eachButtonClicked(int index){
        //正しい隅のノードを結びつけるためのインデックス
        int imageIndex = 0;
        //赤色か黄色か関数読んで判断するときに使う
        int redoryerrow = 0;
        //色を変えるための画像参照用
        int src = 0;
        //押す前のpathがいくつオリジナルノードを生成しているかを取得する
        int originalKazu = 0;
        boolean zeroornot_original = pathNhowmanyOriginals_Map.containsKey(follower);
        if(zeroornot_original == true){
            originalKazu = pathNhowmanyOriginals_Map.get(follower);
        }

        //押す前のノードの色を与える。0ならデフォルトカラー、1ならオリジナルカラー
        redoryerrow = getNodeColor(follower);

        //移動先のノードのパスを生成
        switch (index){
            case 0://上のノード
                follower += "b";
                imageIndex = 0;
                if(redoryerrow == 0){
                    src = R.drawable.for_button22;
                }else if(redoryerrow == 1){
                    src = R.drawable.for_oributton22;
                }
                break;
            case 1://右下のノード
                follower += "d";
                imageIndex = 2;
                if(redoryerrow == 0){
                    src = R.drawable.for_button44;
                }else if(redoryerrow == 1){
                    src = R.drawable.for_oributton44;
                }
                break;
            case 2://左下のノード
                follower += "f";
                imageIndex = 4;
                if(redoryerrow == 0){
                    src = R.drawable.for_button66;
                }else if(redoryerrow == 1){
                    src = R.drawable.for_oributton66;
                }
                break;
        }

        //中央のボタンにある文字列を取得
        string2swap1 = textOnbutton1.getText().toString();

        //editText.setText(string2swap1);
        text4animation.setText(string2swap1);

        //各ボタンにある文字列を取得
        //各ボタンの文字列を中央に，中央のボタンを隅のボタンの文字列に
        //すでに表示されている隅のノードを非表示にする
        //アニメーションの定義
        //すでに表示されているデフォルト・オリジナルノードを非表示にする
        //隅のボタン，文字列を表示
        string2swap2 = textsOnbuttons_array[index].getText().toString();
        texts_array[imageIndex].setText(string2swap1);
        invisibleImageButtons();
        textOnbutton1.setText(string2swap2);
        invisibleButtons();
        imageButtons_array[imageIndex].setVisibility(View.VISIBLE);
        texts_array[imageIndex].setVisibility(View.VISIBLE);

        //適切なアニメーションを隅じゃない6つのノードで開始する
        //indexはimageIndexが使える
        for(int a=0; a<3; a++){
            buttons_array[a].startAnimation(animationSet_array.get(imageIndex * 7 + 6 + a * 2));
            textsOnbuttons_array[a].startAnimation(animationSet_array.get(imageIndex * 7 + 90 + a * 2));
            texts_array[imageIndex].startAnimation(animationSet_array.get(180));
            imageButtons_array[imageIndex].startAnimation(animationSet_array.get(180));
        }
        for(int b=0; b<originalKazu; b++){
            oriButtons_array[b].startAnimation((animationSet_array.get(imageIndex * 7 + 7 + b * 2)));
            textsOnoributtons_array[b].startAnimation(animationSet_array.get(imageIndex * 7 + 91 + b * 2));
            texts_array[imageIndex+1].startAnimation(animationSet_array.get(180));
            imageButtons_array[imageIndex].startAnimation(animationSet_array.get(180));
        }

        //押した時の中心ノードが赤色の時、黄色の時
        if(redoryerrow == 0){
            editText.setBackgroundResource(R.drawable.frame_style);
        }else{
            editText.setBackgroundResource(R.drawable.original_frame_style);
        }
        editText.startAnimation(animationSet_array.get(imageIndex * 7 + 12));
        button1.startAnimation(animationSet_array.get(imageIndex * 7 + 96));
        textOnbutton1.startAnimation(animationSet_array.get(imageIndex * 7 + 96));
        text4animation.startAnimation(animationSet_array.get(imageIndex + 181));
        addButton.startAnimation(animationSet_array.get(180));


        //各ノードの適切なViewの表示と文字列の表示を行う
        visibleNtext2B();

        //真ん中のノードをデフォルトノードの色（赤色）にする
        button1.setBackgroundResource(R.drawable.frame_style);
        //隅のノードを適切な色の背景に変える
        imageButtons_array[imageIndex].setImageResource(src);

        //リストに隅のノードの文字列を覚えさせる
        pathNText_Map.put(follower, string2swap1);

    }

    //隅と真ん中以外のノードを非表示にするための関数
    public void invisibleButtons(){
        for(int k=0; k<perNode; k++){
            buttons_array[k].setVisibility(View.INVISIBLE);
            oriButtons_array[k].setVisibility(View.INVISIBLE);
            oriETs_array[k].setVisibility(View.INVISIBLE);
        }
    }

    //隅のノードを非表示にするための関数
    public void invisibleImageButtons(){
        for(int l=0; l<6; l++){
            imageButtons_array[l].setVisibility(View.INVISIBLE);
            texts_array[l].setVisibility(View.INVISIBLE);
        }
    }

    //addButtonが押されたときに呼ばれる。
    public void addButtonClicked(){
        //今いるノードでのオリジナルノードの数を取得し、それが空(null)の場合
        if(pathNhowmanyOriginals_Map.get(follower) == null){
            count = 0;

            //空文字で追加する
            for(int v=0; v<perNode; v++){
                originalWords_List.add(" ");
            }
        }else{
            //今のpathで作成されたオリジナルノードの数を取得する
            count = pathNhowmanyOriginals_Map.get(follower);
        }
        //一度EditTextをリフレッシュさせる
        oriETs_array[count].setText(" ");
        //EditTextを表示
        oriETs_array[count].startAnimation(animationSet_array.get(2 * count + 1));
        oriETs_array[count].setVisibility(View.VISIBLE);
        //なんでか知らんけど背後に配置されちゃうから前に持ってくる → それでも持ってこれない
        oriETs_array[count].bringToFront();
        showSoftInput(oriETs_array[count]);

        //初めて踏んだpathの場合
        // オリジナルノードが作成されたpathとしてリストに格納する
        if(pathOriginal_List.indexOf(follower) == -1){
            pathOriginal_List.add(follower);
        }

        //pathNhowmanyOriginals_Mapを更新
        pathNhowmanyOriginals_Map.put(follower, count+1);
    }

    //オリジナルノードの文字入力が完了した時に呼ばれるメソッド。EditTextとButtonのViewを切り替える(indexは0~5)
    public void oriButtonsClicked(int index){
        oriIndex = pathOriginal_List.indexOf(follower);

        //EditTextにある文字を取得する
        text2pass = oriETs_array[index].getText().toString();
        //入力してないのに進んじゃう現象をなくすため
        if(text2pass.length() != 1){
            //各oriButtonに文字を渡して表示に、EditTextを非表示に
            textsOnoributtons_array[index].setText(text2pass);
            oriButtons_array[index].setVisibility(View.VISIBLE);
            oriETs_array[index].setText(" ");
            oriETs_array[index].setVisibility(View.INVISIBLE);

            //演算簡略化
            oriInt = oriIndex * perNode + index;

            //EditTextに入力した文字をリストに格納する(addButtonClickedで少なくとも空文字生成されてるから、ここでは条件分岐しなくていい)
            originalWords_List.set(oriInt, text2pass);
        }
    }

    //隅のノードが押された際に呼ばれるメソッド。適宜Viewの表示を変える（戻った先は必ずデフォルトノードを作成しているし、pathは一文字消しただけ）
    public void eachImageButtonClicked(int index){
        editText.setText(" ");

        String follower_end,  pre_follower, pre_follower_end;
        //followerの末尾を一文字削除することでpathを戻したように振る舞う
        pre_follower = follower;
        follower = follower.substring(0, follower.length()-1);

        //1文字削除されたfollowerの最後の文字を取得する
        pre_follower_end = pre_follower.substring(pre_follower.length()-1, pre_follower.length());
        follower_end = follower.substring(follower.length()-1, follower.length());

        //戻る先のpathがいくつオリジナルノードを生成したかどうかを取得する
        int originalKazu = 0;
        boolean zeroornot_original = pathNhowmanyOriginals_Map.containsKey(follower);
        if(zeroornot_original == true){
            originalKazu = pathNhowmanyOriginals_Map.get(follower);
        }
        //戻る先のpathがデフォルトノードを生成したかどうかを取得する
        int zeroornot_default = pathDefault_List.indexOf(follower);

        //戻った時のImageButtonの場所を格納する変数
        int imageIndex = 0;
        int animationIndex = 0;
        boolean origin = false;
        //pathから前のImageButtonの場所を探る
        if(follower_end.equals("a")){
            origin = true;
        }
        switch (follower_end){
            case "b":
                imageIndex = 0;
                break;
            case "c":
                imageIndex = 1;
                break;
            case "d":
                imageIndex = 2;
                break;
            case "e":
                imageIndex = 3;
                break;
            case "f":
                imageIndex = 4;
                break;
            case "g":
                imageIndex = 5;
                break;
        }
        switch (pre_follower_end){
            case "b":
                animationIndex = 0;
                break;
            case "c":
                animationIndex = 1;
                break;
            case "d":
                animationIndex = 2;
                break;
            case "e":
                animationIndex = 3;
                break;
            case "f":
                animationIndex = 4;
                break;
            case "g":
                animationIndex = 5;
                break;
        }
        //隅のノードの文字列を取得
        //隅のノードの文字列を中央に
        //すでに表示されているデフォルトの6つのノード，隅のノード，文字列を非表示にする関数を呼ぶ
        //隅のノード，文字列を表示
        string2swap2 = texts_array[index].getText().toString();
        textOnbutton1.setText(string2swap2);
        text4animation.setText(string2swap2);
        invisibleImageButtons();

        //適切なアニメーションを隅じゃない6つのノードで開始する
        if(zeroornot_default != -1){
            for(int a=0; a<3; a++){
                buttons_array[a].startAnimation(animationSet_array.get(animationIndex * 7 + 48 + a * 2));
                textsOnbuttons_array[a].startAnimation(animationSet_array.get(animationIndex * 7 + 132 + a * 2));
            }
        }
        for(int b=0; b<originalKazu; b++){
            oriButtons_array[b].startAnimation((animationSet_array.get(animationIndex * 7 + 49 + b * 2)));
            textsOnoributtons_array[b].startAnimation(animationSet_array.get(animationIndex* 7 + 133 + b * 2));
        }

        button1.startAnimation(animationSet_array.get(animationIndex * 7 + 54));
        textOnbutton1.startAnimation(animationSet_array.get(animationIndex * 7 + 138));
        text4animation.startAnimation(animationSet_array.get(animationIndex + 187));
        addButton.startAnimation(animationSet_array.get(180));



        invisibleButtons();
        //戻った先が最初の時を例外に隅のノードを表示する
        //前のノードが最初のノードじゃない場合
        if(!origin){
            imageButtons_array[imageIndex].setVisibility(View.VISIBLE);
            texts_array[imageIndex].setVisibility(View.VISIBLE);
            texts_array[imageIndex].setText(pathNText_Map.get(follower));
        }
        //follower_end(前の文字列)が"a"の時
        else {
            //button1.setText(first_Text);
            textOnbutton1.setText(first_Text);
            imageButtons_array[imageIndex].setVisibility(View.INVISIBLE);
        }

        //各ノードの適切なViewの表示と文字列の表示を行う
        visibleNtext2B();

        //一つ前のノードがデフォルトノード(a, b, d, f)の時は移動後の真ん中のノードをデフォルトノードの色にする
        //そうでなければオリジナルノードの色に
        //.equalsじゃないと==だと結合とかしてる文字列は正しく判定されない
        if(follower_end.equals("a") ||follower_end.equals("b") || follower_end.equals("d") || follower_end.equals("f")){
            //真ん中のノードをデフォルトノードの色（赤色）にする
            button1.setBackgroundResource(R.drawable.frame_style);
        }else if(follower_end.equals("c") || follower_end.equals("e") || follower_end.equals("g")){
            //真ん中のノードをオリジナルノードの色（黄色）にする
            button1.setBackgroundResource(R.drawable.original_frame_style);
        }
    }

    //オリジナルのノードが押された時の関数(indexは0~2)
    public void eachOriButtonClicked(int index){
        int imageIndex = 0;
        int redoryerrow = 0;
        int src = 0;

        int originalKazu = pathNhowmanyOriginals_Map.get(follower);

        int zeroornot_default = pathDefault_List.indexOf(follower);

        //押す前のノードの色を与える。0ならデフォルトカラー、1ならオリジナルカラー
        redoryerrow = getNodeColor(follower);

        //移動先のノードのパスを生成
        switch (index){
            case 0://上のノード
                follower += "c";
                imageIndex = 1;
                if(redoryerrow == 0){
                    src = R.drawable.for_button33;
                }else if(redoryerrow == 1){
                    src = R.drawable.for_oributton33;
                }
                break;
            case 1://右下のノード
                follower += "e";
                imageIndex = 3;
                if(redoryerrow == 0){
                    src = R.drawable.for_button55;
                }else if(redoryerrow == 1){
                    src = R.drawable.for_oributton55;
                }
                break;
            case 2://左下のノード
                follower += "g";
                imageIndex = 5;
                if(redoryerrow == 0){
                    src = R.drawable.for_button77;
                }else if(redoryerrow == 1){
                    src = R.drawable.for_oributton77;
                }
                break;
        }
        //中央のボタンにある文字列を取得
        //string2swap1 = button1.getText().toString();
        string2swap1 = textOnbutton1.getText().toString();

        //editText.setText(string2swap1);
        text4animation.setText(string2swap2);


        //各ボタンにある文字列を取得
        //各ボタンの文字列を中央に，中央のボタンを隅のボタンの文字列に
        //すでに表示されているデフォルトのボタン，隅のボタン，文字列を非表示にする関数を呼ぶ
        //隅のボタン，文字列を表示
        //string2swap2 = oriButtons_array[index].getText().toString();
        string2swap2 = textsOnoributtons_array[index].getText().toString();
        //button1.setText(string2swap2);
        textOnbutton1.setText(string2swap2);
        texts_array[imageIndex].setText(string2swap1);
        invisibleImageButtons();


        //適切なアニメーションを隅じゃない6つのノードで開始する
        //indexはimageIndexが使える
        //今のノードがデフォルトノードを生成している時
        if(zeroornot_default != -1){
            for(int a=0; a<3; a++){
                buttons_array[a].startAnimation(animationSet_array.get(imageIndex * 7 + 6 + a * 2));
                textsOnbuttons_array[a].startAnimation(animationSet_array.get(imageIndex * 7 + 90 + a * 2));
                texts_array[imageIndex].startAnimation(animationSet_array.get(180));
                imageButtons_array[imageIndex].startAnimation(animationSet_array.get(180));
            }
        }
        for(int b=0; b<originalKazu; b++){
            oriButtons_array[b].startAnimation((animationSet_array.get(imageIndex * 7 + 7 + b * 2)));
            textsOnoributtons_array[b].startAnimation(animationSet_array.get(imageIndex * 7 + 91 + b * 2));
            texts_array[imageIndex].startAnimation(animationSet_array.get(180));
            imageButtons_array[imageIndex].startAnimation(animationSet_array.get(180));
        }

        //押した時の中心ノードが赤色の時、黄色の時
        if(redoryerrow == 0){
            editText.setBackgroundResource(R.drawable.frame_style);
        }else{
            editText.setBackgroundResource(R.drawable.original_frame_style);
        }
        editText.startAnimation(animationSet_array.get(imageIndex * 7 + 12));
        button1.startAnimation(animationSet_array.get(imageIndex * 7 + 96));
        textOnbutton1.startAnimation(animationSet_array.get(imageIndex * 7 + 96));
        text4animation.startAnimation(animationSet_array.get(imageIndex + 181));
        addButton.startAnimation(animationSet_array.get(180));


        invisibleButtons();
        imageButtons_array[imageIndex].setVisibility(View.VISIBLE);
        texts_array[imageIndex].setVisibility(View.VISIBLE);

        //真ん中のノードをオリジナルノードの色（黄色）にする
        button1.setBackgroundResource(R.drawable.original_frame_style);
        //隅のノードを適切な背景の色に変える
        imageButtons_array[imageIndex].setImageResource(src);

        //各ノードの適切なViewの表示と文字列の表示を行う
        visibleNtext2B();

        //リストに隅のノードの文字列を覚えさせる
        pathNText_Map.put(follower, string2swap1);
    }


    //各ノードの適切なViewの表示と文字列の表示を行う
    public void visibleNtext2B(){
        //現在のノードのパスをpathDefault_ListとpathOriginal_Listで探す
        pathDefaultIndex = pathDefault_List.indexOf(follower);
        pathOriginalIndex = pathOriginal_List.indexOf(follower);


        //現在のpathでデフォルトノードが作られている場合
        if(pathDefaultIndex != -1){
            //デフォルトノードを表示して、その文字を中心のノードを使って引っ張ってくる
            for(int n=0; n<perNode; n++){
                buttons_array[n].setVisibility(View.VISIBLE);
                //buttons_array[n].setText(defaultWords_List.get(pathDefaultIndex * perNode + n));
                textsOnbuttons_array[n].setText(defaultWords_List.get(pathDefaultIndex * perNode + n));
            }
        }

        //現在のpathでオリジナルノードが作られている場合
        if(pathOriginalIndex != -1){
            //for文loopの回数をオリジナルノードを作った数から生成
            int loopmax = pathNhowmanyOriginals_Map.get(follower);
            //オリジナルノードを作った数だけ回して、
            for(int m=0; m<loopmax; m++){
                oriButtons_array[m].setVisibility(View.VISIBLE);
                //oriButtons_array[m].setText(originalWords_List.get(pathOriginalIndex * perNode + m));
                textsOnoributtons_array[m].setText(originalWords_List.get(pathOriginalIndex * perNode + m));
            }
        }
    }

    //引数で与えたfollowerのノードの色をtrueかfalseで返す
    public static int getNodeColor(String now_follower){
        String now_follower_end = " ";
        //デフォルトノードかオリジナルノードか（赤色か黄色か）0がデフォルト
        int doro = 0;

        //1文字削除されたpost_followerの最後の文字を取得する
        now_follower_end = now_follower.substring(now_follower.length()-1, now_follower.length());

        //初期ノードを含むデフォルトノードの時
        if(now_follower_end.equals("a") || now_follower_end.equals("b") || now_follower_end.equals("d") || now_follower_end.equals("f")){
            doro = 0;
        }
        //オリジナルノードの時
        else if(now_follower_end.equals("c") || now_follower_end.equals("e") || now_follower_end.equals("g")){
            doro = 1;
        }

        //0か1でどっちの色か教える
        return doro;
    }

    //EditTextが出てきたと同時にソフトキーボードを表示させるメソッド
    public void showSoftInput(final EditText view){
        if(null == view){
            return;
        }
        view.post(new Runnable() {
            @Override
            public void run() {
                if(null == view){
                    return;
                }
                view.requestFocus();
                InputMethodManager input = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(null != input) {
                    input.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
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
