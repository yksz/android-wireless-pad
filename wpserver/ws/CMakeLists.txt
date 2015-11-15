# libwebsockets
add_subdirectory(libwebsockets)
include_directories(
    "${PROJECT_SOURCE_DIR}/ws/libwebsockets/lib"
    "${PROJECT_BINARY_DIR}/ws/libwebsockets"
)

set(source_files src/main.c)
add_executable(${PROJECT_NAME}_ws ${source_files})
set(extra_libs ${PROJECT_NAME}_lib "websockets")
target_link_libraries(${PROJECT_NAME}_ws ${extra_libs})
