cmake_minimum_required(VERSION 2.8.4)
project(wpserver_udp C)

# wpserver_lib
include_directories("${PROJECT_SOURCE_DIR}/../wpserver_lib/src")
set(SOURCE_FILES ${SOURCE_FILES} ${PROJECT_SOURCE_DIR}/../wpserver_lib/src/logger.c)
set(SOURCE_FILES ${SOURCE_FILES} ${PROJECT_SOURCE_DIR}/../wpserver_lib/src/mouse_win.c)
set(SOURCE_FILES ${SOURCE_FILES} ${PROJECT_SOURCE_DIR}/../wpserver_lib/src/networks_win.c)

set(SOURCE_FILES ${SOURCE_FILES} src/main.c)
add_definitions(-Wall -std=c99)
add_executable(wpserver_udp ${SOURCE_FILES})
