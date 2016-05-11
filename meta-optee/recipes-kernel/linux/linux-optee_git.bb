# Copyright (C) 2016 David Brown <davidb@davidb.org>
# Released under the MIT license (see COPYING.MIT for the terms)

require linux-optee.inc

SRCREV = "84c7fe3be96cb570561cbd9926d47f76b93a6fed"
PV = "4.5+git${SRCPV}"

SRC_URI = "git://github.com/linaro-swg/linux.git;branch=optee \
           file://qemu.conf \
"

DESCRIPTION = "Kernel for optee, blah blah blah"

S = "${WORKDIR}/git"
