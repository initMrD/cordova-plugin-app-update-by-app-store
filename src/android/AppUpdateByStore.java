package com.initmrd.cordova;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;

import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppUpdateByStore extends CordovaPlugin {
    //回调
    private CallbackContext mCallback;
    //Tag
    private static final String TAG = "AppUpdateByStorePlugins";
    //页面Activity
    private Activity mActivity = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        mActivity = cordova.getActivity();
        if (action.equals("checkUpdate")) {
            checkUpdate(args, callbackContext);
            return true;
        }
        return false;
    }

    public void checkUpdate(JSONArray data, CallbackContext callbackContext) {
        Log.d(TAG, data.toString());
    }
}

