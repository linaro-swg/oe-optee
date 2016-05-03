# Build a qemu bios image out of the various images.

inherit image_types

COMPRESSIONTYPES += "gz.bios"

# TODO: The kernel, other than this symlink seems to have going
# away.
KERNEL_BIOS ?= "${DEPLOY_DIR_IMAGE}/zImage"

# The rootfs can either be the real rootfs, or set to "/dev/null" to
# include a blank rootfs (and expecting the real rootfs to be
# mountable.
BIOS_INITRD ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.cpio.gz"

# If the rootfs is not set, BIOS_ROOT_DEVICE should be set to
# something like "root=/dev/sda" for where the given image will be
# accessed.
BIOS_ROOT_DEVICE ?= ""

IMAGE_CMD_bios () {
    libgcc=$(find ${STAGING_DIR_HOST} -name libgcc.a; head -1)
    rm -rf "${WORKDIR}/bios-work"
    mkdir "${WORKDIR}/bios-work"
    echo 'Empty' > ${DEPLOY_DIR_IMAGE}/empty.bin
    mkbios.sh \
        V=0 \
        CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_HOST}" \
        LDFLAGS="" \
        CROSS_COMPILE=${HOST_PREFIX} \
        O=${WORKDIR}/bios-work \
        libgcc=$(${HOST_PREFIX}gcc ${TOOLCHAIN_OPTIONS} --print-libgcc-file-name) \
        BIOS_NSEC_BLOB=${KERNEL_BIOS} \
        BIOS_NSEC_ROOTFS=${BIOS_INITRD} \
        BIOS_SECURE_BLOB=${STAGING_DIR_HOST}/lib/firmware/tee.bin \
        BIOS_ROOT_DEVICE="${BIOS_ROOT_DEVICE}" \
        PLATFORM_FLAVOR=virt
    cp ${WORKDIR}/bios-work/bios.bin ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.bios
}

IMAGE_TYPEDEP_bios = "cpio.gz"
IMAGE_DEPENDS_bios = "optee-os qemu-bios-native virtual/kernel"
