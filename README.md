# PubNub Java Chat

Source files for Java based chat example apps and document code samples live here.

[![Build Status](https://travis-ci.com/pubnub/chat-examples-java.svg?token=33vivoVBsBm3RMEntDqx&branch=master)](https://travis-ci.com/pubnub/chat-examples-java)

## Repository structure

| Directory  | Description |
|:----------:| ----------- |
| `app/examples` | A feature-rich sample app describing best practices for creating a chat app using the PubNub SDK. |
| `app/examples/animal/forest/chat` | Location where the animal chat application is stored. The complete tutorial can be found [here](https://www.pubnub.com/developers/chat-resource-center/docs/getting-started/android/)|
| `app/snippets` | A module containing extensive unit tests, in a form of verified and tested code snippets.<br>They can be found inside [Chat Resource Center](https://www.pubnub.com/developers/chat-resource-center/). |

## Animal Forest Chat Application

## Requirements

* Android 16+
* Android Studio 3.0+
* JDK 8

## Building the project

### Sign Up for a PubNub Account

If you don't already have an account, you can create one for free [here](https://dashboard.pubnub.com/).

1. Sign in to your PubNub [Admin Dashboard](https://dashboard.pubnub.com/), click Create New App, and give your app a name.

1. Select your new app, then click its keyset. Copy the Publish and Subscribe keys. You'll need these keys to include in this project.

1. Scroll down on the Key Options page and enable the following add-on features: [Presence](https://www.pubnub.com/products/presence/), [Storage & Playback](https://www.pubnub.com/products/realtime-messaging/), and [Stream Controller](https://www.pubnub.com/products/realtime-messaging/).

1. Click Save Changes, and you're done!

### Using your PubNub keys

Execute the following commands to add your publish and subscribe keys to your local copy of the app:

```bash
cd app/
echo PUB_KEY="\"YOUR-PUBNUB-PUB-KEY-HERE\"" >> gradle.properties
echo SUB_KEY="\"YOUR-PUBNUB-SUB-KEY-HERE\"" >> gradle.properties
```

This will also create a `app/gradle.properties` file, which is a good place to store confidential information.

## Building and running the sample app

1. Launch Android Studio. On the welcome screen, choose Open an existing Android Studio project.

1. Select the root project folder, `chat-examples-java/`, and click Open.

1. Wait for Gradle to download dependencies and sync the project. This could take several minutes.

1. Choose Run > Run 'app', and pick the device on which you want to run the app.

    (If necessary, create a new virtual device on which to run the app.)

    ![Animal Forest Android Chat View](https://www.pubnub.com/developers/chat-resource-center/img/android/intro_1.png)
