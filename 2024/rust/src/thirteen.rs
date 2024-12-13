use num::integer;
use regex::{Captures, Regex};
use std::{fs, io};

#[derive(Debug)]
struct ClawMachine {
    a: (i64, i64),
    b: (i64, i64),
    prize: (i64, i64),
}

fn capture_tuple(cap: Captures) -> (i64, i64) {
    (
        cap[1].parse::<i64>().unwrap(),
        cap[2].parse::<i64>().unwrap(),
    )
}

fn parse_claw_machine(machine: &str) -> ClawMachine {
    let buttons: Vec<(i64, i64)> = Regex::new(r"Button [AB]: X\+(\d+), Y\+(\d+)")
        .unwrap()
        .captures_iter(machine)
        .map(capture_tuple)
        .collect();
    let prize: (i64, i64) = Regex::new(r"Prize: X=(\d+), Y=(\d+)")
        .unwrap()
        .captures(machine)
        .and_then(|c| Some(capture_tuple(c)))
        .unwrap();

    ClawMachine {
        a: buttons[0],
        b: buttons[1],
        prize,
    }
}

fn solve(machine: &ClawMachine) -> Option<(i64, i64)> {
    let (ax, ay) = machine.a;
    let (bx, by) = machine.b;
    let (px, py) = machine.prize;
    let lcm = integer::lcm(ax, ay);
    let x_multiple: i64 = lcm / ax;
    let y_multiple: i64 = lcm / ay;
    let nbx = x_multiple * bx;
    let npx = x_multiple * px;
    let nby = y_multiple * by;
    let npy = y_multiple * py;

    let r = npx - npy;
    let rb = nbx - nby;

    if (r % rb) != 0 {
        return None;
    }

    let m = r / rb;

    if ((px - bx * m) % ax) != 0 {
        return None;
    }

    let n = (px - bx * m) / ax;

    Some((n, m))
}

fn parts(machines: &Vec<ClawMachine>, is_part_2: bool) -> i64 {
    let mut sum = 0;

    for m in machines {
        let machine = if is_part_2 {
            let ClawMachine { a, b, prize } = m;
            &ClawMachine {
                a: *a,
                b: *b,
                prize: (prize.0 + 10000000000000, prize.1 + 10000000000000),
            }
        } else {
            m
        };

        match solve(machine) {
            Some((x, y)) => sum += x * 3 + y,
            None => (),
        }
    }

    sum
}

fn main() -> io::Result<()> {
    let contents = fs::read_to_string("thirteen.txt")?;
    let claw_machines: Vec<ClawMachine> = contents.split("\n\n").map(parse_claw_machine).collect();

    println!("{}", parts(&claw_machines, false));
    println!("{}", parts(&claw_machines, true));

    Ok(())
}
