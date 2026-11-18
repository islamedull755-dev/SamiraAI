#!/data/data/com.termux/files/usr/bin/bash
set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
VERSION="8.9"
GRADLE_HOME_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/samira-wrapper/gradle-$VERSION"
if [ ! -x "$GRADLE_HOME_DIR/bin/gradle" ]; then
  mkdir -p "$(dirname "$GRADLE_HOME_DIR")"
  TMP_ZIP="${TMPDIR:-/tmp}/gradle-$VERSION-bin.zip"
  echo "Downloading Gradle $VERSION..."
  curl -fL "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip" -o "$TMP_ZIP"
  unzip -q -o "$TMP_ZIP" -d "$(dirname "$GRADLE_HOME_DIR")"
  rm -f "$TMP_ZIP"
fi
exec "$GRADLE_HOME_DIR/bin/gradle" -p "$ROOT" "$@"
