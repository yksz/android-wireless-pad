#include <stdio.h>
#include <stdlib.h>

#include "libwebsockets.h"

int main(int argc, char* argv[])
{
    printf("%s\n", lws_get_library_version());
    return EXIT_SUCCESS;
}
