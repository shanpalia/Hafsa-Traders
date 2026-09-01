# Hafsa Traders - Live Services Setup

This build is Firebase/Firestore-ready for real-time catalogue updates.

## What syncs live
- Admin adds a category -> customer Home/Services receives it.
- Admin adds/edits a service -> name, description, price and settings update live.
- Admin enables/disables a service -> customers receive the new active state.
- Admin deletes a service/category -> local admin changes immediately; for strict remote deletion parity, use the next database migration or keep the item inactive.

## One-time Firebase setup required
1. Open Firebase Console and create/select the project for package `com.hafsatraders.app`.
2. Add Android app with package `com.hafsatraders.app`.
3. Download `google-services.json` and place it exactly here:
   `app/google-services.json`
4. Enable **Cloud Firestore**.
5. Enable **Authentication -> Email/Password** if customer login is required.
6. Build the APK again.

The Gradle build only applies the Google Services plugin when this file exists, so the project can still compile before configuration.

## Recommended Firestore data location
`hafsa_live/catalog`

The app automatically creates/updates this document when the admin changes the catalogue.
Customer devices subscribe to this document with a real-time snapshot listener.

## Security
Do not use permanently-open Firestore write rules in production. Admin write access should be restricted to a Firebase-authenticated admin or a trusted backend/Cloud Function.
