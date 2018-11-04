package com.example.lenovo.supercourse;

import android.os.Environment;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Fetcher {
    private List<JS> allJS=new ArrayList<JS>();
    private static String Filename="JS.txt";
    private static String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/supercourse/";

    public void getJS() throws Exception {
        String referer="http://121.248.70.120/jwweb/ZNPK/TeacherKBFB.aspx";
        String address="http://121.248.70.120/jwweb/ZNPK/Private/List_JS.aspx";
        URL url=new URL(address);
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        con.setDoInput(true);
        con.setRequestProperty("Referer", referer);
        con.setRequestMethod("GET");
        InputStream input=con.getInputStream();
        int len=con.getContentLength();
        System.out.println(len);
        byte[] buf=new byte[512];
        File file=new File(path+Filename);
        if (!file.exists()){
            file.createNewFile();
        }
        OutputStream out=new FileOutputStream(file);
        Log.i("zm","getJS");
        while((len=input.read(buf))!=-1) {
            out.write(buf, 0, len);
            System.out.println(new String(buf,"GB2312"));
            System.out.println("..........");
        }
        input.close();


    }

    public List<JS> parseJS() throws Exception{

        File data=new File(path+Filename);
        org.jsoup.nodes.Document doc= Jsoup.parse(data,"GB2312");
        Element ele=doc.select("script").get(0);
        System.out.println(ele.data());
        String JS=ele.data();
        org.jsoup.nodes.Document doct=Jsoup.parse(JS);
        Elements eles=doct.select("option");
        int len=eles.size();
        System.out.println(len);
        for (Element e: eles) {
            JS js=new JS();
            String jsname=new String(e.text().getBytes("gbk"),"UTF-8");
            String value=new String(e.attr("value").getBytes("gbk"),"UTF-8");
            js.setJSname(jsname);
            js.setValue(value);
            allJS.add(js);

        }
        System.out.println(allJS);
        return allJS;
    }
    private static Properties conf=new Properties();
    public static void getcookie() throws Exception {
        String address="http://121.248.70.120/jwweb/sys/ValidateCode.aspx";
        URL url=new URL(address);
        URLConnection connection=url.openConnection();
        connection.setRequestProperty("Referer","http://121.248.70.120/jwweb/ZNPK/TeacherKBFB.aspx");
        connection.setDoInput(true);
        connection.connect();
        String cookie=connection.getHeaderField("Set-Cookie");
        System.out.println(cookie);
        conf.setProperty("cookie",cookie.split(";")[0]);
        File file=new File(path+"conf.properties");
        if (!file.exists()){
            file.createNewFile();
        }
        conf.store(new FileOutputStream(path+"conf.properties")," comment");
        byte[] buf=new byte[512];
        InputStream inputStream=connection.getInputStream();
        File file1=new File(path+"my.png");
        if (!file1.exists()){
            file1.createNewFile();
        }
        OutputStream outputStream=new FileOutputStream(file1);
        int len=0;
        while((len=inputStream.read(buf))!=-1){
            outputStream.write(buf, 0, len);

        }
        inputStream.close();
        outputStream.close();

    }
    public void getScheduleByTeacher(String selectvalue,String YZM) throws Exception {
        conf.load(new FileInputStream(path+"conf.properties"));
        String cookie=conf.getProperty("cookie");
        String referer="http://121.248.70.120/jwweb/ZNPK/TeacherKBFB.aspx";
        String address="http://121.248.70.120/jwweb/ZNPK/TeacherKBFB_rpt.aspx";
        URL url=new URL(address);
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Cookie", cookie);
        connection.setRequestProperty("Referer", referer);
        connection.setRequestMethod("POST");
        StringBuilder builder=new StringBuilder();
        builder.append("Sel_XNXQ=20180&Sel_JS="+selectvalue+"&type=1&txt_yzm="+YZM);
        OutputStream netout=connection.getOutputStream();
        netout.write(builder.toString().getBytes());
        int len=connection.getContentLength();
        System.out.println(len);
        byte[] buf=new byte[512];
        InputStream inputStream=connection.getInputStream();
        File file=new File(path+selectvalue+"course.txt");
        if (!file.exists()){
            file.createNewFile();
        }
        OutputStream outputStream=new FileOutputStream(file);
        while((len=inputStream.read(buf))!=-1){
            outputStream.write(buf,0,len);
            System.out.println(new String(buf,"GB2312"));
        }
        inputStream.close();
        outputStream.close();


    }
    public String[] parsecourse(String selectvalue) throws Exception{
        File data=new File(path+selectvalue+"course.txt");
        Document document=Jsoup.parse(data,"GB2312");
        String[]course=new String[42];
        Elements elements=document.select("table");
        int c=0;
        if(elements.size()<3){
            course[0]="empty";
            Elements m=document.select("Script");
            if (m.size()>0){
                course[0]="wrong";
            }
        }
        else {
            Elements trs = elements.get(3).select("tr");
            for (int i = 0; i < trs.size(); i++) {
                Elements tds = trs.get(i).select("td");
                if (i == 1 || i == 3) {
                    for (int j = 2; j < tds.size(); j++) {
                        if (tds.get(j).text() == null) {
                            course[c] = null;
                            c++;
                        }
                        course[c] = tds.get(j).text();
                        c++;
                    }
                } else {
                    for (int j = 1; j < tds.size(); j++) {
                        if (tds.get(j).text() == null) {
                            course[c] = null;
                            c++;
                        }
                        course[c] = tds.get(j).text();
                        c++;
                    }
                }
            }
        }
        for(int a=0;a<course.length;a++){
            System.out.println(course[a]);
        }
        return course;
    }

}
