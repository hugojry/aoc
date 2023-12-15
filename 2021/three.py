import sys

strings = [l.rstrip() for l in sys.stdin]
n_digits = len(strings[0])
xs = [int(s, 2) for s in strings]
n = len(xs)

# Part 1
gamma = 0
epsilon = 0

for i in range(n_digits):
    mask = 2**i
    bit_count = 0
    for x in xs:
        if 0 != x & mask:
            bit_count += 1
    if bit_count > n/2:
        gamma += mask
    else:
        epsilon += mask

print(gamma * epsilon)

# Part 2
def part_2(xs, is_o2):
    for i in range(n_digits - 1, -1, -1):
        if not len(xs) > 1:
            break

        mask = 2**i
        ones = 0
        zeros = 0
        for x in xs:
            if mask == x & mask:
                ones += 1
            else:
                zeros += 1

        if is_o2:
            bit_criteria = mask if ones >= zeros else 0
        else:
            bit_criteria = 0 if ones >= zeros else mask
        xs = [x for x in xs if x & mask == bit_criteria]

    return xs[0]


print(part_2(list(xs), True) * part_2(list(xs), False))
