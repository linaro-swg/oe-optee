# Copyright (C) 2016 David Brown <davidb@davidb.org>
# Released under the MIT license (see COPYING.MIT for the terms)

require linux-optee.inc

SRCREV = "2faeddf56a2b168559d77600dedc24f626c68586"
PV = "4.5+git${SRCPV}"

SRCREV_hikey-optee64 = "4ac587ba47e48e7f169e1921ae269e9137f53887"

SRC_URI = "git://github.com/linaro-swg/linux.git;branch=optee"

# The hikey board currently uses its own kernel (based off of 4.4).
SRC_URI_hikey-optee64 = "git://github.com/96boards-hikey/linux.git;branch=hikey-mainline-rebase"
PV_hikey-optee64 = "4.4+git${SRCPV}"

SRC_URI_append_qemu-optee32   = " file://qemu.conf"
SRC_URI_append_fvp-optee64    = " file://fvp.conf"
SRC_URI_append_hikey-optee64 = " file://hikey.conf"
SRC_URI_append_hikey-optee64 = " file://usb_net_dm9601.conf"
SRC_URI_append_hikey-optee64 = " file://ftrace.conf"

DESCRIPTION = "Kernel for optee, blah blah blah"

S = "${WORKDIR}/git"

KERNEL_DEVICETREE_fvp-optee64 = "arm/foundation-v8.dtb"
KERNEL_DEVICETREE_hikey-optee64 = "hisilicon/hi6220-hikey.dtb"
