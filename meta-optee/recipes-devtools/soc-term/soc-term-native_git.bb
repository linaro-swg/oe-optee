# Copyright (C) 2016 David Brown <davidb@davidb.org>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "qemu terminal utility"
LICENSE = "BSD"

inherit native

# TODO: Put this upstream instead of like this.
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=f6c51da2169a596879f7e3714c4e47b3"

SRCREV = "7f2da75e9f106bc3e7ed81dd0ff541a49e04dd8c"
SRC_URI = "git://github.com/linaro-swg/soc_term.git \
           file://LICENSE \
"
PV = "0.0+git${SRCPV}"

S = "${WORKDIR}/git/"

do_configure () {
    :
}

do_compile () {
    oe_runmake -C ${S} || die "make failed"
}

do_install () {
    install -d ${D}/${prefix}/bin/
    install ${S}/soc_term ${D}/${prefix}/bin
}
