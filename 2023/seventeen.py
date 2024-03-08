import sys
import heapq
from collections import defaultdict, namedtuple

with open(sys.argv[1]) as f:
    grid = []
    for line in f.readlines():
        grid.append([int(c) for c in line.strip()])


class Node(namedtuple('Node', ['min', 'max', 'i', 'j', 'di', 'dj', 'n'])):
    __slots__ = ()
    def successors(self):
        successors = []

        if self.n < self.min:
            directions = [(self.di, self.dj)]
        else:
            directions = [(1, 0), (-1, 0)] if self.dj else [(0, 1), (0, -1)]
            directions.append((self.di, self.dj))

        for di, dj in directions:
            i = self.i + di
            j = self.j + dj
            n = self.n + 1 if di == self.di and dj == self.dj else 1

            if n > self.max or i < 0 or i == len(grid) or j < 0 or j == len(grid):
                continue

            successors.append(Node(self.min, self.max, i, j, di, dj, n))

        return successors


def dijkstra(min, max):
    distances = defaultdict(lambda: float('infinity'))
    nodes = [(grid[0][1], Node(min, max,0, 1, 0, 1, 1)),
             (grid[1][0], Node(min, max, 1, 0, 1, 0, 1))]
    for d, node, in nodes:
        distances[node] = d

    while nodes:
        current_distance, current_node = heapq.heappop(nodes)

        if distances[current_node] < current_distance:
            continue

        for successor in current_node.successors():
            cost = grid[successor.i][successor.j]
            new_distance = current_distance + cost
            if new_distance < distances[successor]:
                heapq.heappush(nodes, (new_distance, successor))
                distances[successor] = new_distance

    min = float('infinity')
    for node, distance in distances.items():
        if node.i == len(grid) - 1 and node.j == len(grid) - 1:
            if distance < min:
                min = distance

    return min


print(dijkstra(0, 3))
print(dijkstra(4, 10))
