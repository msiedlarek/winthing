# WinThing

[![Build Status](https://travis-ci.org/msiedlarek/winthing.svg?branch=master)](https://travis-ci.org/msiedlarek/winthing)

A modular background service that makes Windows remotely controllable
through MQTT. For home automation and Internet of Things.

## Compilation

    mvn clean package

## Running

    java -jar -Dwinthing.brokerUrl=tcp://localhost:1883 target/winthing-1.1.0-SNAPSHOT.jar

## Configuration

Configuration can be passed either by Java system properties from command line or application.properties file placed in the classpath.

<table>
<tr><th>Property</th><th>Description</th><th>Default</th>
<tr><td> winthing.brokerUrl </td><td> URL of the MQTT broker to use. </td><td> tcp://localhost:1883 </td></tr>
<tr><td> winthing.brokerUsername </td><td> Username used when connecting to MQTT broker. </td><td> bob </td></tr>
<tr><td> winthing.brokerPassword </td><td> Password used when connecting to MQTT broker. </td><td> supersecret11 </td></tr>
<tr><td> winthing.clientId </td><td> Client ID to present to the broker. </td><td> WinThing </td></tr>
<tr><td> winthing.topicPrefix </td><td> Client ID to present to the broker. </td><td> winthing </td></tr>
<tr><td> winthing.reconnectInterval </td><td> Time interval between connection attempts in seconds. </td><td> 5 </td></tr>
</table>

## Supported messages

The payload of all messages is either empty or a valid JSON element (possibly
a privimite, like a single integer). This means, specifically, that if an
argument is supposed to be a single string, it should be sent in double quotes.

Example valid message payloads:

* `123`
* `true`
* `"notepad.exe"`
* `[1024, 768]`
* `["notepad.exe", "C:\\file.txt", "C:\\"]` (note that JSON string requires escaped backslash)

### Broadcasted status

#### System

<table><tr>
  <th>Topic:</th><td> winthing/system/online </td>
  <th>QoS:</th><td> 2 </td>
  <th>Persistent:</th><td> yes </td>
</tr><tr>
  <th>Payload:</th><td colspan="5"> state:boolean </td>
</tr><tr><td colspan="6">
  True when WinThing is running, false otherwise.
</td></tr></table>

### Commands

#### System

<table><tr>
  <th>Topic:</th><td> winthing/system/commands/shutdown </td>
</tr><tr>
  <th>Payload:</th><td>-</td>
</tr><tr><td colspan="2">
  Trigger immediate system shutdown.
</td></tr></table>

<table><tr>
  <th>Topic:</th><td> winthing/system/commands/reboot </td>
</tr><tr>
  <th>Payload:</th><td>-</td>
</tr><tr><td colspan="2">
  Trigger immediate system reboot.
</td></tr></table>

<table><tr>
  <th>Topic:</th><td> winthing/system/commands/suspend </td>
</tr><tr>
  <th>Payload:</th><td>-</td>
</tr><tr><td colspan="2">
  Trigger immediate system suspend.
</td></tr></table>

<table><tr>
  <th>Topic:</th><td> winthing/system/commands/hibernate </td>
</tr><tr>
  <th>Payload:</th><td>-</td>
</tr><tr><td colspan="2">
  Trigger immediate system hibernate.
</td></tr></table>

<table><tr>
  <th>Topic:</th><td> winthing/system/commands/run </td>
</tr><tr>
  <th>Payload:</th><td>[command:string, arguments:string, workingDirectory:string]</td>
</tr><tr><td colspan="2">
  Run a command. Arguments and working directory are optional (empty string and null by default).
</td></tr></table>

<table><tr>
  <th>Topic:</th><td> winthing/system/commands/open </td>
</tr><tr>
  <th>Payload:</th><td>uri:string</td>
</tr><tr><td colspan="2">
  Opens an URI, like a website in a browser or a disk location in a file browser.
</td></tr></table>

#### Desktop

<table><tr>
  <th>Topic:</th><td> winthing/desktop/commands/close_active_window </td>
</tr><tr>
  <th>Payload:</th><td>-</td>
</tr><tr><td colspan="2">
  Closes currently active window.
</td></tr></table>

<table><tr>
  <th>Topic:</th><td> winthing/desktop/commands/set_display_sleep </td>
</tr><tr>
  <th>Payload:</th><td>displaySleep:boolean</td>
</tr><tr><td colspan="2">
  Puts the display to sleep (on true) or wakes it up (on false).
</td></tr></table>

#### Keyboard

<table><tr>
  <th>Topic:</th><td> winthing/keyboard/commands/press_keys </td>
</tr><tr>
  <th>Payload:</th><td>[key:string...]</td>
</tr><tr><td colspan="2">
  Simulates pressing of given set of keyboard keys. Keys are specified by name.
  List of availble key names and aliases can be found
  [here](src/main/java/com/fatico/winthing/windows/input/KeyboardKey.java).
</td></tr></table>

#### ATI Radeon display driver

<table><tr>
  <th>Topic:</th><td> winthing/radeon/commands/set_best_resolution </td>
</tr><tr>
  <th>Payload:</th><td>-</td>
</tr><tr><td colspan="2">
  Sets the screen to the best available resolution.
</td></tr></table>

<table><tr>
  <th>Topic:</th><td> winthing/radeon/commands/set_resolution </td>
</tr><tr>
  <th>Payload:</th><td>[widthInPixels:integer, heightInPixels:integer]</td>
</tr><tr><td colspan="2">
  Sets the screen to the given resolution.
</td></tr></table>

## License

Copyright 2015-2016 Mikołaj Siedlarek &lt;mikolaj@siedlarek.pl&gt;

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this software except in compliance with the License.
You may obtain a copy of the License at

> http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
