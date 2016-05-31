#! /bin/bash

# Assuming fastboot is in the path (TODO: Consider building this).
# This will flash the parts of the image we are able to build, notably
# the bootloader, secure image, and kernel/initrd.

deploy=build-hikey/tmp-glibc/deploy/images/hikey-optee64

fastboot flash -u bl1 $deploy/bl1.bin
fastboot flash -u fastboot $deploy/fip.bin
fastboot flash -u boot $deploy/optee-image-hikey-optee64.uefi

sleep 2
fastboot reboot
