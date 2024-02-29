import sys

with open(sys.argv[1]) as f:
    codes = f.read().strip().split(',')


def HASH(string):
    total = 0
    for c in string:
        x = ord(c)
        total = (total + x) * 17 % 256
    return total


def part_1():
    total = 0
    for code in codes:
        total += HASH(code)
    print(total)


class Box:
    def __init__(self):
        self.lenses = []

    def insert(self, label, focal_length):
        for i, lens in enumerate(self.lenses):
            if lens[0] == label:
                self.lenses[i][1] = focal_length
                return
        
        self.lenses.append([label, focal_length])

    def remove(self, label):
        for i, lens in enumerate(self.lenses):
            if lens[0] == label:
                del self.lenses[i]
                return


def part_2():
    boxes = [Box() for _ in range(256)]

    for code in codes:
        if code[-1] == '-':
            label = code[:-1]
            box = boxes[HASH(label)].remove(label)
        else:
            label = code[:-2]
            box = boxes[HASH(label)].insert(label, code[-1])

    total = 0
    for i, box in enumerate(boxes):
        for j, lens in enumerate(box.lenses):
            total += (i + 1) * (j + 1) * int(lens[1])
    print(total)


part_2()
