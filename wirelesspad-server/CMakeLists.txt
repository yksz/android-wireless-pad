cmake_minimum_required(VERSION 2.8.4)
project(wirelesspad_server C)

# libwebsockets
add_subdirectory(libwebsockets)
set(EXTRA_LIBS ${EXTRA_LIBS} "websockets")
include_directories(
    "${PROJECT_SOURCE_DIR}/libwebsockets/lib"
    "${PROJECT_BINARY_DIR}/libwebsockets"
)

set(SOURCE_FILES main.c callback_windows.c)
add_definitions(-Wall -std=c99)
add_executable(wirelesspad_server ${SOURCE_FILES})
target_link_libraries(wirelesspad_server ${EXTRA_LIBS})
