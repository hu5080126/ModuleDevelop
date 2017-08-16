package hu.yahui.user.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 2016/6/30.
 */
public class LoginInfoBean implements Parcelable {

    private String accesstoken;
    /**
     * 企业名称
     */
    private String name;

    private boolean isSelect;

    @SerializedName("company_no")
    private String companyNo;

    private String fullname;

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt((isSelect) ? 0 : 1);
        parcel.writeString(accesstoken);
        parcel.writeString(companyNo);
        parcel.writeString(fullname);
    }

    public static final Creator<LoginInfoBean> CREATOR = new Creator<LoginInfoBean>() {
        @Override
        public LoginInfoBean createFromParcel(Parcel parcel) {
            LoginInfoBean changeCompany = new LoginInfoBean();
            changeCompany.name = parcel.readString();
            changeCompany.isSelect = (parcel.readInt() == 0);
            changeCompany.accesstoken = parcel.readString();
            changeCompany.companyNo = parcel.readString();
            changeCompany.fullname = parcel.readString();
            return changeCompany;
        }

        @Override
        public LoginInfoBean[] newArray(int i) {
            return new LoginInfoBean[0];
        }
    };




}
