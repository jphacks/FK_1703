package com.example.h_sato.minutes;

        import android.app.Activity;
        import android.os.Bundle;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import android.widget.TextView;

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

        hashTmp.put("theme", "テーマ");
        hashTmp.put("created_day", "Ruler");
        hashTmp.put("highlight", "長さを測るもの");
        hashTmp.put("node_number", "1");
        list_data.add(new HashMap<String, String>(hashTmp));

        hashTmp.clear();
        hashTmp.put("theme", "ストップウォッチ");
        hashTmp.put("created_day", "StopWatch");
        hashTmp.put("highlight", "時間を計るもの");
        hashTmp.put("node_number", "3");
        list_data.add(new HashMap<String, String>(hashTmp));

        hashTmp.clear();
        hashTmp.put("theme", "体重計");
        hashTmp.put("created_day", "WeightScale");
        hashTmp.put("highlight", "体重を量るもの");
        hashTmp.put("node_number", "5");
        list_data.add(new HashMap<String, String>(hashTmp));

        SimpleAdapter simp = new SimpleAdapter(getApplicationContext(), list_data, R.layout.two_line_list_item,
                new String[]{"theme", "created_day", "highlight", "node_number"}, new int[]{R.id.item_theme, R.id.item_created_day, R.id.item_highlight, R.id.item_node_number});

        ((ListView)findViewById(R.id.listView1)).setAdapter(simp);
    }
}