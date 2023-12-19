import math
import re

with open("six.txt") as f:
    lines = f.readlines()

def parse_ints(s):
    return [int(m) for m in re.findall('[0-9]+', s.split(': ')[1])]

time_distances = zip(parse_ints(lines[0]), parse_ints(lines[1]))

def solve_quadratic(t, d):
    sqrt_term = math.sqrt(t**2 - 4 * d)
    return (t - sqrt_term) / 2, (t + sqrt_term) / 2

def ways_to_win(t, d):
    low, high = solve_quadratic(t, d)
    if low.is_integer():
        low += 1
    if high.is_integer():
        high -= 1
    return math.floor(high) - math.ceil(low) + 1

# part 1
total = 1
for t, d in time_distances:
    total *= ways_to_win(t, d)

print(total)
# part 2

time = int(lines[0].split(': ')[1].replace(' ', ''))
distance = int(lines[1].split(': ')[1].replace(' ', ''))
print(ways_to_win(time, distance))
