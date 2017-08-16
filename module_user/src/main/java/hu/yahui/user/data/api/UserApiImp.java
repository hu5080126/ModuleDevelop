package hu.yahui.user.data.api;

import hu.yahui.module_common.http.GetDataResultListener;
import hu.yahui.module_common.http.HttpClient;
import hu.yahui.user.data.bean.EhrConfig;

import static hu.yahui.module_common.http.DataType.JSON_OBJECT;

/**
 * Created by User on 2017/8/14.
 */

public class UserApiImp implements IUserApi {
    private static UserApiImp mUserApiImp = new UserApiImp();
    
    private UserApiImp() {
    }
    
    public static UserApiImp getInstance() {
        return mUserApiImp;
    }
    
    @Override
    public void getConfig(final GetDataCallBack<EhrConfig> getDataResultListener) {
        HttpClient httpClient = new HttpClient.Builder()
                .setDataType(JSON_OBJECT, EhrConfig.class)
                .setUrl("v1/app/config/")
                .build();
        httpClient.get(new GetDataResultListener<EhrConfig>() {
            @Override
            public void onSuccess(EhrConfig result) {
                getDataResultListener.onSuccess(result);
            }
            
            @Override
            public void onError(int code, String message) {
                getDataResultListener.onError(code, message);
            }
            
            @Override
            public void onFailure(String message) {
                getDataResultListener.onFailure(message);
            }
        });
    }
}
