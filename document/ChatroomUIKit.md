# ChatroomUIKit

*English | [中文](ChatroomUIKit_zh.md)*

# [Sample Demo](https://github.com/apex-wang/ChatroomUIKit#sample-demo)

In this project, there is a best practice demonstration project in the `Example` folder for you to build your own business capabilities.

To experience functions of the ChatroomUIKit, you can scan the following QR code to try a demo.

[![SampleDemo](https://github.com/apex-wang/ChatroomUIKit/blob/main/image/demo.png)](https://github.com/apex-wang/ChatroomUIKit/blob/main/image/demo.png).

# [Chatroom UIKit Guide](https://github.com/apex-wang/ChatroomUIKit#chatroom-uikit-guide)

## [Introduction](https://github.com/apex-wang/ChatroomUIKit#introduction)

This guide presents an overview and usage examples of the ChatroomUIKit framework in Android development, as well as describes various components and features of this UIKit, enabling developers to have a good understanding of the UIKit and make effective use of it.

## [Table of Contents](https://github.com/apex-wang/ChatroomUIKit#table-of-contents)

- [Requirements](https://github.com/apex-wang/ChatroomUIKit#requirements)
- [Installation](https://github.com/apex-wang/ChatroomUIKit#installation)
- [Structure](https://github.com/apex-wang/ChatroomUIKit#structure)
- [QuickStart](https://github.com/apex-wang/ChatroomUIKit#quickStart)
- [AdvancedUsage](https://github.com/apex-wang/ChatroomUIKit#advancedusage)
- [CustomTheme](https://github.com/apex-wang/ChatroomUIKit#customTheme)
- [BusinessFlowchart](https://github.com/apex-wang/ChatroomUIKit#businessflowchart)
- [ApiSequenceDiagram](https://github.com/apex-wang/ChatroomUIKit#apisequencediagram)
- [DesignGuidelines](https://github.com/apex-wang/ChatroomUIKit#designguidelines)
- [Contributing](https://github.com/apex-wang/ChatroomUIKit#contributing)
- [License](https://github.com/apex-wang/ChatroomUIKit#license)

# [Requirements](https://github.com/apex-wang/ChatroomUIKit#requirements)

- Jetpack Compose The minimum support for Android API 21, which is version 5.0
- Android Studio Arctic Fox (2020.3.1) or Higher version
- Use kotlin language
- JDK version 1.8 and above
- Gradle version 7.0.0 and above.

# [Installation](https://github.com/apex-wang/ChatroomUIKit#installation)

You can use build.gradle to rely on the ChatroomUIKit library as a dependency for app projects.

## [Local_module_dependencies](https://github.com/apex-wang/ChatroomUIKit#Local_module_dependencies)

1. Open your project in Android Studio.

2. Choose **File** > **import Module**.

3. Search for **ChatroomUIKit** and select it.

## [Build.gradle](https://github.com/apex-wang/ChatroomUIKit#Build.gradle)

```
implementation 'ChatroomUIKit'
```

# [Structure](https://github.com/apex-wang/ChatroomUIKit#structure)

### [ChatroomUIKit Basic Components](https://github.com/apex-wang/ChatroomUIKit#chatroomuikit-basic-components)

## 目录结构
```
┌─ Example                        // SampleDemo directory
│  ├─ ChatroomListActivity              // Mainly providing room list Activity
│  ├─ ChatroomActivity                  // display ChatroomUIKit chatroom Activity
│  ├─ compose                           // SampleDemo 
│  ├─ http                              // Encapsulated network requests for interaction with app services
│  └─ SplashActivity                    // Program Launch Page
├─ ChatroomService                // ChatroomUIKit Protocol module
│  ├─ model                              // The entity objects used by ChatroomUIKit (user, room information, configuration information)
│  ├─ service                            // The protocols and protocol implementations used by ChatroomUIKit (room protocol, user protocol, gift protocol)
│  │    └─ Protocol                        
│  │         ├─ GiftService              // Gift sending and receiving channel.
│  │         ├─ UserService              // Component for user login and user attribute update.
│  │         └─ ChatroomService          // Component for implementing the protocol for chat room management, including joining and leaving the chat room and sending and receiving messages.
│  └─ ChatroomUIKitClient                // ChatroomUIKit initialization class.
└─ ChatroomUIKit            
       ├─ compose                        // UI Compose(Bottom input box, message list, gift list, bottom drawer)
       ├─ theme                          // Resource files provide properties such as colors, fonts, themes, gradients, and sizes required for the project
       ├─ viewModel                      // data processing
       ├─ widget                         // input widget
       └─ ui                             // search activity
```
# [QuickStart](https://github.com/apex-wang/ChatroomUIKit#quickstart)

This guide provides several usage examples for different ChatroomUIKit components. Refer to the `Examples` folder for detailed code snippets and projects showing various use cases.

Please refer to the following steps to run the Android platform application in Android Studio

* First download the demo to the local location
* Then configure the CHATROOM_APP_KEY and REQUEST_HOST in the local.properties folder in the root directory
* Run demo

### [Step 1: Initialize ChatroomUIKit](https://github.com/apex-wang/ChatroomUIKit#step-1-initialize-chatroomuikit)

```kotlin
class ChatroomApplication : Application() {
    override fun onCreate() {
    
        val chatroomUIKitOptions = ChatroomUIKitOptions(
            uiOptions = UiOptions(
                targetLanguageList = listOf(GlobalConfig.targetLanguage.code),
                useGiftsInList = false,
            )
        )
        
        ChatroomUIKitClient.getInstance().setUp(
            applicationContext = this,
            options = chatroomUIKitOptions,
            appKey = BuildConfig.CHATROOM_APP_KEY
        )
    }
}
```

### [Step 2: Login](https://github.com/apex-wang/ChatroomUIKit#step-2-login)

```kotlin
// Log in to the ChatroomUIKit with the user information of the current user object that conforms to the `UserInfoProtocol` protocol.
// The token needs to be obtained from your app server. You can also log in with a temporary token generated on the Agora Console.
// To generate a user and a temporary user token on the Agora Console, see https://docs.agora.io/en/agora-chat/get-started/enable?platform=ios#manage-users-and-generate-tokens.
ChatroomUIKitClient.getInstance().login("user id","token")
```

### [Step 3: Create chat room](https://github.com/apex-wang/ChatroomUIKit#step-3-create-chat-room-view)

```kotlin
// 1. Get a chat room list and join a chat room. Alternatively, create a chat room on the Agora Console.
// Choose ProjectManager > Operation Manager > Chat Room and click Create Chat Room and set parameters in the displayed dialog box to create a chat room. Get the chat room ID to pass it in to the following `launchRoomView` method.
// 2. Load ComposeChatroom with setContent in activity. ComposeChatroom is a fully packaged chatroom scenario component that we have packaged. 
// 3. Set the parameters required for ComposeChatroom
// 4. Add users to the chat room on the Console.
// Choose ProjectManager > Operation Manager > Chat Room. Select View Chat Room Members in the Action column of a chat room and add users to the chat room in the displayed dialog box.  
// 5.Load the ComposeChatroom view and pass in the roomId and the UserEntity object of the room owner
class ChatroomActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeChatroom(roomId = roomId,roomOwner = ownerInfo)
        }
    }
}
```

[![CreateChatroom](https://github.com/apex-wang/ChatroomUIKit/blob/main/image/CreateChatroom.png)](https://github.com/apex-wang/ChatroomUIKit/blob/main/image/CreateChatroom.png).

# [AdvancedUsage](https://github.com/apex-wang/ChatroomUIKit#advancedusage)

Here are three examples of advanced usage.

### [1.Initializing the chat room compose](https://github.com/apex-wang/ChatroomUIKit#2initializing-the-chat-room-view)

```kotlin
    val chatroomUIKitOptions = ChatroomUIKitOptions(
            chatOptions = ChatSDKOptions(),
            uiOptions = UiOptions(
                targetLanguageList = listOf(GlobalConfig.targetLanguage.code),
                useGiftsInList = false,
            )
        )

    ChatroomUIKitClient.getInstance().setUp(applicationContext = applicationContext,appKey = "Your AppKey",options = chatroomUIKitOptions)
```

### [2.Login](https://github.com/apex-wang/ChatroomUIKit#1login)

```kotlin
class YourAppUser: UserInfoProtocol {
    var userId: String = "your application user id"
            
    var nickName: String = "you user nick name"
            
    var avatarURL: String = "you user avatar url"
            
    var gender: Int = 1
            
    var identity: String =  "you user level symbol url"
            
}
// Use the user information of the current user object that conforms to the UserInfoProtocol protocol to log in to ChatroomUIKit.
// You need to get a user token from your app server. Alternatively, you can use a temporary token. To generate a temporary toke, visit https://docs.agora.io/en/agora-chat/get-started/enable?platform=ios#generate-a-user-token.
ChatroomUIKitClient.getInstance().login(YourAppUser, token, onSuccess = {}, onError = {code,error ->})
```

### [3.Listening to ChatroomUIKit events and errors](https://github.com/apex-wang/ChatroomUIKit#3listening-to-chatroomuikit-events-and-errors)

You can call the `registerRoomResultListener` method to listen for ChatroomUIKit events and errors.

```kotlin
ChatroomUIKitClient.getInstance().registerRoomResultListener(this)
```

# [CustomTheme](https://github.com/apex-wang/ChatroomUIKit#customTheme)

### [Switch original or custom theme](https://github.com/apex-wang/ChatroomUIKit#3switch-original-or-custom-theme)

- Switch to the light or dark theme that comes with the ChatroomUIKit.

```kotlin
ChatroomUIKitClient.getInstance().setCurrentTheme(isDarkTheme)
```

- ChatroomUIKitTheme ChatroomUIKitTheme provides configurable items,
- and developers can implement custom themes by replacing the corresponding configuration items.
- If not configured, the default theme will be used.

```kotlin
@Composable
fun ChatroomUIKitTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    colors: UIColors = if (!isDarkTheme) UIColors.defaultColors() else UIColors.defaultDarkColors(),
    shapes: UIShapes = UIShapes.defaultShapes(),
    dimens: UIDimens = UIDimens.defaultDimens(),
    typography: UITypography = UITypography.defaultTypography(),
    content: @Composable () -> Unit
)
```

# [BusinessFlowchart](https://github.com/apex-wang/ChatroomUIKit#businessflowchart)

The following figure presents the entire logic of business requests and callbacks.

![Overall flow diagram of business logic](https://github.com/apex-wang/ChatroomUIKit/blob/main/image/BusinessFlowchart.png)

# [ApiSequenceDiagram](https://github.com/apex-wang/ChatroomUIKit#apisequencediagram)

The following figure is the best-practice API calling sequence diagram in the `Example` project.

![APIUML](https://github.com/apex-wang/ChatroomUIKit/blob/main/image/Api.png)

# [DesignGuidelines](https://github.com/apex-wang/ChatroomUIKit#designguidelines)

For any questions about design guidelines and details, you can add comments to the Figma design draft and mention our designer Stevie Jiang.

See the [UI design drawing](https://www.figma.com/file/OX2dUdilAKHahAh9VwX8aI/Streamuikit?node-id=137%3A38589&mode=dev).

See the [UI design guidelines](https://www.figma.com/file/OX2dUdilAKHahAh9VwX8aI/Streamuikit?node-id=137)

# [Contributing](https://github.com/apex-wang/ChatroomUIKit#contributing)

Contributions and feedback are welcome! For any issues or improvement suggestions, you can open an issue or submit a pull request.

## [Author](https://github.com/apex-wang/ChatroomUIKit#author)

apex-wang, [1746807718@qq.com](mailto:1746807718@qq.com)

## [License](https://github.com/apex-wang/ChatroomUIKit#license)

ChatroomUIKit is available under the MIT license. See the LICENSE file for more information.
