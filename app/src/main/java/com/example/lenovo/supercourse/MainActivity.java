package com.example.lenovo.supercourse;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button searchbt;
    private ImageButton findJSbt;
    private ListView SelectLV;
    private EditText JSet;
    private List<JS> allJS=new ArrayList<JS>();
    private List<Course> allCourse=new ArrayList<Course>();
    private List<String> selectJS=new ArrayList<String>();
    private JSAdapter JSadapter;
    private Courseadapter courseadapter;
    private Handler handler;
    private String inputText;
    private ImageView img;
    private ListView courselv;
    final String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/supercourse/";
    private String YZM;
    private String selectvalue;
    private Button yzmb;
    private EditText yzmet;
    private String[] course=null;
    private Boolean yzm=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        }

        searchbt=findViewById(R.id.searchbt);
        findJSbt=findViewById(R.id.findJSbt);
        SelectLV=findViewById(R.id.SeleteLV);
        courselv=findViewById(R.id.courselv);
        JSet=findViewById(R.id.JSet);
        img=findViewById(R.id.img);
        yzmb=findViewById(R.id.yzmb);
        yzmet=findViewById(R.id.yzmet);
        SelectLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectvalue=allJS.get(position).getValue();
                    JSet.setText(allJS.get(position).getJSname());
                    selectJS.add(allJS.get(position).getJSname());
                    allJS.clear();
                    allCourse.clear();
                    JSadapter.notifyDataSetChanged();
                    courseadapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        JSadapter=new JSAdapter(this,R.layout.js_layout,allJS);
        courseadapter=new Courseadapter(this,R.layout.course_layout,allCourse);
        courselv.setAdapter(courseadapter);
        SelectLV.setAdapter(JSadapter);



        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){
                    Log.i("zm3",allJS.size()+"");
                    JSadapter.notifyDataSetChanged();
                }
                else if(msg.what==2){
                    Bitmap bmp= BitmapFactory.decodeFile(path+"my.png");
                    img.setImageBitmap(bmp);
                }
                else if(msg.what==3){
                    courseadapter.notifyDataSetChanged();

                }
                else if(msg.what==4){
                    Toast.makeText(MainActivity.this,"验证码输入错误",Toast.LENGTH_LONG).show();
                }
                else if(msg.what==5){
                    Toast.makeText(MainActivity.this,"该老师无课程安排",Toast.LENGTH_LONG).show();
                }
            }
        };

    }


    public void findALLJS(View view){
        final String Filename="JS.txt";
        inputText=JSet.getText().toString();
        Log.i("zm4",inputText);
        new Thread() {
            @Override
            public void run() {
                try {
                    allJS.clear();
                    Fetcher fetcher = new Fetcher();
                    File file=new File(path+Filename);
                    if(!file.exists()) {
                        fetcher.getJS();
                    }

                    File data=new File(path+Filename);
                    org.jsoup.nodes.Document doc= Jsoup.parse(data,"GB2312");
                    Element ele=doc.select("script").get(0);
                    System.out.println(ele.data());
                    String JS=ele.data();
                    org.jsoup.nodes.Document doct=Jsoup.parse(JS);
                    Elements eles=doct.select("option");
                    int len=eles.size();
                    for (Element e: eles) {
                        if(e.text().contains(inputText)) {
                            JS js = new JS();
                            if(selectJS.contains(e.text())) {
                                js.setJSname("***"+e.text());
                                js.setValue(e.attr("value"));
                            }
                            else {
                                js.setJSname(e.text());
                                js.setValue(e.attr("value"));
                            }
                            allJS.add(js);
                        }
                    }

                    Log.i("zm2", allJS.size() + "");
                    handler.sendEmptyMessage(1);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void getyzm(){
        new Thread() {
            @Override
            public void run() {
                try {
                    allJS.clear();
                    Fetcher fetcher = new Fetcher();
                    fetcher.getcookie();
                    handler.sendEmptyMessage(2);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public void findCourse(View view) {
        getyzm();
        for (String s:selectJS){
            if(JSet.getText().toString().contains(s)){
                submityzm(view);
            };
        }
    }

    public void yzm(View view) {
        getyzm();

    }

    public void submityzm(View view) {
        YZM=yzmet.getText().toString();
        Log.i("lt",YZM);
        new Thread() {
            @Override
            public void run() {
                try {
                    allCourse.clear();
                    Fetcher fetcher = new Fetcher();

                    File file=new File(path+selectvalue+"course.txt");
                    if(!file.exists()||yzm==false) {
                        fetcher.getScheduleByTeacher(selectvalue,YZM);
                    }
                    course=fetcher.parsecourse(selectvalue );
                    if(course[0]=="wrong"){
                        yzm=false;
                        getyzm();
                        handler.sendEmptyMessage(4);
                    }
                    else if(course[0]=="empty"){
                        yzm=true;
                        handler.sendEmptyMessage(5);
                    }
                    else{
                        yzm=true;
                        Course c=null;
                        for(int k=0;k<course.length;k++){
                            switch (k%7){
                                case 0:  { c = new Course();
                                    c.setMonday(course[k]);
                                    break;}
                                case 1:c.setTuesday(course[k]);
                                    break;
                                case 2:c.setWednesday(course[k]);
                                    break;
                                case 3:c.setThursday(course[k]);
                                    break;
                                case 4:c.setFriday(course[k]);
                                    break;
                                case 5:c.setSaturday(course[k]);
                                    break;
                                case 6:{c.setSunday(course[k]);
                                    allCourse.add(c);
                                    break;}
                                default:
                                    break;

                            }

                        }
                    }
                    System.out.print(allCourse);
                    handler.sendEmptyMessage(3);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
