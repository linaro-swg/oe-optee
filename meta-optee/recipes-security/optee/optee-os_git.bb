# Copyright (C) 2016 David Brown <davidb@davidb.org>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "OPTEE OS"
DESCRIPTION = "OPTEE OS"
HOMEPAGE = "http://www.optee.org/"
LICENSE = "BSD"
# SECTION = ""
# DEPENDS = ""

# The variables are a bit overwhelming to try and set with Bitbake's
# variable expansion, so just make the decision in Python.
python () {
    machine = d.getVar("MACHINE", True)
    if machine == "qemu-optee32":
        d.setVar("EXTRA_OEMAKE",
            ("PLATFORM=vexpress-qemu_virt" +
             " CFG_ARM64_core=n" +
             " CROSS_COMPILE_core={0}" +
             " CROSS_COMPILE_ta_arm32={0}" +
             " ta-targets=ta_arm32").format(d.getVar("HOST_PREFIX", True)))
        d.setVar("OPTEE_SHORT_MACHINE", "vexpress")
        d.setVar("OPTEE_ARCH", "arm32")
    elif machine == "fvp-optee64":
        d.setVar("EXTRA_OEMAKE",
            ("V=1 PLATFORM=vexpress-fvp" +
             " CFG_ARM64_core=y" +
             " CFG_ARM32_core=n" +
             " CROSS_COMPILE_core={0}" +
             " CROSS_COMPILE_ta_arm64={0}" +
             " ta-targets=ta_arm64").format(d.getVar("HOST_PREFIX", True)))
        d.setVar("OPTEE_SHORT_MACHINE", "vexpress")
        d.setVar("OPTEE_ARCH", "arm64")
    elif machine == "hikey-optee64":
        d.setVar("EXTRA_OEMAKE",
            ("V=1 PLATFORM=hikey" +
             " CFG_ARM64_core=y" +
             " CFG_ARM32_core=n" +
             " CROSS_COMPILE_core={0}" +
             " CROSS_COMPILE_ta_arm64={0}" +
             " ta-targets=ta_arm64").format(d.getVar("HOST_PREFIX", True)))
        d.setVar("OPTEE_SHORT_MACHINE", "hikey")
        d.setVar("OPTEE_ARCH", "arm64")
    else:
        bb.fatal("optee-os doesn't recognize this MACHINE")
}

inherit deploy

SRC_URI = "git://github.com/OP-TEE/optee_os.git"
SRCREV = "6732c943e59698dd4318f84dec276390f66ffac2"
PR = "r0"
PV = "2.0.0+git${SRCPV}"
LIC_FILES_CHKSUM = "file://LICENSE;md5=69663ab153298557a59c67a60a743e5b"

S = "${WORKDIR}/git"

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
