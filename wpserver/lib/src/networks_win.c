#include "networks.h"
#include <stdio.h>
#include <winsock2.h>
#include "logger.h"

void networks_getLocalIPv4(char* localAddr, size_t len)
{
    const char* unknownAddr = "unknown";
    char hostname[64];
    struct hostent* hosts;
    struct in_addr addr;

    if (gethostname(hostname, sizeof(hostname)) != 0) {
        LOG_ERROR("gethostname: %d", WSAGetLastError());
        strncpy(localAddr, unknownAddr, len);
        return;
    }

    hosts = gethostbyname(hostname);
    if (hosts == NULL) {
        LOG_ERROR("gethostbyname: %d", WSAGetLastError());
        strncpy(localAddr, unknownAddr, len);
        return;
    }

    memcpy(&addr, hosts->h_addr, sizeof(struct in_addr));
    strncpy(localAddr, inet_ntoa(addr), len);
    return;
}
