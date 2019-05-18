# PubNub Java Chat

[![Build Status](https://travis-ci.com/pubnub/chat-examples-java.svg?token=33vivoVBsBm3RMEntDqx&branch=master)](https://travis-ci.com/pubnub/chat-examples-java)

This repository contains sample code from the [Chat Resource Center](https://www.pubnub.com/developers/chat-resource-center/).

For more information about this project, or how to create your own chat app using PubNub, please check out our [tutorial](https://www.pubnub.com/developers/chat-resource-center/docs/getting-started/android/).

## Repository structure

| Directory  | Description |
|:----------:| ----------- |
| `app/examples` | Sample applications which show how to implement chat functionality using the PubNub SDK. |
| `app/examples/animal/forest/chat` | Source files for the Animal Forest Chat application. The complete tutorial can be found [here](https://www.pubnub.com/developers/chat-resource-center/docs/getting-started/android/). |
| `app/snippets` | Verified and tested code snippets used in documentation.<br>Snippets from `chatresourcecenter` are used in the [Chat Resource Center](https://www.pubnub.com/developers/chat-resource-center/). |

## Animal Forest Chat Application

## Prerequisites

* Android 16+
* Android Studio 3.0+
* JDK 8

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
echo PUB_KEY="\"YOUR_PUBNUB_PUB_KEY\"" >> gradle.properties
echo SUB_KEY="\"YOUR_PUBNUB_SUB_KEY\"" >> gradle.properties
echo app/gradle.properties >> ../.gitignore
```

This will also create a `app/gradle.properties` file, which is a good place to store confidential information.

## Building and running the app

1. Launch Android Studio. On the welcome screen, choose Open an existing Android Studio project.

1. Select the root project folder, `chat-examples-java/`, and click Open.

1. Wait for Gradle to download dependencies and sync the project. This could take several minutes.

1. Choose Run > Run 'app', and pick the device on which you want to run the app.

    (If necessary, create a new virtual device on which to run the app.)

    ![Animal Forest Android Chat View](https://www.pubnub.com/developers/chat-resource-center/img/android/intro_1.png)
