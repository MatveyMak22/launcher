#!/usr/bin/env sh
#
# Copyright 2011-2022 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Determine the Java command to use to launch the JVM.
if [ -n "" ]; then
    JAVA_HOME_FOR_GRADLE=
elif [ -n "$JAVA_HOME" ]; then
    JAVA_HOME_FOR_GRADLE="$JAVA_HOME"
elif [ -n "$JDK_HOME" ]; then
    JAVA_HOME_FOR_GRADLE="$JDK_HOME"
fi

if [ -n "$JAVA_HOME_FOR_GRADLE" ] && [ -x "$JAVA_HOME_FOR_GRADLE/bin/java" ]; then
    JAVACMD="$JAVA_HOME_FOR_GRADLE/bin/java"
elif [ -x "/usr/bin/java" ]; then
    JAVACMD="/usr/bin/java"
else
    JAVACMD=java
fi

# Determine the script directory.
SCRIPT_DIR=$(dirname "$0")

# Find the Gradle wrapper JAR.
GRADLE_WRAPPER_JAR="$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar"

# Execute the Gradle wrapper.
exec "$JAVACMD" -classpath "$GRADLE_WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
