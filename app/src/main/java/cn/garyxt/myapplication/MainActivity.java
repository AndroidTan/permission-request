package cn.garyxt.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CALL_PHONE_REQ = 0;
    private static final String TAG = "TANCT";
    private static final int FLAG_REQUEST_PERMISSION = 0x1;
    private static final int FLAG_SETTINGS_PERMISSION = 0x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.btn_confirm);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // BEGIN 动态权限申请代码段
        int hasCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (hasCallPhonePermission == PackageManager.PERMISSION_GRANTED) {
            callPhone();
        } else {
            // 这个requestPermission如果用户点击了“禁止后不再提示”，他就不会再唤起系统授权对话框了。
            // 但是会回调onRequestPermissionsResult();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_REQ);
        }
        // END
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

    // BEGIN 动态权限申请回调代码段
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PHONE_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限
                callPhone();
            } else {
                // 没有授权
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        MainActivity.this, Manifest.permission.CALL_PHONE)) {
                    // 没有点选“不再提示”
                    showPermissionDialog(FLAG_REQUEST_PERMISSION);
                } else {
                    // 点选了“不再提示”
                    showPermissionDialog(FLAG_SETTINGS_PERMISSION);
                }
            }
        }
    }
    // END

    private void showPermissionDialog(final int flag) {
        new AlertDialog.Builder(this).setTitle("授权提示")
                .setMessage("需要授予拨打电话权限才能打电话。")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户取消了授权，整个路程结束，不执行任何操作。
                        Toast.makeText(MainActivity.this, "取消拨打电话", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (flag) {
                            case FLAG_REQUEST_PERMISSION:
                                // 用户之前没有点击“不再提示”，但是此处选在继续授权的分支
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_REQ);
                                break;
                            case FLAG_SETTINGS_PERMISSION:
                                // 用户之前点击过“不再提示”，此处选择继续授权的分支
                                startActivity(getAppDetailSettingIntent());
                                break;
                        }

                    }
                }).create().show();
    }


    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        return localIntent;
    }

}
