# Copyright (C) 2016 David Brown <davidb@davidb.org>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "OPTEE OS"
DESCRIPTION = "OPTEE OS"
HOMEPAGE = "http://www.optee.org/"
LICENSE = "BSD"
# SECTION = ""
# DEPENDS = ""

# Determine the optee os based on our build machine.
OPTEE_MACHINE_qemuarm = "vexpress"
OPTEE_MACHINE_qemu-optee32 = "vexpress-qemu_virt"

OPTEE_SHORT_MACHINE_qemu-optee32 = "vexpress"

inherit deploy

SRC_URI = "git://github.com/OP-TEE/optee_os.git"
SRCREV = "b44708c1c842a9e1ebb63c7f6b43774795669c7a"
PR = "r0"
PV = "2.0.0+git${SRCPV}"
LIC_FILES_CHKSUM = "file://LICENSE;md5=69663ab153298557a59c67a60a743e5b"

S = "${WORKDIR}/git"

# TODO: These need to come from the machine so that we can use the
# same recipe no matter which we build for.
EXTRA_OEMAKE = "PLATFORM=${OPTEE_MACHINE}"
EXTRA_OEMAKE += "CFG_ARM64_core=n"
EXTRA_OEMAKE += "CROSS_COMPILE_core=${HOST_PREFIX}"
EXTRA_OEMAKE += "CROSS_COMPILE_ta_arm32=${HOST_PREFIX}"
EXTRA_OEMAKE += "ta-targets=ta_arm32"

OPTEE_ARCH_armv7a = "arm32"
OPTEE_ARCH_aarch64 = "arm64"
OPTEE_ARCH_qemuarm = "arm32"
OPTEE_ARCH_qemu-optee32 = "arm32"

CFG_TEE_TA_LOG_LEVEL ?= "1"
CFG_TEE_CORE_LOG_LEVEL ?= "1"

do_compile () {
    unset LDFLAGS
    export CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_HOST}"

    ${HOST_PREFIX}gcc --version
    # TODO: The log level should be configurable at the top level.
    oe_runmake all \
        CFG_TEE_TA_LOG_LEVEL=${CFG_TEE_TA_LOG_LEVEL} \
	CFG_TEE_CORE_LOG_LEVEL=${CFG_TEE_CORE_LOG_LEVEL}
}

do_install () {
    echo "package: ${PACKAGE_ARCH}"
    echo "arch: ${MACHINE_ARCH}"
    echo "optee arch: '${OPTEE_ARCH}'"
    echo "optee machine: '${OPTEE_MACHINE}'"
    install -d ${D}/lib/firmware/
    install -m 644 ${B}/out/arm-plat-${OPTEE_SHORT_MACHINE}/core/*.bin ${D}/lib/firmware/

    # Install the TA devkit
    install -d ${D}/usr/include/optee/export-user_ta/

    for f in ${B}/out/arm-plat-${OPTEE_SHORT_MACHINE}/export-ta_${OPTEE_ARCH}/*; do
        cp -aR $f ${D}/usr/include/optee/export-user_ta/
    done
}

INHIBIT_PACKAGE_STRIP = "1"

FILES_${PN} = "/lib/firmware/"
FILES_${PN}-dev = "/usr/include/optee"
INSANE_SKIP_${PN}-dev = "staticdev"
