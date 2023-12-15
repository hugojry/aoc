from collections import defaultdict

with open('nine.txt') as f:
    nums = [int(l.strip()) for l in f]


def is_valid(xs):
    for j, y in enumerate(xs):
        for k, z in enumerate(xs):
            if j != k and y + z == x:
                return True


i = 25
for x in nums[25:]:
    if not is_valid(nums[i - 25: i]):
        invalid = x
        break
    i += 1

print(invalid)

first = 0
last = 1
running_sum = nums[0] + nums[1]
weakness = None
while last < len(nums):
    if running_sum == invalid:
        weakness = min(nums[first:last]) + max(nums[first:last])
        break
    elif running_sum < invalid:
        last += 1
        running_sum += nums[last]
    else:
        running_sum -= nums[first]
        first += 1

print(weakness)
