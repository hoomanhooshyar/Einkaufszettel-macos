#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface GoogleSignInBridge : NSObject
+ (void)signInFrom:(UIViewController *)viewController
        completion:(void (^)(NSString * _Nullable idToken,
        NSString * _Nullable displayName,
NSString * _Nullable photoURL,
        NSError * _Nullable error))completion;
@end