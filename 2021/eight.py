import sys
from collections import Counter, defaultdict

entries = []
for line in sys.stdin:
    signals, output = [s.rstrip().split() for s in line.split(' | ')]
    signals = [{c for c in s} for s in signals]
    output = [{c for c in s} for s in output]
    entries.append([signals, output])

# Part 1
frequencies = Counter(len(s) for e in entries for s in e[1])
print(frequencies[2] + frequencies[3] + frequencies[4] + frequencies[7])

# Part 2
def solve(signals):
    by_length = defaultdict(set)
    for s in signals:
        by_length[len(s)].add(frozenset(s))

    digits = {}

    digits[1] = next(iter(by_length[2]))
    digits[7] = next(iter(by_length[3]))
    digits[4] = next(iter(by_length[4]))
    digits[8] = next(iter(by_length[7]))

    for c in by_length[6]:
        if not digits[1] < c:
            digits[6] = c
            break

    for c in by_length[5]:
        if digits[1] < c:
            digits[3] = c
        elif c < digits[6]:
            digits[5] = c
        else:
            digits[2] = c

    for c in by_length[6] - {digits[6]}:
        if digits[3] < c:
            digits[9] = c
        else:
            digits[0] = c

    return digits


total = 0
for signals, output in entries:
    digits = solve(signals)
    codes = {c: d for d, c in digits.items()}
    components = []
    for c in output:
        components.append(str(codes[frozenset(c)]))
    total += int(''.join(components))

print(total)
