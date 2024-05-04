# Hedgehog

Hedgehog is a Java library designed to make it easy to interact with the [Posthog API](https://posthog.com/docs/api/overview), a powerful open-source product analytics platform.

This library is the Java implementation of our Rust library [hedgehog-rs](https://github.com/villainwtf/hedgehog-rs).

## Notice

This repository contains software that was originally developed for internal use by our organization. We have chosen to open-source this software in the hopes that it may be of use to others.

Please note the following important points:
- While we are making this software available to the public, we will not be providing external support. If you choose to use this software, please understand that you do so entirely at your own risk.
- Additionally, we will not be accepting any contributions to this project. The source code is available for you to use and modify as you wish, within the bounds of the included license, but we will not be incorporating any changes or enhancements made by external parties.

## Supported Posthog Features

- [x] Identify users
- [x] Capture events
- [x] Capture events in batch
- [x] Record page views
- [x] Record screen views
- [x] Evaluate feature flags
- [x] Include feature flag information when capturing events
- [x] Feature flag called event
- [x] Override GeoIP information when capturing events based on IP address