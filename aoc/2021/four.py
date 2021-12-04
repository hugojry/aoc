import sys


class Square:
    def __init__(self, rows):
        self.rows = [set(row) for row in rows]
        self.cols = [{row[i] for row in rows} for i in range(len(rows[0]))]

    def is_bingo(self, numbers):
        for r in self.rows:
            if r <= numbers:
                return True
        for c in self.cols:
            if c <= numbers:
                return True

    def unmarked(self, numbers):
        return {x for r in self.rows for x in r if x not in numbers}


lines = list(sys.stdin)
bingo_numbers = [int(x) for x in lines[0].split(',')]
squares = []
for i in range(2, len(lines), 6):
    squares.append(Square([[int(x) for x in row.rstrip().split()]
                           for row in lines[i:i+5]]))

# Part 1
winner = None
numbers = set()
for x in bingo_numbers:
    numbers.add(x)
    for s in squares:
        if s.is_bingo(numbers):
            winner = s
            break
    if winner:
        print(sum(winner.unmarked(numbers)) * x)
        break


# Part 2
numbers = set()
for i, x in enumerate(bingo_numbers):
    numbers.add(x)
    squares = [s for s in squares if not s.is_bingo(numbers)]
    if len(squares) == 1:
        square = squares[0]
        for x in bingo_numbers[i+1:]:
            numbers.add(x)
            if square.is_bingo(numbers):
                print(sum(square.unmarked(numbers)) * x)
                break
        break
