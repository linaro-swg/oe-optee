# Copyright (C) 2016 David Brown <david.brown@linaro.org>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "OPTEE test"
HOMEPAGE = "http://www.optee.org/"

# TODO: Get the license files into the repo, and refer to them here.
LICENSE = "CLOSED"

DEPENDS = "optee-os optee-client"

SRC_URI = "git://github.com/OP-TEE/optee_test.git \
           file://0001-Silence-may-be-unset-CLang-warning.patch \
"
SRCREV = "eba256d9cf0d73bcd3957ce3dffbe1098681c2c6"
PR = "r0"
PV = "2.0.0+git${SRCPV}"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=69663ab153298557a59c67a60a743e5b"

S = "${WORKDIR}/git"

do_compile () {
    export TA_DEV_KIT_DIR=${STAGING_INCDIR}/optee/export-user_ta
    export TEEC_EXPORT=${STAGING_DIR_HOST}/usr

    export OPTEE_CLIENT_EXPORT=${STAGING_DIR_HOST}/usr
    oe_runmake V=1 CROSS_COMPILE_HOST=${HOST_PREFIX} \
        CROSS_COMPILE_TA=${HOST_PREFIX}
}

do_install () {
    install -d ${D}/usr/bin
    install -d ${D}/lib/optee_armtz

    install ${S}/out/xtest/xtest ${D}/usr/bin/

    find ${S}/out/ta -name '*.ta' | while read name; do
        install -m 444 $name ${D}/lib/optee_armtz/
    done
}

FILES_${PN} = "/usr/bin/ /lib/optee_armtz/"

INHIBIT_PACKAGE_STRIP = "1"
