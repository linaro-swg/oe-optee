# Copyright (C) 2016 David Brown <david.brown@linaro.org>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "OPTEE Client libs"
HOMEPAGE = "http://www.optee.org/"
LICENSE = "BSD"
DEPENDS = "python-pycrypto-native"

SRC_URI = " \
    git://github.com/OP-TEE/optee_client.git \
    file://tee-supplicant.init \
"
SRCREV = "17d1addc465a667f375837cdbe4fa7ebac08539b"
PR = "r0"
PV = "2.0.0+git${SRCPV}"
LIC_FILES_CHKSUM = "file://LICENSE;md5=69663ab153298557a59c67a60a743e5b"

S = "${WORKDIR}/git"
B = "${WORKDIR}/git/out"

inherit update-rc.d
inherit pythonnative

# Note that the Makefiles for optee-client are broken, and O= must be
# a relative path.  To make this work, just don't set anything, and
# always use ${S}/out, instead of ${B}.  This breaks 'devtool'.

do_compile () {
    oe_runmake -C ${S} EXPORT_DIR=${D}/usr build-libteec
    oe_runmake -C ${S} EXPORT_DIR=${D}/usr build-tee-supplicant
}

do_install () {
    install -d ${D}/usr
    oe_runmake -C ${S} EXPORT_DIR=${D}/usr install

    install -d ${D}/usr/bin
    install ${S}/out/tee-supplicant/tee-supplicant ${D}/usr/bin

    # Make them proper symlinks.
    cd ${D}/usr/lib
    rm libteec.so libteec.so.1
    ln -s libteec.so.1.0 libteec.so.1
    ln -s libteec.so.1 libteec.so

    # Startup scrpit.
    install -d ${D}/etc/init.d
    install -m 755 ${WORKDIR}/tee-supplicant.init ${D}/etc/init.d/tee-supplicant
}

INITSCRIPT_NAME = "tee-supplicant"

PACKAGES += "tee-supplicant"
FILES_${PN} = "${libdir}/libteec*"
FILES_tee-supplicant = "${bindir}/tee-supplicant"
FILES_${PN} += "${sysconfdir}/init.d/tee-supplicant"

# Debugging
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
