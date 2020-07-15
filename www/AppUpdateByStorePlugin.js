var exec = cordova.require("cordova/exec");
var AppUpdateByStore = {
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
    },
    getUpdateVersion: function (url, successCallback, errorCallback) {
        if (errorCallback == null) {
            errorCallback = function () {
            };
        }

        if (typeof errorCallback !== "function") {
            console.log("AppUpdateByStore.getUpdateVersion failure: failure parameter not a function");
            return;
        }

        if (typeof successCallback !== "function") {
            console.log("AppUpdateByStore.getUpdateVersion failure: success callback parameter must be a function");
            return;
        }

        exec(successCallback, errorCallback, 'AppUpdateByStore', 'getUpdateVersion', [url]);
    }
};


module.exports = AppUpdateByStore;
