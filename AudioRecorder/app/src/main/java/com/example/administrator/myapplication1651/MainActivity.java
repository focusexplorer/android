package com.example.administrator.myapplication1651;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
//import android.media.SubtitleTrack.RenderingWidget;
//import android.media.SubtitleController;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    MediaRecorder recorder;
    MediaPlayer mediaPlayer;
    String path=null;
     Button btn0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        // 生成3个Button// 生成一个LinearLayout，作为布局容器来动态添加3个Button
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        btn0 = new Button(this);
        btn0.setText("show file len");
        btn0.setText("show file len");
        final Button btn1 = new Button(this);
        btn1.setText("1");
        btn1.setText("Button1");
        final Button btn2 = new Button(this);
        btn2.setText("2");
        btn2.setText("Button2");
        final Button btn3 = new Button(this);
        btn3.setText("3");
        btn3.setText("Button3");
        final Button btn4 = new Button(this);
        btn4.setText("4");
        btn4.setText("Button4");

        // 动态把三个Button添加到
        layout.addView(btn0);
        layout.addView(btn1);
        layout.addView(btn2);
        layout.addView(btn3);
        layout.addView(btn4);

        // 点击按钮时，先把原来在布局容器layout上的删掉，再添加上局容器layout，这样本次添加的控件就会排序到最后，以理解动态添加控件的思路
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                layout.removeView(btn1);
//                layout.addView(btn1);

                path = Environment.getExternalStorageDirectory().getAbsolutePath();
                path += "/ione.amr";
                recorder=new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(path);
                try{
                    recorder.prepare();
                }catch(IOException e)
                {
                    Log.e("TT","prepare error");
                }
                recorder.start();

            }
        });

        // 同btn1一样道理
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                layout.removeView(btn2);
//                layout.addView(btn2);
                recorder.stop();
                recorder.reset();
//                recorder.release();
            }
        });

        // 同btn1一样道理
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                layout.removeView(btn3);
//                layout.addView(btn3);

                File file=new File(path);
                FileInputStream fis;
                int file_len=0;
                try{
                    fis=new FileInputStream(file);
                    file_len=fis.available();
                    btn0.setText("file_len:"+file_len);

                }catch(Exception e){
                    Log.e("purper"," file input stream error");
                }
                mediaPlayer = new MediaPlayer();
//                SubtitleController sc = new SubtitleController(context, null, null);
//                sc.mHandler = new Handler();
//                mediaplayer.setSubtitleAnchor(sc, null);

                try{
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                }catch(Exception e)
                {
                    Log.e("purper","error just");
                }
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.reset();
//                }
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                layout.removeView(btn4);
//                layout.addView(btn4);
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        });
        setContentView(layout);
    }
}
