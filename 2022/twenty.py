def output(numbers, positions):
    by_position = {}
    for i, j in enumerate(positions):
        by_position[i] = j
    return [numbers[by_position[n]] for n in range(len(numbers))]


numbers = []
with open('twenty.txt') as f:
    numbers = [int(l.rstrip()) for l in f]

count = len(numbers)
mixed = list(range(count))
for i, x in enumerate(numbers):
    mixed_index = mixed.index(i)
    del mixed[mixed_index]
    mixed.insert(x + mixed_index % count, i)
    print(output(numbers, mixed))
