package hu.yahui.module_common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

/**
 * Created by User on 2017/8/10.
 */

public class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.getInstance().addActivity(this);
    }
    
    /**
     * 检测是否有权限
     * @param permissions
     * @return
     */
    public boolean checkHasPermission(String... permissions) {
        boolean isHas = true;
        for (String s : permissions) {
            if (ContextCompat.checkSelfPermission(this, s) == PERMISSION_DENIED) {
                isHas = false;
                break;
            }
        }
        return isHas;
    }
    
    /**
     * 检测用户以前是否拒绝过该权限
     * true: 用户以前拒绝过该权限
     * false: 第一种： 用户以前拒绝了该权限，并在权限请求系统对话框中选择了 Don't ask again 选项。
     *        第二种： 如果设备规范禁止应用具有该权限，此方法也会返回 false。
     * @param permissions
     * @return
     */
    public boolean shouldNeedShowPermissionRationale(String... permissions) {
        for (String s : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, s)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 请求权限
     * @param code
     * @param permissions
     */
    public void requestPremission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().finishActivity(this);
    }
    
    public boolean getBooleanExtra(String key) {
        return getIntent().getBooleanExtra(key, false);
    }
    
    public String getStringExtra(String key) {
        return getIntent().getStringExtra(key);
    }
    
    public int getIntExtra(String key) {
        return getIntent().getIntExtra(key, 0);
    }
    
}
