import heapq
import math
import sys
from collections import defaultdict

def square_to_graph(square):
    graph = defaultdict(set)
    for i, row in enumerate(square):
        for j, x in enumerate(row):
            if 0 == i == j:
                continue
            edge = ((i, j), x)
            if i < len(square) - 1:
                graph[(i+1, j)].add(edge)
            if i > 0:
                graph[(i-1, j)].add(edge)
            if j < len(square) - 1:
                graph[(i, j+1)].add(edge)
            if j > 0:
                graph[(i, j-1)].add(edge)
    return graph


def shortest_path(graph, destination):
    distances = {node: math.inf for node in graph}
    start = (0, 0)
    distances[start] = 0
    heap = [(0, (0, 0))]
    while distances:
        tentative, node = heapq.heappop(heap)
        if node == destination:
            break
        del distances[node]
        for neighbour, weight in graph[node]:
            new_distance = tentative + weight
            if neighbour in distances and new_distance < distances[neighbour]:
                distances[neighbour] = new_distance
                heapq.heappush(heap, (new_distance, neighbour))
    return distances[destination]


# Part 1
square = [[int(c) for c in l.rstrip()] for l in sys.stdin]
width = len(square)
destination = (width-1, width-1)
print(shortest_path(square_to_graph(square), destination))

# Part 2
big_square = []
for i in range(5):
    for row in square:
        big_row = []
        for j in range(5):
            for x in row:
                tile_value = x + i + j
                big_row.append(tile_value if tile_value <= 9 else tile_value - 9)
        big_square.append(big_row)
destination = (width * 5 - 1, width * 5 - 1)
print(shortest_path(square_to_graph(big_square), destination))
