import sys

height_map = [[]]
for l in sys.stdin:
    row = [9]
    for c in l.rstrip():
        row.append(int(c))
    row.append(9)
    height_map.append(row)
height_map.append([9] * len(height_map[1]))
height_map[0] = list(height_map[-1])

low_points = []
for i in range(1, len(height_map) - 1):
    for j in range(1, len(height_map[0]) - 1):
        x = height_map[i][j]
        if x < height_map[i+1][j] and x < height_map[i][j+1] and \
           x < height_map[i-1][j] and x < height_map[i][j-1]:
            low_points.append((i, j))

# Part 1
print(len(low_points))

# Part 2
def basin_size(i, j):
    seen = set()
    to_visit = {(i, j)}
    while to_visit:
        k, l = to_visit.pop()
        seen.add((k, l))
        for p in [(k + 1, l), (k, l + 1), (k - 1, l), (k, l - 1)]:
            if p not in seen and height_map[p[0]][p[1]] != 9:
                to_visit.add(p)
    return len(seen)

basin_sizes = []
for i, j in low_points:
    basin_sizes.append(basin_size(i, j))

total = 1
for x in sorted(basin_sizes)[-3:]:
    total *= x
print(total)
