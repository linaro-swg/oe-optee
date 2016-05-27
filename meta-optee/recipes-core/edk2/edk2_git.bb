SUMMARY = "EDK2 bootloader"
LICENSE = "BSD"

# The hikey image contains a proprietary binary blob.
LICENSE_hikey-optee64 = "CLOSED"

SRCREV_fvp-optee64 = "131aec010f565a747ac51e6a95e30a37b5df15cc"
SRC_URI_fvp-optee64 = "git://git.linaro.org/landing-teams/working/arm/edk2.git;branch=16.01"
PV_fvp-optee64 = "16.01+git${SRCPV}"

SRCREV_hikey-optee64 = "7da6cdf18127ee52e79f3cf990c8d630fd037784"
SRC_URI_hikey-optee64 = "git://github.com/96boards/edk2.git;branch=hikey"
PV_fvp-optee64 = "hikey+git${SRCPV}"

S = "${WORKDIR}/git/"

EDK2_ARGS = "${@bb.fatal('EDK2 not supported on this platform')}"

# Build configuration for 64-bit FVP.
EDK2_ARGS_fvp-optee64 = " \
    -f ArmPlatformPkg/Scripts/Makefile \
    EDK2_ARCH=AARCH64 \
    EDK2_DSC=ArmPlatformPkg/ArmVExpressPkg/ArmVExpress-FVP-AArch64.dsc \
    EDK2_TOOLCHAIN=GCC49 EDK2_BUILD=RELEASE \
    EDK2_MACROS='-n 6 -D ARM_FOUNDATION_FVP=1 -D ARM_FVP_BOOT_ANDROID_FROM_SEMIHOSTING=1' \
"
EDK2_IMAGE_fvp-optee64 = "Build/ArmVExpress-FVP-AArch64/RELEASE_GCC49/FV/FVP_AARCH64_EFI.fd"

# Build configuration for 64-bit Hikey.
EDK2_ARGS_hikey-optee64 = " \
    -f HisiPkg/HiKeyPkg/Makefile \
    EDK2_ARCH=AARCH64 \
    EDK2_DSC=HisiPkg/HiKeyPkg/HiKey.dsc \
    EDK2_TOOLCHAIN=GCC49 EDK2_BUILD=RELEASE \
"
EDK2_IMAGE_hikey-optee64 = "Build/HiKey/RELEASE_GCC49/FV/BL33_AP_UEFI.fd"

# inherit deploy

do_configure () {
    ${BASH} edksetup.sh
    oe_runmake -C ${S}/BaseTools
}

do_compile () {
    oe_runmake GCC49_AARCH64_PREFIX=${HOST_PREFIX} \
        -j1 \
        -C ${S} \
        ${EDK2_ARGS}
}

do_install () {
    install -d ${D}/lib/firmware
    install -m 644 ${S}/${EDK2_IMAGE} \
        ${D}/lib/firmware
}

do_install_append_hikey-optee64 () {
    install -m 644 ${S}/HisiPkg/HiKeyPkg/NonFree/mcuimage.bin \
        ${D}/lib/firmware
    install -m 644 ${S}/Build/HiKey/RELEASE_GCC49/AARCH64/AndroidFastbootApp.efi \
        ${D}/lib/firmware/fastboot.efi
}

FILES_${PN} = "/lib/firmware"

LIC_FILES_CHKSUM = " \
    file://StdLib/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
"
