package com.tencent.bugly.bugly_crash;

import android.annotation.SuppressLint;
import android.content.Context;

import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Map;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * Description:bugly oa futter plugin
 *
 * @author rockypzhang
 * @since 2019/5/28
 */
public class BuglyCrashPlugin implements MethodCallHandler, FlutterPlugin {
    /**
     * Plugin registration.
     */
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private MethodChannel mMethodChannel;

    /**
     * flutter层调用方法的native层实现
     *
     * @param call   flutter方法回调
     * @param result 返回结果
     */
    @Override
    public void onMethodCall(MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("initCrashReport")) {
            String appId = "";
            boolean isDebug = false;
            if (call.hasArgument("appId")) {
                appId = call.argument("appId");
            }
            if (call.hasArgument("isDebug")) {
                //noinspection ConstantConditions
                isDebug = call.argument("isDebug");
                BuglyCrashPluginLog.isEnable = isDebug;
            }
            CrashReport.initCrashReport(mContext, appId, isDebug);
            BuglyCrashPluginLog.d("onMethodCall initCrashReport");
        } else if (call.method.equals("postException")) {
            String type = "";
            String error = "";
            String stackTrace = "";
            Map<String, String> extraInfo = null;
            if (call.hasArgument("type")) {
                type = call.argument("type");
            }
            if (call.hasArgument("error")) {
                error = call.argument("error");
            }
            if (call.hasArgument("stackTrace")) {
                stackTrace = call.argument("stackTrace");
            }
            if (call.hasArgument("extraInfo")) {
                extraInfo = call.argument("extraInfo");
            }
            BuglyCrashPluginLog.d("type:" + type + "error:" + error + " stackTrace:" + stackTrace
                    + "extraInfo:" + extraInfo);
            int category = 8;
            CrashReport.postException(category, type, error, stackTrace, extraInfo);
        } else if (call.method.equals("setAppChannel")) {
            String appChannel = "";
            if (call.hasArgument("appChannel")) {
                appChannel = call.argument("appChannel");
            }
            CrashReport.setAppChannel(mContext, appChannel);
        } else if (call.method.equals("setAppPackage")) {
            String appPackage = "";
            if (call.hasArgument("appPackage")) {
                appPackage = call.argument("appPackage");
            }
            CrashReport.setAppPackage(mContext, appPackage);
        } else if (call.method.equals("setAppVersion")) {
            String appVersion = "";
            if (call.hasArgument("appVersion")) {
                appVersion = call.argument("appVersion");
            }
            CrashReport.setAppVersion(mContext, appVersion);
            BuglyCrashPluginLog.d("mContext:" + mContext + " appVersion:" + appVersion);
        } else if (call.method.equals("setUserSceneTag")) {
            int userSceneTag = 0;
            if (call.hasArgument("userSceneTag")) {
                //noinspection ConstantConditions
                userSceneTag = call.argument("userSceneTag");
            }
            CrashReport.setUserSceneTag(mContext, userSceneTag);
            BuglyCrashPluginLog.d("mContext:" + mContext + " appVersion:" + userSceneTag);
        } else if (call.method.equals("setUserId")) {
            int userSceneTag = 0;
            String userId = "";
            if (call.hasArgument("userId")) {
                userId = call.argument("userId");
            }
            CrashReport.setUserId(mContext, userId);
            BuglyCrashPluginLog.d("mContext:" + mContext + " appVersion:" + userSceneTag);
        } else if (call.method.equals("putUserData")) {
            String userKey = "";
            String userValue = "";
            if (call.hasArgument("userKey")) {
                userKey = call.argument("userKey");
            }
            if (call.hasArgument("userValue")) {
                userValue = call.argument("userValue");
            }
            CrashReport.putUserData(mContext, userKey, userValue);
            BuglyCrashPluginLog.d("userKey:" + userKey + " userValue:" + userValue);
        } else if (call.method.contains("log")) {
            buglyLog(call);
        } else if (call.method.contains("setServerUrl")) {
            String url = "";
            if (call.hasArgument("url")) {
                url = call.argument("url");
                CrashReport.setServerUrl(url);
            }
            BuglyCrashPluginLog.d("url:" + url);
        } else if (call.method.contains("setDeviceId")) {
            String deviceId;
            if (call.hasArgument("deviceId")) {
                deviceId = call.argument("deviceId");
                CrashReport.setDeviceId(mContext, deviceId);
            }
        } else {
            result.notImplemented();
        }
    }

    /**
     * 打印上报用户自定义日志
     *
     * @param call    flutter方法回调
     */
    private void buglyLog(MethodCall call) {
        String tag = "";
        String content = "";
        if (call.hasArgument("tag")) {
            tag = call.argument("tag");
        }
        if (call.hasArgument("content")) {
            content = call.argument("content");
        }
        switch (call.method) {
            case "logd":
                BuglyLog.d(tag, content);
                break;
            case "logi":
                BuglyLog.i(tag, content);
                break;
            case "logv":
                BuglyLog.v(tag, content);
                break;
            case "logw":
                BuglyLog.w(tag, content);
                break;
            case "loge":
                BuglyLog.e(tag, content);
                break;
        }
        BuglyCrashPluginLog.d("tag:" + tag + " content:" + content);
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        mContext = binding.getApplicationContext();
        mMethodChannel = new MethodChannel(binding.getBinaryMessenger(), "bugly");
        mMethodChannel.setMethodCallHandler(new BuglyCrashPlugin());
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        mMethodChannel.setMethodCallHandler(null);
    }
}
