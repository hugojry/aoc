import sys

octopuses = [[int(x) for x in l.rstrip()] for l in sys.stdin]
deltas = [-1, 0, 1]
flashed = 0
first_synchronized_flash = None
iteration = 0
while True:
    to_visit = set()
    for i in range(10):
        for j in range(10):
            octopuses[i][j] += 1
            if octopuses[i][j] == 10:
                to_visit.add((i, j))

    seen = set()
    while to_visit:
        point = to_visit.pop()
        seen.add(point)
        i, j = point
        for d1 in deltas:
            for d2 in deltas:
                new_point = (i + d1, j + d2)
                k, l = new_point
                if 0 <= k <= 9 and 0 <= l <= 9:
                    octopuses[k][l] += 1
                    if octopuses[k][l] == 10 and new_point not in seen:
                        to_visit.add((k, l))

    for i, j in seen:
        octopuses[i][j] = 0

    if iteration < 100:
        flashed += len(seen)
    iteration += 1
    if len(seen) == 100:
        first_synchronized_flash = iteration
        break

print(flashed)
print(first_synchronized_flash)
