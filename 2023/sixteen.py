import sys

with open(sys.argv[1]) as f:
    grid = []
    for line in f.readlines():
        grid.append(list(line.strip()))


def iterate_beam(i, j, di, dj):
    cell = grid[i][j]

    if cell == '.':
        return i+di, j+dj, di, dj
    elif cell == '\\':
        if dj == 1:
            return i+1, j, 1, 0
        elif dj == -1:
            return i-1, j, -1, 0
        elif di == 1:
            return i, j+1, 0, 1
        else:
            return i, j-1, 0, -1
    elif cell == '/':
        if dj == 1:
            return i-1, j, -1, 0
        elif dj == -1:
            return i+1, j, 1, 0
        elif di == 1:
            return i, j-1, 0, -1
        else:
            return i, j+1, 0, 1
    elif cell == '-':
        if di == 0:
            return i+di, j+dj, di, dj
        else:
            return None
    else:
        if dj == 0:
            return i+di, j+dj, di, dj
        else:
            return None


def find_unique_path(next):
    path = []
    while True:
        i = next[0]
        j = next[1]
        if i < 0 or j < 0 or i == len(grid) or j == len(grid):
            return path

        path.append(next[:2])
        next = iterate_beam(*next)
        if not next:
            return path


def walk_splitters(starting_point, splitter_paths):
    splitters_visited = {starting_point}
    starting_points = [starting_point]

    while starting_points:
        new_starting_points = []

        for point in starting_points:
            paths = splitter_paths[point]
            for p in paths:
                if p:
                    last_point = p[-1]
                    if last_point in splitters_visited:
                        continue
                    elif last_point in splitter_paths:
                        new_starting_points.append(last_point)
                        splitters_visited.add(last_point)

        starting_points = new_starting_points

    return splitters_visited


def find_first_splitter(next):
    i, j = next[:2]
    energized = {(i, j)}
    while grid[i][j] != '|' and grid[i][j] != '-':
        next = iterate_beam(*next)
        i, j = next[:2]
        energized.add((i, j))
        if i < 0 or i == len(grid) or j < 0 or j == len(grid):
            return None, energized
    return (i, j), energized


def total_energized(starting_point, splitters):
    splitter, energized = find_first_splitter(starting_point)
    if splitter:
        splitters_visited = walk_splitters(splitter, splitters)
        for s in splitters_visited:
            for p in splitters[s]:
                energized.update(p)

    return len(energized)


def part_1(splitters):
    print(total_energized((0, 0, 0, 1), splitters))


def part_2(splitters):
    max_energized = 0

    starting_points = []
    max_index = len(grid) - 1
    for i in range(len(grid)):
        starting_points.extend([(i, 0, 0, 1),
                                (0, i, 1, 0),
                                (i, max_index, 0, -1),
                                (max_index, i, -1, 0)])

    for p in starting_points:
        n = total_energized(p, splitters)
        if n > max_energized:
            max_energized = n

    print(max_energized)


horizontal_splitters = []
vertical_splitters = []
for i, row in enumerate(grid):
    for j, c in enumerate(row):
        if c == '-':
            horizontal_splitters.append((i, j))
        elif c == '|':
            vertical_splitters.append((i, j))

splitters = {}
for i, j in horizontal_splitters:
    splitters[i, j] = []
    for p in [(i, j+1, 0, 1), (i, j-1, 0, -1)]:
        splitters[i, j].append(find_unique_path(p))

for i, j in vertical_splitters:
    splitters[i, j] = []
    for p in [(i+1, j, 1, 0), (i-1, j, -1, 0)]:
        splitters[i, j].append(find_unique_path(p))

part_1(splitters)
part_2(splitters)
