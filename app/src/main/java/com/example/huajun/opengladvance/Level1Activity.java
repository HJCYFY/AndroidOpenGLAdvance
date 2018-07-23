package com.example.huajun.opengladvance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.huajun.opengladvance.level1.GLView;

import java.io.IOException;

/**
 * Created by huajun on 18-7-6.
 */

public class Level1Activity extends AppCompatActivity {

    Spinner spinner;
    GLView glView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level1);

        spinner = findViewById(R.id.filter);

        String[] filterName = new String[]{"原图","黑白","模糊","放大镜"};
        spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,filterName));

        spinner.setOnItemSelectedListener(onItemSelectedListener);
        glView = findViewById(R.id.glView);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("HJ","p "+ position);
            glView.setFilter(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

}
