#! /bin/bash

# Run an 'fvp' build.
deploy=~+/build-fvp/tmp-glibc/deploy/images/fvp-optee64
fvp=Foundation_Platformpkg

# Link the run targets into the FVP directory.
ln -sf ${deploy}/Image ${fvp}/kernel
#ln -sf ${deploy}/Image ${fvp}/Image
ln -sf ${deploy}/optee-image-fvp-optee64.cpio.gz ${fvp}/ramdisk.img
#ln -sf ${deploy}/optee-image-fvp-optee64.cpio.gz ${fvp}/filesystem.cpio.gz
ln -sf ${deploy}/Image-foundation-v8.dtb ${fvp}/fdt.dtb

cd $fvp
./models/Linux64_GCC-4.7/Foundation_Platform \
    --cores=4 \
    --secure-memory \
    --visualization \
    --gicv3 \
    --data="${deploy}/bl1.bin"@0x0 \
    --data="${deploy}/fip.bin"@0x8000000
