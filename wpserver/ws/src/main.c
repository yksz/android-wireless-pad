#include <signal.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include "libwebsockets.h"
#include "logger.h"
#include "mouse.h"

static const int kDefaultPort = 7681;

static volatile bool forceExit = false;
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
        { NULL, NULL, 0, 0 } // terminator
};

static void sighandler(int sig)
{
    forceExit = true;
    libwebsocket_cancel_service(context);
}

static int startServer(int port)
{
    struct lws_context_creation_info info = {0};
    info.port = port;
    info.protocols = protocols;
    info.gid = -1;
    info.uid = -1;
    context = libwebsocket_create_context(&info);
    if (context == NULL) {
        lwsl_err("libwebsocket init failed\n");
        return -1;
    }

    signal(SIGINT, sighandler);

    for (int n = 0; n >= 0 && forceExit == false;) {
        n = libwebsocket_service(context, 50);
    }
    libwebsocket_context_destroy(context);
    return 0;
}

static int getAsInt(char* str, int defaultValue)
{
    int i = atoi(str);
    return i != 0 ? i : defaultValue;
}

int main(int argc, char* argv[])
{
    int port = kDefaultPort;
    if (argc > 1) {
        port = getAsInt(argv[1], port);
    }

    logger_initConsoleLogger(stderr);
    logger_setLevel(LogLevel_DEBUG);

    return startServer(port);
}
