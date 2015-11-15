#ifndef MOUSE_H
#define MOUSE_H

#include <stddef.h>

#define MOUSE_COMMAND_MAX_SIZE 16

void mouse_execCommand(char* cmd, size_t len);

#endif /* MOUSE_H */
