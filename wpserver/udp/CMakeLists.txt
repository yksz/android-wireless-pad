set(source_files src/main.c)
add_executable(${PROJECT_NAME}_udp ${source_files})
target_link_libraries(${PROJECT_NAME}_udp ${PROJECT_NAME}_lib)
