# chat-examples-java

Source files for Java based chat example apps and document code samples live here.

## Requirements

- Android 16+
- Android Studio 3.0+
- JDK 8

## Repository structure

| Directory  | Description |
|:----------:| ----------- |
| `app/examples` | A feature-rich sample app describing best practices for creating a chat app using the PubNub SDK. |
| `app/snippets` | A module containing extensive unit tests, in a form of verified and tested code snippets.<br>They can be found inside [Chat Resource Center](https://pubnub.github.io/chat-resource-center/). |

## Building the project

- Add your publish and subscribe keys by executing the following commands:

```bash
cd app/
echo PUB_KEY="\"demo\"" >> gradle.properties
echo SUB_KEY="\"demo\"" >> gradle.properties
```

**NOTE**: replace occurences of `demo` with your own publish and subscribe keys.

This will also create a `app/gradle.properties` file which is a good place to store confidential information.

- Open the project by choosing `Open an existing Android Studio project` from the Android Studio Welcome screen: 

![Android Studio Welcome Screen](https://i.ibb.co/r6VpBp0/3.png "Android Studio Welcome Screen")

- Select the root project folder `chat-examples-java/`.

- Wait for gradle to download dependecies and sync the project.

## Build and run the sample app

- Choose `app` and press the green play button

![Sample app](https://i.ibb.co/58H17nv/4.png "Sample app")

## Build and run the snippets module

- Right click on `snippets` and click `Run 'Tests in 'snippets''`

![Snippets](https://i.ibb.co/6N4cw2B/5.png "Snippets")