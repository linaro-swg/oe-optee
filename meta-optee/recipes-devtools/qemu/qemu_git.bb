# Simplified build of qemu for the purposes of Optee testing.
# We need the latest version from git.
#
# Just build a very simple version just for our purposes.

SUMMARY = "Fast open source processor emulator"
HOMEPAGE = "http://qemu.org/"
LICENSE = "GPLv2 & LGPLv2.1"
DEPENDS = "glib-2.0 zlib pixman"

inherit autotools
BBCLASSEXTEND = "native nativesdk"

SRCREV = "173ff58580b383a7841b18fddb293038c9d40d1c"
SRC_URI = "git://github.com/qemu/qemu.git"
PV = "2.7.0+git${SRCPV}"

LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
                    file://COPYING.LIB;md5=79ffa0ec772fa86740948cb7327a0cc7 "

S = "${WORKDIR}/git/"

#EXTRA_OECONF = "--target-list=arm-softmmu"
#EXTRA_OECONF += "--extra-cflags=\"-Wno-error-""
#EXTRA_OECONF += "--disable-strip"

do_configure () {
    (cd ${S}; git submodule update --init dtc)
    ${S}/configure --target-list=arm-softmmu,aarch64-softmmu --extra-cflags="-Wno-error" \
        --prefix=${prefix} --sysconfdir=${sysconfdir} --libexecdir=${libexecdir} \
        --localstatedir=${localstatedir} \
	--enable-fdt \
        --disable-strip \
        ${EXTRA_OECONF}
}

do_install () {
    export STRIP="true"
    autotools_do_install
}
