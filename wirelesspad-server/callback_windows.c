#include <string.h>

#include "libwebsockets.h"

static void parse(char* message, size_t len) {
    const char* delimiter = " ";
    char* tok = strtok(message, delimiter);
    if (tok == NULL) {
        return;
    }
    if (strncmp(tok, "move", len) == 0) {
        printf("move\n");
    } else if (strncmp(tok, "click", len) == 0) {
        printf("click\n");
    } else if (strncmp(tok, "double-click", len) == 0) {
        printf("double-click\n");
    } else {
    }
}

int callback(struct libwebsocket_context* context,
             struct libwebsocket* wsi,
             enum libwebsocket_callback_reasons reason,
             void* user,
             void* in,
             size_t len) {
    switch (reason) {
        case LWS_CALLBACK_ESTABLISHED:
            break;
        case LWS_CALLBACK_SERVER_WRITEABLE:
            break;
        case LWS_CALLBACK_RECEIVE:
            printf("websocket - receive\n");
            parse(in, len);
            break;
        case LWS_CALLBACK_FILTER_PROTOCOL_CONNECTION:
            break;
        default:
            break;
    }
    return 0;
}
