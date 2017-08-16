package hu.yahui.module_common.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.Stack;

import hu.yahui.module_common.utils.Utils;

/**
 * Created by User on 2017/8/10.
 */

public class BaseApplication extends Application {
    private static BaseApplication mApplication;
    private Stack<Activity> mActivities;
    
    public static BaseApplication getInstance() {
        return mApplication;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        mApplication = this;
        if (Utils.isAppDebug()) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this);
    }
    
    /**
     * 添加指定Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (mActivities == null) {
            mActivities = new Stack<>();
        }
        mActivities.add(activity);
    }
    
    /**
     * 获取当前Activity
     */
    public Activity getCurrentActivity() {
        return mActivities.lastElement();
    }
    
    /**
     * 结束当前Activity
     */
    public void finishCurrentActivity() {
        Activity activity = mActivities.lastElement();
    }
    
    /**
     * 结束指定Class的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            mActivities.remove(activity);
            activity.finish();
            activity = null;
        }
    }
    
    /**
     * 结束指定Class的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : mActivities) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                return;
            }
        }
    }
    
    /**
     * 结束全部activity
     */
    public void finishAllActivity() {
        for (Activity activity : mActivities) {
            if (activity != null) activity.finish();
        }
        mActivities.clear();
    }
    
    
    /**
     * 退出应用程序
     */
    public void exitApp(Context context) {
        try {
            finishAllActivity();
            //杀死后台进程需要在AndroidManifest中声明android.permission.KILL_BACKGROUND_PROCESSES；
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses(context.getPackageName());
            //System.exit(0);
        } catch (Exception e) {
            Log.e("ActivityManager", "app exit" + e.getMessage());
        }
    }
}
