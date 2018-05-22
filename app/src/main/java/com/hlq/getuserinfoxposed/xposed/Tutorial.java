package com.hlq.getuserinfoxposed.xposed;

import android.util.Log;
import android.widget.EditText;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * author : HLQ
 * e-mail : 925954424@qq.com
 * time   : 2018/05/21
 * desc   : Xposed 劫持
 * version: 1.0
 */
public class Tutorial implements IXposedHookLoadPackage {

    private static final String TAG = "HLQ_Struggle";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 1.过滤，Hook指定包名下内容
        if (!lpparam.packageName.equals("com.hlq.userlogindemo")) {
            return; // 不是我们想要的，直接让其滚犊子
        }
        // 2.日志输出，表明当前模块已被装载，可进行下面Hook操作
        XposedBridge.log("Xposed模块已初始化，准备劫持");
        Log.e(TAG, "Xposed模块已初始化，准备劫持");
        // 3. 准备Hook用户名以及密码
        XposedHelpers.findAndHookMethod(
                "com.hlq.userlogindemo.MainActivity", lpparam.classLoader,
                "checkInfo", String.class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        // hook 到方法，准备截取用户名以及密码
                        // 加载需要hook实例类
                        Class c = lpparam.classLoader.loadClass("com.hlq.userlogindemo.MainActivity");
                        // 获取类字段 也可以理解为获取引用实例
                        Field userNameField = c.getDeclaredField("mUserNameID");
                        Field userPwdField = c.getDeclaredField("mUserPwdID");
                        // 简单可以理解为即使你是私有的，我照挖不误
                        userNameField.setAccessible(true);
                        userPwdField.setAccessible(true);
                        // 获取到输入框实例
                        EditText tvUserName = (EditText) userNameField.get(param.thisObject);
                        EditText tvUserPwd = (EditText) userPwdField.get(param.thisObject);
                        // 获取用户输入用户名以及密码
                        String userName = tvUserName.getText().toString();
                        String userPwd = tvUserPwd.getText().toString();
                        // 日志输出
                        XposedBridge.log("Xposed模块已劫持，用户名：" + userName + " 密码：" + userPwd);
                        Log.e(TAG, "Xposed模块已劫持，用户名：" + userName + " 密码：" + userPwd);
                    }
                }
        );
    }
}
