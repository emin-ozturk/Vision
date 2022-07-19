package com.example.vision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class ObjectDetectionCamera extends AppCompatActivity {
    private Camera camera;
    private FrameLayout frameLayout;
    ShowCamera showCamera;
    ObjectDetection objectDetection;
    ImageView imgPhoto, imgCaptureImage, imgBack, imgSave, imgSpeech, imgCamera;
    private Mat imageMat;
    private Bitmap imageBitmap;
    private String defaultLanguage;
    private com.example.vision.Utils utils;
    private String TAG = "Yolo";
    private TextToSpeech mTTS;
    private boolean isLanguage;
    private ProgressBar pbarLoading;
    private String notMatch, saveImage, notLanguage;

    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i("OpenCV", "OpenCV loaded successfully");
                imageMat = new Mat();

                DataBase db = new DataBase(ObjectDetectionCamera.this);
                List<String> list = db.list();

                String[] itemBol = list.get(0).split(" - ");
                defaultLanguage = itemBol[1];
                utils = new com.example.vision.Utils();

                //Kullanılacak dili yükler
                mTTS = new TextToSpeech(ObjectDetectionCamera.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = mTTS.setLanguage(Locale.ENGLISH);
                            switch (defaultLanguage) {
                                case "English":
                                    result = mTTS.setLanguage(Locale.ENGLISH);
                                    isLanguage = true;
                                    notMatch = getString(R.string.matchEN);
                                    saveImage = getString(R.string.saveEN);
                                    notLanguage = getString(R.string.languageEN);
                                    break;
                                case "Français":
                                    result = mTTS.setLanguage(Locale.FRANCE);
                                    isLanguage = true;
                                    notMatch = getString(R.string.matchFR);
                                    saveImage = getString(R.string.saveFR);
                                    notLanguage = getString(R.string.languageFR);
                                    break;
                                case "Deutsch":
                                    result = mTTS.setLanguage(Locale.GERMAN);
                                    isLanguage = true;
                                    notMatch = getString(R.string.matchDE);
                                    saveImage = getString(R.string.saveDE);
                                    notLanguage = getString(R.string.languageDE);
                                    break;
                                case "Español":
                                    isLanguage = false;
                                    notMatch = getString(R.string.matchES);
                                    saveImage = getString(R.string.saveES);
                                    notLanguage = getString(R.string.languageES);
                                    break;
                                case "Türkçe":
                                    result = mTTS.setLanguage(Locale.getDefault());
                                    isLanguage = true;
                                    notMatch = getString(R.string.matchTR);
                                    saveImage = getString(R.string.saveTR);
                                    notLanguage = getString(R.string.languageTR);
                                    break;
                            }

                            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e(TAG, "Language not supported");
                            } else {
                                Log.d(TAG, "Language success");
                            }
                        } else {
                            Log.e(TAG, "Initialization failed");
                        }
                    }
                });
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.object_detection_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        frameLayout = findViewById(R.id.frameLayout);
        imgPhoto = findViewById(R.id.imgPhoto);
        imgCaptureImage = findViewById(R.id.imgCaptureImage);
        imgBack = findViewById(R.id.imgBack);
        imgSave = findViewById(R.id.imgSave);
        imgSpeech = findViewById(R.id.imgSpeech);
        imgCamera = findViewById(R.id.imgCamera);
        pbarLoading = findViewById(R.id.pbarLoading);

        camera = Camera.open();
        showCamera = new ShowCamera(ObjectDetectionCamera.this, camera);
        frameLayout.addView(showCamera);
    }

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File picture_file = utils.getOutputMediaFile();
            if (picture_file != null) {
                detection(data); //Nesne tanımlama
            }
        }
    };


    private void detection(final byte[] data) {
        pbarLoading.setVisibility(View.VISIBLE);
        imgPhoto.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.INVISIBLE);
        imgCaptureImage.setVisibility(View.INVISIBLE);

        //Byte-mat çevirme
        imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        imgPhoto.setImageBitmap(imageBitmap);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imgSave.setVisibility(View.VISIBLE);
                imgSpeech.setVisibility(View.VISIBLE);
                imgCamera.setVisibility(View.VISIBLE);

                Utils.bitmapToMat(imageBitmap, imageMat);

                objectDetection = new ObjectDetection(imageBitmap.getWidth(), imageBitmap.getHeight(), defaultLanguage);

                //Mat-bitmap çevirme
                Mat yoloImage = objectDetection.detectionYolo(imageMat);
                Utils.matToBitmap(yoloImage, imageBitmap);

                imgPhoto.setImageBitmap(imageBitmap);
                imgSave.setVisibility(View.VISIBLE);
                if (objectDetection.getIsObject()) {
                    if (isLanguage) {
                        imgSpeech.setImageResource(R.drawable.ic_mic_on);
                    } else {
                        imgSpeech.setImageResource(R.drawable.ic_mic_off);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), notMatch, Toast.LENGTH_SHORT).show();
                }
                pbarLoading.setVisibility(View.INVISIBLE);
            }
        }, 500);
    }

    public void captureImage(View view) {
        if (camera != null) {
            final Animation animatable = AnimationUtils.loadAnimation(this, R.anim.bounce);
            imgCaptureImage.startAnimation(animatable);
            camera.takePicture(null, null, pictureCallback);
        }
    }

    public void backPage(View view) {
        onBackPressed();
    }

    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, loaderCallback);
        } else {
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void saveImage(View view) {
        final Animation animatable = AnimationUtils.loadAnimation(this, R.anim.bounce);
        imgSave.startAnimation(animatable);

        File picture_file = utils.getOutputMediaFile();
        if (picture_file == null) {
            return;
        }
        //Görüntüye rgb filtresi ekler
        imageBitmap = utils.rgbImage(imageBitmap);
        //Görüntünün boyutunu azaltır
        imageBitmap = utils.getResizedBitmap(imageBitmap, 4);

        utils.saveImage(imageBitmap, picture_file);
        Toast.makeText(this, saveImage, Toast.LENGTH_SHORT).show();
    }

    public void speech(View view) {
        if (objectDetection.getIsObject()) {
            if (isLanguage) {
                utils.speech(objectDetection.getObjectName(), mTTS);
            } else {
                Toast.makeText(this, notLanguage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentMainPage = new Intent(this, MainPage.class);
        startActivity(intentMainPage);
    }

    public void openCamera(View view) {
        Intent intentRefresh = new Intent(this, ObjectDetectionCamera.class);
        startActivity(intentRefresh);
    }
}
