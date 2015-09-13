/**
 * Created by NimitzDEV on 2015/9/12 0012.
 */
package org.nimitzdev.netcorerouterpost;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
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
        if(strRes.equals(arg1.getAction())){
            StringBuilder sb = new StringBuilder();
            Bundle bundle = arg1.getExtras();
            if(bundle!=null){
                Object[] pdus = (Object[])bundle.get("pdus");
                SmsMessage[] msg = new SmsMessage[pdus.length];
                for(int i = 0 ;i<pdus.length;i++){
                    msg[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }

                for(SmsMessage curMsg:msg){
                    if(!curMsg.getDisplayOriginatingAddress().equals("1065921701")){
                        sb.append("接收到不相关的短信");
                        sb.append(curMsg.getDisplayOriginatingAddress());
                    }else{
                        sb.append("获取到密码：");
                        String pwdADSL = "";
                        pwdADSL = curMsg.getDisplayMessageBody();
                        //获取第二节内容
                        String strArgNo2[] = pwdADSL.split("密码为:");
                        //获取密码
                        String strArgNo1[] = strArgNo2[1].split("密码有");
                        String realADSLPwd = strArgNo1[0];
                        sb.append(realADSLPwd);
                        String fullText[] = getMACSettings().split("DIVIDE");
                        String SetMacAddr = fullText[0];
                        String PPPOEUserName = fullText[1];
                        Toast.makeText(arg0,
                                "正在设置路由器",
                                Toast.LENGTH_SHORT).show();
                      POST2Router(SetMacAddr, PPPOEUserName, realADSLPwd);
                    }
                }
                Toast.makeText(arg0,
                         sb.toString(),
                        Toast.LENGTH_SHORT).show();
                //NetCore POST 数据地址 http://192.168.1.1/cgi-bin-igd/netcore_set.cgi
                //NetCore POST 获取信息 mode_name=netcore_get&noneed=noneed
            }
        }
    }

    public String getMACSettings(){
        String MacAddress = "";
        String TAG = "E";
        HttpPost request = new HttpPost("http://192.168.1.1/cgi-bin-igd/netcore_get.cgi");
        JSONObject jparam = new JSONObject();
        try{
            jparam.put("mode_name","netcore_get");
            jparam.put("noneed","noneed");
            StringEntity se = new StringEntity(jparam.toString());
            request.setEntity(se);
             //GET RESPONSE
            HttpResponse httpResponse = new DefaultHttpClient().execute(request);
            String retSrc = EntityUtils.toString(httpResponse.getEntity());
            /*return retSrc;*/
            //GET RESULT
            JSONObject result = new JSONObject(retSrc);
            MacAddress = result.getString("mac_addr") + "DIVIDE" + result.getString("pppoe_username");
            return MacAddress;
        } catch (Exception e) {
            e.printStackTrace();
            MacAddress = e.getMessage();
        }
        return MacAddress;
    }

    //NetCore POST 数据格式 mode_name=netcore_set&shortcut=wan&mac_addr=08-10-78-21-54-53&conntype=1&pppoe_username=18174162081&pppoe_pwd=81502448&default_flag=1
    public void POST2Router(String SetMacAddress,String PPPOEUserName,String PPPOEPwd){

        //发送 JSON 方式
        // 如果路由器使用 POST JSON 的方法传递参数，可以使用下面的方法
        /*        String RM = "\nRUN";
        String TAG = "E";
        HttpPost request = new HttpPost("http://192.168.1.1/cgi-bin-igd/netcore_set.cgi");
      JSONObject jparam = new JSONObject();
        try{
          jparam.put("mode_name","netcore_set");
            jparam.put("shortcut","wan");
            jparam.put("mac_addr",SetMacAddress);
            jparam.put("conntype","1");
            jparam.put("pppoe_username",PPPOEUserName);
            jparam.put("pppoe_pwd",PPPOEPwd);
            jparam.put("default_flag","1");
           StringEntity se = new StringEntity(jparam.toString());
            request.setEntity(se);*//*
            //GET RESPONSE
            HttpResponse httpResponse = new DefaultHttpClient().execute(request);
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            RM = e.getMessage();
        }
        return RM;
    }*/

        //直接URL访问
    try{
        String aURL = "http://192.168.1.1/cgi-bin-igd/netcore_set.cgi?mode_name=netcore_set&shortcut=wan&mac_addr=" +
            SetMacAddress + "&conntype=1&pppoe_username=" + PPPOEUserName + "&pppoe_pwd=" + PPPOEPwd + "&default_falg=1";
        mMessageListener.OnReceived(aURL);
    }catch(Exception e){

    }
    }
    //回调接口
    public interface MessageListener {
        public void OnReceived(String message);
    }

    public void setOnReceivedMessageListener(MessageListener messageListener) {
        this.mMessageListener = messageListener;
    }
}
