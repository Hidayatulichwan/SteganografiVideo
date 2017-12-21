package com.example.ichwan.steganigrafivideo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;


public class EncodeActivity extends AppCompatActivity {


    // Activity request codes
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured videos
    private static final String Video_DIRECTORY_NAME = "Aplikasi video";
    private Uri fileUri;
    private static Uri fileUriOut; // file url to store image/video
    private VideoView videoPreview;
    private Button btnRecordVideo, btnClear, btnSimpan;
    private EditText editTextToClear, isiPesan, isiKey;
    private MediaController mediacontroller;
    String OutputString;
    Timer timer = new Timer();
    DisplayMetrics dm;
    AesAlgoritma aess=new AesAlgoritma();
    private static final String TAG = "EncodeActivity";
    String outputPesan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);
        timer = new Timer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        videoPreview = (VideoView) findViewById(R.id.videoPreview);
        btnRecordVideo = (Button) findViewById(R.id.btnvideo);
        btnClear = (Button) findViewById(R.id.btnBatal);
        btnSimpan = (Button) findViewById(R.id.btnSimpan);
        isiPesan = (EditText) findViewById(R.id.Tekspesan);
        isiKey = (EditText) findViewById(R.id.isiKey);

        if (isiKey.getText().toString().length() == 0) {
            isiKey.setError("Key isi dengan 8 karakter!");
        }

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strkey = isiKey.getText().toString().trim();
                if (strkey.length() == 8) {
                    // isiPesan.setText(chiperTeksDes);


                    sisipkanPesan();



                } else if (fileUri == null) {
                    AlertDialog Gagal = new AlertDialog.Builder(EncodeActivity.this).create();
                    Gagal.setTitle("Message Error");
                    Gagal.setMessage("Video Is Empty!");
                    Gagal.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface EncodeActivity, int which) {

                        }
                    });
                    Gagal.show();


                } else {
                    AlertDialog salah = new AlertDialog.Builder(EncodeActivity.this).create();
                    salah.setTitle("Error Message");
                    salah.setMessage("Key atau kunci Harus 8 karakter");

                    salah.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface EncodeActivity, int which) {

                        }
                    });
                    salah.show();

                }
            }

        });


        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isiPesan.setText("");
                isiKey.setText("");
//
//                File videoFile = new File(fileUri.getPath());
//                videoFile.delete();
//                cleanupVideo();

                //  imgPreview.setImageBitmap(null);
            }
        });
          /*
         * Record video button click event
		 */
        btnRecordVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // record video
                recordVideo();
            }
        });
        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }


    }

    private void cleanupVideo() {
        if (videoPreview.getVisibility() == View.VISIBLE) {
            videoPreview.stopPlayback();
            videoPreview.clearAnimation();
            videoPreview.suspend(); // clears media player
            videoPreview.setVideoURI(null);
            //x mVideoViewButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /*

   * Here we store the file url as it will be null after returning from camera
   * app
   */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                Toast.makeText(this, fileUri.getPath(), Toast.LENGTH_LONG).show();
                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /*
             * Previewing recorded video
             */
    private void previewVideo() {
        dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        int tinggi = dm.heightPixels;
        int lebar = dm.widthPixels;
        try {

            // Boolean canPauseVideo = videoPreview.canPause();
            videoPreview.setVisibility(View.VISIBLE);
            videoPreview.setVideoPath(fileUri.getPath());
            // start playing
            videoPreview.setMinimumHeight(tinggi);
            videoPreview.setMinimumWidth(lebar);
            mediacontroller = new MediaController(this);
            mediacontroller.setMediaPlayer(videoPreview);
            mediacontroller.setAnchorView(videoPreview);
            videoPreview.setMediaController(mediacontroller);
            videoPreview.start();
            videoPreview.canPause();
            videoPreview.requestFocus();
            videoPreview.stopPlayback();
            videoPreview.resume();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ------------ Helper Methods ----------------------
     */


    /*
     * Creating file uri to store image/video
	 */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
    * returning  video
    */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                Video_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
//                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create"
//                        + IMAGE_DIRECTORY_NAME + "directory");
                return null;

//                log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
//                        + IMAGE_DIRECTORY_NAME + " directory");
//                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
            fileUriOut = Uri.fromFile(new File(mediaStorageDir + File.separator + "VID_" + timeStamp + "_ENCODE.mp4"));
        } else {
            return null;
        }
        return mediaFile;


    }

    private void sisipkanPesan() {
        String Pesan = isiPesan.getText().toString();
        String IsiKey = isiKey.getText().toString();

        try {
         outputPesan = aess.encrypt(IsiKey,Pesan);
            Log.d(TAG, "OutputPesan: "+outputPesan);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int i = Steganography.embbeded(fileUri.getPath(), fileUriOut.getPath(), outputPesan);




            Log.d(TAG, "Pesan: "+Pesan);
            Log.d(TAG, "Key: "+IsiKey);
            if (i == Steganography.BERHASIL) {
                if (outputPesan.trim().equals("")) {
                    Toast.makeText(this, "Masukan Pesan! ", Toast.LENGTH_SHORT).show();


                    return;

//                }else if(fileUri == null){
//
//                    Toast.makeText(this, "Rekam Video! ", Toast.LENGTH_SHORT).show();
//                    return;
                } else {
                    AlertDialog builder = new AlertDialog.Builder(EncodeActivity.this).create();
                    builder.setTitle("Message Success");
                    builder.setMessage("Pesan Berhasil Disimpan");
                    builder.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface EncodeActivity, int which) {
                            isiPesan.setText("");
                            isiKey.setText("");
                            File videoFile = new File(fileUri.getPath());
                            videoFile.delete();

                        }
                    });
                    builder.show();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertDialog Gagal = new AlertDialog.Builder(EncodeActivity.this).create();
            Gagal.setTitle("Message Error");
            Gagal.setMessage("Sudah Ada Data");
            Gagal.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface EncodeActivity, int which) {

                }
            });
            Gagal.show();

        }
//        try {
//            int i = Steganography.embbeded(File.ge().trim(),  isiPesan.getText());
//            if(i == Steganography.BERHASIL){
//                Toast.makeText(getApplicationContext(), "Berhasil Menyimpan Data",
//                        Toast.LENGTH_SHORT).show();
//
//                return;
//            }else{
//                Toast.makeText(getApplicationContext(), "Gagal Menyimpan Data",
//                        Toast.LENGTH_SHORT).show();
//                return ;
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }


//    public void DeleteVideo(){
//        File videoFile = new File(fileUri.getPath());
//        videoFile.delete();
//
//    }


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

