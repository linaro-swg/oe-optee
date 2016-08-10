# Copyright (C) 2016 David Brown <davidb@davidb.org>
# Released under the MIT license (see COPYING.MIT for the terms)

require linux-optee.inc

SRCREV = "ca9cb996666675ed0d677d478e6b418ed62997ce"
PV = "4.5+git${SRCPV}"

SRC_URI = "git://github.com/linaro-swg/linux.git;branch=optee"

SRC_URI_append_qemu-optee32   = " file://qemu.conf"
SRC_URI_append_qemu-optee64   = " file://qemu.conf"
SRC_URI_append_fvp-optee64    = " file://fvp.conf"
SRC_URI_append_hikey-optee64 = " file://hikey.conf"
SRC_URI_append_hikey-optee64 = " file://usb_net_dm9601.conf"
SRC_URI_append_hikey-optee64 = " file://ftrace.conf"

DESCRIPTION = "Kernel for optee, blah blah blah"

S = "${WORKDIR}/git"

KERNEL_DEVICETREE_fvp-optee64 = "arm/foundation-v8.dtb"
KERNEL_DEVICETREE_hikey-optee64 = "hisilicon/hi6220-hikey.dtb"
