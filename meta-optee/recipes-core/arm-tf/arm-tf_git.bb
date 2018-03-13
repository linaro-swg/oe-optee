# Copyright (C) 2016 David Brown <david.brown@linaro.org>
# Released under the MIT license (see COPYING.MIT for the terms)

#        <project remote="linaro-swg" path="arm-trusted-firmware" name="arm-trusted-firmware.git" revision="optee_paged_armtf_v1.2" />

SUMMARY = "Arm trusted firmware"
LICENSE = "BSD"
DEPENDS = "edk2 optee-os"

SRCREV_fvp-optee64 = "69fb412f18ee40f9f7707da0d7411aada5ab60a0"
SRC_URI_fvp-optee64 = "git://github.com/linaro-swg/arm-trusted-firmware.git;branch=optee_paged_armtf_v1.2"
PV_fvp-optee64 = "1.2+git${SRCPV}"

SRCREV_hikey-optee64 = "cb6cd2bc548184462871bc5e30190941df0aa4da"
SRC_URI_hikey-optee64 = "git://github.com/96boards-hikey/arm-trusted-firmware.git;branch=hikey"
PV_hikey-optee64 = "1.1+git${SRCPV}"

# Currently, we don't build the edk2 for this platform, but instead just use a
# prebuilt snapshot.  Note that this seems to break out-of-tree
# compilation, as the EXTERNALSRC seems to prevent it from downloading
# the snapshot.
SRCREV_qemu-optee64 = "b3a3dde456445d294d83b18faf2769f6aa14ab48"
SRC_URI_qemu-optee64 = " \
    git://github.com/linaro-swg/arm-trusted-firmware.git;branch=optee_v2.1.0_paged_armtf_v1.2 \
    file://0001-workaround-for-macro-error.patch \
    http://snapshots.linaro.org/components/kernel/leg-virt-tianocore-edk2-upstream/989/QEMU-KERNEL-AARCH64/RELEASE_GCC49/QEMU_EFI.fd;sha256sum=2da5b905a30c04bbf999975f844da123f40fe9d60ed276ca6e6c3b595e35b16c;md5sum=b3cee74b5b9fcd076772a7bd2e06ff27 \
"
PV_qemu-optee64 = "1.2+git${SRCPV}"
DEPENDS_qemu-optee64 = "optee-os"

COMPATIBLE_MACHINE = "(fvp|hikey|qemu)-optee64"

ARMTF_ARGS_fvp-optee64 = " \
        BL32=${STAGING_DIR_HOST}/lib/firmware/tee.bin \
        BL33=${STAGING_DIR_HOST}/lib/firmware/FVP_AARCH64_EFI.fd \
        DEBUG=0 \
        ARM_TSP_RAM_LOCATION=tdram \
        PLAT=fvp \
        SPD=opteed \
"

ARMTF_ARGS_hikey-optee64 = " \
        BL32=${STAGING_DIR_HOST}/lib/firmware/tee.bin \
        BL33=${STAGING_DIR_HOST}/lib/firmware/BL33_AP_UEFI.fd \
        BL30=${STAGING_DIR_HOST}/lib/firmware/mcuimage.bin \
        DEBUG=0 \
        PLAT=hikey \
        SPD=opteed \
"

ARMTF_ARGS_qemu-optee64 = " \
        BL32=${STAGING_DIR_HOST}/lib/firmware/tee.bin \
        BL33=${WORKDIR}/QEMU_EFI.fd \
        ARM_TSP_RAM_LOCATION=tdram \
        PLAT=qemu \
        DEBUG=0 \
        LOG_LEVEL=50 \
        ERROR_DEPRECATED=1 \
        BL32_RAM_LOCATION=tdram \
        SPD=opteed \
"

ARMTF_PLAT_fvp-optee64 = "fvp"
ARMTF_PLAT_hikey-optee64 = "hikey"
ARMTF_PLAT_qemu-optee64 = "qemu"

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
        V=1 \
        ${ARMTF_ARGS} \
        all fip
}

do_install () {
    :
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 644 ${S}/build/${ARMTF_PLAT}/release/bl1.bin ${DEPLOYDIR}
    install -m 644 ${S}/build/${ARMTF_PLAT}/release/bl2.bin ${DEPLOYDIR}
    install -m 644 ${S}/build/${ARMTF_PLAT}/release/bl31.bin ${DEPLOYDIR}
    install -m 644 ${S}/build/${ARMTF_PLAT}/release/fip.bin ${DEPLOYDIR}
}

addtask deploy before do_package after do_install
