#include <stdio.h>

#include "libwebsockets.h"

int callback(struct libwebsocket_context* context,
             struct libwebsocket* wsi,
             enum libwebsocket_callback_reasons reason,
             void* user,
             void* in,
             size_t len)
{
    switch (reason) {
        case LWS_CALLBACK_ESTABLISHED:
            printf("websocket - established\n");
            break;
        case LWS_CALLBACK_SERVER_WRITEABLE:
            printf("websocket - server writeable\n");
            break;
        case LWS_CALLBACK_RECEIVE:
            printf("websocket - receive: message='%s'\n", in);
            break;
        case LWS_CALLBACK_FILTER_PROTOCOL_CONNECTION:
            printf("websocket - filter protocol connection\n");
            break;
        default:
            break;
    }
    return 0;
}
