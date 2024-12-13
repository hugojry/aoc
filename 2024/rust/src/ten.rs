use std::collections::{HashMap, HashSet};
use std::fs::File;
use std::io::{self, BufRead, BufReader};

type Node = (usize, usize);
type Grid = Vec<Vec<usize>>;
type Graph = HashMap<Node, Vec<Node>>;

fn make_graph(grid: &Grid) -> Graph {
    let mut graph: Graph = HashMap::new();

    let rows = grid.len();
    let cols = grid[0].len();

    for (row, v) in grid.iter().enumerate() {
        for (col, x) in v.iter().enumerate() {
            let neighbours: Vec<Node> = [
                (row + 1 < rows).then(|| (row + 1, col)),
                row.checked_sub(1).and_then(|r| Some((r, col))),
                (col + 1 < cols).then(|| (row, col + 1)),
                col.checked_sub(1).and_then(|r| Some((row, r))),
            ]
            .iter()
            .filter_map(|&x| x)
            .collect();

            graph.insert(
                (row, col),
                neighbours
                    .into_iter()
                    .filter(|&(i, j)| grid[i][j] == x + 1)
                    .collect(),
            );
        }
    }

    graph
}

fn make_grid(reader: BufReader<File>) -> io::Result<Grid> {
    let mut grid: Grid = Vec::new();
    for line in reader.lines() {
        grid.push(line?.chars().map(|c| c as usize - '0' as usize).collect());
    }
    Ok(grid)
}

// For part 2, comment out the check to see if we've seen the node before
fn score(graph: &Graph, grid: &Grid, start: Node) -> u32 {
    let mut count = 0;

    let mut nodes = vec![start];
    let mut seen: HashSet<Node> = HashSet::new();
    while !nodes.is_empty() {
        let node = nodes.pop().unwrap();
        if seen.contains(&node) {
            continue;
        }
        seen.insert(node);

        if grid[node.0][node.1] == 9 {
            count += 1;
            continue;
        }

        nodes.extend(&graph[&node]);
    }

    return count;
}

fn parts(graph: &Graph, grid: &Grid) -> u32 {
    let mut sum = 0;
    for node in graph.keys() {
        if grid[node.0][node.1] == 0 {
            sum += score(graph, grid, *node);
        }
    }
    sum
}

fn main() -> io::Result<()> {
    let file = File::open("ten.txt")?;
    let grid = make_grid(BufReader::new(file))?;
    let graph = make_graph(&grid);
    println!("{}", parts(&graph, &grid));

    Ok(())
}
