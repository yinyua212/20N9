package com.example.a20n9.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a20n9.R;
import com.example.a20n9.database.AppDataBase;
import com.example.a20n9.database.entity.CalendarNote;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;

public class CalendarAddActivity extends AppCompatActivity {

    TextView tvDate;
    EditText etContent;
//    ImageView imgMap;
    ImageView imgCamera;
    Button btnOK;
    Button btnCancel;

    AppDataBase db;

    static int PICK_FROM_GALLERY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_add);

        initial();

        Intent intent = getIntent();
        String dateStr = intent.getStringExtra("date");

        if (db == null) {
            db = Room.databaseBuilder(this, AppDataBase.class, "db1").allowMainThreadQueries().build();
        }

        tvDate.setText(dateStr);

        CalendarNote calendarNote = db.calendarDao().getCalendarNoteByDate(dateStr);
        if (calendarNote != null) {
            etContent.setText(calendarNote.content);
            showPic();
        }
    }

    private void initial() {

        tvDate = findViewById(R.id.tvDate);
        etContent = findViewById(R.id.etContent);
        imgCamera = findViewById(R.id.imgCamera);
//        imgMap = findViewById(R.id.imgMap);
        btnOK = findViewById(R.id.btnOK);
        btnCancel = findViewById(R.id.btnCancel);

        //OK按鈕
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CalendarNote calendarNote = db.calendarDao().getCalendarNoteByDate(tvDate.getText().toString());
                //insert
                if (calendarNote == null) {
                    CalendarNote newCalendarNote = new CalendarNote();
                    newCalendarNote.date = tvDate.getText().toString();
                    newCalendarNote.content = etContent.getText().toString();
                    db.calendarDao().insert(newCalendarNote);
                }
                //update
                else {
                    String dateStr = tvDate.getText().toString();
                    String contentStr = etContent.getText().toString();
                    String addressStr = "";

                    db.calendarDao().update(dateStr, contentStr, addressStr);
                }
                finish();
            }
        });

        //取消按鈕
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //上傳圖片
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePic();
            }
        });

//        //google 定位
//        imgMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateMap();
//            }
//        });

        //編輯:如果是預設自動清除文字
        etContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(hasWindowFocus()){
                    if ("點擊開始編輯".equals(etContent.getText().toString())) {
                        etContent.setText("");
                    }
                }
            }
        });
    }


    //上傳照片
    private void updatePic() {
        Intent i = new Intent(Intent.ACTION_PICK, null);
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(i, PICK_FROM_GALLERY);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                CropImage.activity(selectedImage).setAspectRatio(120, 60).start(this);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/20N9/";
                fileName += tvDate.getText().toString().replace("/", "_") + ".jpg";

                try {
                    File root = new File(fileName);
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File file = new File(fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();

                    FileOutputStream out = new FileOutputStream(file);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                    //顯示照片
                    imgCamera.setImageBitmap(BitmapFactory.decodeFile(fileName));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void showPic() {
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/20N9/";
        fileName += tvDate.getText().toString().replace("/", "_") + ".jpg";
        File file = new File(fileName);

        if (file.exists()) {
            imgCamera.setImageBitmap(BitmapFactory.decodeFile(fileName));
        }
    }

    private void updateMap(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent,1);
    }
}