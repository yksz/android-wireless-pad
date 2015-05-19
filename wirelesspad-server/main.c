#include <getopt.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

#include "libwebsockets.h"

static struct libwebsocket_context* context;

static int callback_mouse(
        struct libwebsocket_context* context,
        struct libwebsocket* wsi,
        enum libwebsocket_callback_reasons reason,
        void* user,
        void* in,
        size_t len)
{
    switch (reason) {
        case LWS_CALLBACK_ESTABLISHED:
            lwsl_notice("websocket - established");
            break;
        case LWS_CALLBACK_SERVER_WRITEABLE:
            lwsl_notice("websocket - server writeable");
            break;
        case LWS_CALLBACK_RECEIVE:
            lwsl_notice("websocket - receive");
            break;
        case LWS_CALLBACK_FILTER_PROTOCOL_CONNECTION:
            lwsl_notice("websocket - filter_protocol_connection");
            break;
        default:
            break;
    }
    return 0;
}

static struct libwebsocket_protocols protocols[] = {
    {
        "mouse-protocol", // name
        callback_mouse, // callback
         0, // per_session_data_size
        64, // max frame size / rx buffer
    },
    { NULL, NULL, 0, 0 } // terminator
};

static bool startServer(int port)
{
    struct lws_context_creation_info info = { 0 };
    info.port = port;
    info.protocols = protocols;
    info.gid = -1;
    info.uid = -1;
    context = libwebsocket_create_context(&info);
    if (context == NULL) {
        lwsl_err("libwebsocket init failed\n");
        return false;
    }

    for (int n = 0; n >= 0;) {
        n = libwebsocket_service(context, 50);
    }
    libwebsocket_context_destroy(context);
    return true;
}

int main(int argc, char* argv[])
{
    int port = 8080;

    int c = 0;
    while ((c = getopt(argc, argv, "p:h")) != -1) {
        switch (c) {
            case 'p':
                port = atoi(optarg);
                break;
            case 'h':
                printf("usage: wirelesspad-server "
                        "[-h] [-p=<port>]\n");
                return EXIT_SUCCESS;
        }
    }
    if (!startServer(port)) {
        return EXIT_FAILURE;
    }
    return EXIT_SUCCESS;
}
