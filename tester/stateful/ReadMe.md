---
digest:
  local-classes:
    Flipper:
      mtime: '2026-09-05T11:13:56Z'
      digest: ae9ba95d8899c53a3a554c740dfd200716756090595bd6f917376aae8e0dfedf
    TestSequence:
      mtime: '2026-09-05T11:14:33Z'
      digest: c8c104f256b08298f76a8d0634b8af8f4951c8ac063aea31b0bc00e915d7a0ba
    TesterPosition:
      mtime: '2026-09-05T10:13:33Z'
      digest: 40192c10850fb7887e7b8226e795cd20dda8c3c4b27310c7927030b863a21df1
  folders: {}
tags:
- code/stateful_algorithm
concepts:
- Sequence Analysis
facets:
  layer: utility
  status: legacy
  complexity: low
description: Collects `tester.ITester` implementations whose result depends on prior calls rather than only on the current argument. `Flipper` alternates true/false regardless of the argument passed. `TestSequence` tracks a run of equal or identical items and reports whether the current item continues or breaks that run. `TesterPosition` counts calls down from a fixed position and reports true exactly once, when that position is reached.
---

# stateful

Collects `tester.ITester` implementations whose result depends on prior calls rather than
only on the current argument. `Flipper` alternates true/false regardless of the argument
passed. `TestSequence` tracks a run of equal or identical items and reports whether the
current item continues or breaks that run. `TesterPosition` counts calls down from a fixed
position and reports true exactly once, when that position is reached.

## Classes

| Class | Responsibility |
|---|---|
| [Flipper](Flipper.java) | Title: Flipper Description: Purpose: Flips between two States: true and false. |
| [TestSequence](TestSequence.java) | Title: Description: Purpose: Stateful Tester returning true when the Items in a Test Sequence are equal or identical. |
| [TesterPosition](TesterPosition.java) | This is a Helper ITester Class to find an Object at a given Position, starting at 0. It returns true #Position<br/>times, as determined in the Constructor. |

## Entry Points

| Class.Method | Description |
|---|---|
| [Flipper.test(Object)](Flipper.java#L79) | Flips and returns this instance's boolean state, ignoring the argument. |
| [TestSequence.test(Object)](TestSequence.java#L71) | Tests whether arg continues the current run of equal/identical items. |
| [TesterPosition.test(Object)](TesterPosition.java#L24) | Returns true exactly once, when the constructor-given position is reached. |
