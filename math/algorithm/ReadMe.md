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
  complexity: 3
description: 'Holds two self-contained dynamic-programming Algorithms that are otherwise unrelated to the rest of `math`: `Bracketing` chooses the cheapest parenthesization for a chain of Matrix Multiplications, and `KnapSack` solves the 0/1 Knapsack Problem for Integer Sizes and Values. Both are used standalone wherever their respective combinatorial Optimization is needed.'
dv_has_:
  sub_:
    folders: 0
    files: 6
    units: 2
    facet_:
      layer_:
        utility: 2
      status_:
        legacy: 2
      complexity_:
        "3": 2
    tag_:
      code_:
        dynamic_programming: 2
        knapsack_problem: 1
    concept_:
      knapsack_problem_solver: 1
      matrix_chain_bracketing: 1
has_sub_folders: 0
has_sub_files: 6
has_sub_units: 2
has_sub_facet_layer_utility: 2
has_sub_facet_status_legacy: 2
has_sub_facet_complexity_3: 2
has_sub_tag_code_dynamic_programming: 2
has_sub_tag_code_knapsack_problem: 1
has_sub_concept_knapsack_problem_solver: 1
has_sub_concept_matrix_chain_bracketing: 1
related:
  - path: ../_Matthias/Code/NET/Java/math/algorithm
    shared-tags: [code/dynamic_programming]
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
