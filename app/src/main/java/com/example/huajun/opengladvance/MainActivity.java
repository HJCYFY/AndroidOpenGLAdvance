package com.example.huajun.opengladvance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ArrayList<MenuItem> mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mMenu = new ArrayList<>();
        mMenu.add(new MenuItem("绘制形体",Level0Activity.class));
        mMenu.add(new MenuItem("图片处理",Level1Activity.class));
        mMenu.add(new MenuItem("压缩纹理",Level2Activity.class));
        mMenu.add(new MenuItem("FBO使用",Level3Activity.class));
        mMenu.add(new MenuItem("FBO进阶",Level4Activity.class));
        mMenu.add(new MenuItem("Camera预览",Level5Activity.class));
        mMenu.add(new MenuItem("Camera进阶",Level6Activity.class));
        mMenu.add(new MenuItem("图像混合",Level7Activity.class));
        mMenu.add(new MenuItem("光照",Level8Activity.class));
        mMenu.add(new MenuItem("Pikachu",Level9Activity.class));
        mRecyclerView.setAdapter(new MenuAdapter());
        requestPermission();
    }

    private class MenuItem {
        String name;
        Class<?> clazz;
        public MenuItem(String s,Class<?> c) {
            name = s;
            clazz = c;
        }
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_button,parent,false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {
            return mMenu.size();
        }

        class  ViewHolder extends RecyclerView.ViewHolder{
            Button mButton;
            ViewHolder(View view) {
                super(view);
                mButton = view.findViewById(R.id.mBtn);
                mButton.setOnClickListener(onClickListener);
            }

            void setPosition(int position) {
                MenuItem item = mMenu.get(position);
                mButton.setText(item.name);
                mButton.setTag(position);
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int)v.getTag();
            MenuItem item = mMenu.get(position);
            startActivity(new Intent(MainActivity.this,item.clazz));
        }
    };

    public void requestPermission(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            boolean result= ((ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) ||
                    (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) ||
                            (ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED ));
            if(result){
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA
                        ,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }
        }
    }
}
