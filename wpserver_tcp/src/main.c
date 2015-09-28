#include <stdbool.h>
#include <stdio.h>
#include <winsock2.h>
#include "mouse.h"
#include "logger.h"

#pragma comment(lib, "ws2_32.lib")

static const int DEFAULT_PORT = 7681;
static const int QUEUE_SIZE = 5;

static int receiveLine(SOCKET sock, char* buf, size_t buf_size)
{
    int total_size = 0;
    int recv_size;
    while ((recv_size = recv(sock, buf, 1, 0)) > 0) { // 1byte by 1byte
        total_size += recv_size;
        if (buf_size - 1 <= 1) { // max size
            buf[recv_size] = '\0';
            break;
        }
        if (buf[0] == '\n') { // line feed
            buf[0] = '\0';
            break;
        }
        buf = &buf[recv_size];
        buf_size -= recv_size;
    }
    if (recv_size == -1) {
        fprintf(stderr, "ERROR: recv: %d\n", WSAGetLastError());
        return -1;
    }
    return total_size;
}

static bool receiveMessageFrom(SOCKET client_sock)
{
    char msg[MOUSE_COMMAND_MAX_SIZE] = {0};
    if (receiveLine(client_sock, msg, sizeof(msg))) {
        mouse_execCommand(msg, sizeof(msg));
        return true;
    } else {
        return false;
    }
}

static bool acceptClient(SOCKET server_sock)
{
    struct sockaddr_in client_addr;
    int len;
    SOCKET client_sock;

    len = sizeof(client_addr);
    client_sock = accept(server_sock, (struct sockaddr*) &client_addr, &len);
    if (client_sock == INVALID_SOCKET) {
        fprintf(stderr, "ERROR: socket: %d\n", WSAGetLastError());
        return false;
    }
    printf("%s connected\n", inet_ntoa(client_addr.sin_addr));

    while (receiveMessageFrom(client_sock));

    closesocket(client_sock);
    return true;
}

static bool startServer(int port)
{
    WSADATA data;
    SOCKET sock;
    BOOL soval;
    struct sockaddr_in server_addr;

    if (WSAStartup(MAKEWORD(2, 0), &data) != 0) {
        fprintf(stderr, "ERROR: WSAStartup: %d\n", WSAGetLastError());
        return false;
    }

    sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == INVALID_SOCKET) {
        fprintf(stderr, "ERROR: socket: %d\n", WSAGetLastError());
        return false;
    }

    soval = 1;
    setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, (const char*) &soval, sizeof(soval));

    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port);
    server_addr.sin_addr.S_un.S_addr = htonl(INADDR_ANY);
    if (bind(sock, (struct sockaddr*) &server_addr, sizeof(server_addr)) == SOCKET_ERROR) {
        fprintf(stderr, "ERROR: bind: %d\n", WSAGetLastError());
        return false;
    }

    if (listen(sock, QUEUE_SIZE) == SOCKET_ERROR) {
        fprintf(stderr, "ERROR: listen: %d\n", WSAGetLastError());
        return false;
    }

    printf("Listening on port %d\n", port);
    for (;;) {
        acceptClient(sock);
    }

    closesocket(sock);
    WSACleanup();
    return true;
}

int main(int argc, char** argv)
{
    logger_setLevel(LogLevel_DEBUG);

    int port = DEFAULT_PORT;
    if (argc > 1) {
        int num = atoi(argv[1]);
        port = num ? num : port;
    }
    return startServer(port) ? EXIT_SUCCESS : EXIT_FAILURE;
}
