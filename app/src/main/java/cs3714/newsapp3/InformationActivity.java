package cs3714.newsapp3;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            finish();
            return;
        }
        setContentView(R.layout.fragment_list);
        Intent intent=getIntent();
        String info=intent.getStringExtra(MainActivity.INFORMATION);
        TextView text=(TextView) findViewById(R.id.list_text);
        text.append(info);
    }
}
