package com.example.h_sato.minutes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ArrayList<HashMap<String, String>> list_data = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hashTmp = new HashMap<String, String>();
        ListView listView = (ListView) findViewById(R.id.listView1);


        for(int i=0; i<jArray_length()-1; i++) {
            hashTmp.put("theme", json_data_get.getString("theme"));
            hashTmp.put("created_info", json_data_get.getString("created_info"));
            // hashTmp.put("highlight", );
            // hashTmp.put("node_number", ); // intだけどstringにしとく
            list_data.add(new HashMap<String, String>(hashTmp));
        }

        SimpleAdapter simp = new SimpleAdapter(getApplicationContext(), list_data, R.layout.two_line_list_item,
                new String[]{"theme", "created_info", "highlight", "node_number"}, new int[]{R.id.item_theme, R.id.item_created_info, R.id.item_highlight, R.id.item_node_number});

        ((ListView)findViewById(R.id.listView1)).setAdapter(simp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView listView = (ListView) parent;
                // クリックされたアイテムを取得します
                String item = (String) listView.getItemAtPosition(position);
            }
        });

        View plusmark = findViewById(R.id.add);
        plusmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // テキスト入力用Viewの作成
                final EditText editView = new EditText(ListActivity.this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(ListActivity.this);
                dialog.setTitle("トークテーマ");
                dialog.setView(editView); // OKボタンの設定
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    // OKボタンをタップした時の処理をここに記述
                        //インテントの作成
                        Intent intent = new Intent(this, MainActivity.class);
                        //データをセットしてるだけでサーバーに送ってない
                        intent.putExtra("sendText",editText.getText().toString());
                        //遷移先の画面を起動
                        // StartActivity(intent);
                    }
                });

                // キャンセルボタンの設定
                dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    // キャンセルボタンをタップした時の処理をここに記述
                    }
                });
                dialog.show();
            }
        });
    }
}