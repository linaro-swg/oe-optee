# Copyright (C) 2016 David Brown <davidb@davidb.org>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Optee reference OE image"
# HOMEPAGE = ""
LICENSE = "MIT"
#SECTION = ""

DEPENDS_fvp-optee64 = "arm-tf"
DEPENDS_hikey-optee64 = "arm-tf"
DEPENDS_qemu-optee64 = "arm-tf"

# By default the image has quite a bit of stuff in it.  Uncomment
# these few lines to only install the bare minimum, making a minimal
# busybox setup.
IMAGE_INSTALL = "packagegroup-core-boot ${ROOTFS_PKGMANAGE_BOOTSTRAP} ${CORE_IMAGE_EXTRA_INSTALL}"
IMAGE_LINGUAS = " "
IMAGE_INSTALL += "coreutils file"

# Install the packages for optee.
IMAGE_INSTALL += "tee-supplicant"
IMAGE_INSTALL += "optee-test"

#SRC_URI = ""

PV = "1.0.0"
PR = "r1"

inherit core-image

# In addition, build a cpio.gz rootfs.  This only really makes sense
# with the minimal image.
IMAGE_FSTYPES += "cpio.gz"

# IMAGE_ROOTFS_SIZE ?= "8192"
# IMAGE_ROOTFS_EXTRA_SPACE_append = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "" ,d)}"
