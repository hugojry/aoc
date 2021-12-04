import sys


class Square:
    def __init__(self, rows):
        self.rows = [set(row) for row in rows]
        self.cols = [{row[i] for row in rows} for i in range(len(rows[0]))]

    def mark(self, x):
        for r in self.rows:
            r.discard(x)
        for c in self.cols:
            c.discard(x)

    def is_bingo(self):
        for r in self.rows:
            if not r:
                return True
        for c in self.cols:
            if not c:
                return True

    def unmarked(self):
        return [x for r in s.rows for x in r]


def make_squares(lines):
    squares = []
    for i in range(0, len(lines), 6):
        squares.append(Square([[int(x) for x in row.rstrip().split()]
                               for row in lines[i:i+5]]))
    return squares


lines = list(sys.stdin)
bingo_numbers = [int(x) for x in lines[0].split(',')]

# Part 1
squares = make_squares(lines[2:])
winner = None
for x in bingo_numbers:
    for s in squares:
        s.mark(x)
        if s.is_bingo():
            winner = s
            break
    if winner:
        print(sum(winner.unmarked()) * x)
        break
        

# Part 2
squares = make_squares(lines[2:])
for i, x in enumerate(bingo_numbers):
    for s in squares:
        s.mark(x)
    squares = [s for s in squares if not s.is_bingo()]
    if len(squares) == 1:
        break

square = squares[0]
for x in bingo_numbers[i+1:]:
    square.mark(x)
    if square.is_bingo():
        print(sum(square.unmarked()) * x)
        break
