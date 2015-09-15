#include <stdio.h>
#include <winsock2.h>

#pragma comment(lib, "ws2_32.lib")

static void move(int x, int y)
{
    printf("move: x=%d, y=%d\n", x, y);
    POINT point = {0};
    GetCursorPos(&point);
    SetCursorPos(point.x + x, point.y + y);
}

static void scroll(int amount)
{
    printf("scroll: amount=%d\n", amount);
    mouse_event(MOUSEEVENTF_WHEEL, 0, 0, amount, 0);
}

static void leftClick()
{
    printf("leftClick\n");
    mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
    Sleep(10);
    mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
}

static void rightClick()
{
    printf("rightClick\n");
    mouse_event(MOUSEEVENTF_RIGHTDOWN, 0, 0, 0, 0);
    Sleep(10);
    mouse_event(MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
}

static void doubleClick()
{
    printf("doubleClick\n");
    leftClick();
    Sleep(GetDoubleClickTime() * 0.5);
    leftClick();
}

static void parseReceivedMessage(char* message, size_t len)
{
    const char* delimiter = " ";
    char* tok = strtok(message, delimiter);
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
        return;
    }
}

static void receiveMessage(SOCKET sock)
{
    char buf[32];
    for (;;) {
        memset(buf, 0, sizeof(buf));
        recv(sock, buf, sizeof(buf), 0);
        parseReceivedMessage(buf, sizeof(buf));
    }
}

int main(int argc, char** argv)
{
    int port = 7681;
    WSADATA data;
    SOCKET sock;
    struct sockaddr_in recv_addr;

    if (argc > 1) {
        int num = atoi(argv[1]);
        port = num ? num : port;
    }

    if (WSAStartup(MAKEWORD(2, 0), &data) != 0) {
        fprintf(stderr, "ERROR: WSAStartup: %d\n", WSAGetLastError());
        exit(1);
    }

    sock = socket(AF_INET, SOCK_DGRAM, 0);
    if (sock == INVALID_SOCKET) {
        fprintf(stderr, "ERROR: socket: %d\n", WSAGetLastError());
        exit(1);
    }

    memset(&recv_addr, 0, sizeof(recv_addr));
    recv_addr.sin_family = AF_INET;
    recv_addr.sin_port = htons(port);
    recv_addr.sin_addr.S_un.S_addr = htonl(INADDR_ANY);
    if (bind(sock, (struct sockaddr*) &recv_addr, sizeof(recv_addr)) == SOCKET_ERROR) {
        fprintf(stderr, "ERROR: bind: %d\n", WSAGetLastError());
        exit(1);
    }

    printf("Listening on port %d\n", port);
    receiveMessage(sock);

    closesocket(sock);
    WSACleanup();
    return 0;
}
