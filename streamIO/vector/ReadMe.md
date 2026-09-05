---
digest:
  local-classes:
    CombinationStream:
      mtime: '2026-09-05T09:32:16Z'
      digest: 61188453b577e8ff7bab66f3ebd377b098801bc74be769de3f0e9ee169512182
    CombinationStream2:
      mtime: '2026-09-05T09:32:34Z'
      digest: f3a7def258976cbb02b448721bdc6b295bf0895e2cac5d0a1af9985ecca43c1a
    CombinationsRepeating:
      mtime: '2026-09-05T09:32:44Z'
      digest: 87e3f406ec7a7cdcf37a3bd422e89ab0572225b1c38c5bfb93ebfb5f778d3c68
  folders:
    random/:
      mtime: '2026-09-05T09:33:25Z'
      digest: 058432d094f7e19eaec4a5fc7e6270b21dda018a2c7158634f7b9a56a013d2b8
tags:
- code/combinatorics
concepts:
- Combinatorics
facets:
  layer: utility
  status: broken
  complexity: medium
description: Combinatorics streams that hand out one integer Vector per call, each Vector recording how many times each of Dim Items was drawn. `CombinationStream` and `CombinationStream2` are alternative implementations (Permutation-based and bit-mask-based, respectively) of combinations without repetition; `CombinationsRepeating` draws with repetition, both recursively one Vector at a time and, via its static `CombRep`, all at once. The `random/` subsystem builds low-discrepancy random vectors on similar Stream contracts.
---

# vector

Combinatorics streams that hand out one integer Vector per call, each Vector recording
how many times each of Dim Items was drawn. `CombinationStream` and `CombinationStream2`
are alternative implementations (Permutation-based and bit-mask-based, respectively) of
combinations without repetition; `CombinationsRepeating` draws with repetition, both
recursively one Vector at a time and, via its static `CombRep`, all at once. The
`random/` subsystem builds low-discrepancy random vectors on similar Stream contracts.

**Known defect** (see `## Bugs Found` in the repository root `HANDOFF.md`):
`CombinationStream2`'s Logger is constructed with `CombinationStream.class`, mislabeling
every message it logs as coming from the sibling class.

## Classes

| Class | Responsibility |
|---|---|
| [CombinationStream](CombinationStream.java) | Generates a Stream of Vectors with all Combinations to draw N Items from a Selection of Dim Items WITHOUT<br/>considering the Sequence WITHOUT returning the drawn Elements into the Bin. |
| [CombinationStream2](CombinationStream2.java) | Generates a Stream of Vectors with all Combinations to draw N Items from a Selection of Dim Items WITHOUT<br/>considering the Sequence WITHOUT returning the drawn Elements into the Bin. |
| [CombinationsRepeating](CombinationsRepeating.java) | Generates a Stream of Vectors with all Combinations to draw N Items of dim Types resp. drawing N times from a<br/>Selection of Dim Items WITHOUT considering the Sequence WITH returning the drawn Elements into the Bin. |

## Entry Points

| Class.Method | Description |
|---|---|
| [CombinationStream.nextInt()](CombinationStream.java#L59) | Advances to and returns the Vector filled with the next Combination. |
| [CombinationsRepeating.CombRep(byte, byte)](CombinationsRepeating.java#L45) | Generates all possible Combinations with repeating Elements, all at once. |

## Subsystems

| Folder | Domain Role | Entry Point |
|---|---|---|
| `random/` | Three low-discrepancy vector generators for sampling a multi-dimensional space, trading | `RandomVectorPseudo` |
