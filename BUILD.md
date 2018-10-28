# Build scrcpy

Here are the instructions to build _scrcpy_ (client and server).

You may want to build only the client: the server binary, which will be pushed
to the Android device, does not depend on your system and architecture. In that
case, use the [prebuilt server] (so you will not need Java or the Android SDK).

[prebuilt server]: #prebuilt-server


## Requirements

You need [adb]. It is available in the [Android SDK platform
tools][platform-tools], or packaged in your distribution (`android-adb-tools`).

On Windows, download the [platform-tools][platform-tools-windows] and extract
the following files to a directory accessible from your `PATH`:
 - `adb.exe`
 - `AdbWinApi.dll`
 - `AdbWinUsbApi.dll`

The client requires [FFmpeg] and [LibSDL2]. Just follow the instructions.

[adb]: https://developer.android.com/studio/command-line/adb.html
[platform-tools]: https://developer.android.com/studio/releases/platform-tools.html
[platform-tools-windows]: https://dl.google.com/android/repository/platform-tools-latest-windows.zip
[ffmpeg]: https://en.wikipedia.org/wiki/FFmpeg
[LibSDL2]: https://en.wikipedia.org/wiki/Simple_DirectMedia_Layer



## System-specific steps

### Linux

Install the required packages from your package manager.

#### Debian/Ubuntu

```bash
# runtime dependencies
sudo apt install ffmpeg libsdl2-2.0.0

# client build dependencies
sudo apt install make gcc pkg-config meson \
                 libavcodec-dev libavformat-dev libavutil-dev \
                 libsdl2-dev

# server build dependencies
sudo apt install openjdk-8-jdk
```

#### Fedora

```bash
# enable RPM fusion free
sudo dnf install https://download1.rpmfusion.org/free/fedora/rpmfusion-free-release-$(rpm -E %fedora).noarch.rpm

# client build dependencies
sudo dnf install SDL2-devel ffms2-devel meson gcc make

# server build dependencies
sudo dnf install java
```



### Windows

#### Cross-compile from Linux

This is the preferred method (and the way the release is built).

From _Debian_, install _mingw_:

```bash
sudo apt install mingw-w64 mingw-w64-tools
```

You also need the JDK to build the server:

```bash
sudo apt install openjdk-8-jdk
```

Then generate the releases:

```bash
make -f Makefile.CrossWindows
```

It will generate win32 and win64 releases into `dist/`.


#### In MSYS2

From Windows, you need [MSYS2] to build the project. From an MSYS2 terminal,
install the required packages:

[MSYS2]: http://www.msys2.org/

```bash
# runtime dependencies
pacman -S mingw-w64-x86_64-SDL2 \
          mingw-w64-x86_64-ffmpeg

# client build dependencies
pacman -S mingw-w64-x86_64-make \
          mingw-w64-x86_64-gcc \
          mingw-w64-x86_64-pkg-config \
          mingw-w64-x86_64-meson
```

For a 32 bits version, replace `x86_64` by `i686`:

```bash
# runtime dependencies
pacman -S mingw-w64-i686-SDL2 \
          mingw-w64-i686-ffmpeg

# client build dependencies
pacman -S mingw-w64-i686-make \
          mingw-w64-i686-gcc \
          mingw-w64-i686-pkg-config \
          mingw-w64-i686-meson
```

Java (>= 7) is not available in MSYS2, so if you plan to build the server,
install it manually and make it available from the `PATH`:

```bash
export PATH="$JAVA_HOME/bin:$PATH"
```

### Mac OS

Install the packages with [Homebrew]:

[Homebrew]: https://brew.sh/

```bash
# runtime dependencies
brew install sdl2 ffmpeg

# client build dependencies
brew install pkg-config meson
```

Additionally, if you want to build the server, install Java 8 from Caskroom, and
make it avaliable from the `PATH`:

```bash
brew tap caskroom/versions
brew cask install java8
export JAVA_HOME="$(/usr/libexec/java_home --version 1.8)"
export PATH="$JAVA_HOME/bin:$PATH"
```

### Docker

See [pierlon/scrcpy-docker](https://github.com/pierlon/scrcpy-docker).


## Common steps

If you want to build the server, install the [Android SDK] (_Android Studio_),
and set `ANDROID_HOME` to its directory. For example:

[Android SDK]: https://developer.android.com/studio/index.html

```bash
export ANDROID_HOME=~/android/sdk
```

If you don't want to build the server, use the [prebuilt server].

Clone the project:

```bash
git clone https://github.com/Genymobile/scrcpy
cd scrcpy
```

Then, build:

```bash
meson x --buildtype release --strip -Db_lto=true
cd x
ninja
```

### Run

To run without installing:

```bash
./run x [options]
```


### Install

After a successful build, you can install _scrcpy_ on the system:

```bash
sudo ninja install    # without sudo on Windows
```

This installs two files:

 - `/usr/local/bin/scrcpy`
 - `/usr/local/share/scrcpy/scrcpy-server.jar`

Just remove them to "uninstall" the application.

You can then [run](README.md#run) _scrcpy_.


## Prebuilt server

 - [`scrcpy-server-v1.3.jar`][direct-scrcpy-server].  
   _(SHA-256: 0f9a5a217f33f0ed7a1498ceb3c0cccf31c53533893aa952e674c1571d2740c1)_

[direct-scrcpy-server]: https://github.com/Genymobile/scrcpy/releases/download/v1.3/scrcpy-server-v1.3.jar

Download the prebuilt server somewhere, and specify its path during the Meson
configuration:

```bash
meson x --buildtype release --strip -Db_lto=true \
    -Dprebuilt_server=/path/to/scrcpy-server.jar
cd x
ninja
sudo ninja install
```
