#!/usr/bin/env sh
set -e
if [ -n "$GRADLE_HOME" ] && [ -x "$GRADLE_HOME/bin/gradle" ]; then
  exec "$GRADLE_HOME/bin/gradle" "$@"
fi
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi
printf '%s\n' 'Gradle is not installed. Use the CodeMagic workflow, which provisions Gradle 9.3.1.' >&2
exit 1
