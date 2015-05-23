#include <ctype.h>
#include <stdio.h>
#include <string.h>

#define WIN32_LEAN_AND_MEAN
#include <windows.h>

#include "libwebsockets.h"

static void moveCursor(int x, int y)
{
    printf("move %d %d\n", x, y);
    POINT point = {0};
    GetCursorPos(&point);
    SetCursorPos(point.x + x, point.y + y);
}

static void parseReceiveMessage(char* message, size_t len)
{
    const char* delimiter = " ";
    char* tok = strtok(message, delimiter);
    if (tok == NULL) {
        return;
    }
    if (strncmp(tok, "move", len) == 0) {
        tok = strtok(NULL, delimiter);
        int x = (tok != NULL) ? atoi(tok) : 0;
        tok = strtok(NULL, delimiter);
        int y = (tok != NULL) ? atoi(tok) : 0;
        moveCursor(x, y);
    } else if (strncmp(tok, "leftclick", len) == 0) {
        printf("leftclick\n");
    } else if (strncmp(tok, "rightclick", len) == 0) {
        printf("rightclick\n");
    } else {
        return;
    }
}

int callback(struct libwebsocket_context* context,
             struct libwebsocket* wsi,
             enum libwebsocket_callback_reasons reason,
             void* user,
             void* in,
             size_t len)
{
    switch (reason) {
        case LWS_CALLBACK_ESTABLISHED:
            break;
        case LWS_CALLBACK_SERVER_WRITEABLE:
            break;
        case LWS_CALLBACK_RECEIVE:
            parseReceiveMessage(in, len);
            break;
        case LWS_CALLBACK_FILTER_PROTOCOL_CONNECTION:
            break;
        default:
            break;
    }
    return 0;
}
