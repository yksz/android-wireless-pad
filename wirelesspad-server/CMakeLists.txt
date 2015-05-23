cmake_minimum_required(VERSION 2.8.4)
project(wirelesspad_server C)

option(MOCK "Use the mock code" OFF)

# libwebsockets
add_subdirectory(libwebsockets)
set(EXTRA_LIBS ${EXTRA_LIBS} "websockets")
include_directories(
    "${PROJECT_SOURCE_DIR}/libwebsockets/lib"
    "${PROJECT_BINARY_DIR}/libwebsockets")

set(SOURCE_FILES src/main.c)
if(MOCK)
    set(SOURCE_FILES ${SOURCE_FILES} src/callback_mock.c)
else()
    set(SOURCE_FILES ${SOURCE_FILES} src/callback_windows.c)
endif()
add_definitions(-Wall -std=c99)
add_executable(wirelesspad_server ${SOURCE_FILES})
target_link_libraries(wirelesspad_server ${EXTRA_LIBS})
