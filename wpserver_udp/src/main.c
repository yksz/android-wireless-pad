#include <stdbool.h>
#include <stdio.h>
#include <winsock2.h>
#include "logger.h"
#include "mouse.h"

#pragma comment(lib, "ws2_32.lib")

static const int DEFAULT_PORT = 7681;

static void receiveMessageFrom(SOCKET sock)
{
    char msg[MOUSE_COMMAND_MAX_SIZE];
    for (;;) {
        memset(msg, 0, sizeof(msg));
        recvfrom(sock, msg, sizeof(msg), 0, NULL, 0);
        mouse_execCommand(msg, sizeof(msg));
    }
}

static bool startReceiver(int port)
{
    WSADATA data;
    SOCKET sock;
    struct sockaddr_in recv_addr;

    if (WSAStartup(MAKEWORD(2, 0), &data) != 0) {
        fprintf(stderr, "ERROR: WSAStartup: %d\n", WSAGetLastError());
        return false;
    }

    sock = socket(AF_INET, SOCK_DGRAM, 0);
    if (sock == INVALID_SOCKET) {
        fprintf(stderr, "ERROR: socket: %d\n", WSAGetLastError());
        return false;
    }

    memset(&recv_addr, 0, sizeof(recv_addr));
    recv_addr.sin_family = AF_INET;
    recv_addr.sin_port = htons(port);
    recv_addr.sin_addr.S_un.S_addr = htonl(INADDR_ANY);
    if (bind(sock, (struct sockaddr*) &recv_addr, sizeof(recv_addr)) == SOCKET_ERROR) {
        fprintf(stderr, "ERROR: bind: %d\n", WSAGetLastError());
        return false;
    }

    printf("Listening on port %d\n", port);
    receiveMessageFrom(sock);

    closesocket(sock);
    WSACleanup();
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
    return startReceiver(port) ? EXIT_SUCCESS : EXIT_FAILURE;
}
