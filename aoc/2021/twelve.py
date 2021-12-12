import sys
from collections import defaultdict

graph = defaultdict(set)
for l in sys.stdin:
    start, end = l.rstrip().split('-')
    graph[start].add(end)
    graph[end].add(start)


class Part1Path:
    def __init__(self, node, visited):
        self.node = node
        self.visited = visited

    def children(self, graph):
        paths = []
        for node in graph[self.node]:
            if not (node.islower() and node in self.visited):
                paths.append(Part1Path(node, self.visited | {self.node}))
        return paths


completed = []
uncompleted = [Part1Path('start', set())]
while uncompleted:
    path = uncompleted.pop(0)
    for c in path.children(graph):
        if c.node == 'end':
            completed.append(c)
        else:
            uncompleted.append(c)

print(len(completed))


class Part2Path:
    def __init__(self, node, visited, double_visited_cave):
        self.node = node
        self.visited = visited
        self.double_visited_cave = double_visited_cave

    def children(self, graph):
        paths = []
        for node in graph[self.node]:
            visited = self.visited | {self.node}
            if not (node.islower() and node in self.visited):
                paths.append(Part2Path(node, visited, self.double_visited_cave))
            elif node != 'start' and node != 'end' and not self.double_visited_cave:
                paths.append(Part2Path(node, visited, node))
        return paths


completed = []
uncompleted = [Part2Path('start', set(), None)]
while uncompleted:
    path = uncompleted.pop(0)
    for c in path.children(graph):
        if c.node == 'end':
            completed.append(c)
        else:
            uncompleted.append(c)

print(len(completed))
