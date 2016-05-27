# Optee demonstration for OpenEmbedded

This is a sample layer/distro for OpenEmbedded to recreate the optee
development and test environments.  The primary development
environment for optee is [described in its
documentation](https://github.com/OP-TEE/optee_os/blob/master/README.md).
Although made for development, this OE tree is **not** supported.

## Goals

Some goals and non-goals

- Create the same type of development environment as the official
  optee builds.  This means, for example, that the builds generally
  run out of an initrd and are intended for rapid development and
  turnaround.
- Although they may be useful as an example, these recipes are not
  intended to be directly usable in another environment.  In
  otherwords, this layer, and its dependents are designed to
  standalone.
- This is not intended to be a distribution.  The root images are
  minimal, and have just enough functionality to test OPTEE itself.

## Organization

This git project contains its dependents as submodules.  This makes
setup quite a bit easier, even though it really isn't how OE is
intended to be used.  As such, the layer configuration is a little
strange (including ".." in many of the path components).  There is
also a different init script, since the base OE init script assumes a
specific directory layout that is incompatible with git submodules.

Most of the additions are new recipes for bitbake.  These are all
under the meta-optee directory.  They include a specialized kernel,
and the various secure bootloaders necessary.

Everything is packaged up with the 'optee-image' package.  This same
package can be used on any of these targets to build the image(s)
necessary for that target.  There are a handful of shell scripts at
the top-level directory to make running the images a bit easier.

# Supported targets

Currently, oe-optee supports the following targets.

- **qemu-optee32**: Builds everything as 32-bit, and builds an image
  suitable to run within Qemu.  This target is fully self-contained
  and is the easiest to use (bitbake will build qemu for you).
  Unfortunately, it is currently only 32-bit.

- **fvp-optee64**: Builds a full 64-bit system, and builds an image
  suitable to run within ARM's Foundation Platform emulator.  This
  emulator is freely available, but requires acceptance of a
  click-through license.  Bitbake will build everything needed to run
  on this platform, and you will only need to download the Foundation
  Platform manually.

- **hikey-optee64**: Builds a full 64-bit system, and builds an image
  suitable to run on the Hikey 96board.  This build is not complete,
  and you should use one of the 96board reference images to initially
  flash your device (it will work with either a Debian or Android
  system).  Since it runs entirely in a ramdisk, the data partitions
  are not affected, and it is easy to switch back and forth.

# Setup and build

OE has very few system requirements, generally you'll need cpio and
texinfo.  It will print early on any packages that are missing.  Most
of everything else that it needs will be downloaded and built.

Building optee-os, however, will require the host's python to have
pycrypto installed.

To build this image, you'll need about 15-20 GB of space.  If you
disable 'rm-work' to leave build trees expanded, you'll need a lot
more.

The first time you build, it will take a while, as OE builds all of
the necessary toolchains itself.  Once this is built, as long as you
keep the build/sstate-cache directory, subsequent builds will only
rebuild things that haven't been built (or have changed).

## Clone this repo
```
$ cd path/to/workdir
$ git clone https://github.com/linaro-swg/oe-optee
```

## Get dependencies
Download the dependencies, including the bitbake build tool that is at
the heart of OpenEmbedded.  These are submodules so can easily be
downloaded.
```
$ cd oe-optee
$ git submodule update --init
```

## Setup environment for OE
```
$ source ./optee-init-build-env
```
This should print a message out about setting up a default config.  It
will, by default, create a directory called `build` and cd you to this
directory.  Feel free to look at `conf/local.conf` in this build
directory.  Most notably, at the end are some lines that can be
uncommented to add things such as ssh, gdb and strace to the image.
This can also be done later, and the image quickly regenerated.  A
section below describes these in more detail.

This init script also accepts an argument of a directory to use
instead of 'build'.  This is useful to be able to build for more than
one target, provided you have sufficient space.

## Build the image
Ask OE to build our optee-image:
```
$ bitbake optee-image
```
On my dev machine, this takes about 45 minutes the first time.  Later
builds will be much faster as intermediate results will be cached.

# Running on qemu.
## Run the image.
You will need two other terminal windows to run the serial consoles
used by qemu.  In one, run `./term.sh`, and in the other, run
`./term2.sh`.  You can then run `./runarm.sh` (note you have to be
outside of the build directory to run this, and it is hardcoded for
the location of the build directory.

Entering `c` and return in at the qemu prompt should start booting the
linux kernel on the first terminal window.  To test optee, do
something along the lines of:

```
login: root
# xtest
```

Note that the tee-supplicant will be started automatically by an init
script.

# Running on FVP.
In addition to the QEMU 32-bit target, you can also build a 64-bit
target that runs on ARM's FVP emulator.  Which target builds is
determined by the MACHINE variable in build/conf/local.conf.  Set this
to "fvp-optee64" to build the FVP 64-bit target.

You will need to get the emulator yourself (ARM requires an acceptance
to download, but otherwise does not charge for this emulator).  The
runfvp.sh script assumes the emulator is extracted under
Foundation_Platformpkg, but this can be changed by editing this
script.  The script also assumes a different build directory (so that
both platforms can be built at the same time).

Run the runfvp.sh script, which will fire up two xterms for the two
consoles, and a small monitor window to show the status of the
emulator itself.

# Running on Hikey.
The hikey target can be built by uncommenting the "hikey-optee64"
MACHINE definition line.  There is a flash-hikey.sh script in the top
directory which will use a (previously installed) fastboot command to
flash the images this package creates.  If your board is not currently
running an image (either Android or Debian should be fine), follow the
instructions at http://96boards.org/ to flash an initial system.

# Hacking
The [Yocto Project](https://www.yoctoproject.org/) has a lot of good
documentation about using OpenEmbedded.  Here are a few hints.

## Debugging and ssh
The file `build/conf/local.conf` has some useful settings.  The line
```
INHERIT += "rm_work"
```
saves a lot of disk space by deleting the build directories after each
build.  You can comment this line out to leave some of this data.

There are configuration lines at the end to enable ssh, strace, and
even gdb.

The build images land in `build/tmp-glibc/deploy/images/qemu-optee32`.
For example, in addition to the combined "bios" image, the kernel, and
various formats of the root filesystem end up here.

If you make changes to the config file (or any recipes), simply run
`bitbake optee-image` to regenerate this image again.

### Using gdb

(TODO: Write a bit more here)
gdb runs directly on the target, but doesn't have source or debug
symbols.  It seems that the optee packages aren't generating proper
debug packages.

Gdb can also be used remotely, use:
```
$ bitbake gdb-cross
$ tmp-glibc/sysroots/x86_64-linux/usr/bin/arm-oe-linux-gnueabi/arm-oe-linux-gnueabi-gdb \
    tmp-glibc/sysroots/qemu-optee32/usr/bin/exename
(gdb) target remote localhost:2159
```
You can also run the gdb server:
```
# gdbserver tcp:2159 path/to/exe
```
and then use gdb on the host.  You 

## Working on the source to a project
OE has a useful development script called `devtool` which is useful
for using your own source tree instead of it fetching and building
itself.

For example, if you wanted to work on optee-test (the recipe name, not
the repo name), you would do:
```
$ devtool modify -x optee-test optee-test
```
Where the first 'optee-test' is the recipe name, and the second is a
local directory to clone the git repo into.  This tool will create a
'.bbappend' file under 'workspace' to tell bitbake to build from your
source tree, rather than getting the source itself.

You can do this for as many recipes as you'd like.  However, keep in
mind that recipes like this will end up always being built, since it
doesn't know if you have changed something.  To have OE stop using
your source, and go back to the original:
```
$ devtool reset optee-test
```
This will leave the source directory in place, but remote the bbappend
file.  Rerun the above 'modify' command, but without the '-x' to start
using your source directory again for builds.
