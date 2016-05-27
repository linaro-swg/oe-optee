# Assemble grub and images into a fat image for an EFI partition.

inherit image_types

# TODO: The kernel, other than this symlink seems to have going
# away.
UEFI_KERNEL_BIOS ?= "${DEPLOY_DIR_IMAGE}/Image"
UEFI_DTB ?= "${DEPLOY_DIR_IMAGE}/Image-hi6220-hikey.dtb"

# The rootfs can either be the real rootfs, or set to "/dev/null" to
# include a blank rootfs (and expecting the real rootfs to be
# mountable.
UEFI_BIOS_INITRD ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.cpio.gz"

IMAGE_CMD_uefi () {
    rm -rf "${WORKDIR}/bios-work"
    mkdir "${WORKDIR}/bios-work"
    mformat -i "${WORKDIR}/bios-work/boot.img" \
        -n 63 -h 255 -T 98280 -v BOOT -C \
        ::
    mmd -i "${WORKDIR}/bios-work/boot.img" ::EFI
    mmd -i "${WORKDIR}/bios-work/boot.img" ::EFI/BOOT

    mcopy -i "${WORKDIR}/bios-work/boot.img" \
        ${UEFI_KERNEL_BIOS} ::Image
    mcopy -i "${WORKDIR}/bios-work/boot.img" \
        ${UEFI_DTB} ::hi6220-hikey.dtb
    mcopy -i "${WORKDIR}/bios-work/boot.img" \
        ${UEFI_BIOS_INITRD} ::ramdisk.img

    mcopy -i "${WORKDIR}/bios-work/boot.img" \
        ${DEPLOY_DIR_IMAGE}/grubaa64.efi ::EFI/BOOT/grubaa64.efi

    mcopy -i "${WORKDIR}/bios-work/boot.img" \
        ${STAGING_DIR_HOST}/lib/firmware/fastboot.efi ::EFI/BOOT/fastboot.efi

    # TODO: Allow this to be configured.
    mcopy -i "${WORKDIR}/bios-work/boot.img" \
        ${DEPLOY_DIR_IMAGE}/grub_uart3.cfg ::EFI/BOOT/grub.cfg

    cp ${WORKDIR}/bios-work/boot.img ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.uefi
}

IMAGE_TYPEDEP_uefi = "cpio.gz"
IMAGE_DEPENDS_uefi = "arm-grub virtual/kernel dosfstools-native mtools-native"

IMAGE_TYPES += "uefi"
