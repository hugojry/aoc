import sys
from collections import defaultdict

template = next(sys.stdin).rstrip()
next(sys.stdin)
rules = dict(l.rstrip().split(' -> ') for l in sys.stdin)
chains = defaultdict(list)
for pair, ins in rules.items():
    chains[pair] = [pair[0] + ins, ins + pair[1]]

pairs = defaultdict(int)
for i in range(len(template) - 1):
    pairs[template[i:i+2]] += 1

for _ in range(40):
    new_pairs = defaultdict(int)
    for pair, count in pairs.items():
        for chain_pair in chains[pair]:
            new_pairs[chain_pair] += count
    pairs = new_pairs

counts = defaultdict(int)
for pair, x in pairs.items():
    counts[pair[0]] += x
counts[template[-1]] += 1
print(max(counts.values()) - min(counts.values()))
