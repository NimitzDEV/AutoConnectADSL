package org.nimitzdev.netcorerouterpost;

//DEFAULT
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
//EXTENDS
import java.util.List;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    String SENT_SMS_ACTION="SENT_SMS_ACTION";
    String DELIVERED_SMS_ACTION="DELIVERED_SMS_ACTION";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String telNo = "1065921701";
                String content = "m";
                //Send Text Message
                Button btn = (Button) findViewById(R.id.btn);
                btn.setEnabled(false);
                btn.setText("正在执行操作...");
                sendSMS(telNo, content);
            }
        });
    }

    private void init(){
        final WebView mWebView;
        mWebView=(WebView) findViewById(R.id.webViewURL);
        ReceiverDemo mMessageListener;
        mMessageListener = new ReceiverDemo();
        mMessageListener.setOnReceivedMessageListener(new ReceiverDemo.MessageListener() {
            @Override
            public void OnReceived(String message) {
                mWebView.loadUrl(message);
                Button btn = (Button)findViewById(R.id.btn);
                btn.setEnabled(false);
                btn.setText("操作已经完成，可以退出");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Send SMS
     * @param phoneNumber
     * @param message
     */
    private void sendSMS(String phoneNumber, String message) {

        //create the sentIntent parameter
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent,
                0);
        // create the deilverIntent parameter
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0,
                deliverIntent, 0);

        SmsManager sms = SmsManager.getDefault();
        if (message.length() > 70) {
            List<String> msgs = sms.divideMessage(message);
            for (String msg : msgs) {
                sms.sendTextMessage(phoneNumber, null, msg, sentPI, deliverPI);
            }
        } else {
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliverPI);
        }
        Toast.makeText(MainActivity.this, "正在获取上网密码...", Toast.LENGTH_LONG).show();

        //register the Broadcast Receivers
        registerReceiver(new BroadcastReceiver(){
                             @Override
                             public void onReceive(Context _context,Intent _intent)
                             {
                                 switch(getResultCode()){
                                     case Activity.RESULT_OK:
                                         Toast.makeText(getBaseContext(),
                                                 "密码获取短信成功发送,稍后将自动链接网络",
                                                 Toast.LENGTH_SHORT).show();
                                         break;
                                     case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                         Toast.makeText(getBaseContext(),
                                                 "发送失败：RESULT_ERROR_GENERIC_FAILURE",
                                                 Toast.LENGTH_SHORT).show();
                                         break;
                                     case SmsManager.RESULT_ERROR_RADIO_OFF:
                                         Toast.makeText(getBaseContext(),
                                                 "发送失败：RESULT_ERROR_RADIO_OFF",
                                                 Toast.LENGTH_SHORT).show();
                                         break;
                                     case SmsManager.RESULT_ERROR_NULL_PDU:
                                         Toast.makeText(getBaseContext(),
                                                 "发送失败：RESULT_ERROR_NULL_PDU",
                                                 Toast.LENGTH_SHORT).show();
                                         break;
                                 }
                             }
                         },
                new IntentFilter(SENT_SMS_ACTION));
        registerReceiver(new BroadcastReceiver(){
                             @Override
                             public void onReceive(Context _context,Intent _intent)
                             {
                                 Toast.makeText(getBaseContext(),
                                         "正在等待回应",
                                         Toast.LENGTH_SHORT).show();
                             }
                         },
                new IntentFilter(DELIVERED_SMS_ACTION));
    }
}
