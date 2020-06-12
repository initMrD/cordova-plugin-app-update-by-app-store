#import <Cordova/CDVPlugin.h>

@interface AppUpdateByStore : CDVPlugin
@property(nonatomic,copy) NSString* appId;
- (void)setAppId:(NSString *)appId;
- (NSString*) appId;
- (void)checkUpdate:(CDVInvokedUrlCommand*)command;
@end
