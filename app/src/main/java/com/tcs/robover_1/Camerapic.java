package com.tcs.robover_1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class Camerapic extends AppCompatActivity {
    ImageView img;
    static boolean waitornot = false;
    Button post;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        img = (ImageView) findViewById(R.id.img);


        String path = getIntent().getStringExtra("path");
        String filePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "FaceDetection" + File.separator + path;
        bmp = BitmapFactory.decodeFile(filePath);
        img.setImageBitmap(bmp);
        waitornot = true;


        FaceDetectionActivity.picturetaken = false;
        FaceDetectionActivity.waiting = true;

        FaceDetectionActivity.checkcammusic = true;
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                Intent i = new Intent(Camerapic.this, FaceDetectionActivity.class);
                startActivity(i);
                FaceDetectionActivity.dontdistrb = false;
                finish();
            }
        }, 7000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
