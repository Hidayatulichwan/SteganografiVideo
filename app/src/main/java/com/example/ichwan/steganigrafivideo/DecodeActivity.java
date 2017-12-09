package com.example.ichwan.steganigrafivideo;


import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.MediaController;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;


public class DecodeActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 1;
    private Uri fileUri;
    String tempat;
    DisplayMetrics dm;
    private Button btnTakeVid;
    private VideoView videoPreview2;
    private MediaController mediacontroller;
    private TextView outputTexs, info;
    String srcPath = null;
    String convertedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnTakeVid = (Button) findViewById(R.id.btnTakeVid);
        outputTexs = (TextView) findViewById(R.id.outputTeks);
        info = (TextView) findViewById(R.id.info);
        videoPreview2 = (VideoView) findViewById(R.id.videoPreview2);
        btnTakeVid.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Takevideo();
            }
        });


    }


    public void Takevideo() {

        Intent i = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/*.mp4");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);

    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {

                Uri uri = data.getData();
                convertedPath = getRealPathFromURI(uri);
                //info.setText("Real Path: " + convertedPath + "\n");

                videoPreview2.setVideoURI(uri);
                videoPreview2.requestFocus();
                videoPreview2.start();
                videoPreview2.canPause();
                videoPreview2.stopPlayback();
                videoPreview2.resume();
                mediacontroller = new MediaController(this);
                mediacontroller.setMediaPlayer(videoPreview2);
                mediacontroller.setAnchorView(videoPreview2);
                videoPreview2.setMediaController(mediacontroller);

                int i = 0;
                try {
                    i = Steganography.retriveMessage(convertedPath);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }if (i == Steganography.SUDAH_ADA_DATA) {
                    outputTexs.setText("Pesn Asli : " + new String(Steganography.getMessage()) + "\n Setelah didecode :" +
                                                        " ");
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Pesan tidak ditemukan!\",\n" + "\"Error!", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Video Tidak Dapat Di Buka\",\n" + "\"Error!", Toast.LENGTH_SHORT)
                        .show();


            }

        }
    }



    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Video.Media.DATA };

        //This method was deprecated in API level 11
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(
                this,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
