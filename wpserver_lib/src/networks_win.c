#include "networks.h"
#include <stdio.h>
#include <winsock2.h>

void networks_getLocalIPv4(char* local_addr, size_t len)
{
    char* unknwon_addr = "unknown";

    char hostname[64];
    if (gethostname(hostname, sizeof(hostname)) != 0) {
        fprintf(stderr, "ERROR: gethostname: %d\n", WSAGetLastError());
        strncpy(local_addr, unknwon_addr, len);
        return;
    }

    struct hostent* hosts = gethostbyname(hostname);
    if (hosts == NULL) {
        fprintf(stderr, "ERROR: gethostbyname: %d\n", WSAGetLastError());
        strncpy(local_addr, unknwon_addr, len);
        return;
    }

    struct in_addr addr;
    memcpy(&addr, hosts->h_addr, sizeof(struct in_addr));
    strncpy(local_addr, inet_ntoa(addr), len);
    return;
}
