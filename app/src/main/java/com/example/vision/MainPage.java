package com.example.vision;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class MainPage extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 20;
    private TextView txtHeader, txtGallery, txtCamera, txtLiveView, txtshortLanguage;
    private static final int CAMERA_PERMISSION_CODE = 102;
    private static final int STORAGE_PERMISSION_CODE = 103;
    private RadioButton radioTR, radioEN, radioFR, radioDE, radioES;
    private String defaultLanguage;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_page);

        txtHeader = findViewById(R.id.txtHeader);
        txtGallery = findViewById(R.id.txtGallery);
        txtCamera = findViewById(R.id.txtCamera);
        txtLiveView = findViewById(R.id.txtVideo);
        txtshortLanguage = findViewById(R.id.txtLanguage);

        DataBase db = new DataBase(this);
        db.insert("Türkçe");
        List<String> list = db.list();

        try {
            String[] itemBol = list.get(0).split(" - ");
            defaultLanguage = itemBol[1];
        } catch (Exception e) {
            defaultLanguage = "Türkçe";
        }
        selectLanguage(defaultLanguage);
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
    }

    private void selectLanguage(String defaultLanguage) {
        switch (defaultLanguage) {
            case "Türkçe":
                txtHeader.setText(R.string.objectDetectionTR);
                txtGallery.setText(R.string.galleryTR);
                txtCamera.setText(R.string.cameraTR);
                txtLiveView.setText(R.string.liveViewTR);
                txtshortLanguage.setText(R.string.shortLanguageTR);
                break;
            case "English":
                txtHeader.setText(R.string.objectDetectionEN);
                txtGallery.setText(R.string.galleryEN);
                txtCamera.setText(R.string.cameraEN);
                txtLiveView.setText(R.string.liveViewEN);
                txtshortLanguage.setText(R.string.shortLanguageEN);
                break;
            case "Français":
                txtHeader.setText(R.string.objectDetectionFR);
                txtGallery.setText(R.string.galleryFR);
                txtCamera.setText(R.string.cameraFR);
                txtLiveView.setText(R.string.liveViewFR);
                txtshortLanguage.setText(R.string.shortLanguageFR);
                break;
            case "Deutsch":
                txtHeader.setText(R.string.objectDetectionDE);
                txtGallery.setText(R.string.galleryDE);
                txtCamera.setText(R.string.cameraDE);
                txtLiveView.setText(R.string.liveViewDE);
                txtshortLanguage.setText(R.string.shortLanguageDE);
                break;
            case "Español":
                txtHeader.setText(R.string.objectDetectionES);
                txtGallery.setText(R.string.galleryES);
                txtCamera.setText(R.string.cameraES);
                txtLiveView.setText(R.string.liveViewES);
                txtshortLanguage.setText(R.string.shortLanguageES);
                break;
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            if (requestCode == CAMERA_PERMISSION_CODE) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Yolo", "Kamera izni var");
                } else {
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                }
            }
            if (requestCode == STORAGE_PERMISSION_CODE) {
                Log.d("Yolo", "Dosya izni var");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Yolo", "Kamera ve dosya izni verildi");
                }
            }
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        }
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Yolo", "Dosya izni verildi");
                loadYoloFiles();
            }
        }
    }

    public void gallery(View view) {
        LinearLayout linearGallery = findViewById(R.id.linearGallery);
        final Animation animatable = AnimationUtils.loadAnimation(this, R.anim.bounce);
        linearGallery.startAnimation(animatable);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intentImage = new Intent(MainPage.this, ObjectDetectionImage.class);
                    startActivity(intentImage);
                }
            }, SPLASH_TIME_OUT);
        }
    }

    public void camera(View view) {
        LinearLayout linearCamera = findViewById(R.id.linearCamera);
        final Animation animatable = AnimationUtils.loadAnimation(this, R.anim.bounce);
        linearCamera.startAnimation(animatable);

        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intentCamera = new Intent(MainPage.this, ObjectDetectionCamera.class);
                    startActivity(intentCamera);
                }
            }, SPLASH_TIME_OUT);
        }
    }

    public void video(View view) {
        LinearLayout linearVideo = findViewById(R.id.linearVideo);
        final Animation animatable = AnimationUtils.loadAnimation(this, R.anim.bounce);
        linearVideo.startAnimation(animatable);

        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intentVideo = new Intent(MainPage.this, ObjectDetectionLiveView.class);
                    startActivity(intentVideo);
                }
            }, SPLASH_TIME_OUT);
        }

    }
    private void loadYoloFiles() {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return;
        } else {
            File folder_gui = new File(Environment.getExternalStorageDirectory() + "/Vision/dnns");
            if (!folder_gui.exists()) {
                folder_gui.mkdirs();
            }
        }

        File cfg = new File(Environment.getExternalStorageDirectory() + "/Vision/dnns/yolov3-tiny.cfg");
        if (!cfg.exists()) {
            try {
                InputStream is = getAssets().open("dnns/yolov3-tiny.cfg");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                FileOutputStream fos = new FileOutputStream(cfg);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        File weights = new File(Environment.getExternalStorageDirectory() + "/Vision/dnns/yolov3-tiny.weights");
        if (!weights.exists()) {
            try {
                InputStream is = getAssets().open("dnns/yolov3-tiny.weights");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                FileOutputStream fos = new FileOutputStream(weights);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void openSettings(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
        View viewLanguage = getLayoutInflater().inflate(R.layout.languages, null);

        radioTR = viewLanguage.findViewById(R.id.radioTR);
        radioEN = viewLanguage.findViewById(R.id.radioEN);
        radioFR = viewLanguage.findViewById(R.id.radioFR);
        radioDE = viewLanguage.findViewById(R.id.radioDE);
        radioES = viewLanguage.findViewById(R.id.radioES);

        LinearLayout layoutTR = viewLanguage.findViewById(R.id.layoutTR);
        LinearLayout layoutEN = viewLanguage.findViewById(R.id.layoutEN);
        LinearLayout layoutFR = viewLanguage.findViewById(R.id.layoutFR);
        LinearLayout layoutDE = viewLanguage.findViewById(R.id.layoutDE);
        LinearLayout layoutES = viewLanguage.findViewById(R.id.layoutES);

        final TextView txtTR = viewLanguage.findViewById(R.id.txtTR);
        final TextView txtEN = viewLanguage.findViewById(R.id.txtEN);
        final TextView txtFR = viewLanguage.findViewById(R.id.txtFR);
        final TextView txtDE = viewLanguage.findViewById(R.id.txtDE);
        final TextView txtES = viewLanguage.findViewById(R.id.txtES);

        TextView txtCancel = viewLanguage.findViewById(R.id.txtCancel);
        TextView txtOk = viewLanguage.findViewById(R.id.txtOk);
        TextView txtHeader = viewLanguage.findViewById(R.id.txtHeader);

        builder.setView(viewLanguage);
        final AlertDialog dialog = builder.create();
        dialog.show();

        switch (defaultLanguage) {
            case "Türkçe":
                txtHeader.setText(R.string.selectLanguageTR);
                txtCancel.setText(R.string.cancelTR);
                txtOk.setText(R.string.okTR);
                radioTR.setChecked(true);
                break;
            case "English":
                txtHeader.setText(R.string.selectLanguageEN);
                txtCancel.setText(R.string.cancelEN);
                txtOk.setText(R.string.okEN);
                radioEN.setChecked(true);
                break;
            case "Français":
                txtHeader.setText(R.string.selectLanguageFR);
                txtCancel.setText(R.string.cancelFR);
                txtOk.setText(R.string.okFR);
                radioFR.setChecked(true);
                break;
            case "Deutsch":
                txtHeader.setText(R.string.selectLanguageDE);
                txtCancel.setText(R.string.cancelDE);
                txtOk.setText(R.string.okDE);
                radioDE.setChecked(true);
                break;
            case "Español":
                txtHeader.setText(R.string.selectLanguageES);
                txtCancel.setText(R.string.cancelES);
                txtOk.setText(R.string.okES);
                radioES.setChecked(true);
                break;
        }

        layoutTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioTR.setChecked(true);
                radioEN.setChecked(false);
                radioFR.setChecked(false);
                radioDE.setChecked(false);
                radioES.setChecked(false);
            }
        });
        layoutEN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioTR.setChecked(false);
                radioEN.setChecked(true);
                radioFR.setChecked(false);
                radioDE.setChecked(false);
                radioES.setChecked(false);
            }
        });
        layoutFR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioTR.setChecked(false);
                radioEN.setChecked(false);
                radioFR.setChecked(true);
                radioDE.setChecked(false);
                radioES.setChecked(false);
            }
        });
        layoutDE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioTR.setChecked(false);
                radioEN.setChecked(false);
                radioFR.setChecked(false);
                radioDE.setChecked(true);
                radioES.setChecked(false);
            }
        });
        layoutES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioTR.setChecked(false);
                radioEN.setChecked(false);
                radioFR.setChecked(false);
                radioDE.setChecked(false);
                radioES.setChecked(true);
            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String language = "";
                if (radioTR.isChecked()) {
                    language = txtTR.getText().toString();
                } else if (radioEN.isChecked()) {
                    language = txtEN.getText().toString();
                } else if (radioFR.isChecked()) {
                    language = txtFR.getText().toString();
                } else if (radioDE.isChecked()) {
                    language = txtDE.getText().toString();
                } else if (radioES.isChecked()) {
                    language = txtES.getText().toString();
                }

                dialog.cancel();

                DataBase db = new DataBase(getApplicationContext());
                db.update(1, language);
                selectLanguage(language);
                defaultLanguage = language;
            }
        });
    }
}
