# Hafsa Traders — CodeMagic Signed APK

This project is configured to build **only the signed release APK** in CodeMagic.

CodeMagic workflow: `hafsa-traders-release`

Expected artifact: `hafsatraders.apk`

The workflow explicitly downloads and uses Gradle 9.3.1, attaches the CodeMagic signing identity `paliaapk-release`, builds `assembleRelease`, verifies the APK with `apksigner`, and publishes only the signed APK artifact.
