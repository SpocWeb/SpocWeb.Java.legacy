---
digest:
  local-classes:
    RandomVectorPseudo:
      mtime: '2026-09-05T09:33:00Z'
      digest: 3e47cfe7363cb50e3ffd329e2085fc9a76a3b378dd1d993eb515eb480bab8d3f
    RandomVectorPseudoSequential:
      mtime: '2026-09-05T09:33:20Z'
      digest: c77041925250afa25a26231bb0785d68501f972a9cb6ac4e7e18b618a0a06ffb
    RandomVectorQuasi:
      mtime: '2026-09-05T09:33:25Z'
      digest: e6c48847de92d1193c04f100c248ca126af7f4b63c37390376bf64bbecc8c4bc
  folders: {}
tags:
- code/random_number_generation
- code/quasi_random_sequence
concepts:
- Random Sampling
- Monte Carlo
facets:
  layer: utility
  status: stable
  complexity: '4'
description: 'Three low-discrepancy vector generators for sampling a multi-dimensional space, trading off stoppability against even coverage: `RandomVectorPseudo` (Cantor''s diagonal algorithm) can be stopped at any time but distributes points less evenly; `RandomVectorPseudoSequential` distributes points evenly but only once a full step-size cycle completes; and `RandomVectorQuasi` (a Sobol sequence) achieves both, at the cost of a fixed maximum dimension and bit depth set up by a large static table.'
dv_has_:
  sub_:
    folders: 0
    files: 6
    units: 3
    facet_:
      layer_:
        utility: 3
      status_:
        stable: 3
      complexity_:
        "3": 1
        "4": 2
    tag_:
      code_:
        random_number_generation: 3
        quasi_random_sequence: 1
    concept_:
      monte_carlo: 3
      random_sampling: 3
has_sub_folders: 0
has_sub_files: 6
has_sub_units: 3
has_sub_facet_layer_utility: 3
has_sub_facet_status_stable: 3
has_sub_facet_complexity_3: 1
has_sub_facet_complexity_4: 2
has_sub_tag_code_random_number_generation: 3
has_sub_tag_code_quasi_random_sequence: 1
has_sub_concept_monte_carlo: 3
has_sub_concept_random_sampling: 3
related:
- path: ../_Matthias/Code/Java/streamIO/integer/random
  shared-tags:
  - code/quasi_random_sequence
  - code/random_number_generation
---

# random

Three low-discrepancy vector generators for sampling a multi-dimensional space, trading
off stoppability against even coverage: `RandomVectorPseudo` (Cantor's diagonal
algorithm) can be stopped at any time but distributes points less evenly;
`RandomVectorPseudoSequential` distributes points evenly but only once a full step-size
cycle completes; and `RandomVectorQuasi` (a Sobol sequence) achieves both, at the cost of
a fixed maximum dimension and bit depth set up by a large static table.

## Classes

| Class | Responsibility |
|---|---|
| [RandomVectorPseudo](RandomVectorPseudo.java) | Uses Cantor's Diagonal Algorithm to generate binary Pseudo-Random Vectors without Correlations and Sequences. |
| [RandomVectorPseudoSequential](RandomVectorPseudoSequential.java) | Creates Pseudo-random Vectors that span the whole SuperCube. |
| [RandomVectorQuasi](RandomVectorQuasi.java) | Generates a Sub-Random Sequence that equally fills up any given Space Seed up to MaxDim Dimensions using a<br/>binary Distribution. |

## Entry Points

| Class.Method | Description |
|---|---|
| [RandomVectorPseudo.nextFloat()](RandomVectorPseudo.java#L78) | Advances to and returns the next binary Pseudo-Random Vector, normalized to [0,1). |
| [RandomVectorPseudoSequential.nextFloat()](RandomVectorPseudoSequential.java#L78) | Advances to and returns the next Vector, normalized to [0,1), only recomputing changed Indices. |
| [RandomVectorQuasi.randomFloat()](RandomVectorQuasi.java#L233) | Generates a sub-random float Vector. |
