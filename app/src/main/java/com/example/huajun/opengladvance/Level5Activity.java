package com.example.huajun.opengladvance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.huajun.opengladvance.level5.GLView;

/**
 * Created by huajun on 18-7-12.
 */

public class Level5Activity extends AppCompatActivity {

    Spinner spinner;
    GLView glView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level5);
        spinner = findViewById(R.id.spinner);
        String[] filterName = new String[]{"黑白","交叉冲印"};
        spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,filterName));

        glView = findViewById(R.id.glView);
        spinner.setOnItemSelectedListener(onItemSelectedListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("HJ","onItemSelected");

            glView.setFilterType(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
