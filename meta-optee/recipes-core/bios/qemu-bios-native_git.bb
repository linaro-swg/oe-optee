SUMMARY = "BIOS buildng tool for Qemu+optee"
LICENSE = "BSD"

inherit native

SRCREV = "a1d0240357f977b51cc530a5e7f67f20e401a5b2"
SRC_URI = "git://github.com/linaro-swg/bios_qemu_tz_arm.git \
"
PV = "0.0+git${SRCPV}"

# TODO: This package is missing any license information.  We can use a
# source file (which has the license), but this will be wrong when the
# revision changes.
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=7d06880e6eb0a003bc215cd7efa30c6e \
    file://COPYING.NEWLIB;md5=fced02ba02d66f274d4847d27e80af74 \
"

S = "${WORKDIR}/git/"

do_configure () {
    :
}

do_compile () {
    :
}

# This is a little weird, but we put the source under the deploy dir,
# so that the package tool can find it?
do_install() {
    install -d ${D}/${prefix}/lib/qemu-bios/
    cp -a ${S}/* ${D}/${prefix}/lib/qemu-bios/
    install -d ${D}/${prefix}/bin/
    unset LDFLAGS
    export CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_HOST}"
    cat > ${D}/${prefix}/bin/mkbios.sh <<EOF
#! /bin/bash

echo "Building Qemu TZ bios"
base=\$(realpath \$(dirname \$0)/../lib/qemu-bios)
# LDFLAGS may have linker options, which fail if being passed to the
# linker.
echo "LDFLAGS: \${LDFLAGS}"
echo "CFLAGS: \${LDFLAGS}"
make -C \$base "\$@"
EOF
    chmod 755 ${D}/${prefix}/bin/mkbios.sh
}
