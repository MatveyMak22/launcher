@rem
@rem Copyright 2011-2022 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@echo off

setlocal

rem Determine the Java command to use to launch the JVM.
if not "%JAVA_HOME%" == "" goto findJavaFromJavaHome
if not "%JDK_HOME%" == "" goto findJavaFromJdkHome

rem On Windows, the Java command usually exists in the PATH.
where java >nul 2>nul
if %errorlevel% == 0 goto execute

echo.Error: JAVA_HOME or JDK_HOME environment variable not found.
echo.Please set JAVA_HOME or JDK_HOME to point to a valid Java installation.
goto end

:findJavaFromJavaHome
set JAVACMD="%JAVA_HOME%\bin\java.exe"
if exist %JAVACMD% goto execute
goto findJavaFromPath

:findJavaFromJdkHome
set JAVACMD="%JDK_HOME%\bin\java.exe"
if exist %JAVACMD% goto execute
goto findJavaFromPath

:findJavaFromPath
set JAVACMD=java
where %JAVACMD% >nul 2>nul
if %errorlevel% == 0 goto execute

echo.Error: Java executable not found in PATH.
echo.Please ensure Java is installed and accessible via PATH, or set JAVA_HOME or JDK_HOME.
goto end

:execute
rem Determine the script directory.
set SCRIPT_DIR=%~dp0

rem Find the Gradle wrapper JAR.
set GRADLE_WRAPPER_JAR="%SCRIPT_DIR%gradle\wrapper\gradle-wrapper.jar"

rem Execute the Gradle wrapper.
%JAVACMD% -classpath %GRADLE_WRAPPER_JAR% org.gradle.wrapper.GradleWrapperMain %*

:end
endlocal