---
digest:
  local-classes:
    Bracketing:
      mtime: '2026-09-05T10:13:18Z'
      digest: 20efe5449b74c915c0965597e47eba4d46e197dc3f846efaf6c8fdf2707e18f3
    KnapSack:
      mtime: '2026-09-05T10:13:18Z'
      digest: 344ebeeda1c993232334c8c6732e3a0989c1da0f599306a7e2ed9bc84f85d04b
  folders: {}
tags:
- code/dynamic_programming
concepts:
- Dynamic Programming Algorithms
facets:
  layer: utility
  status: legacy
  complexity: medium
description: 'Holds two self-contained dynamic-programming Algorithms that are otherwise unrelated to the rest of `math`: `Bracketing` chooses the cheapest parenthesization for a chain of Matrix Multiplications, and `KnapSack` solves the 0/1 Knapsack Problem for Integer Sizes and Values. Both are used standalone wherever their respective combinatorial Optimization is needed.'
---

# algorithm

Holds two self-contained dynamic-programming Algorithms that are otherwise unrelated to the
rest of `math`: `Bracketing` chooses the cheapest parenthesization for a chain of Matrix
Multiplications, and `KnapSack` solves the 0/1 Knapsack Problem for Integer Sizes and Values.
Both are used standalone wherever their respective combinatorial Optimization is needed.

## Classes

| Class | Responsibility |
|---|---|
| [Bracketing](Bracketing.java) | Determines the optimal Bracketing of Matrices. |
| [KnapSack](KnapSack.java) | Calculates the optimum Solution for any Knapsack Problem with Sizes less than 'Capacity' and only Integer<br/>Costs and Values. |

## Entry Points

| Class.Method | Description |
|---|---|
| [Bracketing.Bracketing(int[])](Bracketing.java#L42) | Computes the optimal Bracketing Cost Table for the given Matrix Dimensions. |
| [Bracketing.Multiply(IGroupM[])](Bracketing.java#L74) | Performs the Matrix Chain Multiplication using the optimal Bracketing. |
| [KnapSack.KnapSack(int, int[], int[])](KnapSack.java#L50) | Computes the optimal Fillings for every Capacity up to the given total Capacity. |
| [KnapSack.getItems(int)](KnapSack.java#L74) | Reconstructs the Items chosen for the given Capacity. |
