#import "AppUpdateByStore.h"
#import <Cordova/CDVPluginResult.h>


@implementation AppUpdateByStore

// 检查升级入口
- (void)checkUpdate:(CDVInvokedUrlCommand*)command{
    NSString *url = command.arguments[0];
    [self getNetJson:url];
}

// 从服务器获取json
- (void)getNetJson:(NSString*)url_str{
    //请求路径
    NSURL *url = [NSURL URLWithString:url_str];
    //创建请求对象 默认get
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
    //获得会话对象
    NSURLSession *session = [NSURLSession sharedSession];
    NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (error == nil) {
            //解析服务器返回的数据
            //说明：（此处返回的数据是JSON格式的，因此使用NSJSONSerialization进行反序列化处理）
            NSDictionary *dict = [[NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil] objectForKey:@"ios"];
            [self matchVersion:dict];

        }

    }];
    [dataTask resume];
}
// 对比当前版本号
- (void)matchVersion:(NSDictionary*)dict{
    NSString *version = [dict objectForKey:@"version"];
    NSString *forceVersion = [dict objectForKey:@"forceVersion"];
    NSString *desc = [dict objectForKey:@"description"];
    self.appId = [dict objectForKey:@"appId"];
    if([self compareVesionWithServerVersion:version]){
        [self showAlert:desc force:[self compareVesionWithServerVersion:forceVersion]];
    }
}

// 弹出提示
- (void)showAlert:(NSString*)desc force:(BOOL)force{
    UIAlertController* alert = [UIAlertController
                                alertControllerWithTitle:@"版本更新"
                                message:desc
                                preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction* updateAction = [UIAlertAction actionWithTitle:@"进行更新" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {
        //响应事件
        [self update];
        if(force){
            [self performSelector:@selector(notExistCall)];
            abort();
        }
    }];
    UIAlertAction* cancelAction = [UIAlertAction actionWithTitle:@"以后再说" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {
        //响应事件
        NSLog(@"action = %@", action);
    }];
    if(!force){
        [alert addAction:cancelAction];
    }
    [alert addAction:updateAction];

    dispatch_sync(dispatch_get_main_queue(), ^{
        [self.viewController presentViewController:alert animated:YES completion:nil];
    });

}
// 更新
- (void)update{
    if(self.appId!=nil&&![self.appId isEqual:@""]){
        NSString *urlStr = [NSString stringWithFormat:@"itms-apps://itunes.apple.com/cn/app/id%@?mt=8",self.appId];
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:urlStr]];
    }
}

// 对比服务器版本
- (BOOL)compareVesionWithServerVersion:(NSString *)version{
    //服务器返回版本
    NSArray *versionArray = [version componentsSeparatedByString:@"."];
    //当前版本
    NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
    NSString *appVersion = [infoDictionary objectForKey:@"CFBundleShortVersionString"];
    NSArray *currentVesionArray = [appVersion componentsSeparatedByString:@"."];

    NSInteger a = (versionArray.count> currentVesionArray.count)?currentVesionArray.count : versionArray.count;
    for (int i = 0; i< a; i++) {
        NSInteger b = [[currentVesionArray objectAtIndex:i] integerValue];
        NSInteger a = [[versionArray objectAtIndex:i] integerValue];

        if (a > b) {
            NSLog(@"有新版本");
            return YES;
        }else if(a < b){
            return NO;
        }
    }
    return NO;
}

@end
