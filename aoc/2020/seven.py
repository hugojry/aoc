import re
from collections import defaultdict

num_re = re.compile('[1-9][0-9]*')


def parse_line(line):
    [parent, children] = line.replace('bags', '') \
                             .replace('bag', '') \
                             .replace('.', '') \
                             .split(' contain ')
    parent = parent.strip()
    bag = [parent, []]
    for c in [c.strip() for c in children.split(', ')]:
        if c != 'no other':
            n = int(num_re.match(c)[0])
            bag[1].append([num_re.sub('', c).strip(), n])
    return bag


with open('seven.txt') as f:
    bags = [parse_line(l) for l in f]

child_parents = defaultdict(set)
parent_children = {}
for parent, children in bags:
    parent_children[parent] = dict(children)
    for color, _ in children:
        child_parents[color].add(parent)


to_visit = {'shiny gold'}
visited = set()
while to_visit:
    child = to_visit.pop()
    for parent in child_parents.get(child, []):
        if parent not in visited:
            to_visit.add(parent)
            visited.add(parent)

print(len(visited))


def bagception(children, bag, n):
    bags = 0
    for b, m in children.get(bag, {}).items():
        bags += m + bagception(children, b, m)
    return n * bags


print(bagception(parent_children, 'shiny gold', 1))
