import SwiftUI
import ComposeApp
import FirebaseCore
import FirebaseAppCheck
@main
struct iOSApp: App {
    init(){
        #if DEBUG
        AppCheck.setAppCheckProviderFactory(AppCheckDebugProviderFactory())
        #endif
        FirebaseApp.configure()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
                .task {
                    let ping = FirebasePing()
                    do{
                        let message = try await ping.run()
                        print(message)
                    }catch{
                        print("Firebase health FAILED: \(error)")
                    }
                }
        }
    }
}
