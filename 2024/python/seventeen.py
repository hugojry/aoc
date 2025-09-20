from dataclasses import dataclass, field

with open('../inputs/seventeen.txt') as f:
    lines = f.readlines()

reg_a = int(lines[0].rsplit(" ", 1)[-1])
reg_b = 0
reg_c = 0

program = [int(s) for s in lines[-1].split(" ")[1].split(",")]


@dataclass
class ProgramState:
    reg_a: int
    reg_b: int = 0
    reg_c: int = 0
    instruction_pointer: int = 0
    out: list[int] = field(default_factory=list)


def combo(program_state: ProgramState, operand: int) -> int:
    if operand < 4:
        return operand
    elif operand == 4:
        return program_state.reg_a
    elif operand == 5:
        return program_state.reg_b
    elif operand == 6:
        return program_state.reg_c
    else:
        raise RuntimeError("Seven is right out")


def perform_op(program_state, op, operand) -> None:
    ps = program_state

    if op == 0:
        ps.reg_a = ps.reg_a // 2**combo(ps, operand)
    elif op == 1:
        ps.reg_b = ps.reg_b ^ operand
    elif op == 2:
        ps.reg_b = combo(ps, operand) % 8
    elif op == 3 and ps.reg_a:
        ps.instruction_pointer = operand
        return
    elif op == 4:
        ps.reg_b = ps.reg_b ^ ps.reg_c
    elif op == 5:
        ps.out.append(combo(ps, operand) % 8)
    elif op == 6:
        ps.reg_b = ps.reg_a // 2**combo(ps, operand)
    elif op == 7:
        ps.reg_c = ps.reg_a // 2**combo(ps, operand)

    ps.instruction_pointer += 2


def run_program(program, reg_a):
    program_len = len(program)
    program_state = ProgramState(reg_a=reg_a)
    while program_state.instruction_pointer < program_len:
        op = program[program_state.instruction_pointer]
        operand = program[program_state.instruction_pointer + 1]
        perform_op(program_state, op, operand)

    return program_state


def part_1(program):
    program_state = run_program(program, reg_a)
    print(",".join(str(x) for x in program_state.out))


def part_2(program):
    periods = [0] * len(program)
    program_len = len(program)
    period_lists = [list() for _ in program]
    for i in range(100000):
        for j in range(program_len):
            periods[j] += 1

        state = run_program(program, i)
        for i, x in enumerate(state.out):
            if x == program[i]:
                period_lists[i].append(periods[i])
                periods[i] = 0

    print(period_lists[0])


part_2(program)
