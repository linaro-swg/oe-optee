# Copyright (C) 2016 Linaro

DESCRIPTION = "Linux kernel with optee patches"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel
require recipes-kernel/linux/linux-dtb.inc

python () {
    machine = d.getVar("MACHINE", True)
    if machine == "qemu-optee32":
        d.setVar("LINUX_OPTEE_CONFIGS",
            ("arch/arm/configs/vexpress_defconfig " +
                "{0}/qemu.conf").format(d.getVar("WORKDIR", True)))
    elif machine == "fvp-optee64":
        d.setVar("LINUX_OPTEE_CONFIGS",
            ("arch/arm64/configs/defconfig " +
                "{0}/fvp.conf").format(d.getVar("WORKDIR", True)))
    elif machine == "hikey-optee64":
        d.setVar("LINUX_OPTEE_CONFIGS",
            ("arch/arm64/configs/defconfig " +
                "{0}/hikey.conf " +
                "{0}/usb_net_dm9601.conf " +
                "{0}/ftrace.conf").format(d.getVar("WORKDIR", True)))
    else:
        bb.fatal("Unsupported machine")
}

# Lets see what happens.
COMPATIBLE_MACHINE = "(qemu|fvp|hikey)-optee(32|64)"
KERNEL_IMAGETYPE ?= "Image"

KERNEL_CONFIG_COMMAND () {
    cd ${S}
    ./scripts/kconfig/merge_config.sh \
        -O ${B} \
        ${LINUX_OPTEE_CONFIGS} || die "Unable to configure"

    # oe_runmake -C ${S} O=${B} vexpress_defconfig ${WORKDIR}/qemu.conf
}
