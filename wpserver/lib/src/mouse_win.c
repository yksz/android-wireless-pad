#include "mouse.h"
#include <ctype.h>
#include <stdio.h>
#include <string.h>
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include "logger.h"

static void move(int x, int y)
{
    LOGGER_DEBUG("move: x=%d, y=%d\n", x, y);
    POINT point = {0};
    GetCursorPos(&point);
    SetCursorPos(point.x + x, point.y + y);
}

static void scroll(int amount)
{
    LOGGER_DEBUG("scroll: amount=%d\n", amount);
    mouse_event(MOUSEEVENTF_WHEEL, 0, 0, amount, 0);
}

static void leftClick()
{
    LOGGER_DEBUG("leftClick\n");
    mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
    Sleep(10);
    mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
}

static void rightClick()
{
    LOGGER_DEBUG("rightClick\n");
    mouse_event(MOUSEEVENTF_RIGHTDOWN, 0, 0, 0, 0);
    Sleep(10);
    mouse_event(MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
}

static void doubleClick()
{
    LOGGER_DEBUG("doubleClick\n");
    leftClick();
    Sleep(GetDoubleClickTime() * 0.5);
    leftClick();
}

void mouse_execCommand(char* cmd, size_t len)
{
    const char* delimiter = " ";
    char* tok = strtok(cmd, delimiter);
    if (tok == NULL) {
        return;
    }
    if (strncmp(tok, "mv", len) == 0) {
        tok = strtok(NULL, delimiter);
        int x = (tok != NULL) ? atoi(tok) : 0;
        tok = strtok(NULL, delimiter);
        int y = (tok != NULL) ? atoi(tok) : 0;
        move(-x, -y);
    } else if (strncmp(tok, "sr", len) == 0) {
        tok = strtok(NULL, delimiter);
        int amount = (tok != NULL) ? atoi(tok) : 0;
        scroll(-amount);
    } else if (strncmp(tok, "lc", len) == 0) {
        leftClick();
    } else if (strncmp(tok, "rc", len) == 0) {
        rightClick();
    } else if (strncmp(tok, "dc", len) == 0) {
        doubleClick();
    } else {
        LOGGER_TRACE("%s\n", tok);
        return;
    }
}
