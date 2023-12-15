import sys

dots = set()
for l in sys.stdin:
    if l == '\n':
        break
    i, j = [int(x) for x in l.rstrip().split(',')]
    dots.add((i, j))

folds = []
for l in sys.stdin:
    axis, fold = l.rstrip().split('=')
    folds.append((axis[-1], int(fold)))

for j, (axis, x) in enumerate(folds):
    if j == 1:
        print(len(dots))

    i = 0 if axis == 'x' else 1
    new_dots = set()
    for dot in dots:
        if dot[i] > x:
            new_dot = list(dot)
            new_dot[i] = 2 * x - new_dot[i]
            new_dots.add(tuple(new_dot))
        else:
            new_dots.add(dot)
    dots = new_dots

max_x = max([d[0] for d in dots])
max_y = max([d[1] for d in dots])
for i in range(max_y + 1):
    for j in range(max_x + 1):
        print('#' if (j, i) in dots else '.', end='')
    print()
