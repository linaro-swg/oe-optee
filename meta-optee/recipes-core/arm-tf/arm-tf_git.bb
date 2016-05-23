# Copyright (C) 2016 David Brown <david.brown@linaro.org>
# Released under the MIT license (see COPYING.MIT for the terms)

#        <project remote="linaro-swg" path="arm-trusted-firmware" name="arm-trusted-firmware.git" revision="optee_paged_armtf_v1.2" />

SUMMARY = "Arm trusted firmware"
LICENSE = "BSD"
DEPENDS = "edk2 optee-os"

SRCREV = "69fb412f18ee40f9f7707da0d7411aada5ab60a0"
SRC_URI = "git://github.com/linaro-swg/arm-trusted-firmware.git;branch=optee_paged_armtf_v1.2"
PV = "1.2+git${SRCPV}"

LIC_FILES_CHKSUM = " \
    file://license.md;md5=829bdeb34c1d9044f393d5a16c068371 \
    "

S = "${WORKDIR}/git/"

inherit deploy

do_configure () {
    :
}

do_compile () {
    unset CFLAGS
    unset LDFLAGS
    unset AS
    oe_runmake \
        CROSS_COMPILE=${HOST_PREFIX} \
        BL32=${STAGING_DIR_HOST}/lib/firmware/tee.bin \
        BL33=${STAGING_DIR_HOST}/lib/firmware/FVP_AARCH64_EFI.fd \
        DEBUG=0 \
        ARM_TSP_RAM_LOCATION=tdram \
        PLAT=fvp \
        SPD=opteed \
        V=1 \
        all fip
}

do_install () {
    :
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 644 ${S}/build/fvp/release/bl1.bin ${DEPLOYDIR}
    install -m 644 ${S}/build/fvp/release/fip.bin ${DEPLOYDIR}
}

addtask deploy before do_package after do_install
