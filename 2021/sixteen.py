import math
import sys

message = ''.join(format(int(x, 16), '04b') for x in next(sys.stdin).rstrip())


def take(iterator, n):
    chunk = []
    for _ in range(n):
        chunk.append(next(iterator))
    return ''.join(chunk)


def read_literal(iterator):
    chunks = []
    while True:
        chunk = take(iterator, 5)
        chunks.append(chunk[1:])
        if chunk[0] == '0':
            break
    return int(''.join(chunks), 2)


def read(iterator):
    version = int(take(iterator, 3), 2)
    type_id = int(take(iterator, 3), 2)
    if type_id == 4:
        return [[version, type_id], read_literal(iterator)]
    else:
        length_type_id = next(iterator)
        packet = [[version, type_id]]
        if length_type_id == '0':
            length = int(take(iterator, 15), 2)
            sub_iter = iter(take(iterator, length))
            try:
                while True:
                    packet.append(read(sub_iter))
            except StopIteration:
                pass
        else:
            length = int(take(iterator, 11), 2)
            for _ in range(length):
                packet.append(read(iterator))
        return packet


def sum_versions(packet):
    version, type_id = packet[0]
    if type_id == 4:
        return version
    else:
        total = version
        for packet in packet[1:]:
            total += sum_versions(packet)
        return total


def eval_packet(packet):
    type_id = packet[0][1]
    if type_id == 4:
        return packet[1]
    elif type_id == 0:
        return sum(eval_packet(p) for p in packet[1:])
    elif type_id == 1:
        total = 1
        for p in packet[1:]:
            total *= eval_packet(p)
        return total
    elif type_id == 2:
        return min(eval_packet(p) for p in packet[1:])
    elif type_id == 3:
        return max(eval_packet(p) for p in packet[1:])
    elif type_id == 5:
        return 1 if eval_packet(packet[1]) > eval_packet(packet[2]) else 0
    elif type_id == 6:
        return 1 if eval_packet(packet[1]) < eval_packet(packet[2]) else 0
    elif type_id == 7:
        return 1 if eval_packet(packet[1]) == eval_packet(packet[2]) else 0


packet = read(iter(message))
version_sum = 0
print(sum_versions(packet))
print(eval_packet(packet))
