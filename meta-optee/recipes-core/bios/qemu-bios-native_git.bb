SUMMARY = "BIOS buildng tool for Qemu+optee"
LICENSE = "BSD"

inherit native

SRCREV = "dad8551a66d3ca121ad1f2e577f68dbb4b8e76db"
SRC_URI = "git://github.com/linaro-swg/bios_qemu_tz_arm.git"
PV = "0.0+git${SRCPV}"

# TODO: This package is missing any license information.  We can use a
# source file (which has the license), but this will be wrong when the
# revision changes.
LIC_FILES_CHKSUM = "file://bios/main.c;md5=01e0350fc6da2e564dc71b422f6c3354"

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
