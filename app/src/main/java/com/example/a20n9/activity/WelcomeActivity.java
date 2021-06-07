package com.example.a20n9.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.example.a20n9.R;
import com.example.a20n9.database.AppDataBase;
import com.example.a20n9.database.entity.BasicInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION = 1;
    private static boolean PERMISSION_ALL = true;

    private static int SPLASH_TIMEOUT = 100;
    AppDataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WelcomeActivity.this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    REQUEST_PERMISSION);
        }else{
            go();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            for (int grantResult : grantResults) {

                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    PERMISSION_ALL = false;
                }
            }

            if (PERMISSION_ALL) {
                go();
            } else {
                Toast.makeText(this, "需要同意所有權限才能使用此app", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void go() {
        if (db == null) {
            db = Room.databaseBuilder(this.getBaseContext(), AppDataBase.class, "db1").allowMainThreadQueries().build();
        }
        List<BasicInfo> basinInfoList = db.basicInfoDao().getAll();
        //設定預設值
        if (basinInfoList.size() == 0) {
            BasicInfo basicInfo = new BasicInfo();
            basicInfo.id = 1;
            basicInfo.nameLeft = "請點選修改名稱";
            basicInfo.nameRight = "請點選修改名稱";
            basicInfo.showDays = true;

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            basicInfo.firstDate = sdf.format(calendar.getTime());

            db.basicInfoDao().insert(basicInfo);

            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.blank_profile);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/20N9/";
            String fileLeftName = fileName + "left.jpg";
            String fileRightName = fileName + "right.jpg";

            String[] fs = {fileLeftName, fileRightName};
            for (String f : fs) {
                try {
                    File root = new File(f);
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File file = new File(f);
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();

                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}