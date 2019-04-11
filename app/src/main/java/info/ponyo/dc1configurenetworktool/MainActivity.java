package info.ponyo.dc1configurenetworktool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * @author zxq
 */
public class MainActivity extends AppCompatActivity {

    public static final String MESSAGE = "{\"header\":\"phi-plug-0001\",\"uuid\":\"00010\",\"action\":\"wifi=\",\"auth\":\"\",\"params\":{\"ssid\":\"%s\",\"password\":\"%s\"}}\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("DC1配网工具");

        TextView tvTips = findViewById(R.id.tv_tips);
        tvTips.setText("1. 长按DC1电源键\n2. 连接wifi:PHI_PLUG1_xxxx\n3. 输入wifi名称和密码\n4. 点击配置按钮\n5. 收到配网成功的消息后插排会自动重启");

        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(v -> {
            EditText etSsid = findViewById(R.id.et_ssid);
            EditText etPwd = findViewById(R.id.et_pwd);
            String ssid = etSsid.getText().toString().trim();
            String pwd = etPwd.getText().toString().trim();
            String msg = String.format(Locale.getDefault(), MESSAGE, ssid, pwd);
            new Thread(() -> {
                new UDPClient(new Listener()).send(msg);
            }).start();
        });
    }

    private class Listener implements UdpListener {

        @Override
        public void onSuccess(String content) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "网络配置成功", Toast.LENGTH_LONG).show());
        }

        @Override
        public void onFail(String message) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show());
        }
    }
}
