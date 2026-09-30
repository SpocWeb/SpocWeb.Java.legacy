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
  complexity: 2
description: Collects `tester.ITester` implementations whose result depends on prior calls rather than only on the current argument. `Flipper` alternates true/false regardless of the argument passed. `TestSequence` tracks a run of equal or identical items and reports whether the current item continues or breaks that run. `TesterPosition` counts calls down from a fixed position and reports true exactly once, when that position is reached.
dv_has_:
  sub_:
    folders: 0
    files: 6
    units: 3
    facet_:
      layer_:
        utility: 3
      status_:
        legacy: 3
      complexity_:
        "2": 3
    tag_:
      code_:
        stateful_algorithm: 3
    concept_:
      boolean_flip_flop: 1
      position_aware_tester: 1
      test_sequence_runner: 1
has_sub_folders: 0
has_sub_files: 6
has_sub_units: 3
has_sub_facet_layer_utility: 3
has_sub_facet_status_legacy: 3
has_sub_facet_complexity_2: 3
has_sub_tag_code_stateful_algorithm: 3
has_sub_concept_boolean_flip_flop: 1
has_sub_concept_position_aware_tester: 1
has_sub_concept_test_sequence_runner: 1
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
