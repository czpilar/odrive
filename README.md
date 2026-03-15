oDrive
======

- `odrive-cmd` is simple java command line application for uploading file(s) to Microsoft OneDrive.
- `odrive-core` is core library which provide ability for uploading file(s) to Microsoft OneDrive.

oDrive as command line application
----------------------------------

### Usage
usage: `odrive [-a [code]] [-d <dir>] [-f <file>] [-h] [-l] [-p <props>] [-v]`

 `-a [code]` - process authorization; waits for code if not provided<br/>
 `-d <dir>` - directory for upload; creates new one if no directory exists; default is odrive-uploads<br/>
 `-f <file>` - upload file(s)<br/>
 `-h` - show this help<br/>
 `-l` - display authorization link<br/>
 `-p <props>` - path to oDrive properties file<br/>
 `-v` - show oDrive version

### How to authorize application manually
1. generate authorization URL:<br/>
   `odrive -p odrive.properties -l`
2. copy and paste URL to your browser to receive authorization code
3. authorize application with received authorization code:<br/>
   `odrive -p odrive.properties -a <code>`

### How to authorize application automatically
1. generate authorization URL and wait for authorization code:<br/>
   `odrive -p odrive.properties -l -a`
2. copy and paste URL to your browser to receive authorization code
3. application waits 5 minutes to receive authorization code
4. application is authorized automatically with received authorization code

### How to upload files
Upload file(s) to OneDrive:<br/>
   `odrive -p odrive.properties -f <file1> <file2> <file3>`

Files are uploaded to `odrive-uploads` directory by default.

If you want to change upload directory:

- change `odrive.uploadDir` property in _properties_ file
- or pass directory in `-d <dir>` argument:<br/>
   `odrive -p odrive.properties -f <file> -d <path>/<to>/<dir>`

### How to use properties file
- `odrive.refreshToken` - OneDrive refresh token; this property is updated automatically by oDrive
- `odrive.uploadDir` - path to dir where files will be uploaded: `<path>/<to>/<dir>`

oDrive as core library
----------------------
oDrive core can be used in any other application to provide ability for uploading file(s) to Microsoft OneDrive.

### Usage
1. add implementation of `IODriveCredential` interface to spring context
   or use `SimpleODriveCredential` or extend `AbstractODriveCredential`
2. provide client ID as `odrive.core.drive.clientId` property in spring context
3. optionally provide tenant as `odrive.core.drive.tenant` property in spring context (default is `common`)
4. import oDrive spring context with annotation @Import(net.czpilar.odrive.core.context.ODriveCoreContext.class)
5. autowire `IFileService` and use file uploading methods

License
=======

    Copyright 2026 David Pilar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
