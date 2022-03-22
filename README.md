# WPay Android Frames SDK

This project contains an Android library that can facilitate
applications using the [Frames SDK](https://github.com/w-pay/sdk-wpay-web-frames)

It is recommended that developers understand the fundamentals of the Frames SDK before using
this library.

| :memo: | The SDK is currently in development. Therefore parts may change. |
|--------|:-----------------------------------------------------------------|

# Usage

This library loads the Frames Javascript SDK into a WebView and provides hooks for an application
to receive messages from the SDK. The Application provides the "host HTML" that the JS SDK uses to
load the web content to allow the user to securely enter information like credit card numbers.
Consequently the Application can tailor the HTML as required, applying styling and adding other
web content.

To use the Frames JS SDK in an Android application, an instance of `FramesView` should be added
to a layout in the Application.

An Application can instruct the JS SDK via the posting of `JavascriptCommand`s, and receive messages
from the SDK via the use of the `FramesWebView.Callback`. This allows the Application to orchestrate
the usage of the SDK with native logic and UI components eg: Buttons.

## Frames SDK Version

The Android SDK comes bundled with the Frames JS SDK so that the Android SDK can use a known
version at runtime. The JS file in the `assets` dir is taken from the `dist` of the Frames SDK
repo and currently targets version `2.1.0`.

## Authentication

The Application currently bears the responsibility for acquiring an access token for the SDK 

## Logging

Logging within the SDK is comprised of two levels.

The first is the interaction between the Application
and the Frames JS SDK/WebView. The `FramesView.Logger` interface allows the Application to provide
a logger to the view.

The second is the logging from within the Frames JS SDK itself. The `LogLevel` enum allows the
log level for the JS SDK to be set at SDK creation time. If none is given, the log level defaults
to `ERROR` for security.

# Sample App

The `app` module of the project contains a sample app that uses the SDK. The activities in the
sample app document the workflow/usage of the Frames SDK.

The sample app highlights the ability to combine native controls like buttons with web controls,
and doubles as a test harness for development.

Once a card has been tokenised, the instrument ID is displayed. Using that ID the payment instrument
can then used. The new instrument will be in the customer's instrument list. To tokenise the same
card again, the instrument will need to be removed from the customer's profile first.

An example of how to use 3DS is also included. Apps will need to determine the best way to display
3DS challenges based on their needs.

See:
 - List customer payment instruments `GET /instore/customer/instruments`
 - Remove payment instrument `DELETE /instore/customer/instruments/:id` 

# Testing

There are no automated tests for the library as the library is a wrapper around the Frames JS SDK
which makes automated testing difficult. The sample app doubles as a development/testing harness for
the SDK.

# Publishing

Newer versions of the SDK are tagged in GitHub and are published via [JitPack](https://jitpack.io/#w-pay/sdk-wpay-android-frames).