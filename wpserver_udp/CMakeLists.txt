cmake_minimum_required(VERSION 2.8.4)
project(wpserver_udp C)

set(SOURCE_FILES src/main.c)
add_definitions(-Wall -std=c99)
add_executable(wpserver ${SOURCE_FILES})
