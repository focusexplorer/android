package com.example.purper.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;

import java.lang.reflect.Array;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    Thread ct;
    private AudioRecord mAudioRecord;
    boolean mIsLoopExit=true;
    int mMinBufferSize;
    final int array_len=1000000;
    byte[] all_data=new byte[array_len];
    int cur_len=0;

    CapThread c1thread=new CapThread();

    volatile boolean CTL_RECORD=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
//        boolean flag = PermissionChecker.checkSelfPermission(this, "android.permission.RECORD_AUDIO") == PermissionChecker.PERMISSION_GRANTED;
//        if (flag) {
//            Log.d("cap", "has permisson");
////            return;
//        } else {
//            Log.d("cap", "no permisson");
////            this.startActivity(getAppDetailSettingIntent());
////            return;
//        }

        final Button b = (Button)findViewById(R.id.button);
        b.setText("Start");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(b.getText().toString()=="Start") {
                    b.setText("Stop");
                    mAudioRecord.startRecording();
                    CTL_RECORD=true;
                    cur_len=0;
                }
                else if(b.getText().toString()=="Stop")
                {
                    b.setText("Start");
                    CTL_RECORD=false;
                    mAudioRecord.stop();
                    TextView tv = (TextView) findViewById(R.id.sample_text);
//                    tv.setText(Arrays.toString(all_data));
                    tv.setText(Integer.toString(cur_len));
                }
        }});

        // Here, thisActivity is the current activity
        Activity thisActivity;
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    723);
        }
        mMinBufferSize= AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,mMinBufferSize);
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e("cap", "AudioRecord initialize fail !");
            return;
        }
        c1thread.start();
    }

    private class CapThread extends Thread{

        public void run()
        {
            while (true) {
//                SystemClock.sleep(10);
                if (CTL_RECORD) {
                    if(cur_len > array_len - mMinBufferSize)
                    {
                        Log.e("cap", "all_data buffer is full");
                    }
                    else
                    {
                        byte[] buffer = new byte[mMinBufferSize];
                        int ret = mAudioRecord.read(buffer, 0, mMinBufferSize);
                        if (ret < 0) {
                            Log.e("cap", "Error read");
                        }
                        else
                        {
                            Log.d("cap", Integer.toString(ret)+"/"+ Integer.toString(mMinBufferSize)+"=>"+Arrays.toString(buffer));
                            System.arraycopy(buffer, 0, all_data, cur_len, ret);
                            cur_len += ret;

                        }
                    }
                }
            }
        }
    }    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 723: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("cap","permisson was granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.d("cap","permissin denied");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
