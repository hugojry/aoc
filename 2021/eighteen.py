import functools
import sys
from itertools import permutations

numbers = [eval(l) for l in sys.stdin]


def go_left(loc):
    tree, path = loc
    if path:
        left, up, _ = path
        if left is not None:
            return left, (None, up, tree)


def go_right(loc):
    tree, path = loc
    if path:
        _, up, right = path
        if right is not None:
            return right, (tree, up, None)


def go_up(loc):
    tree, path = loc
    if path:
        left, up, right = path
        if left is not None:
            return [left, tree], up
        else:
            return [tree, right], up


def go_down(loc):
    tree, path = loc
    if isinstance(tree, list):
        return tree[0], (None, path, tree[1])


def root(loc):
    up = go_up(loc)
    while up:
        loc = up
        up = go_up(loc)
    return loc


def depth(loc):
    path = loc[1]
    depth = 0
    while path:
        depth += 1
        path = path[1]
    return depth


def take_path(loc, path):
    for p in path:
        loc = go_down(loc)
        if 'right' == p:
            loc = go_right(loc)
    return loc


def explode(loc):
    (x, y), _ = loc

    path_from_root = []
    tracer = loc
    while tracer:
        path_from_root.append('right' if go_left(tracer) else 'left')
        tracer = go_up(tracer)
    path_from_root = list(reversed(path_from_root[:-1]))

    left = go_left(loc)
    while not left:
        up = go_up(loc)
        if not up:
            break
        loc = up
        left = go_left(loc)
    if left:
        while not isinstance(left[0], int):
            left = go_right(go_down(left))
        a, path = left
        loc = (x + a, path)

    loc = take_path(root(loc), path_from_root)
    right = go_right(loc)
    while not right:
        up = go_up(loc)
        if not up:
            break
        loc = up
        right = go_right(loc)
    if right:
        while not isinstance(right[0], int):
            right = go_down(right)
        b, path = right
        loc = (y + b, path)

    loc = take_path(root(loc), path_from_root)
    return 0, loc[1]


def split(loc):
    x, path = loc
    return [x//2, x // 2 if x % 2 == 0 else x//2 + 1], path


def traverse(loc, pred, f):
    if pred(loc):
        return True, f(loc)
    else:
        down = go_down(loc)
        if down:
            return traverse(down, pred, f)
        right = go_right(loc)
        if right:
            return traverse(right, pred, f)
        up = go_up(loc)
        while True:
            right = go_right(up)
            if right:
                return traverse(right, pred, f)
            new_up = go_up(up)
            # hit the top
            if not new_up:
                return False, up
            up = new_up


def reduce_explosion(loc):
    def should_explode(loc):
        return isinstance(loc[0], list) and depth(loc) == 4
    return traverse(loc, should_explode, explode)


def reduce_split(loc):
    def should_split(loc):
        return isinstance(loc[0], int) and loc[0] > 9
    return traverse(loc, should_split, split)


def reduce(loc):
    did_explode, loc = reduce_explosion(loc)
    if did_explode:
        return did_explode, loc
    return reduce_split(loc)


def add(x, y):
    op, loc = reduce(([x, y], None))
    while op:
        op, loc = reduce(root(loc))
    return root(loc)[0]


def magnitude(tree):
    if isinstance(tree, list):
        return magnitude(tree[0]) * 3 + magnitude(tree[1]) * 2
    else:
        return tree


total = functools.reduce(add, numbers)
print(magnitude(total))

maximum = 0
for x, y in permutations(numbers, 2):
    mag = magnitude(add(x, y))
    if mag > maximum:
        maximum = mag
print(maximum)
