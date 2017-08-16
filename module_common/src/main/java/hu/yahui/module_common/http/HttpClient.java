package hu.yahui.module_common.http;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import hu.yahui.module_common.R;
import hu.yahui.module_common.utils.NetworkUtils;
import hu.yahui.module_common.utils.StringUtils;
import hu.yahui.module_common.utils.ToastUtils;
import hu.yahui.module_common.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by User on 2017/8/10.
 */

public class HttpClient {
    private static OkHttpClient mOkHttpClient;
    private static HttpClient mHttpClient;
    private Retrofit mRetrofit;
    private ApiServer mApiServer;
    private Call<ResponseBody> mCall;
    
    
    private static String BASE_URL = "http://api-test.2haohr.com/";
    private static String mCurrentBaseUrl = "";
    
    private Builder mBuilder;
    private static final Map<String, Call> CALL_MAP = new HashMap<>();
    
    private HttpClient() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .build();
    }
    
    public void setBuilder(Builder mBuilder) {
        this.mBuilder = mBuilder;
    }
    
    public Builder getBuilder() {
        return mBuilder;
    }
    
    public static HttpClient getInstance() {
        if (mHttpClient == null) {
            mHttpClient = new HttpClient();
        }
        return mHttpClient;
    }
    
    private void refreshRetrofit(String baseUrl) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(mOkHttpClient)
                .build();
        mApiServer = mRetrofit.create(ApiServer.class);
    }
    
    public Retrofit getRetrofit() {
        return mRetrofit;
    }
    
    public void get(GetDataResultListener getDataResultListener) {
        Builder builder = mBuilder;
        if (!builder.mParams.isEmpty()) {
            String value = "";
            for (Map.Entry<String, String> entry : builder.mParams.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue();
                String span = value.equals("") ? "" : "&";
                String part = StringUtils.buffer(span, mapKey, "=", mapValue);
                value = StringUtils.buffer(value, part);
            }
            builder.setUrl(StringUtils.buffer(builder.mUrl, "?", value));
        }
        mCall = mApiServer.executeGet(builder.mUrl);
        putCall(builder, mCall);
        requestData(builder, getDataResultListener, mCall);
        
    }
    
    public void post(GetDataResultListener getDataResultListener) {
        Builder builder = mBuilder;
        mCall = mApiServer.executePost(builder.mUrl, builder.mParams);
        putCall(builder, mCall);
        requestData(builder, getDataResultListener, mCall);
    }
    
    private void requestData(final Builder builder, final GetDataResultListener getDataResultListener,
                             Call<ResponseBody> call) {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showLongToastSafe(R.string.current_internet_invalid);
            getDataResultListener.onFailure(Utils.getString(R.string.current_internet_invalid));
            return;
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (200 == response.code()) {
                    try {
                        String result = response.body().string();
                        parseData(result, builder.mClass, builder.mDataType, getDataResultListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (!response.isSuccessful() || 200 != response.code()) {
                    getDataResultListener.onError(response.code(), response.message());
                }
                removeCall(builder.mUrl);
            }
            
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                getDataResultListener.onFailure(t.getMessage());
                removeCall(builder.mUrl);
            }
        });
    }
    
    
    /**
     * 添加某个请求
     */
    private synchronized void putCall(Builder builder, Call call) {
        if (TextUtils.isEmpty(builder.mTag))
            return;
        synchronized (CALL_MAP) {
            CALL_MAP.put(builder.mTag.toString() + builder.mUrl, call);
        }
    }
    
    /**
     * 取消某个界面都所有请求，或者是取消某个tag的所有请求;
     * 如果要取消某个tag单独请求，tag需要传入tag+url
     *
     * @param tag 请求标签
     */
    public synchronized void cancelCall(String tag) {
        if (tag == null)
            return;
        List<String> list = new ArrayList<>();
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.startsWith(tag.toString())) {
                    CALL_MAP.get(key).cancel();
                    list.add(key);
                }
            }
        }
        for (String s : list) {
            removeCall(s);
        }
    }
    
    /**
     * 移除某个请求
     *
     * @param url 添加的url
     */
    private synchronized void removeCall(String url) {
        if (TextUtils.isEmpty(url)) return;
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.contains(url)) {
                    url = key;
                    break;
                }
            }
            CALL_MAP.remove(url);
        }
    }
    
    
    public static final class Builder {
        private String mBaseUrl = "";
        private String mUrl;
        private String mTag;
        private Map<String, String> mParams = new HashMap<>();
        
        /*返回数据的类型,默认是string类型*/
        @DataType.Type
        private int mDataType = DataType.STRING;
        
        /*解析类*/
        private Class mClass;
        
        public Builder() {
            
        }
        
        /**
         * 请求地址的baseUrl，最后会被赋值给HttpClient的静态变量BASE_URL；
         *
         * @param url 请求地址的baseUrl
         */
        public Builder setBaseUrl(String url) {
            this.mBaseUrl = url;
            return this;
        }
        
        /**
         * 除baseUrl以外的部分，
         * 例如："mobile/login"
         *
         * @param url path路径
         */
        public Builder setUrl(String url) {
            this.mUrl = url;
            return this;
        }
        
        /**
         * 给当前网络请求添加标签，用于取消这个网络请求
         *
         * @param mTag 标签
         */
        public Builder setTag(String mTag) {
            this.mTag = mTag;
            return this;
        }
        
        /**
         * 添加请求参数
         *
         * @param key   键
         * @param value 值
         */
        public Builder setParams(String key, String value) {
            this.mParams.put(key, value);
            return this;
        }
        
        /**
         * 响应体类型设置,如果要响应体类型为STRING，请不要使用这个方法
         *
         * @param dataType 响应体类型，分别:STRING，JSON_OBJECT,JSON_ARRAY,XML
         * @param clazz    指定的解析类
         * @param <T>      解析类
         */
        public <T> Builder setDataType(@DataType.Type int dataType, Class<T> clazz) {
            this.mDataType = dataType;
            this.mClass = clazz;
            return this;
        }
        
        
        public HttpClient build() {
            HttpClient httpClient = HttpClient.getInstance();
            if (!TextUtils.isEmpty(mBaseUrl)) {
                httpClient.refreshRetrofit(mBaseUrl);
                mCurrentBaseUrl = mBaseUrl;
            } else if (httpClient.getRetrofit() == null || !mCurrentBaseUrl.equals(BASE_URL)) {
                //初始化
                httpClient.refreshRetrofit(BASE_URL);
                mCurrentBaseUrl = BASE_URL;
            }
            httpClient.setBuilder(this);
            return httpClient;
        }
    }
    
    /**
     * 数据解析方法
     *
     * @param data                  要解析的数据
     * @param clazz                 解析类
     * @param bodyType              解析数据类型
     * @param getDataResultListener 回调方数据接口
     */
    @SuppressWarnings("unchecked")
    private void parseData(String data, Class clazz, @DataType.Type int bodyType, GetDataResultListener getDataResultListener) {
        switch (bodyType) {
            case DataType.STRING:
                getDataResultListener.onSuccess(data);
                break;
            case DataType.JSON_OBJECT:
                getDataResultListener.onSuccess(DataParseUtil.parseObject(data, clazz));
                break;
            case DataType.JSON_ARRAY:
                getDataResultListener.onSuccess(DataParseUtil.parseToArrayList(data, clazz));
                break;
            case DataType.XML:
                getDataResultListener.onSuccess(DataParseUtil.parseXml(data, clazz));
                break;
            default:
                Logger.e("http parse tip:", "if you want return object, please use bodyType() set data type");
                break;
        }
    }
}
