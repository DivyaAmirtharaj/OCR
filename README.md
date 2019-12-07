# OCR

Optical Character Recognition (OCR) is a real-time mobile Android app that allows users to scan, translate and read aloud text in any major language.

## Prerequisites

Google API console account with "APIs and Services" enabled.\
Google cloud API credential file in json format.\
Android Studio, with a recent version of the Android SDK.

## Installation

Download the OCR directory from this repository. Or 
```
git clone https://github.com/DivyaAmirtharaj/OCR.git
```

In Android Studio, open the OCR directory as an existing Android Studio project.

Install the following libraries by including the following lines in OCR/app/build.gradle
```
   implementation 'com.android.support:support-v4:24.0.0'
   implementation 'com.google.android.gms:play-services-vision:9.8.0'
   implementation 'com.android.support:design:24.0.0'
   implementation('com.google.cloud:google-cloud-translate:1.16.0')
```
## Compilation
Have minimum SDK Version of 21 to have Translate API work.  Have following code in build.grade under android block.
```
   compileSdkVersion 26
    defaultConfig {
        applicationId "edu.harvard.cs50.divya.amirtharaj.OCR"
        minSdkVersion 21
        targetSdkVersion 26
        versionCode 1
        versionName "1.0"
        multiDexEnabled true
    }
 ```

## Setting up Translate API in Google
Translation of OCR text is done through Google API.  For that API service for transation needs to be set in Google API console.  Follow below step to setup the API service
>1. [Sign in to Google API Console](https://console.cloud.google.com/apis/)
>2. Use existing project or create "New Project"
>3. On the Google Cloud Platform dashboard, select "APIs & Services" on the navigation window at the left side
>4. Click on the "Enable APIs and Services" button
>5. Search for "Cloud Translation API" and click "Enable" button
>6. Click the "Enable billing" and setup the trial service and provide payment information.

## Setting up API Credentials
To secure the API service and to restrict the access to authorized apps, use following steps.

>1. Click "Create Credentials on the Google API Console
>2. Select "Cloud Translation API", check "No, I'm not using them and click on "Which credentials do I need?"
>3. Enter your information and select "JSON" as the key type and click "Cotinue"
>4. The credentials file will be downloaded in JSON format.  Save this file in Android Studio raw folder

## Internet Permission for Translate API
Add internet permission to AndroidMaifest.xml
```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="edu.harvard.cs50.divya.amirtharaj.ocr"
          android:installLocation="auto">

    <uses-feature android:name="android.hardware.camera"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.INTERNET"/>
```

## OCR 
Following libraries are used for Character Recoginition
```
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
```


## Text To Speech TTS
TextToSpeech class used to speak the OCR captured and Translated Text.
### Locale 
Locale is used to set the language and country of speaker.
```
Locale locale = new Locale( <<Language>>, <<Country>>);
TextToSpeech.OnInitListener listener =  new TextToSpeech.OnInitListener() {

public void onInit(final int status) {  tts.setLanguage(locale); } };

tts = new TextToSpeech(this.getApplicationContext(), listener);
Locale.setDefault(locale);
Configuration config = new Configuration();
config.setLocale(locale);

````
Language  | Code | Country
----------|----- | -------------
English|en | US
Spanish|es | MX
French|fr | FR
Japanese|ja | JP
Chinese|zh | CN
German|de | DE
Italian|it | IT
Korean|ko | KR
Hindi|hi | KR
Russian|ru | RU
Arabic|ar | AE
Greek|el | GR
Hebrew|he | IL
Vietnamese|vi | VN
Tamil|ta | IN


## Translation 
Following libraries are used for Translation
```
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
```
### Credentials
Credential file generated from the Google API service is stored res/raw folder as cred.json

```
 try (InputStream is = getResources().openRawResource(R.raw.cred)) {
      final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);
   } catch (IOException ioe) {
      ioe.printStackTrace();
   }
```        

### Starting Translate Service
Using the above credentials to call the API service
```
TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
OCRSettings.setTranslate( translateOptions.getService());
```
Translate translate = translateOptions.getService()
Translation translation = translate.translate(<<inputText>>, Translate.TranslateOption.targetLanguage(OCRSettings.getLang()), Translate.TranslateOption.model("base"));
```
