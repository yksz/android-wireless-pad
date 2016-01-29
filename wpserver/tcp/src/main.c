#include <stdio.h>
#include <winsock2.h>
#include "logger.h"
#include "mouse.h"
#include "netutil.h"

#pragma comment(lib, "ws2_32.lib")

static const int kDefaultPort = 7681;
static const int kQueueSize = 5;

static int receiveLine(SOCKET sock, char* buf, int len)
{
    int offset = 0;
    int size;

    while ((size = recv(sock, &buf[offset], 1, 0)) > 0) { // 1byte by 1byte
        offset += size;
        if (offset >= len - 1) { // max size
            buf[len - 1] = '\0';
            break;
        }
        if (buf[offset - 1] == '\n') { // line feed
            buf[offset - 1] = '\0';
            break;
        }
    }
    if (size == -1) {
        LOG_ERROR("recv: %d", WSAGetLastError());
        return -1;
    }
    LOG_TRACE("recv size=%d", offset);
    return offset;
}

static int receiveCommand(SOCKET sock)
{
    char msg[MOUSE_COMMAND_MAX_SIZE] = {0};

    if (receiveLine(sock, msg, sizeof(msg)) == -1) {
        return -1;
    }
    mouse_execCommand(msg, sizeof(msg));
    return 0;
}

static int acceptClient(SOCKET serverSock)
{
    struct sockaddr_in clientAddr;
    int len;
    SOCKET clientSock;

    len = sizeof(clientAddr);
    clientSock = accept(serverSock, (struct sockaddr*) &clientAddr, &len);
    if (clientSock == INVALID_SOCKET) {
        LOG_ERROR("accept: %d", WSAGetLastError());
        return -1;
    }
    LOG_INFO("%s connected", inet_ntoa(clientAddr.sin_addr));

    while (receiveCommand(clientSock) == 0);

    closesocket(clientSock);
    return 0;
}

static int createServerSocket(SOCKET sock, int port, int queueSize)
{
    BOOL soval;
    struct sockaddr_in serverAddr;

    sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == INVALID_SOCKET) {
        LOG_ERROR("socket: %d", WSAGetLastError());
        return -1;
    }

    soval = 1;
    setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, (const char*) &soval, sizeof(soval));

    memset(&serverAddr, 0, sizeof(serverAddr));
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(port);
    serverAddr.sin_addr.S_un.S_addr = htonl(INADDR_ANY);

    if (bind(sock, (struct sockaddr*) &serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
        LOG_ERROR("bind: %d", WSAGetLastError());
        return -1;
    }

    if (listen(sock, queueSize) == SOCKET_ERROR) {
        LOG_ERROR("listen: %d", WSAGetLastError());
        return -1;
    }
    return 0;
}

static int startServer(int port, int queueSize)
{
    WSADATA data;
    SOCKET sock;
    char ip[20];

    if (WSAStartup(MAKEWORD(2, 0), &data) != 0) {
        LOG_ERROR("WSAStartup: %d", WSAGetLastError());
        return -1;
    }
    if (createServerSocket(sock, port, queueSize) != 0) {
        return -1;
    }
    netutil_getLocalIPv4(ip, sizeof(ip));
    LOG_INFO("Listening on IP address %s, port %d", ip, port);

    for (;;) {
        acceptClient(sock);
    }

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

    return startServer(port, kQueueSize);
}
