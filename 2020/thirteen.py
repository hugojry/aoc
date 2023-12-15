import math

with open('thirteen.txt') as f:
    earliest = int(f.readline())
    schedule = []
    for i, s in enumerate(f.readline().strip().split(',')):
        if not s == 'x':
            schedule.append((i, int(s)))

    min_wait_time = math.inf
    for _, x in schedule:
        wait_time = x - (earliest % x)
        if wait_time < min_wait_time:
            min_wait_time = wait_time
            first_bus = x

    for i, x in schedule:
        pass
