package com.example.a20n9.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a20n9.R;
import com.example.a20n9.database.AppDataBase;
import com.example.a20n9.database.entity.BasicInfo;
import com.example.a20n9.database.entity.MemoryDay;
import com.example.a20n9.utility.AlertReceiver;
import com.google.firebase.storage.FirebaseStorage;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment {

    ViewGroup viewHome;

    LayoutInflater inflater;
    ViewGroup container;
    AppDataBase db;
    TextView nameLeft;
    TextView nameRight;
    TextView togetherDays;
    TextView firstDate;
    ImageView imgLeft;
    ImageView imgRight;

    FirebaseStorage storage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewHome == null) {
            viewHome = (ViewGroup) inflater.inflate(R.layout.home_view, container, false);
            this.inflater = inflater;
            this.container = container;

            nameLeft = viewHome.findViewById(R.id.nameLeft);
            nameRight = viewHome.findViewById(R.id.nameRight);
            togetherDays = viewHome.findViewById(R.id.togetherDays);
            firstDate = viewHome.findViewById(R.id.firstDate);
            imgLeft = viewHome.findViewById(R.id.imgLeft);
            imgRight = viewHome.findViewById(R.id.imgRight);

            storage = FirebaseStorage.getInstance();

            if (db == null) {
                db = Room.databaseBuilder(viewHome.getContext(), AppDataBase.class, "db1").allowMainThreadQueries().build();
            }
            List<BasicInfo> basinInfoList = db.basicInfoDao().getAll();

            // 載入資料
            loadBasicInfo(basinInfoList);


            // set onClick event
            nameLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenuPersonal(view);
                }
            });

            nameRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenuPersonal(view);
                }
            });

            imgLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenuPersonal(view);
                }
            });

            imgRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenuPersonal(view);
                }
            });

            togetherDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenuDays(view);
                }
            });

            firstDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenuDays(view);
                }
            });
        }
        return viewHome;
    }

    //更新 名稱、大頭貼
    private void showPopupMenuPersonal(View v) {
        final String[] menu = {"修改大頭貼", "修改名稱"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(this.getContext());
        dialog.setItems(menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {

                    //修改大頭貼
                    case 0:
                        if (v.getId() == R.id.nameLeft || v.getId() == R.id.imgLeft) {
                            updateProfilePic("left");
                        } else if (v.getId() == R.id.nameRight || v.getId() == R.id.imgRight) {
                            updateProfilePic("right");
                        }
                        break;
                    //修改名稱
                    case 1:
                        if (v.getId() == R.id.nameLeft || v.getId() == R.id.imgLeft) {
                            updateName("left");
                        } else if (v.getId() == R.id.nameRight || v.getId() == R.id.imgRight) {
                            updateName("right");
                        }

                        break;

//                    //修改合照
//                    case 2:
//                        break;
                }
            }
        });

        dialog.show();
    }

    // 更新 紀念日
    private void showPopupMenuDays(View v) {
        String[] menu = {"修改紀念日", "修改紀念日顯示方式"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(this.getContext());
        dialog.setItems(menu, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    //修改紀念日
                    case 0:
                        BasicInfo basicInfo = db.basicInfoDao().getAll().get(0);
                        String[] array = basicInfo.firstDate.split("/");
                        int year = Integer.parseInt(array[0]);
                        int month = Integer.parseInt(array[1]) - 1;
                        int day = Integer.parseInt(array[2]);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                                String firstDate_str = y + "/" + (m + 1) + "/" + d;
                                db.basicInfoDao().updateFirstDate(firstDate_str);
                                loadBasicInfo(db.basicInfoDao().getAll());
                                List<MemoryDay> memoryDays = db.memoryDayDao().getAll();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                Date firstDate = null;
                                try {
                                    firstDate = sdf.parse(firstDate_str);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

//                                //更新數字紀念日
//                                for (MemoryDay md : memoryDays) {
//                                    Calendar c = Calendar.getInstance();
//                                    c.setTime(firstDate);
//                                    c.add(Calendar.DATE, md.days - 1);
//                                    db.memoryDayDao().update(sdf.format(c.getTime()), md.days);
//                                }

                                //更新周年
                                db.memoryDayDao().deleteAnniversary(true);
                                for (int i = 1; i <= 100; i++) {
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(firstDate);
                                    c.add(Calendar.YEAR, i);
                                    MemoryDay anniversary = new MemoryDay();
                                    anniversary.date = sdf.format(c.getTime());
                                    anniversary.mark = i + "周年";
                                    anniversary.anniversary = true;
                                    anniversary.days = (int) ((c.getTimeInMillis() - firstDate.getTime()) / ((1000 * 60 * 60 * 24)));
                                    db.memoryDayDao().insert(anniversary);
                                }

                            }
                        }, year, month, day);
                        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());

                        datePickerDialog.show();

                        break;
                    //修改紀念日顯示方式
                    case 1:
                        List<BasicInfo> list = db.basicInfoDao().getAll();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                        try {
                            Date firstDate = sdf.parse(list.get(0).firstDate);
                            Calendar firstDateCal = Calendar.getInstance();
                            firstDateCal.setTime(firstDate);
                            Calendar todayCal = Calendar.getInstance();
//

                            if (list.get(0).showDays.booleanValue()) {
                                StringBuilder result = new StringBuilder("");

                                int diffY = 0;
                                int diffM = 0;
                                int diffD = 0;

                                //年
                                diffY = todayCal.get(Calendar.YEAR) - firstDateCal.get(Calendar.YEAR);

                                //月
                                if (todayCal.get(Calendar.MONTH) <= firstDateCal.get(Calendar.MONTH)) {
                                    diffM = todayCal.get(Calendar.MONTH) + 12 - firstDateCal.get(Calendar.MONTH);
                                    diffY -= 1;
                                } else {
                                    diffM = todayCal.get(Calendar.MONTH) - firstDateCal.get(Calendar.MONTH);
                                }


                                if (todayCal.get(Calendar.DAY_OF_MONTH) < firstDateCal.get(Calendar.DAY_OF_MONTH)) {
                                    //取得當月月份天數 - firsttDate + today.Day
                                    YearMonth yearMonth = YearMonth.of(firstDateCal.get(Calendar.YEAR), firstDateCal.get(Calendar.MONTH));
                                    diffD = (yearMonth.lengthOfMonth() - firstDateCal.get(Calendar.DAY_OF_MONTH) + 1) + todayCal.get(Calendar.DAY_OF_MONTH);
                                    diffM -= 1;
                                } else {
//                                    if(todayCal.get(Calendar.YEAR) == firstDateCal.get((Calendar.YEAR)) &&
//                                            todayCal.get(Calendar.MONTH) == firstDateCal.get((Calendar.MONTH)))
//                                    {
                                    diffD = todayCal.get(Calendar.DAY_OF_MONTH) - firstDateCal.get(Calendar.DAY_OF_MONTH) + 1;

                                }

                                if (diffY > 0)
                                    result.append(diffY + " 年 ");
                                if (diffM > 0)
                                    result.append(diffM + " 個月 ");
                                if(diffD > 0)
                                    result.append(diffD + " 天 ");

                                togetherDays.setText(result);
                                db.basicInfoDao().updateShowDays(false);
                            } else {
                                Date today = todayCal.getTime();
                                long d = (today.getTime() - firstDate.getTime()) / (24 * 60 * 60 * 1000) + 1;
                                String result = d + " 天 ";
                                togetherDays.setText(result);
                                db.basicInfoDao().updateShowDays(true);
                            }
                        } catch (ParseException e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }


                        break;
//                    //修改合照
//                    case 2:
//                        break;
                }
            }
        });

        dialog.show();
    }

    // 更新名稱
    public void updateName(String leftOrRight) {
        View v = inflater.inflate(R.layout.alertdialog_edit_name, container, false);
        new AlertDialog.Builder(this.getContext())
                .setTitle("請輸入新名稱")
                .setView(v)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etNewName = (EditText) v.findViewById(R.id.editText1);
                        String newName = etNewName.getText().toString();
                        if (newName.length() > 0) {
                            switch (leftOrRight) {
                                case "left":
                                    db.basicInfoDao().updateNameLeft(newName);
                                    break;
                                case "right":
                                    db.basicInfoDao().updateNameRight(newName);
                                    break;
                            }
                            loadBasicInfo(db.basicInfoDao().getAll());
                        } else {
                            Toast.makeText(container.getContext(), "請輸入新名稱", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    // 更新大頭貼
    static int PICK_FROM_GALLERY = 1;
    static String leftOrRight = "";

    private void updateProfilePic(String leftOrRight) {
        Intent i = new Intent(Intent.ACTION_PICK, null);
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        this.leftOrRight = leftOrRight;
        startActivityForResult(i, PICK_FROM_GALLERY);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                CropImage.activity(selectedImage).setAspectRatio(150, 150).start(getContext(), this);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/20N9/";

                if (leftOrRight.equals("left")) {
                    imgLeft.setImageURI(resultUri);
                    fileName += "left.jpg";


                } else if (leftOrRight.equals("right")) {
                    imgRight.setImageURI(resultUri);
                    fileName += "right.jpg";
                }


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
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), result.getUri());
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void loadBasicInfo(List<BasicInfo> list) {
        BasicInfo basicInfo = list.get(0);
        nameLeft.setText(basicInfo.nameLeft);
        nameRight.setText(basicInfo.nameRight);
        firstDate.setText(basicInfo.firstDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date firstDate = sdf.parse(basicInfo.firstDate);
            Date today = Calendar.getInstance().getTime();
            long d = (today.getTime() - firstDate.getTime()) / (24 * 60 * 60 * 1000) + 1;

            if (basicInfo.showDays.booleanValue()) {
                StringBuilder result = new StringBuilder("");
                if (d >= 365) {
                    result.append((d / 365) + " 年 ");
                    d = d % 365;
                }
                if (d >= 30) {
                    result.append((d / 30) + " 個月 ");
                    if (d % 30 > 0) {
                        result.append((d % 30) + " 天 ");
                    }
                }
                togetherDays.setText(result);
            } else {
                togetherDays.setText(d + " 天 ");
            }
            togetherDays.setText(d + " 天 ");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/20N9/";
        String leftFileName = fileName + "left.jpg";
        File leftFile = new File(leftFileName);

        if (leftFile.exists()) {
            imgLeft.setImageBitmap(BitmapFactory.decodeFile(leftFileName));
        }

        String rightFileName = fileName + "right.jpg";
        File rightFile = new File(rightFileName);

        if (rightFile.exists()) {
            imgRight.setImageBitmap(BitmapFactory.decodeFile(rightFileName));
        }
    }


}