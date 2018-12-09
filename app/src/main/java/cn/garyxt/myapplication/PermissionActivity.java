package cn.garyxt.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public abstract class PermissionActivity extends BaseActivity {
    private static final int FLAG_REQUEST_PERMISSION = 0x1;
    private static final int FLAG_SETTINGS_PERMISSION = 0x2;

    protected abstract void performJob(final int REQ_CODE);

    // BEGIN 动态权限申请回调代码段

    /**
     * check permission and do the job which require this permission <br/>
     * you should implement performJob() at the same time and do the actual work in the method.
     *
     * @param permission permission required to do job.
     */
    protected void jobUnderPermission(String permission, final int JOB_CODE) {
        // BEGIN 动态权限申请代码段
        int hasCallPhonePermission = ContextCompat.checkSelfPermission(this, permission);
        if (hasCallPhonePermission == PackageManager.PERMISSION_GRANTED) {
            performJob(JOB_CODE);
        } else {
            // 这个requestPermission如果用户点击了“禁止后不再提示”，他就不会再唤起系统授权对话框了。
            // 但是会回调onRequestPermissionsResult();
            ActivityCompat.requestPermissions(this, new String[]{permission}, JOB_CODE);
        }
        // END
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 用户授予了权限
            performJob(requestCode);
        } else {
            // 没有授权
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    PermissionActivity.this, Manifest.permission.CALL_PHONE)) {
                // 没有点选“不再提示”
                showPermissionDialog(FLAG_REQUEST_PERMISSION, requestCode);
            } else {
                // 点选了“不再提示”
                showPermissionDialog(FLAG_SETTINGS_PERMISSION, requestCode);
            }
        }
    }
    // END

    private void showPermissionDialog(final int flag, final int requestCode) {
        new AlertDialog.Builder(this).setTitle("授权提示")
                .setMessage("需要授予拨打电话权限才能打电话。")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户取消了授权，整个路程结束，不执行任何操作。
                        Toast.makeText(PermissionActivity.this, "取消拨打电话", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (flag) {
                            case FLAG_REQUEST_PERMISSION:
                                // 用户之前没有点击“不再提示”，但是此处选在继续授权的分支
                                ActivityCompat.requestPermissions(PermissionActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE}, requestCode);
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
