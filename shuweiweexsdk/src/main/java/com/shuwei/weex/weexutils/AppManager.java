package com.shuwei.weex.weexutils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Stack;

/**
 */
public class AppManager {

    private Stack<WeakReference<Activity>> activityStack = new Stack<>();
    private static AppManager instance;

    private AppManager() {
    }

    public synchronized static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(WeakReference<Activity> activity) {
        activityStack.add(activity);
    }

    /**
     * 移除Activity到堆栈
     */
    public void removeActivity(WeakReference<Activity> activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        WeakReference<Activity> activity = activityStack.lastElement();
        return activity == null ? null : activity.get();
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        WeakReference<Activity> activity = activityStack.lastElement();
        finishActivity(activity == null ? null : activity.get());
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (WeakReference<Activity> activity : activityStack) {
            if (activity != null && activity.get() != null && activity.get().getClass().equals(cls)) {
                finishActivity(activity.get());
                break;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0; i < activityStack.size(); i++) {

            WeakReference<Activity> activityWeakReference = activityStack.get(i);
            if (null != activityWeakReference) {
                finishActivity(activityWeakReference.get());
            }
        }
        activityStack.clear();
    }

    /**
     * 获取指定的Activity
     *
     * @author kymjs
     */
    public Activity getActivity(Class<?> cls) {
        for (WeakReference<Activity> activity : activityStack) {
            if (activity != null && activity.get() != null && activity.get().getClass().equals(cls)) {
                return activity.get();
            }
        }
        return null;
    }

    public int getStackIndex(WeakReference<Activity> activity) {
        if (activity == null) {
            return 0;
        }
        return activityStack.search(activity);
    }

    public void finishAfterIndexActivity(WeakReference<Activity> activity) {
        finishAfterIndexActivity(getStackIndex(activity));
    }

    public void finishAfterIndexActivity(int index) {
        if (index >= activityStack.size()) {
            return;
        }
        for (int i = index + 1, size = activityStack.size(); i < size; i++) {
            WeakReference<Activity> activityWeakReference = activityStack.get(i);
            if (null != activityWeakReference) {
                finishActivity(activityWeakReference.get());
            }
        }
    }

    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            finishAllActivity();
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}