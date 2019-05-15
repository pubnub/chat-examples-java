# PubNub Java Chat

Source files for Java based chat example apps and document code samples live here.

[![Build Status](https://travis-ci.com/pubnub/chat-examples-java.svg?token=33vivoVBsBm3RMEntDqx&branch=master)](https://travis-ci.com/pubnub/chat-examples-java)

## Repository structure

| Directory  | Description |
|:----------:| ----------- |
| `app/examples` | A feature-rich sample app describing best practices for creating a chat app using the PubNub SDK. |
| `app/examples/animal/forest/chat` | Location where the animal chat application is stored. The complete tutorial can be found [here](https://www.pubnub.com/developers/chat-resource-center/docs/getting-started/android/).|
| `app/snippets` | A module containing extensive unit tests, in a form of verified and tested code snippets.<br>They can be found inside [Chat Resource Center](https://www.pubnub.com/developers/chat-resource-center/). |

## Animal Forest Chat Application

## Requirements

* Android 16+
* Android Studio 3.0+
* JDK 8

## Prerequisites

### Sign Up for a PubNub Account

If you don't already have an account, you can create one for free [here](https://dashboard.pubnub.com/).

* Login to your PubNub Account
* Select Your Project > Your Key. Click on Key Info and copy your `Publish Key` and `Subscribe Key`
* Enable the following add-on features on your key: Presence, Storage & Playback, Stream Controller

### Using your PubNub keys

Add your publish and subscribe keys by executing the following commands:

```bash
cd app/
echo PUB_KEY="\"YOUR_PUBNUB_PUB_KEY\"" >> gradle.properties
echo SUB_KEY="\"YOUR_PUBNUB_SUB_KEY\"" >> gradle.properties
echo app/gradle.properties >> ../.gitignore
```

This will also create a `app/gradle.properties` file which is a good place to store confidential information.

## Building the project

1. Clone the repo

1. Open the project by choosing `Open an existing Android Studio project` from the Android Studio Welcome screen

1. Select the root project folder `chat-examples-java/`

1. Choose the `app` module and click `Run`

## Further Information

For more information about this project, or how to create your own chat app using PubNub, please check out our [tutorial](https://www.pubnub.com/developers/chat-resource-center/docs/getting-started/android/).