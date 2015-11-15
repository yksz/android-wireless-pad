#include <signal.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include "libwebsockets.h"
#include "logger.h"
#include "mouse.h"

static const int DEFAULT_PORT = 7681;

static volatile bool force_exit = false;
static struct libwebsocket_context* context;

static int callback(struct libwebsocket_context* context,
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
            mouse_execCommand(in, len);
            break;
        case LWS_CALLBACK_FILTER_PROTOCOL_CONNECTION:
            break;
        default:
            break;
    }
    return 0;
}

static struct libwebsocket_protocols protocols[] = {
        {
            "wirelesspad-protocol", // name
            callback, // callback
            0, // per_session_data_size
            64, // max frame size / rx buffer
        },
        {NULL, NULL, 0, 0} // terminator
};

static void sighandler(int sig)
{
    force_exit = true;
    libwebsocket_cancel_service(context);
}

static bool startServer(int port)
{
    struct lws_context_creation_info info = {0};
    info.port = port;
    info.protocols = protocols;
    info.gid = -1;
    info.uid = -1;
    context = libwebsocket_create_context(&info);
    if (context == NULL) {
        lwsl_err("libwebsocket init failed\n");
        return false;
    }

    signal(SIGINT, sighandler);

    for (int n = 0; n >= 0 && !force_exit;) {
        n = libwebsocket_service(context, 50);
    }
    libwebsocket_context_destroy(context);
    return true;
}

int main(int argc, char* argv[])
{
    logger_setLevel(LogLevel_DEBUG);

    int port = DEFAULT_PORT;
    if (argc > 1) {
        int num = atoi(argv[1]);
        port = num ? num : port;
    }
    return startServer(port) ? EXIT_SUCCESS : EXIT_FAILURE;
}
