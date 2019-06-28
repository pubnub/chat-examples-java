# Contributing to the Java Chat Examples

Your contributions to this project are very much welcome! Here are a 
few tips to make things work efficiently.

## General information

All contributions — edits _and_ new content — should be in the form of
pull requests. This keeps everyone from stepping on each others' toes,
and allows us all to discuss the change and make suggestions for 
improvement.

When you create your PR, please tag Ivan as a reviewer.

> **NOTE** The pull request process makes things efficient, and allows 
the whole team to participate. If a pull request doesn’t work for you,
just email Ivan and they will create one for you.

## Coding Standards

You can check your code for issues with

```bash
./gradlew clean check
```

Make sure to resolve all lint warnings before opening a pull request. 
Errors from the tests are _separate_ from problems with your code style.
Refer to [the testing instructions](#testing-snippets) for details on the tests.

## Making a Pull Request

### Before you Start

Please ensure that any work is initially branched off `master`, and 
rebased often.

### After you're Done

Please, make sure to follow these [commit message guidelines](https://github.com/angular/angular.js/blob/master/DEVELOPERS.md#-git-commit-guidelines)
when committing changes as part of a pull request. 

If editing the snippets, make sure to [run the tests](#testing-snippets) before committing.

### Content

#### Snippets

Snippets are organized in the form of _tests_, where each example 
should be tested to work.  
Snippets are used by the Docusaurus `include` plugin, which will render them 
instead of placeholders. Each `include` directive relies on _tag_ names
which should be placed around snippet code:  

```js
// tag::CON-2[]
PNConfiguration pnConfiguration = new PNConfiguration();
pnConfiguration.setSubscribeKey(SUB_KEY);
pnConfiguration.setPublishKey(PUB_KEY);

PubNub pubNub = new PubNub(pnConfiguration);
// end::CON-2[]
```

If code that should not be included in the docs is between the tags, it can be ignored.

```java
// tag::ignore[]
System.out.println("DEBUG");
// end::ignore[]
```

#### Testing Snippets
Before you can run the tests, you'll need a set of PubNub keys for testing.

1. Login to your [admin dashboard](https://admin.pubnub.com) and create a _new_ app.

1. Copy the Publish Key and Subscribe Key into your `gradle.properties` as `PUB_KEY` and `SUB_KEY`.

```
PUB_KEY="pub-c-XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"
SUB_KEY="sub-c-XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"
```

1. Enable Storage & Playback, Stream Controller, Presence, and Access Manager, then save the changes.

```bash
./gradlew clean check
```

This will run the tests for the snippets with Gradle. 
When testing locally, tests for push notifications _will_ be skipped so 
that you do not have to provide push certificates.
These tests will run normally on Travis when you make your pull request.

> **NOTE** Sometimes one of the tests will fail without reason. This can be
resolved by rerunning the tests.