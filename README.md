# cordova-plugin-app-update-by-app-store
跳转app商店升级应用

## 1、安装
```
npm i @initmrd/app-update  
```
```
ionic cordova plugin add https://github.com/initMrD/cordova-plugin-app-update-by-app-store.git
```
## 2、将配置文件放到服务器静态资源目录下

### 各参数内容说明

- version 版本号 低于该版本提示升级
- forceVersion 强制升级版本号 低于该版本强制升级
- description 升级提示
- store 要搜索的商店列表的包名（Android only）
- downloadWeb 如果用户手机里未安装应用上架的应用商城需要跳转的页面（Android only）

### 配置文件内容

```json
{
  "ios": {
    "version": "0.0.2",
    "forceVersion": "0.0.1",
    "description": "有新的版本了，赶快来下载吧！"
  },
  "android": {
    "version": "0.0.2",
    "forceVersion": "0.0.1",
    "description": "有新的版本了，赶快来下载吧！",
    "downloadWeb": "http://192.168.0.104",
    "store": [
      "com.huawei.appmarket"
    ]
  }
}
```
## 3、项目中调用
app.module.ts
```
...
import {AppUpdateByStore} from '@initmrd/app-update';
...

@NgModule({
    ...
    providers: [
        ...
        AppUpdateByStore,
        ...
    ],
    ...
})
export class AppModule {
}
```

app.component.ts
```
...
import {AppUpdateByStore} from '@initmrd/app-update';
...

@Component({
    selector: 'app-root',
    templateUrl: 'app.component.html',
    styleUrls: ['app.component.scss']
})
export class AppComponent {
    constructor(
        ...
        private appUpdateByStore: AppUpdateByStore,
        ...
    ) {
        this.initializeApp();
    }

    initializeApp() {
        this.platform.ready().then(() => {
            ...
            this.appUpdateByStore.checkUpdate('http://192.168.0.104/update.json', () => {
                alert('success');
            }, () => {
                alert('error');
            });
            ...
        });
    }
}
```

## 代码流程
```flow
st=>start: 开始
e=>end: 结束
dismiss=>end: 隐藏弹窗
update=>operation: 判断是否需要升级
force=>operation: 判断是否需要强制升级
store=>operation: 判断是否用户是否已安装上架商城
toStore=>operation: 跳转商城
toDownload=>operation: 跳转下载页面
alertForce=>operation: 弹窗,无取消
alert=>operation: 弹窗,有取消
userOp=>operation: 用户判断是否升级
userUpdateOp=>operation: 用户点击升级
updateCond=>condition: version > localVersion
forceCond=>condition: forceVersion > localVersion
userCond=>condition: yes or no
storeCond=>condition: yes or no

st->update->updateCond
updateCond(yes,right)->force
updateCond(no)->e
force->forceCond
forceCond(yes,right)->alertForce
alertForce->userUpdateOp
userUpdateOp->store
forceCond(no)->alert
alert->userOp
userOp->userCond
userCond(no)->dismiss
userCond(yes,right)->store
store->storeCond
storeCond(yes)->toStore
storeCond(no)->toDownload

```
