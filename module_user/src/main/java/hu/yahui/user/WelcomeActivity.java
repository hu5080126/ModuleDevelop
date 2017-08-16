package hu.yahui.user;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.yahui.module_common.base.BaseActivity;
import hu.yahui.user.data.api.IUserApi;
import hu.yahui.user.data.api.UserApiImp;
import hu.yahui.user.data.bean.EhrConfig;
import hu.yahui.user.data.bean.LoginInfoBean;

@Route(path = "/user/welcome")
public class WelcomeActivity extends BaseActivity {
    @BindView(R2.id.user_welcome_login_btn)
    public Button mButton;
    @BindView(R2.id.user_welcome_register_btn)
    public Button mRegisterBtn;
    private LoginInfoBean mLoginInfo;
    private int regCount;
    private static final int READ_PHONE = 100;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        mButton.getBackground().setLevel(1);
        checkPermission();
    }
    
    private void checkPermission() {
        if (getBooleanExtra("isLoginOutStart")) {
            mButton.setVisibility(View.VISIBLE);
            mRegisterBtn.setVisibility(View.VISIBLE);
        } else {
            String[] permissions = new String[]{android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!checkHasPermission(permissions)) {
                requestPremission(READ_PHONE, permissions);
            } else {
                initValue();
            }
        }
    }
    
    private void initValue() {
        UserApiImp.getInstance().getConfig(new IUserApi.GetDataCallBack<EhrConfig>() {
            @Override
            public void onSuccess(EhrConfig data) {
                ARouter.getInstance().build("/main/main").navigation();
            }
    
            @Override
            public void onFailure(String msg) {
        
            }
    
            @Override
            public void onError(int code, String msg) {
        
            }
        });
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_PHONE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initValue();
                } else {
                    
                }
                break;
        }
    }
}
