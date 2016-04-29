#! /bin/bash

root=build/tmp-glibc/sysroots/x86_64-linux
deploy=build/tmp-glibc/deploy/images/qemu-optee32

$root/usr/bin/qemu-system-arm \
	-s -S \
	-nographic \
	-machine virt,secure=on \
	-cpu cortex-a15 \
	-bios $deploy/optee-image-qemu-optee32.bios \
	-serial tcp:localhost:54320 -serial tcp:localhost:54321 \
	-m 1058 \
	-netdev user,id=unet,hostfwd=tcp::5001-:22,hostfwd=tcp::2159-:2159 \
	-device virtio-net-device,netdev=unet \
	-drive file=${deploy}/optee-image-qemu-optee32.ext4,if=virtio,format=raw \

exit 0

	-kernel /mnt/oe/oe-core/build/tmp-glibc/deploy/images/qemuarm/zImage-qemuarm.bin \
	-net user \
	-initrd tmp-glibc/deploy/images/qemuarm/optee-image-qemuarm.cpio.gz \
	--append "console=ttyAMA0,1152000"
	-net nic,model=virtio \
	-net tap,vlan=0,ifname=tap0,script=no,downscript=no \

	-netdev user,id=unet \
	-device virtio-net-device,netdev=unet \
	-show-cursor \
	-serial mon:vc \
	-usb -usbdevice wacom-tablet \

	-drive file=/mnt/oe/oe-core/build/tmp-glibc/deploy/images/qemuarm/core-image-minimal-qemuarm-20160420193340.rootfs.ext4,if=virtio,format=raw \
	--append "root=/dev/vda rw console=ttyAMA0,115200 console=ttyAMA0 ip=192.168.7.2::192.168.7.1:255.255.255.0 mem=128M highres=off rootfstype=ext4 "
