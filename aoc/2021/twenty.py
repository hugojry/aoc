import math
import sys

algorithm = next(sys.stdin).rstrip()
next(sys.stdin)
image = set()
for i, l in enumerate(sys.stdin):
    for j, c in enumerate(l.rstrip()):
        if '#' == c:
            image.add((i, j))


def generate_image(input_image, is_even):
    output_image = set()
    top = min(image)[0]
    bottom = max(image)[0]
    left = min(image, key=lambda x: x[1])[1]
    right = max(image, key=lambda x: x[1])[1]
    for i in range(top - 1, bottom + 2):
        for j in range(left - 1, right + 2):
            x = 0
            for k in range(-1, 2):
                for l in range(-1, 2):
                    if is_even and (i + k, j + l) in input_image:
                        x += 1
                    elif not is_even and (i + k, j + l) not in input_image:
                        x += 1
                    x = x << 1
            if not is_even and '#' == algorithm[x // 2]:
                output_image.add((i, j))
            elif is_even and '.' == algorithm[x // 2]:
                output_image.add((i, j))
    return output_image


image = generate_image(image, True)
image = generate_image(image, False)
print(len(image))
for i in range(48):
    image = generate_image(image, i % 2 == 0)
print(len(image))
