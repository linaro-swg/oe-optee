# Copyright (C) 2016 Linaro

DESCRIPTION = "Linux kernel with optee patches"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel

# Unsure here.  We don't use an appended dtb, but for non-qemu, we
# will need to get the dtbs out.
# require recipes-kernel/linux/linux-dtb.inc

# Lets see what happens.
COMPATIBLE_MACHINE = "qemu-optee32"
KERNEL_IMAGETYPE ?= "Image"

KERNEL_CONFIG_COMMAND () {
    cd ${S}
    ./scripts/kconfig/merge_config.sh \
        -O ${B} \
        arch/arm/configs/vexpress_defconfig \
        ${WORKDIR}/qemu.conf || die "Unable to configure"

    # oe_runmake -C ${S} O=${B} vexpress_defconfig ${WORKDIR}/qemu.conf
}
