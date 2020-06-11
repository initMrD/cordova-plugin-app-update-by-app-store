"use strict";

var exec = cordova.require("cordova/exec");
var AppUpdateByStore = {
    /**
     * 初始化ocr
     * @param successCallback
     * @param errorCallback
     */
    checkUpdate: function (url, successCallback, errorCallback) {


        if (errorCallback == null) {
            errorCallback = function () {
            };
        }

        if (typeof errorCallback !== "function") {
            console.log("AppUpdateByStore.checkUpdate failure: failure parameter not a function");
            return;
        }

        if (typeof successCallback !== "function") {
            console.log("AppUpdateByStore.checkUpdate failure: success callback parameter must be a function");
            return;
        }

        exec(successCallback, errorCallback, 'AppUpdateByStore', 'checkUpdate', [url]);
    }
};


module.exports = AppUpdateByStore;
