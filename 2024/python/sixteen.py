from collections import defaultdict

with open('../inputs/sixteen.txt') as f:
    lines = f.readlines()

grid = {}
for i, line in enumerate(lines):
    for j, c in enumerate(line.strip()):
        grid[(i, j)] = c


def neighbors(grid, node):
    pos, direction = node
    di, dj = direction
    if di == 0:
        turns = [(1, 0), (-1, 0)]
    else:
        turns = [(0, 1), (0, -1)]

    i, j = pos
    neighbors = []
    for turn in turns:
        tdi, tdj = turn
        new_pos = (i + tdi, j + tdj)
        if grid.get(new_pos, '#') != '#':
            neighbors.append(((new_pos, turn), 1001))

    ahead_pos = (i + di, j + dj)
    if grid.get(ahead_pos, '#') != '#':
        neighbors.append(((ahead_pos, direction), 1))

    return neighbors


def dijkstra(grid):
    start = None
    for pos, c in grid.items():
        if c == 'S':
            start = pos
            break

    assert start

    tentative_distances = {(start, (0, 1)): 0}
    visited = {}
    paths = defaultdict(list)

    while tentative_distances:
        nodes = iter(tentative_distances.items())
        min_node, min_distance = next(nodes)

        for node, distance in nodes:
            if distance < min_distance:
                min_node = node
                min_distance = distance

        visited[min_node] = min_distance
        del tentative_distances[min_node]

        if grid.get(min_node[0]) == 'E':
            return visited, paths

        for neighbor, cost in neighbors(grid, min_node):
            if neighbor in visited:
                continue

            tentative = min_distance + cost
            if tentative <= tentative_distances.get(neighbor, tentative + 1):
                tentative_distances[neighbor] = tentative
                paths[neighbor].append(min_node)

    raise RuntimeError


distances, paths = dijkstra(grid)

end = None
for pos, c in grid.items():
    if c == 'E':
        end = pos
        break

assert end
print(end)

end_node = None
for node in distances:
    pos = node[0]
    if pos == end:
        print(distances[node])
        end_node = node
        break

assert end_node
print(end_node)


def all_paths(paths, end):
    steps = {end}

    while end:
        prev = paths.get(end)
        if not prev:
            return steps

        if len(prev) > 1:
            for step in prev:
                steps.update(all_paths(paths, step))
            break

        steps.add(end)
        end = prev[0]

    return steps

print(len(all_paths(paths, end_node)) + 1)
