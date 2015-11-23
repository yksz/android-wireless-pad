#include <stdio.h>
#include <winsock2.h>
#include "logger.h"
#include "mouse.h"
#include "networks.h"

#pragma comment(lib, "ws2_32.lib")

static const int kDefaultPort = 7681;

static int receiveCommand(SOCKET* sock)
{
    char msg[MOUSE_COMMAND_MAX_SIZE] = {0};

    if ((recvfrom(*sock, msg, sizeof(msg), 0, NULL, NULL)) == -1) {
        LOG_ERROR("recvfrom: %d", WSAGetLastError());
        return -1;
    }
    mouse_execCommand(msg, sizeof(msg));
    return 0;
}

static int createServerSocket(SOCKET* sock, int port)
{
    struct sockaddr_in serverAddr;

    *sock = socket(AF_INET, SOCK_DGRAM, 0);
    if (*sock == INVALID_SOCKET) {
        LOG_ERROR("socket: %d", WSAGetLastError());
        return -1;
    }

    memset(&serverAddr, 0, sizeof(serverAddr));
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(port);
    serverAddr.sin_addr.S_un.S_addr = htonl(INADDR_ANY);

    if (bind(*sock, (struct sockaddr*) &serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
        LOG_ERROR("bind: %d", WSAGetLastError());
        return -1;
    }
    return 0;
}

static int startServer(int port)
{
    WSADATA data;
    SOCKET sock;
    char ip[20];

    if (WSAStartup(MAKEWORD(2, 0), &data) != 0) {
        LOG_ERROR("WSAStartup: %d", WSAGetLastError());
        return -1;
    }
    if (createServerSocket(&sock, port) != 0) {
        return -1;
    }
    networks_getLocalIPv4(ip, sizeof(ip));
    LOG_INFO("Listening on IP address %s, port %d", ip, port);

    while (receiveCommand(&sock) == 0);

    closesocket(sock);
    WSACleanup();
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
