cmake_minimum_required(VERSION 2.8.4)
project(wpserver_ws C)

option(MOCK "Use the mock code" OFF)

# libwebsockets
add_subdirectory(libwebsockets)
set(EXTRA_LIBS ${EXTRA_LIBS} "websockets")
include_directories(
    "${PROJECT_SOURCE_DIR}/libwebsockets/lib"
    "${PROJECT_BINARY_DIR}/libwebsockets")

# wpserver_lib
include_directories("${PROJECT_SOURCE_DIR}/../wpserver_lib/src")
set(SOURCE_FILES ${SOURCE_FILES} ${PROJECT_SOURCE_DIR}/../wpserver_lib/src/logger.c)
if(MOCK)
    set(SOURCE_FILES ${SOURCE_FILES} ${PROJECT_SOURCE_DIR}/../wpserver_lib/src/mouse_mock.c)
else()
    set(SOURCE_FILES ${SOURCE_FILES} ${PROJECT_SOURCE_DIR}/../wpserver_lib/src/mouse_windows.c)
endif()

set(SOURCE_FILES ${SOURCE_FILES} src/main.c)
add_definitions(-Wall -std=c99)
add_executable(wpserver ${SOURCE_FILES})
target_link_libraries(wpserver ${EXTRA_LIBS})
