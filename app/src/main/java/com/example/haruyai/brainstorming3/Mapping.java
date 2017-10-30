package com.example.haruyai.brainstorming3;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haruya.i on 2017/10/27.
 */

public class Mapping extends AppCompatActivity {

    MainActivity brainstorming = new MainActivity();

    List<String> defaultNodes= brainstorming.defaultWords_List;
    List<String> originalNodes= brainstorming.originalWords_List;

    BubblePicker bubblePicker;

    int image = R.drawable.likesilk;

    int default_color = Color.parseColor("#ff7777");
    int original_color = Color.parseColor("#fff470");


    //ActionBar上のアイコン（Item）が押されたことを検知するための関数
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //IDごとにボタンを押した際の処理を記述
        switch (item.getItemId()) {
            //戻るアイコンが押された時
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapping_layout);

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
            // CutomViewを表示するか
            actionBar.setDisplayShowCustomEnabled(true);
        }


        bubblePicker = (BubblePicker) findViewById(R.id.picker);
        ArrayList<PickerItem> listItems = new ArrayList<>();
        for (int i = 0; i < defaultNodes.size(); i++) {
            if(defaultNodes.get(i).equals(" ") || defaultNodes.get(i).equals("") || defaultNodes.get(i).equals(null)){
                break;
            }
            PickerItem item = new PickerItem(defaultNodes.get(i), default_color, Color.WHITE, getDrawable(image));
            listItems.add(item);
        }
        for (int j=0; j<originalNodes.size(); j++) {
            if(originalNodes.get(j).equals(" ") || originalNodes.get(j).equals("") || originalNodes.get(j).equals(null)) {
                break;
            }
            PickerItem item = new PickerItem(originalNodes.get(j), original_color, Color.WHITE, getDrawable(image));
            listItems.add(item);
        }

        bubblePicker.setItems(listItems);
        bubblePicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem pickerItem) {
                //Toast.makeText(getApplicationContext(), "" + pickerItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
                //name[0] = "clicked";

                ArrayList<PickerItem> listItems = new ArrayList<>();
                for (int i = 0; i < defaultNodes.size(); i++) {
                    if(defaultNodes.get(i).equals(" ") || defaultNodes.get(i).equals("") || defaultNodes.get(i).equals(null)){
                        break;
                    }
                    PickerItem item = new PickerItem(defaultNodes.get(i), default_color, Color.WHITE, getDrawable(image));
                    listItems.add(item);
                }
                for (int j = 0; j < originalNodes.size(); j++) {
                    if(originalNodes.get(j).equals(" ") || originalNodes.get(j).equals("") || originalNodes.get(j).equals(null)) {
                        break;
                    }
                    PickerItem item = new PickerItem(originalNodes.get(j), original_color, Color.WHITE, getDrawable(image));
                    listItems.add(item);
                }
            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem pickerItem) {
                //Toast.makeText(getApplicationContext(), "" + pickerItem.getTitle() + " Deselected", Toast.LENGTH_SHORT).show();
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
