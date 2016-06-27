# Copyright (C) 2016 David Brown <davidb@davidb.org>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "qemu terminal utility"
LICENSE = "BSD"

inherit native

# TODO: Put this upstream instead of like this.
LIC_FILES_CHKSUM = "file://LICENSE;md5=8276d74bfe4bafd148ef0507ad386949"

SRCREV = "5493a6e7c264536f5ca63fe7511e5eed991e4f20"
SRC_URI = "git://github.com/linaro-swg/soc_term.git"
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
