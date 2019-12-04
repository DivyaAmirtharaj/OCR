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

Install following libraries by including following dependencies lines in OCR/app/build.grade file\
   >implementation 'com.android.support:support-v4:24.0.0'\
   implementation 'com.google.android.gms:play-services-vision:9.8.0'\
   implementation 'com.android.support:design:24.0.0'\
   implementation('com.google.cloud:google-cloud-translate:1.16.0')


## Setting up Translate API in Google
>1. [Sign in to Google API Console](https://console.cloud.google.com/apis/)\
>2. Use existing project or create "New Project"\
>3. On the Google Cloud Platform dashboard, select "APIs & Services" on the navigation window at the left side\
>4. Click on the "Enable APIs and Services" button\
>5. Search for "Cloud Translation API" and click "Enable" button\


