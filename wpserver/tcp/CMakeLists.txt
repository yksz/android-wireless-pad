set(source_files src/main.c)
add_executable(${PROJECT_NAME}_tcp ${source_files})
target_link_libraries(${PROJECT_NAME}_tcp ${PROJECT_NAME}_lib)
