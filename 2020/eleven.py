class Seats:
    def __init__(self, seats):
        self.seats = seats
        self.length = len(seats)
        self.width = len(seats[0])

    def is_occupied_ray(self, i, j, di, dj):
        i += di
        j += dj
        while i >= 0 and j >= 0 and i < self.length and j < self.width:
            if self.seats[i][j] != '.':
                return self.seats[i][j] == '#'
            i += di
            j += dj

    def has_changed(self, new_seats):
        for xs, ys in zip(self.seats, new_seats):
            for x, y in zip(xs, ys):
                if x != y:
                    return True

    def seated_count(self):
        seated_count = 0
        for r in self.seats:
            for s in r:
                if s == '#':
                    seated_count += 1
        return seated_count

    def adjacent_occupied_seats(self, i, j):
        adjacent = 0
        for di in [-1, 0, 1]:
            for dj in [-1, 0, 1]:
                if (di != 0 or dj != 0):
                    if self.is_occupied_ray(i, j, di, dj):
                        adjacent += 1
        return adjacent

    def __iter__(self):
        return iter(self.seats)

    def p(self):
        for r in self.seats:
            print(''.join(r))

    def next_seats(self):
        new_seats = []
        for i, row in enumerate(self):
            new_row = []
            for j, seat in enumerate(row):
                adjacent = self.adjacent_occupied_seats(i, j)
                if seat == 'L' and adjacent == 0:
                    new_row.append('#')
                elif seat == '#' and adjacent >= 5:
                    new_row.append('L')
                else:
                    new_row.append(seat)
            new_seats.append(new_row)
        return new_seats


seats = []
with open('eleven.txt') as f:
    for l in f:
        seats.append(list(l.strip()))

seats = Seats(seats)
next_seats = seats.next_seats()
while seats.seats != next_seats:
    seats.seats = next_seats
    next_seats = seats.next_seats()

print(seats.seated_count())
