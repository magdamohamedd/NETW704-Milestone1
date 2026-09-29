# NETW704 – Milestone 1

Magda Mohamed 61-3206

## Overview
Milestone1 is an Android application written in Kotlin.
It uses Firebase Authentication for email/password sign-up
and sign-in, and Firebase Realtime Database for user profiles.

## Features
- Create an account using email and password.
- Sign in and sign out.
- Reject incorrect login credentials.
- View and edit name, address, and phone number.
- Save profile details to Firebase.
- Automatically load saved details after signing in.
- Display database updates in real time.

## Tools
- Android Studio
- Kotlin and XML layouts
- Firebase Authentication
- Firebase Realtime Database

## Running the project
1. Download or clone this repository.
2. Open the project folder in Android Studio.
3. Ensure google-services.json is inside the app folder.
4. Allow Gradle to sync and download dependencies.
5. Start an Android emulator or connect an Android device.
6. Select the app configuration and click Run.

## Firebase configuration
The Android application ID is com.example.milestone1.

Enable Email/Password in Firebase Authentication.
Create a Firebase Realtime Database.

The current database URL is:
https://milestone-1-d35a9-default-rtdb.firebaseio.com

To use a different Firebase project:
1. Register an Android app with the same application ID.
2. Download its google-services.json into the app folder.
3. Replace the database URL in ProfileActivity.kt.
4. Enable Email/Password authentication and apply the rules below.

## Database structure
Each profile is stored at users/{uid}, where uid is the
identifier assigned by Firebase Authentication.

Example:
{
  "users": {
    "USER_UID": {
      "name": "Test User",
      "address": "Test Address",
      "phone": "01000000000"
    }
  }
}

Passwords are managed by Firebase Authentication.
They are not stored in the profile database.

## Database rules
Apply these rules in Firebase Realtime Database:

{
  "rules": {
    "users": {
      "$uid": {
        ".read": "auth != null && auth.uid === $uid",
        ".write": "auth != null && auth.uid === $uid"
      }
    }
  }
}

## Current registration flow
After account creation, the user opens the profile screen,
enters their details, and presses Save Profile to store them.

## Tests completed
- Account creation and successful sign-in.
- Incorrect-password rejection.
- Saving all profile fields.
- Loading saved fields after signing out and back in.
- Editing and saving the address.
- Updating a value in Firebase and seeing it change in the app.