package com.example.huajun.opengladvance;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;

import com.example.huajun.opengladvance.level8.GLRender;
import com.example.huajun.opengladvance.level8.GLView;

/**
 * Created by huajun on 18-7-19.
 */

public class Level8Activity extends AppCompatActivity {
    GLView glView;

    SeekBar seekBar1,seekBar2,seekBar3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HJ","SSS");
        setContentView(R.layout.activity_level8);

        glView = findViewById(R.id.glView);

        seekBar1 = findViewById(R.id.seekBar);
        seekBar2 = findViewById(R.id.seekBar2);
        seekBar3 = findViewById(R.id.seekBar3);

        seekBar1.setMax(100);
        seekBar2.setMax(100);
        seekBar3.setMax(100);

        seekBar1.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBar2.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBar3.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }
    float ambient;
    float diffuse;
    float specular;

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.seekBar:
                    ambient = progress/100.0f;
                    glView.setStrength(ambient,diffuse,specular);
                    break;
                case R.id.seekBar2:
                    diffuse = progress/100.0f;
                    glView.setStrength(ambient,diffuse,specular);
                    break;
                case R.id.seekBar3:
                    specular = progress/100.0f;
                    glView.setStrength(ambient,diffuse,specular);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
