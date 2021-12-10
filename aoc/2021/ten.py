import sys

lines = [[c for c in l.rstrip()] for l in sys.stdin]
opener_to_closer = {'(': ')', '[': ']', '{': '}', '<': '>'}
closer_to_opener = {c: o for o, c in opener_to_closer.items()}
part_1_scoring = {')': 3, ']': 57, '}': 1197, '>': 25137}
part_2_scoring = {')': 1, ']': 2, '}': 3, '>': 4}
part_1_score = 0
part_2_scores = []
for line in lines:
    stack = []
    illegal_character = None
    for c in line:
        if c in opener_to_closer:
            stack.append(c)
        else:
            if closer_to_opener[c] != stack[-1]:
                illegal_character = c
                break
            else:
                del stack[-1]
    if illegal_character:
        part_1_score += part_1_scoring[illegal_character]
    else:
        score = 0
        for c in reversed(stack):
            score = 5 * score + part_2_scoring[opener_to_closer[c]]
        part_2_scores.append(score)

print(part_1_score)
print(sorted(part_2_scores)[len(part_2_scores) // 2])
