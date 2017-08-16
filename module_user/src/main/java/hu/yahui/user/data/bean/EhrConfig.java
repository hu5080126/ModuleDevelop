package hu.yahui.user.data.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 2017/8/14.
 */

public class EhrConfig {
    
    @SerializedName("app_check_flag")
    int appCheckFlag;
    
    @SerializedName("customer_service_hotline")
    String customerServiceHotline;
    
    @SerializedName("h5_file_url")
    String h5FileUrl;
    
    public boolean isAppCheckFlag() {
        return appCheckFlag == 1;
    }
    
    public String getCustomerServiceHotline() {
        return customerServiceHotline;
    }
    
    public String getH5FileUrl() {
        return h5FileUrl;
    }
}
