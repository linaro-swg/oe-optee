# Optee demonstration for OpenEmbedded

This is a fork of the official OpenEmbedded Core repo, with some minor
changes, and an optee-layer added.  This optee layer adds a few
things:

- a new MACHINE 'qemu-optee32'.  This machine runs within qemu,
  building a secure bios.
- optee-linux.  A kernel, with the optee drivers in it.
- optee-image.  A full image that is ready to run with a provided
  script within qemu.

# Setup and build

OE has very few system requirements, generally you'll need cpio and
texinfo.  It will print early on any packages that are missing.  Most
of everything else that it needs will be downloaded and built.

Building optee-os, however, will require the host's python to have
pycrypto installed.

To build this image, you'll need about 5 GB of space.  If you disable
'rm-work' to leave build trees expanded, you'll need a lot more.

The first time you build, it will take a while, as OE builds all of
the necessary toolchains itself.  Once this is built, as long as you
keep the build/sstate-cache directory, subsequent builds will only
rebuild things that haven't been built (or have changed).

## Clone this repo
```
$ cd path/to/workdir
$ git clone https://github.com/d3zd3z/oe-optee
```

## Get bitbake and openembedded-core
Bitbake and openembedded-core live in separate repos.  I've added them
as git submodules to make it easy to get the right branches.
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
This can also be done later, and the image quickly regenerated.

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

Entering `c` and return in at the qemu prompt should start boot the
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
