require everest-core_2026.02.0.inc
#SRCREV = "b666184ef94a70008388cdec7ec41c14549e9bad"
SRCREV = "${AUTOREV}"
SRC_URI:append = " file://everest.service"

SRC_URI = "git://github.com/Rolec-Services/EVerest.git;protocol=https;branch=rolec/dev/2026.02.0 \
"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"