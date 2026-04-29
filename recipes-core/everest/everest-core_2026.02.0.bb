LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

require everest-core_2026.02.0.inc
SRCREV = "b666184ef94a70008388cdec7ec41c14549e9bad"
SRC_URI:append = " file://everest.service"

do_compile[network] = "0"

inherit cmake pkgconfig systemd python3native python3targetconfig

DEPENDS = " \
    boost \
    curl \
    evcli-native \
    everest-cmake \
    ftxui \
    libevent \
    libnfc-nci \
    libpcap \
    mqttc \
    nodejs-native \
    openssl \
    pugixml \
    rsync-native \
    sdbus-c++ \
    sigslot \
    ftxui \
    mosquitto \
    nlohmann-json \
    json-schema-validator \
    fmt \
    date \
    catch2 \
    rapidyaml \
    libwebsockets \
    python3-pybind11 \
    python3-pybind11-json \
    libcap \
    json-rpc-cxx \
    doxygen-native \
"

RDEPENDS:${PN} += "libevent openssl"

INSANE_SKIP:${PN} = "already-stripped useless-rpaths arch file-rdeps"

FILES:${PN} += "${libdir}/everest/* ${datadir}/everest/*"

EXTRA_OECMAKE += " \
    -DDISABLE_EDM=ON \
    -DNO_FETCH_CONTENT=ON \
    -DEVEREST_ENABLE_RUN_SCRIPT_GENERATION=OFF \
    -Deverest-core_INSTALL_EV_CLI_IN_PYTHON_VENV=OFF \
    -Deverest-core_USE_PYTHON_VENV=OFF \
    -DEV_SETUP_PYTHON_EXECUTABLE_USE_PYTHON_VENV=OFF \
    -DPYTHON_MODULE_EXTENSION=.so \
    -DPYBIND11_PYTHONLIBS_OVERWRITE=OFF \
    -DEVEREST_INSTALL_ADMIN_PANEL=OFF \
    -DLOG_INSTALL=ON \
    -DEVEREST_SQLITE_INSTALL=ON \
    -DFRAMEWORK_INSTALL=ON \
    -DTIMER_INSTALL=ON \
    -DEVSE_SECURITY_INSTALL=ON \
    -DOCPP_INSTALL=ON \
    -DPYBIND11_INTERFACE_INCLUDE_DIRECTORIES='${RECIPE_SYSROOT}${includedir};${RECIPE_SYSROOT}${includedir}/python${PYTHON_BASEVERSION}' \
"

#SYSTEMD_SERVICE:${PN} = "everest.service"

do_configure:prepend() {
    local pycmake="${S}/lib/everest/framework/everestpy/src/everest/CMakeLists.txt"
    if [ -f ${pycmake} ]; then
        sed -i 's|INTERFACE_INCLUDE_DIRECTORIES ${PYBIND11_INTERFACE_INCLUDE_DIRECTORIES}|INTERFACE_INCLUDE_DIRECTORIES "${PYBIND11_INTERFACE_INCLUDE_DIRECTORIES}"|g' ${pycmake}
    fi
}

do_install:append() {
    # Boost::system is header-only in modern Boost and may not provide a
    # boost_system CMake package component. Relax the generated dependency.
    if [ -f ${D}${libdir}/cmake/everest-timer/everest-timer-config.cmake ]; then
        sed -i 's/find_dependency(Boost COMPONENTS system)/find_dependency(Boost)/' \
            ${D}${libdir}/cmake/everest-timer/everest-timer-config.cmake
    fi
}

PACKAGECONFIG ??= "openssl"

PACKAGECONFIG[mbedtls] = "-DUSING_MBED_TLS=ON,-DUSING_MBED_TLS=OFF,mbedtls,,,openssl"
PACKAGECONFIG[openssl] = "-DUSING_MBED_TLS=OFF,-DUSING_MBED_TLS=ON,openssl,,,mbedtls"

#do_install:append() {
#    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
#        install -d ${D}${systemd_system_unitdir}
#        install -m 0644 ${WORKDIR}/everest.service ${D}${systemd_system_unitdir}/
#    fi
#}

OECMAKE_CXX_FLAGS += "-Wno-narrowing"

EXTRA_OECMAKE:append = "${@bb.utils.contains('DISTRO_FEATURES', 'tpm2', ' -DUSING_TPM2=ON', '', d)}"
