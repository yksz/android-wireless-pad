#include <ctype.h>
#include <stdio.h>
#include <string.h>

#define WIN32_LEAN_AND_MEAN
#include <windows.h>

#include "libwebsockets.h"

static void moveCursor(int x, int y)
{
    lwsl_debug("moveCursor: x=%d, y=%d\n", x, y);
    POINT point = {0};
    GetCursorPos(&point);
    SetCursorPos(point.x + x, point.y + y);
}

static void scroll(int amount)
{
    lwsl_debug("scroll: amount=%d\n", amount);
    mouse_event(MOUSEEVENTF_WHEEL, 0, 0, amount, 0);
}

static void leftClick()
{
    lwsl_debug("leftClick\n");
    mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
    Sleep(10);
    mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
}

static void rightClick()
{
    lwsl_debug("rightClick\n");
    mouse_event(MOUSEEVENTF_RIGHTDOWN, 0, 0, 0, 0);
    Sleep(10);
    mouse_event(MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
}

static void doubleClick()
{
    lwsl_debug("doubleClick\n");
    leftClick();
    Sleep(GetDoubleClickTime() * 0.5);
    leftClick();
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
        moveCursor(-x, -y);
    } else if (strncmp(tok, "scroll", len) == 0) {
        tok = strtok(NULL, delimiter);
        int amount = (tok != NULL) ? atoi(tok) : 0;
        scroll(-amount);
    } else if (strncmp(tok, "leftClick", len) == 0) {
        leftClick();
    } else if (strncmp(tok, "rightClick", len) == 0) {
        rightClick();
    } else if (strncmp(tok, "doubleClick", len) == 0) {
        doubleClick();
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
