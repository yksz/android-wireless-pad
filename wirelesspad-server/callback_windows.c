#include <ctype.h>
#include <string.h>

#include "libwebsockets.h"

static void moveCursor(int x, int y)
{
    printf("move (%d, %d)\n", x, y);
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
    } else if (strncmp(tok, "click", len) == 0) {
        printf("click\n");
    } else if (strncmp(tok, "doubleclick", len) == 0) {
        printf("doubleclick\n");
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
            printf("websocket - receive\n");
            parseReceiveMessage(in, len);
            break;
        case LWS_CALLBACK_FILTER_PROTOCOL_CONNECTION:
            break;
        default:
            break;
    }
    return 0;
}
