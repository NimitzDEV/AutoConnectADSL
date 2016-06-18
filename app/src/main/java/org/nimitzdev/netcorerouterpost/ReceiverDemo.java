/**
 * Created by NimitzDEV on 2015/9/12 0012.
 */
package org.nimitzdev.netcorerouterpost;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class ReceiverDemo extends BroadcastReceiver {
    private static final String strRes = "android.provider.Telephony.SMS_RECEIVED";
    private static MessageListener mMessageListener;

    public ReceiverDemo() {
        super();
    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub
        if (strRes.equals(arg1.getAction())) {
            StringBuilder sb = new StringBuilder();
            Bundle bundle = arg1.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] msg = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                for (SmsMessage curMsg : msg) {
                    if (!curMsg.getDisplayOriginatingAddress().equals("1065921701")) {
                        sb.append("接收到不相关的短信");
                        sb.append(curMsg.getDisplayOriginatingAddress());
                    } else {
                        sb.append("获取到密码：");
                        String pwdADSL = "";
                        pwdADSL = curMsg.getDisplayMessageBody();
                        //获取第二节内容
                        String strArgNo2[] = pwdADSL.split("密码为:");
                        //获取密码
                        String strArgNo1[] = strArgNo2[1].split("密码有");
                        String realADSLPwd = strArgNo1[0];
                        sb.append(realADSLPwd);
                        Log.i("e", pwdADSL);
                        Toast.makeText(arg0,
                                "正在请求CGI程序",
                                Toast.LENGTH_SHORT).show();
                        POST2Router(realADSLPwd);
                    }
                }
                Toast.makeText(arg0,
                        sb.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    * 生成请求连接
    * */
    public void POST2Router( String PPPOEPwd) {
        try {
//            String aURL = "http://192.168.1.1/cgi-bin/fast?pppoeun=" + PPPOEUserName + "&pppoepwd=" + PPPOEPwd;
            mMessageListener.OnReceived(PPPOEPwd);
        } catch (Exception e) {

        }
    }

    public void setOnReceivedMessageListener(MessageListener messageListener) {
        this.mMessageListener = messageListener;
    }

    //回调接口
    public interface MessageListener {
        public void OnReceived(String message);
    }
}
