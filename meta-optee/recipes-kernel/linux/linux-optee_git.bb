# Copyright (C) 2016 David Brown <davidb@davidb.org>
# Released under the MIT license (see COPYING.MIT for the terms)

require linux-optee.inc

SRCREV = "14c79ba4cacd131917e391cb56e4f5e7e5101adb"
PV = "4.5+git${SRCPV}"

SRC_URI = "git://github.com/linaro-swg/linux.git;branch=optee \
           file://qemu.conf \
           file://fvp.conf \
"

DESCRIPTION = "Kernel for optee, blah blah blah"

S = "${WORKDIR}/git"

KERNEL_DEVICETREE_fvp-optee64 = "arm/foundation-v8.dtb"
