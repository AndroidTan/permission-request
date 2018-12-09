package cn.garyxt.myapplication;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends PermissionActivity implements View.OnClickListener {


    private static final String TAG = "TANCT";
    private static final int CALL_PHONE_REQ = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.btn_confirm);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        jobUnderPermission(Manifest.permission.CALL_PHONE, CALL_PHONE_REQ);
    }

    @Override
    protected void performJob(int REQ_CODE) {
        switch (REQ_CODE) {
            case CALL_PHONE_REQ:
                callPhone();
                break;
        }
    }

    private void callPhone() {
        EditText editText = findViewById(R.id.input_box);
        String text = editText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "输入为空", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + text);
            intent.setData(data);
            startActivity(intent);
        }
    }


}
