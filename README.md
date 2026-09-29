# NETW704 – Milestone 1

**Name:** Magda Mohamed  
**Student ID:** 61-3206

## Overview
Milestone1 is an Android application written in Kotlin.
It uses Firebase Authentication for email/password registration
and sign-in, and Firebase Realtime Database for storing user profiles.

## Features
- Register with email, password, full name, address and phone number.
- Automatically save profile details immediately after registration.
- Sign in and sign out.
- Reject incorrect login credentials.
- View and edit name, address and phone number.
- Save profile changes to Firebase.
- Automatically retrieve saved details after signing in.
- Display database updates in real time.
- Restrict profile access to the authenticated profile owner.

## Tools
- Android Studio
- Kotlin and XML layouts
- Firebase Authentication
- Firebase Realtime Database

## Running the project
1. Download or clone this repository.
2. Open the project root folder in Android Studio.
3. Ensure `google-services.json` is inside the `app` folder.
4. Configure your local Android SDK location if prompted.
5. Allow Gradle to sync and download the required dependencies.
6. Start an Android emulator or connect an Android device.
7. Ensure the device has internet access.
8. Select the `app` configuration and click Run.

## Firebase configuration
The Android application ID is `com.example.milestone1`.

The application uses Email/Password authentication and
Firebase Realtime Database.

The current database URL is:

```text
https://milestone-1-d35a9-default-rtdb.firebaseio.com
```

To use a different Firebase project:
1. Register an Android app with the same application ID.
2. Replace `app/google-services.json` with the configuration file
   downloaded from your Firebase project.
3. Enable Email/Password in Firebase Authentication.
4. Create a Firebase Realtime Database.
5. Replace the database URL in both `MainActivity.kt`
   and `ProfileActivity.kt`.
6. Apply and publish the database rules below.
7. Sync Gradle and run the application.

## Registration and sign-in flow
1. Select **Create an account** on the sign-in screen.
2. Enter an email, password, full name, address and phone number.
3. Select **Register**.
4. Firebase Authentication creates the account and assigns a UID.
5. The app automatically saves the profile under `users/{uid}`
   in Firebase Realtime Database.
6. After the profile write succeeds, the app opens **My Profile**.

The initial profile is saved during registration without requiring
the user to press **Save Profile**.

If account creation succeeds but the profile write fails,
the registration screen provides a retry option.

Existing users can sign in using their email and password.
Invalid credentials produce an error and do not open the profile screen.

## Profile management
The profile screen displays the signed-in user's email and
saved name, address and phone number.

Users can edit their details and select **Save Profile**.
The app requires all three profile fields to be filled in.

A realtime database listener retrieves profile data and reflects
database updates in the screen. Signing out returns the user
to the sign-in screen.

## Database structure
Each profile is stored at `users/{uid}`, where `uid` is the
identifier assigned by Firebase Authentication.

Example:

```json
{
  "users": {
    "USER_UID": {
      "name": "Test User",
      "address": "Test Address",
      "phone": "01000000000"
    }
  }
}
```

Profile values are stored as strings. Storing the phone number
as a string preserves leading zeros.

The email is retrieved from Firebase Authentication.
Passwords are managed by Firebase Authentication and are not
stored in the profile database.

## Database rules
Apply and publish these rules in Firebase Realtime Database:

```json
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
```

These rules allow an authenticated user to read and write only
the profile stored under their own UID.

## Main project files
- `MainActivity.kt`: registration, sign-in and initial profile storage.
- `ProfileActivity.kt`: profile retrieval, editing, saving and sign-out.
- `activity_main.xml`: sign-in and registration layouts.
- `activity_profile.xml`: profile screen layout.
- `AndroidManifest.xml`: application configuration and activity declarations.

## Manual verification
The following behaviors were checked during development:
- Creating an account and storing its profile in Firebase.
- Signing out and signing back in to retrieve saved details.
- Editing and saving profile fields.
- Reflecting database changes in the profile screen.
- Rejecting an incorrect password.
- Building and running the application on a Pixel 7 emulator
  with Android 15 (API 35).

## Tests completed
- Account creation and successful sign-in.
- Incorrect-password rejection.
- Saving all profile fields.
- Loading saved fields after signing out and back in.
- Editing and saving the address.
- Updating a value in Firebase and seeing it change in the app.
