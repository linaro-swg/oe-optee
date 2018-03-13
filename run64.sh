#! /bin/bash

root=build-64/tmp-glibc/sysroots/x86_64-linux
deploy=build-64/tmp-glibc/deploy/images/qemu-optee64

# When running semihosting mode, we need access to all of the images
# in one place.
work=tmpqemu$$
mkdir $work

ln -sr ${deploy}/bl1.bin $work
ln -sr ${deploy}/bl2.bin $work
ln -sr ${deploy}/bl31.bin $work
ln -sr ${deploy}/fip.bin $work
ln -sr build-64/tmp-glibc/sysroots/qemu-optee64/lib/firmware/tee.bin $work/bl32.bin
ln -sr build-64/downloads/QEMU_EFI.fd $work/bl33.bin

cd $work

# strace -f \
../$root/usr/bin/qemu-system-aarch64 \
	-nographic \
	-machine virt,secure=on \
	-cpu cortex-a57 \
	-bios bl1.bin \
	-semihosting \
	-d unimp \
	-serial tcp:localhost:54320 -serial tcp:localhost:54321 \
	-m 1057 \
	-netdev user,id=unet,hostfwd=tcp::5001-:22,hostfwd=tcp::2159-:2159 \
	-device virtio-net-device,netdev=unet \
	-device virtio-scsi-device,id=scsi \
	-drive file=../${deploy}/optee-image-qemu-optee64.ext4,id=rootimg,if=none,format=raw \
	-device scsi-hd,drive=rootimg \
	-kernel ../${deploy}/Image \
	--append "console=ttyAMA0,38400 keep_bootcon root=/dev/vda2" \
	-initrd ../${deploy}/optee-image-qemu-optee64.cpio.gz

cd ..
rm -rf $work

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
