import sys

vents = []
for line in sys.stdin:
    start, _, end = line.rstrip().split()
    start = [int(x) for x in start.split(',')]
    end = [int(x) for x in end.split(',')]
    vents.append(sorted([start, end]))

# Part 1
seen = set()
overlapping = set()
for start, end in vents:
    x1, y1 = start
    x2, y2 = end
    if x1 == x2 or y1 == y2:
        for x in range(x1, x2 + 1):
            for y in range(y1, y2 + 1):
                point = (x, y)
                if point in seen:
                    overlapping.add(point)
                seen.add(point)

print(len(overlapping))

# Part 2
for start, end in vents:
    x1, y1 = start
    x2, y2 = end
    if x1 != x2 and y1 != y2:
        if y1 <= y2:
            range_y = range(y1, y2 + 1)
        else:
            range_y = range(y1, y2 - 1, -1)
        for x, y in zip(range(x1, x2 + 1), range_y):
            point = (x, y)
            if point in seen:
                overlapping.add(point)
            seen.add(point)

print(len(overlapping))
