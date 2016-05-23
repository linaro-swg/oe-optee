SUMMARY = "EDK2 bootloader"
LICENSE = "BSD"

SRCREV = "131aec010f565a747ac51e6a95e30a37b5df15cc"
SRC_URI = "git://git.linaro.org/landing-teams/working/arm/edk2.git;branch=16.01"
PV = "16.01+git${SRCPV}"

S = "${WORKDIR}/git/"

# inherit deploy

do_configure () {
    ${BASH} edksetup.sh
    oe_runmake -C ${S}/BaseTools
}

do_compile () {
    oe_runmake GCC49_AARCH64_PREFIX=${HOST_PREFIX} \
        -j1 \
        -C ${S} \
        -f ArmPlatformPkg/Scripts/Makefile \
        EDK2_ARCH=AARCH64 \
        EDK2_DSC=ArmPlatformPkg/ArmVExpressPkg/ArmVExpress-FVP-AArch64.dsc \
        EDK2_TOOLCHAIN=GCC49 EDK2_BUILD=RELEASE \
        EDK2_MACROS="-n 6 -D ARM_FOUNDATION_FVP=1 -D ARM_FVP_BOOT_ANDROID_FROM_SEMIHOSTING=1"
}

do_install () {
    install -d ${D}/lib/firmware
    install -m 644 ${S}/Build/ArmVExpress-FVP-AArch64/RELEASE_GCC49/FV/FVP_AARCH64_EFI.fd \
        ${D}/lib/firmware
}

FILES_${PN} = "/lib/firmware"

LIC_FILES_CHKSUM = " \
    file://ArmPkg/License.txt;md5=9246519a5aad2d1a83cf822b5b543cdb \
    file://EdkShellBinPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://StdLib/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://SourceLevelDebugPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://MdePkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://NetworkPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://EmulatorPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://IntelFrameworkPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://FatBinPkg/License.txt;md5=77e7dde47fae030cd7c049d47bfe03d0 \
    file://UefiCpuPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://EdkShellPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://CryptoPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://PerformancePkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://Vlv2DeviceRefCodePkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://BeagleBoardPkg/License.txt;md5=9246519a5aad2d1a83cf822b5b543cdb \
    file://BaseTools/License.txt;md5=a041d47c90fd51b4514d09a5127210e6 \
    file://EdkCompatibilityPkg/License.txt;md5=7fc5786d81b4e12d93fc1a52c7849bf1 \
    file://AppPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://Nt32Pkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://ShellBinPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://OptionRomPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://Vlv2TbltDevicePkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://OvmfPkg/License.txt;md5=343dc88e82ff33d042074f62050c3496 \
    file://SecurityPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://MdeModulePkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://StdLibPrivateInternalFiles/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://ArmPlatformPkg/License.txt;md5=0f0cc8dab05eeb3c4012f9d2249a0342 \
    file://ShellPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://DuetPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://Omap35xxPkg/License.txt;md5=9246519a5aad2d1a83cf822b5b543cdb \
    file://ArmVirtPkg/License.txt;md5=f65140530401b29fd2bfc5a89c7fad81 \
    file://PcAtChipsetPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://EmbeddedPkg/License.txt;md5=9246519a5aad2d1a83cf822b5b543cdb \
    file://IntelFrameworkModulePkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://CorebootPayloadPkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
    file://CorebootModulePkg/License.txt;md5=ffd52cf9a8e0e036b9a61a0de2dc87ed \
"
