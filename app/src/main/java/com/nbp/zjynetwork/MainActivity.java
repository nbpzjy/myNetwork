package com.nbp.zjynetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    EditText ed_name,ed_password;
    private String link = "http://192.168.123.206/mynetwork/index.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ed_name = (EditText) findViewById(R.id.main_user_name);
        ed_password = (EditText) findViewById(R.id.main_pass_word);
    }


    public void login(View view){

        String name = ed_name.getText().toString();
        String password = ed_password.getText().toString();

//        mt(name+password);

        final Map<String, String> params = new HashMap<>();
        params.put("username",name);
        params.put("password",password);




        String method = "POST";

        if ("GET".equals(method)){
            (new Thread(){

                @Override
                public void run() {
                    super.run();
                    doHttpGet(link,params);
                }
            }).start();


        }else if ("POST".equals(method)){
            (new Thread(){
                @Override
                public void run() {
                    super.run();
                    doHttpPost("jianyong","123");
                }
            }).start();

        }
    }
    //--------------------------

    public String doHttpPost(String username, String password) {
        String path = "http://192.168.123.206/mynetwork/index.php";
        BufferedReader reader = null;
        StringBuffer sb;

        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");

            //数据准备
            String data = "username=" + username + "&password=" + password;
            //至少要设置的两个请求头
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", data.length() + "");

            //post的方式提交实际上是留的方式提交给服务器
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data.getBytes());

            //获得结果码
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                //请求成功
//                mt("POST请求成功！");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mt("POST请求成功！");
                    }
                });


                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                final String result = reader.readLine();
                Log.d("------result------", "POST" + result);

                //Handler机制来做？ 或者下面的方法
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mt(result);
                    }
                });


//                return IOSUtil.inputStream2String(is);
            } else {
                //请求失败
//                mt("POST请求失败！");
                //Handler机制来做？ 或者下面的方法
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mt("POST请求失败！");
                    }
                });
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

        //--------------------------
//
//    //POST方法
//    private void doHttpPost(String link, Map<String,String> params) {
//
//        BufferedReader reader = null;
//        StringBuffer sb;
//
//        try{
//
//            URL url = new URL(link);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setConnectTimeout(5000);
//            conn.setRequestMethod("POST");
//
//            //获得请求参数
//            sb = new StringBuffer("");
////            sb.append("?");
//
//            for (String key: params.keySet()
//                    ){
//                sb.append(key+"="+params.get(key)+"&");
//            }
//
//            sb.deleteCharAt(sb.length()-1);
//
//            Log.d("-----link-----","POST"+sb.toString());
//
//            //开始请求
//            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
//            conn.setRequestProperty("Content-Length",sb.toString().length()+"");
//
//            //POST实际上是以流的方式提交给服务器
//            conn.setDoOutput(true);
//            OutputStream outPutStream = conn.getOutputStream();
//            outPutStream.write(sb.toString().getBytes());
//
//            //获得结果码
//            int responseCode = conn.getResponseCode();
//
//            //以结果码来判断是不是请求成功
//            if (responseCode == HttpURLConnection.HTTP_OK){
//                //表示成功
////                InputStream inPutStream = conn.getInputStream();
//                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                final String result = reader.readLine();
//                Log.d("------result------","POST"+result);
//
//                //Handler机制来做？ 或者下面的方法
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mt(result);
//                    }
//                });
//
//            }else {
//                return;
//            }
//
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    //GET方法
    private void doHttpGet(String link, Map<String, String> params) {


        //不能在主线程中做网络请求这样的延时操作=====需要在子线程中去做


        //创建缓存对象
        StringBuffer sb = null;
        BufferedReader reader = null;

        //通过网址来实现HTTP访问
        try {
            //http://192.168.123.206/mynetwork/index.php    username    password


            sb = new StringBuffer(link);
            sb.append("?");
            for (String key: params.keySet()
                    ){
                sb.append(key+"="+params.get(key)+"&");
            }

            sb.deleteCharAt(sb.length()-1);

            Log.d("-----link-----","GET"+sb.toString());

            link = sb.toString();

            URL url = new URL(link);
            //建立联系
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //获得服务器端的回复
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK){
                //读取答案
                //I-O流来读取

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                final String result = reader.readLine();
                Log.d("------result------","GET"+result);

                //Handler机制来做？ 或者下面的方法
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mt(result);
                    }
                });


            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //封装Toast
    public void mt(String message){

        Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
    }
}
