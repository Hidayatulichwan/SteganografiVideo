package com.example.ichwan.steganigrafivideo;


import android.content.Intent;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.MediaController;
import android.view.View;
import android.widget.VideoView;


public class DecodeActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 1;

    private Button btnTakeVid;
    private VideoView videoPreview2;
    private MediaController mediacontroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnTakeVid = (Button) findViewById(R.id.btnTakeVid);
        videoPreview2 = (VideoView) findViewById(R.id.videoPreview2);
        btnTakeVid.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Takevideo();
            }
        });


    }


    public void Takevideo() {

        Intent i = new Intent(Intent.ACTION_VIEW,android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/*.mp4");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Uri selectedImageUri = data.getData();
                videoPreview2.setVideoURI(selectedImageUri);
                videoPreview2.requestFocus();
                videoPreview2.start();
                videoPreview2.canPause();
                videoPreview2.stopPlayback();
                videoPreview2.resume();
                mediacontroller = new MediaController(this);
                mediacontroller.setMediaPlayer(videoPreview2);
                mediacontroller.setAnchorView(videoPreview2);
                videoPreview2.setMediaController(mediacontroller);
            }
        }
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
