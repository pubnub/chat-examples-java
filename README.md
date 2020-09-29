# PubNub Java Chat

[![Build Status](https://travis-ci.com/pubnub/chat-examples-java.svg?token=33vivoVBsBm3RMEntDqx&branch=master)](https://travis-ci.com/pubnub/chat-examples-java)

This repository contains sample code for a chat application built using the PubNub Android SDK.

![Animal Forest Android Chat View](https://www.pubnub.com/docs/chat/img/android/intro_1.png) | ![Animal Forest Android Channel Details](https://www.pubnub.com/docs/chat/img/android/intro_2.png)
--|---|

## Repository structure

| Directory  | Description |
|:----------:| ----------- |
| `app/examples/animal/forest/chat` | Source files for the Animal Forest Chat application.|

## Animal Forest Chat Application

## Prerequisites

* Android Studio 3.0+
* PubNub SDK
* A PubNub Chat account

### Create a PubNub Account

To run this application you must obtain publish and subscribe keys from the [Admin Portal](https://dashboard.pubnub.com/). If you don't already have an account, you can create one for free.

1. Sign in to the PubNub Admin Portal.

1. Click Create New App. Give your app a name, and select Chat App as the app type. Select a region to store your user data (for example, Portland). Click Create.

1. Click your new app to open its settings, then click its keyset.

1. Locate the Publish and Subscribe keys. You'll need these keys to include in this project.

### Using your PubNub keys

Execute the following commands to add your publish and subscribe keys to your local copy of the app.

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

1. Choose Run > Run 'app', and pick the device on which you want to run the app. If necessary, create a new virtual device on which to run the app.

## Further Information

For more information about this project, or how to create your own chat app using PubNub, please check out the [PubNub Chat Documentation](https://www.pubnub.com/docs/chat/overview).
