import math
import sys

measurements = [int(l.rstrip()) for l in sys.stdin]

# Part 1
count = 0
previous = math.inf
for m in measurements:
    if m > previous:
        count += 1
    previous = m

print(count)

# Part 2
windows = [sum(measurements[i:i+3]) for i in range(len(measurements) - 2)]
previous = math.inf
count = 0
for w in windows:
    if w > previous:
        count += 1
    previous = w

print(count)
