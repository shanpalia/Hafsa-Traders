# Firebase Admin Login Setup

The app now uses Firebase Authentication for the Admin Login and Firestore for admin authorization.

## 1. Add Firebase config

In Firebase Console, create/select the Android app with package name:

`com.hafsatraders.app`

Download `google-services.json` and put it at:

`app/google-services.json`

Do not commit a different project's configuration.

## 2. Create admin account

In Firebase Authentication > Users, create the admin user with Email/Password authentication.

## 3. Authorize the admin

In Firestore create:

`admins/{USER_UID}`

with:

```text
role: "admin"
active: true
```

The app only opens the Admin Dashboard after Firebase sign-in succeeds and this Firestore role check passes.

## 4. Secure Firestore

Use Firestore Security Rules so users cannot create or modify their own `admins` documents. Admin authorization should be controlled from the Firebase console/backend, not from the APK.
