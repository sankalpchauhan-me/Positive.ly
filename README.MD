
## Positive.ly

The app is Published on PlayStore:
<p align="center">
<a href='https://play.google.com/store/apps/details?id=me.sankalpchauhan.positively'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

# Description

Positive.ly is an android app that can help one to be more happy and stay positive. It is a feature packed combination of positive thinking the app
is primarily divided into 3 sections:-

- Positive Podcasts: This section lists popular podcasts which primarily focuses on positive thinking, Motivation, Living a happier life etc

- Positive Quotes: This section includes compilation of positive thinking quotes messages and proverbs to boost one’s emotional & mental energy level.

- Positive/Gratitude Affirmations/Journal: Here user can store voice notes & affirmations, which he/she can listen at any time, these will be stored in local storage as well as synced to database & storage if user consents Positive Thinking is an emotional & mental perception that looks on bright side of life & expects positive outcomes.

##### Problem it solves:
In these tough times of pandemic, everywhere we look whether on news channels, newspapers or social media. Mostly we see negative news & stats, along with that people during quarantine can’t interact much with their friends or relatives, which might take a heavy toll on their mental health. In these times positive thinking and feeling that everything will be okay is very important to stay focused on work & stay motivated. But there is no centralised source which provide all of this at one place. Social media can’t be used for this as it’s a mixed bag of information containing both positive & negative articles. This app is intended for a general use, which can go beyond ages as one always requires some positivity in life at any stage at any given time, so this app can be used at any time.

# Intended User

Anyone can use this app. However, target users are the one who are currently in home
quarantine or are undergoing nationwide lockdown, so that they can remain motivated & maintain good mental hygiene

# Features

- Provides Podcasts
- Provide Quotes
- Voice Journaling
- Offline & Online sync (so that app can be used across devices)
- Widget to show a positive quote

# User Interface

## Sign In Screen

### Sign Up Screen


## Podcast Screen

### Quotes Screen


## Podcast Detail Screen

### Quotes Detail Screen


## Recordings Screen

### App Widget


# Key Considerations

#### How will the app handle data persistence?

I will use Firebase Database to handle online sync & SQLite for local data persistence (like maintaining favourites). The app will also maintain a favourites database which will be stored locally using Room ORM

#### Describe any edge or corner cases in the UX.

- Handling empty states across the app (e.g. If the API doesn’t return a response)
- If the user is recording a journal what happens if he presses back button,
- Maintaining exoplayer states for a consistent user experience

##### Describe any libraries you’ll be using and share your reasoning for including

- Picasso to handle the loading and caching of images.
- Timber for logging
- Butteknife for view binding
- Retrofit for REST requests (includes okhttp3:logging-interceptor & gson-converter)
- Room (including Android Lifecycle Extensions): ORM for SQLite
- Shimmer Library for improved UI while loading data
- Material Drawer: A powerful library to implement Navigation drawer with multi account support along with added features & effects
- Exoplayer: For media playback of podcasts

##### Describe how you will implement Google Play Services or other external services.

Services I will use:

- AdMob: Using google’s ad services to run ads on the app
- Firebase Auth: To maintain user’s log in and sign up details
- Firebase Database: To sync data across devices
- ListenNotes API: To Fetch podcasts & show it’s details to user

# Next Steps: Required Tasks


## Task 1: Project Setup

- Setting up ListenNotes API to fetch podcasts
- Set up a quotes api.
- Configure and include the latest versions of all libraries required


### Task 2: Implement UI for Each Activity and Fragment

- Set up UI for log in & sign up
- Implement UI for Main Activity
- Implement UI for Gratitude Voice Journaling
- Creating Podcasts Fragment & Implement it’s UI
- Creating Quotes Fragment & Implement it’s UI
- Creating Podcasts Detail Screen & Custom Exoplayer Interface
- Creating Quotes Detail page along with Share button

### Task 3: Setting UP Firebase Auth & Database

- Implement Firebase Database
- Implement Firebase Auth methods that includes
    o Passwordless Sign In
    o Google Sign In
    o Email & Password Sign In
    o Email & Password Log in
    o Anonymous User Sign In
    o Forgot Password Feature

### Task 4: Setting Up ListenNotes API & Quotes API

- Setting up listen notes api & Quotes API along with proper caching mechanism to
    minimize network calls
- Setting up Retrofit & okHttp client
- Creating appropriate methods to fetch & display data in UI

### Task 5: Creating Voice Journal

- Initialize Media Player & Media Recorder to record user’s journal in the app
- Creating List to show these recordings to user, which they can listen to anytime

### Task 6 : UI Polishing, animations & Material Design

- Polish user experience by providing animations across the app
- To verify that the app follows material design guidelines for a consistent user
    experience

### Task 7 : Widget Designing & Implementation

- Creating UI for the widget
- Show top quotes of the day to the user in the widget


### Task 8 : Testing & Verification

- UI testing using espresso
- Re-verify all edge cases are covered & all states are correctly handled by the app

# How to build ?

Create a Firebase Project and download the google_services.json file
Put that file into the app folder and you are good to go

Open terminal and type the below command to generate debug build

`./gradlew assembleDebug`

Open terminal and type the below command to generate release build

`./gradlew assembleRelease`

After running the above command, By default the output can be found in app/build/outputs/apk/debug/apk-debug.apk

MIT License

Copyright (c) 2020 Sankalp Singh Chauhan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


