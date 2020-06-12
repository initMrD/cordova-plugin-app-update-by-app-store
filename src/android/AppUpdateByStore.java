package com.initmrd.cordova;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.ContextThemeWrapper;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class AppUpdateByStore extends CordovaPlugin {
    //回调
    private CallbackContext mCallback;
    //Tag
    private static final String TAG = "AppUpdateByStorePlugins";
    //页面Activity
    private Activity mActivity = null;

    // 下载url
    private String downloadUrl = "";

    // 商店列表
    private JSONArray storeList = null;

    /**
     * 入口
     *
     * @param action          The action to execute.
     * @param args            The exec() arguments.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return
     * @throws JSONException
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        mActivity = cordova.getActivity();
        if (action.equals("checkUpdate")) {
            checkUpdate(args, callbackContext);
            return true;
        }
        return false;
    }

    /**
     * 检查更新
     *
     * @param data
     * @param callbackContext
     */
    public void checkUpdate(JSONArray data, CallbackContext callbackContext) {
        try {
            String path = data.getString(0);//更新检查文件地址
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // 从服务器获得一个输入流
                InputStream is = conn.getInputStream();
                check(is);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查版本
     *
     * @param is
     */
    private void check(InputStream is) {
        // 解析json
        String iss = is2String(is);
        // 需要升级
        boolean update = updateApp(getVersionName(mActivity), parseJson(iss, "version"));
        // 强制升级
        boolean forceUpdate = updateApp(getVersionName(mActivity), parseJson(iss, "forceVersion"));
        // 升级描述
        String description = parseJson(iss, "description");
        // APK地址
        downloadUrl = parseJson(iss, "downloadWeb");
        storeList = parseJsonArray(iss, "store");
        // 需要升级则弹出提示
        if (update) {
            showDialog(forceUpdate, description);
        }
    }

    /**
     * 流到字符串
     *
     * @param inputStream
     * @return
     */
    private String is2String(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try {

            String line;

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str = sb.toString();
        return str;
    }

    /**
     * 解析json
     *
     * @param s
     * @param name
     * @return
     */
    private String parseJson(String s, String name) {
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject jsonData = jsonObject.getJSONObject("android");
            result = jsonData.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private JSONArray parseJsonArray(String s, String name) {
        JSONArray result = null;
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject jsonData = jsonObject.getJSONObject("android");
            result = jsonData.getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取本地版本号
     *
     * @param context
     * @return
     */
    public String getVersionName(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 判断是否升级APP
     *
     * @param localVersion
     * @param newVersion
     * @return
     */
    public boolean updateApp(String localVersion, String newVersion) {
        String[] localVersionArray = localVersion.split("\\.");
        String[] newVersionArray = newVersion.split("\\.");
        if (localVersionArray.length < newVersionArray.length) {
            int cha = newVersionArray.length - localVersionArray.length;
            for (int i = 0; i < cha; i++) {
                localVersion = localVersion + ".0";
            }
            localVersionArray = localVersion.split("\\.");
        }
        try {
            for (int i = 0; i < newVersionArray.length; i++) {
                int temp = Integer.parseInt(newVersionArray[i]);
                int compar = Integer.parseInt(localVersionArray[i]);
                if (temp > compar) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 弹窗
     */
    public void showDialog(boolean isForce, String desc) {
        AlertDialog.Builder ab = new AlertDialog.Builder(new ContextThemeWrapper(mActivity, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert));
        ab.setTitle("升级");
        ab.setMessage(desc);
        ab.setPositiveButton("升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 点击升级之后弹窗不关闭
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                toStore();
            }
        });
        if (!isForce) {
            ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 点击升级再点取消，则可以关闭
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
        }
        AlertDialog updateDialog = ab.create();
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setCancelable(false);
        updateDialog.show();
    }

    private void toStore() {
        String appPkg = mActivity.getPackageName();

        if (TextUtils.isEmpty(appPkg)) {
            toDownload();
            return;

        }
        for (int i = 0; i < storeList.length(); i++) {
            try {
                String marketPkg = storeList.getString(i);
                Uri uri = Uri.parse("market://details?id=" + appPkg);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                if (!TextUtils.isEmpty(marketPkg)) {

                    intent.setPackage(marketPkg);

                }

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                mActivity.startActivity(intent);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        toDownload();


    }

    // 前往页面下载
    private void toDownload() {
        if (!TextUtils.isEmpty(downloadUrl)) {
            Uri uri = Uri.parse(downloadUrl);

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            mActivity.startActivity(intent);
        }

    }

}

