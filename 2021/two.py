import sys

# Part 1
commands = []
for l in sys.stdin:
    tokens = l.rstrip().split()
    commands.append([tokens[0], int(tokens[1])])

x = 0
y = 0
for d, u in commands:
    if d == 'down':
        y += u
    elif d == 'up':
        y -= u
    elif d == 'forward':
        x += u

print(x * y)

# Part 2
x = 0
y = 0
aim = 0
for d, u in commands:
    if d == 'down':
        aim += u
    elif d == 'up':
        aim -= u
    elif d == 'forward':
        x += u
        y += u * aim

print(x * y)
