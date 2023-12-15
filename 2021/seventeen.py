import sys

x, y = next(sys.stdin).rstrip()[13:].split(', ')
min_x, max_x = [int(c) for c in x[2:].split('..')]
min_y, max_y = [int(c) for c in y[2:].split('..')]

# Part 1
max_vy = -min_y - 1
print(int((max_vy * (max_vy + 1)) / 2))

# Part 2
min_vx = int((-1 + (1 + 8*min_x)**0.5) / 2) + 1
count = 0
for i in range(min_vx, max_x + 1):
    for j in range(min_y, max_vy + 1):
        vx = i
        vy = j
        x = 0
        y = 0
        while x <= max_x and y >= min_y:
            x += vx
            y += vy
            if vx > 0:
                vx -= 1
            vy -= 1
            if min_x <= x <= max_x and min_y <= y <= max_y:
                count += 1
                break
print(count)
