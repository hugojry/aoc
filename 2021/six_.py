import sys
from collections import Counter

fish = Counter(int(x) for x in list(sys.stdin)[0].rstrip().split(','))

for d in range(256):
    new_fish = fish[0]
    fish[0] = 0
    for i in range(8):
        fish[i] = fish[i+1]
    fish[6] += new_fish
    fish[8] = new_fish
    if d == 79:
        print(sum(x for x in fish.values()))

print(sum(x for x in fish.values()))
