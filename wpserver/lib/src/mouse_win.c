#include "mouse.h"
#include <ctype.h>
#include <stdio.h>
#include <string.h>
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include "logger.h"

static void move(int x, int y)
{
    POINT point = {0};
    GetCursorPos(&point);
    SetCursorPos(point.x + x, point.y + y);
}

static void scroll(int amount)
{
    mouse_event(MOUSEEVENTF_WHEEL, 0, 0, amount, 0);
}

static void leftClick(void)
{
    mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
    Sleep(10);
    mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
}

static void rightClick(void)
{
    mouse_event(MOUSEEVENTF_RIGHTDOWN, 0, 0, 0, 0);
    Sleep(10);
    mouse_event(MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
}

static void doubleClick(void)
{
    leftClick();
    Sleep(GetDoubleClickTime() * 0.5);
    leftClick();
}

void mouse_execCommand(char* cmd, size_t len)
{
    const char* delimiter = " ";
    char* tok;
    int x, y;
    int amount;

    tok = strtok(cmd, delimiter);
    if (tok == NULL) {
        return;
    }
    if (strncmp(tok, "mv", len) == 0) {
        tok = strtok(NULL, delimiter);
        x = (tok != NULL) ? atoi(tok) : 0;
        tok = strtok(NULL, delimiter);
        y = (tok != NULL) ? atoi(tok) : 0;
        move(-x, -y);
        LOG_DEBUG("move: x=%d, y=%d", -x, -y);
    } else if (strncmp(tok, "sr", len) == 0) {
        tok = strtok(NULL, delimiter);
        amount = (tok != NULL) ? atoi(tok) : 0;
        scroll(-amount);
        LOG_DEBUG("scroll: amount=%d", -amount);
    } else if (strncmp(tok, "lc", len) == 0) {
        leftClick();
        LOG_DEBUG("leftClick");
    } else if (strncmp(tok, "rc", len) == 0) {
        rightClick();
        LOG_DEBUG("rightClick");
    } else if (strncmp(tok, "dc", len) == 0) {
        doubleClick();
        LOG_DEBUG("doubleClick");
    } else {
        LOG_TRACE("%s", tok);
        return;
    }
}
