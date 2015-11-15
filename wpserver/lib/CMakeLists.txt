set(source_files
    src/logger.c
    src/mouse_win.c
    src/networks_win.c
)
add_library(${PROJECT_NAME}_lib STATIC ${source_files})
target_include_directories(${PROJECT_NAME}_lib PUBLIC include)
