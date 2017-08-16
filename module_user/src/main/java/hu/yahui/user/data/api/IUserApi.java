package hu.yahui.user.data.api;

import hu.yahui.user.data.bean.EhrConfig;

/**
 * Created by User on 2017/8/14.
 */

public interface IUserApi {
    void getConfig(GetDataCallBack<EhrConfig> getDataCallBack);
    
    interface GetDataCallBack<T> {
        void onSuccess(T data);
        void onFailure(String msg);
        void onError(int code, String msg);
    }
}
