#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>

#define NUM_MAPS 7

int parse_map(FILE *f, unsigned long *out)
{
    int i = 0;
    int rc;
    for (;;) {
        // buffer overflow weeoooo
        // consumes the empty line after the end of the map
        rc = fscanf(f, "%lu %lu %lu\n", &out[i], &out[i+1], &out[i+2]);
        if (rc != 3)
            break;
        i += 3;
    }
    return i / 3;
}

unsigned long location(unsigned long **maps, int *lens, unsigned long seed) {
    unsigned long source = seed;
    unsigned long dest;

    unsigned long *map, map_offset, source_start, dest_start;
    long offset, n;
    int len;
    int is_in_map = 0;
    for (int i = 0; i < NUM_MAPS; i++) {
        map = maps[i];
        len = lens[i];

        for (int j = 0; j < len; j++) {
            map_offset = j * 3;
            dest_start = map[map_offset];
            source_start = map[map_offset + 1];
            n = map[map_offset + 2];

            offset = source - source_start;
            if (0 <= offset && offset < n) {
                dest = dest_start + offset;
                is_in_map = 1;
                break;
            }
        }

        if (!is_in_map)
            dest = source;

        source = dest;
    }

    return dest;
}

int main(int argc, char *argv[])
{
    if (argc != 2)
        return -1;

    FILE *f = fopen("five.txt", "r");
    int bufsize = sizeof(char) * 1024;
    char *line = malloc(bufsize);
    fgets(line, bufsize, f);

    size_t len = strlen(line);
    int num_seeds = 0;
    for (size_t i = 0; i < len; i++)
        if (line[i] == ' ')
            num_seeds++;

    unsigned long seeds[num_seeds];
    int i;
    line += 6;
    unsigned long seed_cpy;
    for (i = 0; i < num_seeds; i++) {
        sscanf(line, " %lu", &seeds[i]);
        line++; // space

        seed_cpy = seeds[i];
        do {
            seed_cpy /= 10;
            line++;
        } while (seed_cpy != 0);
    }

    unsigned long *mapbuf = malloc(sizeof(unsigned long) * 1024);

    fgets(line, bufsize, f);
    unsigned long *maps[NUM_MAPS];
    int lens[NUM_MAPS];
    size_t map_size;
    for (i = 0; i < NUM_MAPS; i++) {
        fgets(line, bufsize, f);

        int lines = parse_map(f, mapbuf);

        map_size = lines * 3 * sizeof(unsigned long);
        maps[i] = malloc(map_size);
        lens[i] = lines;
        memcpy(maps[i], mapbuf, map_size);
    }

    int chunk_index = atoi(argv[1]);
    unsigned long total_seeds = 0;
    for (i = 0; i < num_seeds; i += 2)
        total_seeds += seeds[i+1];

    unsigned long chunk_len = total_seeds / 10;
    unsigned long chunk_start = chunk_index * chunk_len;

    unsigned long current_seed_num = 0;
    unsigned long min = ULONG_MAX;
    unsigned long loc;
    unsigned long seeds_run = 0;
    for (i = 0; i < num_seeds; i += 2) {
        current_seed_num += seeds[i+1];
        if (seeds_run >= chunk_len)
            break;
        if (chunk_start < current_seed_num) {
            current_seed_num -= seeds[i+1];
            unsigned long limit = seeds[i] + seeds[i+1];
            long offset = chunk_start - current_seed_num;
            if (offset > 0) {
                current_seed_num += offset;
            }
            unsigned long start = seeds[i] + chunk_start - current_seed_num;;
            for (unsigned long j = start; j < limit; j++) {
                if (current_seed_num == chunk_start + chunk_len + 9) // do a few extra
                    break;
                loc = location(maps, lens, j);
                seeds_run++;
                current_seed_num++;
                if (loc < min)
                    min = loc;
            }
        }
    }

    printf("%lu\n", min);

    return 0;
}
