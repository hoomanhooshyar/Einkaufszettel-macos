#import "GoogleSignInBridge.h"
#import <GoogleSignIn/GoogleSignIn.h>

@implementation GoogleSignInBridge

+ (void)signInFrom:(UIViewController *)viewController
        completion:(void (^)(NSString * _Nullable idToken,
                NSString * _Nullable displayName,
                NSString * _Nullable photoURL,
                NSError * _Nullable error))completion {

    // مقدار CLIENT_ID را از GoogleService-Info.plist بگیر
    NSString *clientID = @"1012387304775-6fqg05r0negot1vn49o4p5fe7i0vrj60.apps.googleusercontent.com";
    GIDConfiguration *config = [[GIDConfiguration alloc] initWithClientID:clientID];

    GIDSignIn *signIn = [GIDSignIn sharedInstance];
    signIn.configuration = config;

    [signIn signInWithPresentingViewController:viewController
                                    completion:^(GIDSignInResult * _Nullable result, NSError * _Nullable error) {
                                        if (error) {
                                            completion(nil, nil, nil, error);
                                            return;
                                        }

                                        GIDGoogleUser *user = result.user;
                                        NSString *idToken = user.idToken.tokenString;
                                        NSString *displayName = user.profile.name;
                                        NSURL *photo = [user.profile imageURLWithDimension:200];
                                        completion(idToken, displayName, photo.absoluteString, nil);
                                    }];
}

@end