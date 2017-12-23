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
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;


public class DecodeActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 1;
    private Uri fileUri;
    String tempat;
    DisplayMetrics dm;
    private Button btnBrowseVid, mbtnRetrieve;
    private VideoView videoPreview2;
    private MediaController mediacontroller;
    private EditText isiKey2;
    private TextView outputTexs, info;
    String srcPath = null;
    String convertedPath;
    AesAlgoritma Aess = new AesAlgoritma();
    String outputDecode;
    private static final String TAG = "EncodeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnBrowseVid = (Button) findViewById(R.id.btnBrowseVid);
        mbtnRetrieve = (Button) findViewById(R.id.btnRetrieve);
        outputTexs = (TextView) findViewById(R.id.outputTeks);
        isiKey2 = (EditText) findViewById(R.id.isiKey2);
        info = (TextView) findViewById(R.id.info);
        videoPreview2 = (VideoView) findViewById(R.id.videoPreview2);
        btnBrowseVid.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Takevideo();
                outputTexs.setText("");
                isiKey2.setText("");
            }
        });

        mbtnRetrieve.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                String strkey = getPesan(isiKey.getText().toString().trim());
//
//                if (strkey.isEmpty()) {
//                    Toast.makeText(getApplicationContext(),"Tidak ada Pesan yang bisa dipecahkan dengan key tersebut!",Toast.LENGTH_LONG).show();
//                }
                String strkey = isiKey2.getText().toString().trim();
                if (strkey.length()==8){
                    RetrieveTeks();

                } else if (convertedPath == null) {
                    AlertDialog Gagal = new AlertDialog.Builder(DecodeActivity.this).create();
                    Gagal.setTitle("Message Error");
                    Gagal.setMessage("Video Is Empty!");
                    Gagal.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface EncodeActivity, int which) {

                        }
                    });
                    Gagal.show();


                } else {
                    AlertDialog salah = new AlertDialog.Builder(DecodeActivity.this).create();
                    salah.setTitle("Error Message");
                    salah.setMessage("Key atau kunci Salah");

                    salah.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface EncodeActivity, int which) {

                        }
                    });
                    salah.show();

                }
            }
        });

    }


    public void Takevideo() {

        Intent i = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/*.mp4");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);

    }

    public void RetrieveTeks() {


        int i = 0;
        try {
            i = Steganography.retriveMessage(convertedPath);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (i == Steganography.SUDAH_ADA_DATA) {
            String IsiKey = isiKey2.getText().toString();
            if (IsiKey.isEmpty()){
                AlertDialog builder = new AlertDialog.Builder(DecodeActivity.this).create();
                builder.setTitle("Message error");
                builder.setMessage("Key kosong atau kurang dari 8");
                builder.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface DecodeActivity, int which) {
                    }
                });
                builder.show();

            } else if(IsiKey.length()==8 ) {
                AlertDialog builder = new AlertDialog.Builder(DecodeActivity.this).create();
                try {
                    //Untuk Melihat Size Pesan Asli /Plantext
                    String pesanasli = new String(Steganography.getMessage());
                    double bytes3 = pesanasli.length();
                    double kilobytes3= (bytes3/1024);
                    Log.d(TAG," OuptputBytes3 : " + bytes3);
                    Log.d(TAG," OuptputKiloBytes3 : " + kilobytes3);

                    //Untuk Melihat Size ChiperText
                    outputTexs.setText("Pesan Hasil Dekrip : " + Aess.decrypt(IsiKey, new String(Steganography.getMessage())));
                    double bytes2 = outputTexs.length();
                    double kilobytes2= (bytes2/1024);
                    Log.d(TAG," OuptputBytes2 : " + bytes2);
                    Log.d(TAG," OuptputKiloBytes2 : " + kilobytes2);
                    Log.d(TAG, "OutputDecode: " + outputTexs);
                    Log.d(TAG, "HasilEnkrip: " + new String(Steganography.getMessage()));

                    builder.setTitle("Message Berhasil");
                    builder.setMessage("Pesan Berhasil Di Retrieve");

                    builder.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface DecodeActivity, int which) {
                        }
                    });

                    builder.show();

                } catch (Exception e) {
                    Log.d(TAG, "RetrieveTeks: error");
                    e.printStackTrace();
                    builder.setTitle("Message GAGAL");
                    builder.setMessage("Pesan GAGAL Di Retrieve");
                    builder.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface DecodeActivity, int which) {
                        }
                    });
                    builder.show();

                }
            }


        } else {
            Toast.makeText(getApplicationContext(),
                    "Pesan tidak ditemukan!\",\n" + "\"Error!", Toast.LENGTH_SHORT)
                    .show();
            AlertDialog builder = new AlertDialog.Builder(DecodeActivity.this).create();
            builder.setTitle("Message Error ");
            builder.setMessage("Pesan Tidak di Temukan!");
            builder.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface DecodeActivity, int which) {
                    isiKey2.setText("");
                    outputTexs.setText("");

                }
            });
            builder.show();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {

                Uri uri = data.getData();
                convertedPath = getRealPathFromURI(uri);
                info.setText("Real Path: " + convertedPath + "\n");
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
//               int i = 0;
//                try {
//                    i = Steganography.retriveMessage(convertedPath);
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }if (i == Steganography.SUDAH_ADA_DATA) {
//                  outputTexs.setText("Pesan Asli : " + new String(Steganography.getMessage()) + "\n Setelah didecode :" +
//                                                        " ");
//                } else {
//                    Toast.makeText(getApplicationContext(),
//                            "Pesan tidak ditemukan!\",\n" + "\"Error!", Toast.LENGTH_SHORT)
//                            .show();
//                }
//            } else {
//                Toast.makeText(getApplicationContext(),
//                        "Video Tidak Dapat Di Buka\",\n" + "\"Error!", Toast.LENGTH_SHORT)
//                        .show();




            }

        }
    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Video.Media.DATA};

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
