package com.example.haruyai.brainstorming3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by haruya.i on 2017/10/30.
 */

public class Summary extends AppCompatActivity{
    private String string;
    private String[] summary_list = new String[3];
    private String[] keywords_list = new String[3];

    private String summary;
    private String keywords;

    private TextView tv4string, tv4summary, tv4keywords;

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
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_layout);

        Intent intent = getIntent();

        string = intent.getStringExtra("resultString");
        summary_list = intent.getStringArrayExtra("summary");
        keywords_list = intent.getStringArrayExtra("keywords");

        tv4string = (TextView)this.findViewById(R.id.string_line);
        tv4summary = (TextView)this.findViewById(R.id.summary_line);
        tv4keywords = (TextView)this.findViewById(R.id.keywords_line);

        if(summary_list[0].equals("null")){
            summary = "文章が短すぎるため要約できませんでした";
        }if(keywords_list[0].equals("null")){
           keywords = "文章が短すぎるためキーワードを抽出できませんでした";
        }else{
            summary = "要約：\n    ";
            keywords = "キーワード：\n    ";

            for(int i=0; i<3; i++){
                summary += summary_list[i] + " \n";
                keywords += keywords_list[i] + " ";
            }
        }

        tv4string.setText(string);
        tv4summary.setText(summary);
        tv4keywords.setText(keywords);


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
