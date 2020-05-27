package org.vhdl.controller.utils;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PostUtil {
    public static String sendPost(String url, String param){
        OutputStreamWriter out =null;
        BufferedReader reader = null;
        /*存储要返回的值*/
        String response = "";
        //创建连接
        try {
            URL httpUrl; //HTTP URL类 用这个类来创建连接
            //创建URL
            httpUrl = new URL(url);
            //建立连接
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");//请求头
            //conn.setRequestProperty("connection", "keep-alive");
            conn.setUseCaches(false);//设置不要缓存
            conn.setInstanceFollowRedirects(true);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setReadTimeout(180000);

            conn.connect();
            //POST请求写给服务器数据
            out = new OutputStreamWriter(conn.getOutputStream());
            out.write(param);
            //out.writeBytes(json);
            out.flush();
            //读取响应收到服务器传回的数据
            int statusCode = conn.getResponseCode();//状态码
            Log.d("http status", String.valueOf(statusCode));//字符串转换
            InputStream is;
            if (statusCode <299 && statusCode >199 ) {
                is = conn.getInputStream();//正常
            } else {
                is = conn.getErrorStream();//异常
            }
            reader = new BufferedReader(new InputStreamReader(is));
            String lines;
            StringBuilder result = new StringBuilder();
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), StandardCharsets.UTF_8);
                response += lines;//
            }
            Log.d("http result ", response);
            reader.close();

            // 断开连接
            conn.disconnect();

        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(reader!=null){
                    reader.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return response;
    }

}
