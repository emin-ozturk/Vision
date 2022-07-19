package com.example.vision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ObjectDetectionImage extends AppCompatActivity {
    private ImageView imageView, imgSpeech;
    private Bitmap imageBitmap;
    private TextToSpeech mTTS;
    private ProgressBar pbarLoading;
    private String TAG = "Yolo";
    private String defaultLanguage;
    int screenWidth, screenHeight;
    private com.example.vision.Utils utils;
    ObjectDetection objectDetection;
    private boolean isObject;//Nesne olup olmadığını kontrol eder
    private boolean isLanguage;//Dil desteği olup olmadığını kontrol eder
    private boolean isStart;
    private String notMatch, saveImage, notLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.object_detection_image);
        OpenCVLoader.initDebug();

        imageView = findViewById(R.id.imageView);
        imgSpeech = findViewById(R.id.imgSpeech);
        pbarLoading = findViewById(R.id.pbarLoading);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //İlk açılışta gcihazdaki resimleri listeler
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);

        isObject = false;
        isLanguage = false;
        isStart = false;

        DataBase db = new DataBase(this);
        List<String> list = db.list();
        String[] itemBol = list.get(0).split(" - ");
        defaultLanguage = itemBol[1];

        utils = new com.example.vision.Utils();

        //Kullanılacak dili yükler
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
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
    }

    public void openGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                isStart = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Alınan görüntüyü ekranada gösterir
            imageView.setImageBitmap(imageBitmap);
            pbarLoading.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Seçilen görüntüdeki nesnelerin tespiti
                    detection(imageBitmap);
                }
            }, 1000);
        }
        if (!isStart && data == null) {
            onBackPressed();
        }
    }

    public void detection(Bitmap bitmap) {
        Mat imageMat = new Mat();
        Utils.bitmapToMat(bitmap, imageMat);
        objectDetection = new ObjectDetection(bitmap.getWidth(), bitmap.getHeight(), defaultLanguage);
        imageMat = objectDetection.detectionYolo(imageMat);

        if (objectDetection.getIsObject()) {
            Utils.matToBitmap(imageMat, bitmap);
            imageView.setImageBitmap(bitmap);
            if (isLanguage) {
                imgSpeech.setImageResource(R.drawable.ic_mic_on);
            }
            isObject = true;
        } else {
            Toast.makeText(getApplicationContext(), notMatch, Toast.LENGTH_SHORT).show();
            imgSpeech.setImageResource(R.drawable.ic_mic_off);
            isObject = false;
        }
        pbarLoading.setVisibility(View.INVISIBLE);
    }

    public void saveImage(View view) {
        File file = utils.getOutputMediaFile();
        if (file == null || imageBitmap == null) {
            return;
        }
        //Görüntüye rgb filtresi ekler
        imageBitmap = utils.rgbImage(imageBitmap);
        //Görüntünün boyutunu küçültme
        imageBitmap = utils.getResizedBitmap(imageBitmap, 4);

        utils.saveImage(imageBitmap, file);
        Toast.makeText(this, saveImage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();

//        if (isSearch) {
//            double imageWidth = imageBitmap.getWidth(); //Resim uzunluğu
//            double imageHeight = imageBitmap.getHeight(); //Resim yüksekliği
//            double width = imageView.getWidth(); //Ekran uzunluğu
//            double height = imageView.getHeight(); //Ekran yüksekliği
//
//            String text = "";
//            int[] ind = indices.toArray();
//            int i = 0;
//            while (i < ind.length) {
//                int idx = ind[i];
//
//                Rect box = boxesArray[idx];
//
//                int idGuy = clsIds.get(idx);
//
//                //Tıklama koordinatlarını resme uygun koordinatlara çevirme
//                double left = (imageWidth * x) / width;
//                double right = (imageWidth * x) / width;
//                double top = (imageHeight * y) / height;
//                double bottom = (imageHeight * y) / height;
//
//                if (left >= box.tl().x && right <= box.br().x && top >= box.tl().y && bottom <= box.br().y) {
//                    Log.d(TAG, cocoNames.get(idGuy));
//                    text = cocoNames.get(idGuy);
//                    speech(text);
//
//                    break;
//                }
//
//                i++;
//            }
//        }

        return super.onTouchEvent(event);
    }


    //Nesneleri seslendiren metod
    public void speech(View view) {
        if (isObject) {
            if (isLanguage) {
                utils.speech(objectDetection.getObjectName(), mTTS);
            } else {
                Toast.makeText(this, notLanguage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void backPage(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentMainPage = new Intent(this, MainPage.class);
        startActivity(intentMainPage);
    }
}
