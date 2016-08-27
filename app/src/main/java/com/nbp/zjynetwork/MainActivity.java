package com.nbp.zjynetwork;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
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
    static TextView mTextViewStatus;
    RadioGroup mRadioGroup;
    static String method = "GET";
    static ImageView ivMainTop;
    private String link = "http://192.168.31.206/mynetwork/index.php";
    private final static int REQUEST_SUCCESS = 98;//表示请求成功（与登录与否无关，可能登录失败也肯成功）

    static Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case REQUEST_SUCCESS:

                    String sRes = (String) msg.obj;
                    mTextViewStatus.setText(sRes);
                    ivMainTop.setImageResource(R.mipmap.olymp);
                    break;

            }
            return false;
        }
    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ed_name = (EditText) findViewById(R.id.main_user_name);
        ed_password = (EditText) findViewById(R.id.main_pass_word);
        mTextViewStatus = (TextView) findViewById(R.id.main_tv_status);
        mRadioGroup = (RadioGroup) findViewById(R.id.rbtn_method_selection);
        ivMainTop = (ImageView) findViewById(R.id.main_iv_top);

        RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.rbtn_method_selection);

        final RadioButton[] radioButtons = new RadioButton[2];
        radioButtons[0] = (RadioButton) findViewById(R.id.method_get);
        radioButtons[1] = (RadioButton) findViewById(R.id.method_post);

        assert mRadioGroup != null;
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < 2;i++){
                    if (radioButtons[i].isChecked() == true){
                        Log.d("选择模式",i+""+method);

                        if (i == 0){
                            method = "GET";
                            Log.d("选择模式",i+""+method);
                            break;
                        }

                        if (i == 1){
                            method = "POST";
                            Log.d("选择模式",i+""+method);
                            break;
                        }

                    }
                }
            }
        });
    }


    public void login(View view){

        String name = ed_name.getText().toString();
        String password = ed_password.getText().toString();

//        mt(name+password);

        final Map<String, String> params = new HashMap<>();
        params.put("username",name);
        params.put("password",password);




//        String method = mString;

        if ("GET".equals(method)){
            Log.d("----method-------","GET");
            (new Thread(){

                @Override
                public void run() {
                    super.run();
                    doHttpGet(link,params);
                }
            }).start();


        }else if ("POST".equals(method)){

//            Log.d("------POST-method------","POST");

            new Thread(){
                @Override
                public void run() {
                    Log.d("---post---","1到这里了么？");
                    super.run();
                    Log.d("------httpPost----",doHttpPost_one(link,params));
                }
            }.start();

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

    private String doHttpPost_one(String link, Map<String,String> params){

        Log.d("---post---","2到这里了么？");
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);


            StringBuffer sb = new StringBuffer("");
            for (String key: params.keySet()
                    ){
                sb.append(key+"="+params.get(key)+"&");
            }

            sb.deleteCharAt(sb.length()-1);

//            Log.d("-----link-----","GET"+sb.toString());

            String parameters = sb.toString();


            DataOutputStream out = new DataOutputStream(conn.getOutputStream());//获取网络输出流
            byte[] bytes = parameters.getBytes();

            out.write(bytes);//把数据写到输出流
            out.flush();
            out.close();

            //读取服务器返回数据
            //获取输入流
            Log.d("---post---","3到这里了么？");
            StringBuilder builder = new StringBuilder("");
            Log.d("---post response code--",conn.getResponseCode()+""+"HTTTP_OK: "+HttpURLConnection.HTTP_OK);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){

                Log.d("---post---","4到这里了么？");
                //准备读取返回数据
                BufferedReader reader = new BufferedReader(  new InputStreamReader(conn.getInputStream()));
                String line = "";



                while ((line = reader.readLine()) != null){

                    builder.append(line);


                }
                reader.close();
                conn.disconnect();
                Log.d("---post---","5到这里了么？");

            }

            return builder.toString();



        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    //GET方法
    private void doHttpGet(String link, Map<String, String> params) {


        //不能在主线程中做网络请求这样的延时操作=====需要在子线程中去做


        //创建缓存对象
        StringBuffer sb = null;
        BufferedReader reader = null;

        //通过网址来实现HTTP访问
        try {
            //http://192.168.123.206/mynetwork/index.php    username    password


            String getLink = "http://192.168.31.206/mynetwork/get.php";
            sb = new StringBuffer(getLink);
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
            conn.setRequestMethod("GET");
            //获得服务器端的回复
            int code = conn.getResponseCode();
            Log.d("---GET---CODE--","response code: "+code+"");
            if (code == HttpURLConnection.HTTP_OK){
                //读取答案
                //I-O流来读取

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                final String result = reader.readLine();
                Log.d("------result------","GET"+result);

////                Handler机制来做？ 或者下面的方法
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mt(result);
//                        mTextViewStatus.setText(result);
//                        ivMainTop.setImageResource(R.mipmap.olymp);
//
//                    }
//                });

                Message message  = Message.obtain();
                message.obj = result;//通过message传任何对象
               message.what = REQUEST_SUCCESS;//通过信息的存在原理--为什么要发送，如请求错误，超时，等...
                mHandler.sendMessage(message);
//                mTextViewStatus.setText(result);
//                ivMainTop.setImageResource(R.mipmap.olymp);


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
