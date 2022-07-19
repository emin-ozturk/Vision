package com.example.vision;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.speech.tts.TextToSpeech;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

class Utils {
    //Görüntüyü boyutlandırma
    Bitmap getResizedBitmap(Bitmap image, int rate) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1 && width > 1000 && height > 1000) {
            width = width / rate;
            height = (int) (width / bitmapRatio);
        } else if (bitmapRatio < 1 && height > 1000 && width > 1000){
            height = height / rate;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    //Kayıt edilcek görüntünün konumu döndürür
    File getOutputMediaFile() {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        } else {
            File folder_gui = new File(Environment.getExternalStorageDirectory() + File.separator + "Vision/Vision Images");
            if (!folder_gui.exists()) {
                folder_gui.mkdirs();
            }
            return new File(folder_gui, UUID.randomUUID().toString() + ".jpg");
        }
    }

    //Görüntüye filtre uygular
    Bitmap rgbImage(Bitmap bitmap) {
        Mat Rgba = new Mat();
        Mat grayMat = new Mat();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        org.opencv.android.Utils.bitmapToMat(bitmap, Rgba);
        Imgproc.cvtColor(Rgba, grayMat, Imgproc.COLOR_RGB2RGBA);
        org.opencv.android.Utils.matToBitmap(grayMat, grayBitmap);

        return bitmap;
    }

    //Görüntü katdetme
    void saveImage(Bitmap bitmap, File file) {
        try {
            //Bitmap-byte çevirme
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes); //Resmi kaydetme
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Nesneleri seslendiren metod
    void speech(String text, TextToSpeech mTTS) {
        mTTS.setPitch(1);
        mTTS.setSpeechRate(1);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
