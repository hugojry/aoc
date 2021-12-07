import math
import sys

crabs = [int(x) for x in list(sys.stdin)[0].rstrip().split(',')]

# Part 1
minimum = math.inf
for x in range(min(crabs), max(crabs) + 1):
    total = 0
    for c in crabs:
        total += abs(c - x)
    if total < minimum:
        minimum = total

print(minimum)

# Part 2
minimum = math.inf
for x in range(min(crabs), max(crabs) + 1):
    total = 0
    for c in crabs:
        delta = abs(c - x)
        total += int((delta + 1) / 2 * delta)
    if total < minimum:
        minimum = total

print(minimum)
