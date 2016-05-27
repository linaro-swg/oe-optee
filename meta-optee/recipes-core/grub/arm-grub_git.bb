# Copyright (C) 2016 David Brown <david.brown@linaro.org>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Grub cross compiled for ARM"
LICENSE = "GPLv3"
DEPENDS = ""

SRCREV = "b524fa27f56381bb0efa4944e36f50265113aee5"
SRC_URI = " \
    git://git.savannah.gnu.org/grub.git \
    file://grub.configfile \
    file://grub_uart0.cfg \
    file://grub_uart3.cfg \
"
PV = "2.00+git${SRCPV}"

S = "${WORKDIR}/git/"
B = "${WORKDIR}/build/"

inherit deploy

# This isn't really a native package, but it is built oddly.  We
# configure grub with 'CC' set to the native compiler, and with
# TARGET_CC set to the target compiler.
# The only product of this build is the efi bootloader file.

EXTRA_OECONF += " \
    --target=aarch64 \
    --enable-boot-time \
"

# Rearrange the variables, using assignment, and exporting them to the
# configure and build.
export TARGET_CC := "${CC}"
export TARGET_STRIP := "${STRIP}"
export TARGET_OBJCOPY := "${OBJCOPY}"
export CC := "${BUILD_CC}"
export CPP := "${BUILD_CPP}"
export CFLAGS := "${BUILD_CFLAGS}"
export LDLAGS := "${BUILD_LDFLAGS}"
export LIBS := ""
export STRIP := "${BUILD_STRIP}"
export OBJCOPY := "objcopy"

do_configure () {
    ( cd ${S}
      ./autogen.sh )
    ( cd ${B}
      ${S}/configure \
        ${EXTRA_OECONF} )
}

do_qa_configure () {
    :
}

do_compile () {
    oe_runmake -C ${B}
    ( cd ${B}
      ./grub-mkimage \
        --verbose \
        --output=grubaa64.efi \
        --config=${WORKDIR}/grub.configfile \
        --format=arm64-efi \
        --directory=grub-core \
        --prefix=/boot/grub \
        boot chain configfile efinet ext2 fat gettext help linux \
        loadenv lsefi normal part_gpt part_msdos read search \
        search_fs_file search_fs_uuid search_label terminal terminfo \
        tftp time )
}

do_install () {
    :
}

do_deploy () {
    install -d ${DEPLOYDIR}
    install -m 644 ${B}/grubaa64.efi ${DEPLOYDIR}
    install -m 644 ${WORKDIR}/grub_uart0.cfg ${DEPLOYDIR}
    install -m 644 ${WORKDIR}/grub_uart3.cfg ${DEPLOYDIR}
}

addtask deploy before do_package after do_install

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# Some stuff from native.class that we do want.
deltask package
deltask packagedata
deltask package_qa
deltask package_write_ipk
deltask package_write_deb
deltask package_write_rpm
deltask package_write
