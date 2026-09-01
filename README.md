<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/49ffe48e-35a3-43a8-a61c-1de13de4104d

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. If you want Firebase customer/admin login, add your own `app/google-services.json` from your Firebase project (this file is intentionally not included in the ZIP).
5. Run the app on an emulator or physical device.
6. For a release APK, use the included CodeMagic workflow and configure the signing environment variables/profile there.


## Admin login

The customer Profile screen contains an Admin Login entry. Admin access uses Firebase Authentication plus a Firestore `admins/{uid}` document with `role: "admin"` and `active: true`. Add your own Firebase configuration before using online admin login.

## CodeMagic

Use `codemagic.yaml`. The workflow downloads Gradle 9.3.1 explicitly, so it does not depend on a preinstalled Gradle version.


## UI fixes in this build
- Safe status/navigation bar insets for portrait phones.
- Android system Back closes sheets/forms, returns to the previous section, and exits from Home.
- New green/cream rounded Hafsa Traders launcher icon.
- Release workflow outputs only a signed `hafsatraders.apk`.

## Fixes applied
- Removed the Google Services Gradle plugin from the build so the project can compile even when `google-services.json` is not present in the ZIP. Firebase dependencies remain available; runtime Firebase login still requires the user's own Firebase configuration.
- Simplified `compileSdk` configuration for better Android Gradle Plugin compatibility.
- Aligned the KSP plugin version with the Kotlin 2.2.10 compiler line.
- Added a `.gitignore` to prevent signing keys, Firebase config, local SDK paths and build output from being accidentally published.


## Pickup-only production workflow
Admin controls service categories, services, prices, shop address, phone/WhatsApp and UPI settings from the Admin panel. Orders contain uploaded document/photo URIs and can be opened from the order detail screen. Marking an order READY creates the customer message: "Your order is ready. Please visit the shop and pick up your order."

### WhatsApp notification
A truly automatic WhatsApp message to the shop owner requires a server-side WhatsApp Business Platform integration (Meta Cloud API or an approved provider). A normal Android app cannot silently send a WhatsApp message in the background without user interaction. For production, send each new order to a backend/Cloud Function and call the WhatsApp Business API from there.

### Customer push notification
For a real notification even when the customer app is closed, configure Firebase Cloud Messaging and send an FCM message from a trusted backend whenever the admin changes an order to READY.


## Website Update Check
Customer Profile includes a **Check for Updates** button. Configure `Website Update Check URL` in Admin Settings to a public JSON endpoint, for example:
```json
{
  "version": "1.1.0",
  "updateUrl": "https://example.com/download/hafsa-traders.apk",
  "required": false,
  "message": "New features and fixes are available."
}
```
The app compares `version` with the installed app version. If newer, it shows **Update Now**; otherwise it shows that the app is up to date.


## Order history and tracking
- Customer Orders shows every order belonging to the signed-in customer account (Active, Completed and All filters).
- Each order detail now shows a timestamped tracking history: Received -> Processing -> Ready for Pickup -> Completed.
- Every admin status change creates a tracking event with the exact change time and a customer notification.
- Cross-device, truly live tracking still requires a shared backend such as Firebase Firestore/Cloud Functions; Room tracking is real for status changes made in the same installed app/database.
