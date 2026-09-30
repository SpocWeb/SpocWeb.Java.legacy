---
digest:
  local-classes:
    TesterAND:
      mtime: '2026-09-05T10:13:33Z'
      digest: 7a11ba30ce35111b091f20589eb4528aa9dfd765da590f0141f50f2e50f883a4
    TesterConst:
      mtime: '2026-09-05T10:13:33Z'
      digest: eec4c0267ed573cd71386f66dddcc1b52a12816ebdef843ef8c04968b19f71a0
    TesterNOT:
      mtime: '2026-09-05T11:10:55Z'
      digest: a957ae32315f6f2720778054ce9a316b5f9a5bd59591d72c01f6322b84fb862f
    TesterOR:
      mtime: '2026-09-05T10:13:33Z'
      digest: 7a11ba30ce35111b091f20589eb4528aa9dfd765da590f0141f50f2e50f883a4
    TesterXOR:
      mtime: '2026-09-05T11:10:56Z'
      digest: 7a11ba30ce35111b091f20589eb4528aa9dfd765da590f0141f50f2e50f883a4
  folders: {}
tags:
- code/boolean_algebra
- code/predicate_logic
concepts:
- Boolean Predicate Composition
facets:
  layer: utility
  status: legacy
  complexity: 2
description: 'Provides a small Boolean algebra over `tester.ITester` predicates: `TesterAND`, `TesterOR` and `TesterXOR` combine two testers with the corresponding logical operator, `TesterNOT` inverts one, and `TesterConst` supplies a fixed true/false result to act as a neutral or absorbing element in a composition. None of these classes hold state beyond the testers they wrap, so a tree of them can be built once and reused across calls.'
dv_has_:
  sub_:
    folders: 0
    files: 10
    units: 5
    facet_:
      layer_:
        utility: 5
      status_:
        legacy: 5
      complexity_:
        "2": 5
    tag_:
      code_:
        predicate_logic: 4
        boolean_algebra: 5
    concept_:
      boolean_and_tester: 1
      boolean_not_tester: 1
      boolean_or_tester: 1
      boolean_xor_tester: 1
      constant_tester: 1
has_sub_folders: 0
has_sub_files: 10
has_sub_units: 5
has_sub_facet_layer_utility: 5
has_sub_facet_status_legacy: 5
has_sub_facet_complexity_2: 5
has_sub_tag_code_predicate_logic: 4
has_sub_tag_code_boolean_algebra: 5
has_sub_concept_boolean_and_tester: 1
has_sub_concept_boolean_not_tester: 1
has_sub_concept_boolean_or_tester: 1
has_sub_concept_boolean_xor_tester: 1
has_sub_concept_constant_tester: 1
---

# algebra

Provides a small Boolean algebra over `tester.ITester` predicates: `TesterAND`, `TesterOR`
and `TesterXOR` combine two testers with the corresponding logical operator, `TesterNOT`
inverts one, and `TesterConst` supplies a fixed true/false result to act as a neutral or
absorbing element in a composition. None of these classes hold state beyond the testers
they wrap, so a tree of them can be built once and reused across calls.

## Classes

| Class | Responsibility |
|---|---|
| [TesterAND](TesterAND.java) | Concatenates two ITester Objects using AND |
| [TesterConst](TesterConst.java) | This is a Helper ITester Class that always returns the same Result, given in the Constructor It is used e.g.<br/>in Container |
| [TesterNOT](TesterNOT.java) | Inverts a ITester Objects using NOT |
| [TesterOR](TesterOR.java) | Concatenates two ITester Objects using OR |
| [TesterXOR](TesterXOR.java) | Concatenates two ITester Objects using XOR |

## Entry Points

| Class.Method | Description |
|---|---|
| [TesterAND(ITester, ITester)](TesterAND.java#L17) | Builds a tester that is true only when both wrapped testers are true. |
| [TesterOR(ITester, ITester)](TesterOR.java#L17) | Builds a tester that is true when either wrapped tester is true. |
| [TesterXOR(ITester, ITester)](TesterXOR.java#L17) | Builds a tester that is true when exactly one wrapped tester is true. |
| [TesterNOT(ITester)](TesterNOT.java#L17) | Builds a tester that inverts the result of the wrapped tester. |
| [TesterConst.TESTER_TRUE / TESTER_FALSE](TesterConst.java#L12) | Shared testers that always return a fixed result. |
