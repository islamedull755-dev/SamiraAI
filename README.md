# Samira AI — Android Starter (V1.0)

Owner label: Eidul Boss

This is the first runnable Android foundation: premium dark dashboard, anime avatar artwork, quick actions, system speech recognition, Android Text-to-Speech, and an HTTPS backend connection point.

## What works in this starter
- Native Kotlin Android app with XML layouts.
- Dashboard and included Samira concept artwork.
- Voice input through Android's speech-recognition service.
- Spoken replies through Android Text-to-Speech.
- Secure-backend connection point (`POST /chat`, JSON request `{"message":"..."}`, response `{"reply":"..."}`).
- YouTube music search shortcut.
- Android Settings shortcut.

## Not implemented yet
Calling Mode is currently a voice-input shortcut, not a real phone-call service.
The Gemini backend, long-term memory, real-time animated lip sync, messaging integrations, and advanced phone automation are planned for later phases. This starter does not claim those features are already working.

## Requirements
- Android SDK Platform 35 and Build Tools 35.x
- JDK 17 or newer (JDK 21 is suitable)
- Gradle 8.9 (the included `gradlew` downloads Gradle 8.9 on first run)
- Internet access for Gradle and Android dependencies

## Termux setup (Android)
Termux package availability varies by repository. Install basic tools:

```bash
pkg update && pkg upgrade -y
pkg install openjdk-21 git curl wget unzip zip -y
java -version
```

Install/configure Android SDK command-line tools and Platform 35 using a supported Android SDK installation method. Set paths after confirming the actual SDK location:

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin"
```

If your SDK is stored elsewhere, replace the example path. Do not assume that installing `aapt2` alone installs the full Android SDK.

## Extract and build
```bash
unzip SamiraAI-Termux-Project.zip
cd SamiraAI
chmod +x gradlew
./gradlew --version
./gradlew assembleDebug
```

APK output:
```text
app/build/outputs/apk/debug/app-debug.apk
```

## Install on the phone
If Android platform-tools `adb` is installed and usable:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
Otherwise open the APK from a file manager and approve Android's install prompt for that file source.

## Connect a real AI backend
Do not put a Gemini API key in the APK. Create a private HTTPS backend that keeps the API key server-side and implements:

`POST https://YOUR-SERVER/chat`

Request:
```json
{"message":"Hello Samira"}
```
Response:
```json
{"reply":"Hello Eidul Boss! How can I help?"}
```

Then set the URL in `app/build.gradle.kts` by replacing the empty `AI_BACKEND_URL` value in both build types with your own HTTPS endpoint. Rebuild the APK.

## Troubleshooting
- `JAVA_HOME` or Java version errors: check `java -version`; use JDK 17+.
- `SDK location not found`: set `ANDROID_HOME` and `ANDROID_SDK_ROOT` to the real SDK directory.
- `platform android-35 not found`: install Android SDK Platform 35.
- `Could not resolve ...`: check internet, repository access, and Gradle sync.
- `Permission denied: ./gradlew`: run `chmod +x gradlew`.
- Termux native Android builds can fail because some Android SDK/NDK tools are not packaged for Android. If a build tool reports an unsupported host or executable, build on a supported Linux/Windows/macOS computer or CI runner, then install the resulting APK on your phone.

## Gradle wrapper note
This archive includes `gradlew`, `gradlew.bat`, and `gradle-wrapper.properties`. The Unix launcher bootstraps the pinned Gradle 8.9 distribution directly so a binary `gradle-wrapper.jar` is not required. This is a self-bootstrapping launcher, not the official generated Gradle wrapper JAR.
