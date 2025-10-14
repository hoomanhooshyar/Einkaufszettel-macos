import SwiftUI
import ComposeApp
import FirebaseCore
import FirebaseAppCheck
@main
struct iOSApp: App {

    init(){
        FirebaseApp.configure()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
