cmake_minimum_required(VERSION 2.8.4)
project(wpserver C)

set(CMAKE_C_FLAGS "-Wall")

add_subdirectory(lib)
add_subdirectory(tcp)
add_subdirectory(udp)
add_subdirectory(ws)
