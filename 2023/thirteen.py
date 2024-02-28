import sys

with open(sys.argv[1]) as f:
    puzzles = []
    for block in f.read().split('\n\n'):
        puzzle = []
        for line in block.split('\n'):
            if line:
                puzzle.append(list(line))
        puzzles.append(puzzle)


def transpose(twod_list):
    transposed = []
    for i in range(len(twod_list[0])):
        transposed.append([l[i] for l in twod_list])
    return transposed


def find_symmetry(puzzle, existing=None):
    for i in range(len(puzzle) - 1):
        j = i
        k = i + 1
        match = True
        while j >= 0 and k < len(puzzle):
            if puzzle[j] != puzzle[k]:
                match = False
                break

            j -= 1
            k += 1

        if match:
            if i != existing:
                return i


def part_1():
    sum = 0
    for p in puzzles:
        horizontal = find_symmetry(p)
        if horizontal is not None:
            sum += 100 * (horizontal + 1)
            continue

        vertical = find_symmetry(transpose(p))
        if vertical == None:
            raise Exception("Huh?")

        sum += vertical + 1

    print(sum)


def find_smudge(puzzle, existing, is_existing_horizontal):
    for i in range(len(puzzle)):
        for j in range(len(puzzle[0])):
            puzzle[i][j] = '.' if puzzle[i][j] == '#' else '#'

            if is_existing_horizontal:
                horizontal = find_symmetry(puzzle, existing)
            else:
                horizontal = find_symmetry(puzzle)

            if horizontal is not None:
                return True, horizontal

            if is_existing_horizontal:
                vertical = find_symmetry(transpose(puzzle))
            else:
                vertical = find_symmetry(transpose(puzzle), existing)

            if vertical is not None:
                return False, vertical

            puzzle[i][j] = '.' if puzzle[i][j] == '#' else '#'

    raise ValueError("Doesn't fit")
            

def part_2():
    sum = 0
    for p in puzzles:
        horizontal = find_symmetry(p)
        if horizontal is not None:
            split = horizontal
            is_horizontal = True
        else:
            split = find_symmetry(transpose(p))
            is_horizontal = False

        new_split_is_horizontal, new_split = find_smudge(p, split, is_horizontal)

        if new_split_is_horizontal:
            sum += 100 * (new_split + 1)
        else:
            sum += (new_split + 1)

    print(sum)


part_1()
part_2()
