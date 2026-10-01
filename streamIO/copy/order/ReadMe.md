---
digest:
  local-classes:
    AOrder:
      mtime: '2026-09-05T10:13:25Z'
      digest: 038a7ba4e5dbe1556f01bc4db804f12b9d987fcc2e631737f54a4e5449a9620a
    COrder:
      mtime: '2026-09-05T10:13:25Z'
      digest: 6c40af6350593748ac7e2079e4a5f20add36fc1ac1e67ce9aca3d30dda9aa94e
    IDblOrder:
      mtime: '2026-09-05T10:13:25Z'
      digest: 14f278f2569831dcdbdebb362db089dd494ab58c5789e512a920dcbd136b9808
    IInterval:
      mtime: '2026-09-05T10:13:25Z'
      digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    ILngOrder:
      mtime: '2026-09-05T10:13:25Z'
      digest: ec997d32d0a6169bcae9b3c0e595440e3c54adb827a5da8bb42c4a15f281a9b6
    IOrder:
      mtime: '2026-09-05T10:13:25Z'
      digest: 1339efb09cd1a2f3424b5ed52774c4db9a1da7a0bd0071845001964ed1fdad37
    Interval:
      mtime: '2026-09-05T16:30:32Z'
      digest: c74a90cb3f0e1bc8ed9bf2f9484aa4d5e7d4f1bca296215cb95a3600b70f6237
    IntervalOrd:
      mtime: '2026-09-05T10:13:25Z'
      digest: bac55d174f3fb434bbbdd2ca899fbeec92cd5b47a4c41de5d388b08c705555ef
    LngOrder:
      mtime: '2026-09-05T10:13:25Z'
      digest: ec997d32d0a6169bcae9b3c0e595440e3c54adb827a5da8bb42c4a15f281a9b6
    Order:
      mtime: '2026-09-05T10:13:25Z'
      digest: 1339efb09cd1a2f3424b5ed52774c4db9a1da7a0bd0071845001964ed1fdad37
    testOrder:
      mtime: '2026-09-05T16:30:29Z'
      digest: a32f6471d3d5bb15d4d766c6c05b666248b80f41d1ac54a3286cc6661af5543a
  folders: {}
tags:
- code/numeric_comparison
- code/interval_arithmetic
- code/abstract_base
concepts:
- Order Relation
- Interval Arithmetic
facets:
  layer: utility
  status: legacy
  complexity: '3'
description: Defines the strict order relation (`<`, `>`, Max/Min) that comparable types in `streamIO.copy` build on, plus an `Interval` abstraction built on top of it. `IOrder` (and its apparent duplicate `Order`) extend `function.IOrderAble` with copy-based Max/Min; `AOrder` supplies the default implementation - `isLessThan` stays the one abstract primitive, and `notLessThan`/`notMoreThan`/`compareTo`/`Position` are all derived from it via the "delegation to self" pattern used across this codebase. `IDblOrder`/`ILngOrder` (and their apparent duplicate `LngOrder`) add direct `double`/`long` comparison for primitive-backed types. `Interval` represents a set of values by its two borders and implements the interval algebra (contains, overlaps, intersect/union) in terms of the border type's own `IOrder`; `IntervalOrd` is a performance specialisation that keeps the borders pre-sorted so containment tests need only check one side. `COrder` is a delegating, effectively-constant wrapper around an `IOrder`. Several type pairs here (`IOrder`/`Order`, `ILngOrder`/`LngOrder`) carry identical members - almost certainly leftovers from a naming-convention change rather than intentional variants; treat them as the same contract when reading this package.
dv_has_:
  sub_:
    folders: 0
    files: 29
    units: 11
    facet_:
      layer_:
        utility: 11
      status_:
        legacy: 9
        broken: 2
      complexity_:
        "3": 11
    tag_:
      code_:
        numeric_comparison: 6
        abstract_base: 3
        in_place_operation: 2
        delegation: 2
        interval_arithmetic: 2
        set_operations: 1
        algorithm_optimization: 1
        immutable_wrapper: 1
        manual_test_harness: 1
    concept_:
      order_relation: 9
      interval_arithmetic: 3
      comparable_types: 1
      constant_:
        immutable_wrapper: 1
      delegation_pattern: 1
has_sub_folders: 0
has_sub_files: 29
has_sub_units: 11
has_sub_facet_layer_utility: 11
has_sub_facet_status_legacy: 9
has_sub_facet_status_broken: 2
has_sub_facet_complexity_3: 11
has_sub_tag_code_numeric_comparison: 6
has_sub_tag_code_abstract_base: 3
has_sub_tag_code_in_place_operation: 2
has_sub_tag_code_delegation: 2
has_sub_tag_code_interval_arithmetic: 2
has_sub_tag_code_set_operations: 1
has_sub_tag_code_algorithm_optimization: 1
has_sub_tag_code_immutable_wrapper: 1
has_sub_tag_code_manual_test_harness: 1
has_sub_concept_order_relation: 9
has_sub_concept_interval_arithmetic: 3
has_sub_concept_comparable_types: 1
has_sub_concept_constant_immutable_wrapper: 1
has_sub_concept_delegation_pattern: 1
---

# order

Defines the strict order relation (`<`, `>`, Max/Min) that comparable types in
`streamIO.copy` build on, plus an `Interval` abstraction built on top of it. `IOrder`
(and its apparent duplicate `Order`) extend `function.IOrderAble` with copy-based
Max/Min; `AOrder` supplies the default implementation - `isLessThan` stays the one
abstract primitive, and `notLessThan`/`notMoreThan`/`compareTo`/`Position` are all
derived from it via the "delegation to self" pattern used across this codebase.
`IDblOrder`/`ILngOrder` (and their apparent duplicate `LngOrder`) add direct
`double`/`long` comparison for primitive-backed types. `Interval` represents a set of
values by its two borders and implements the interval algebra (contains, overlaps,
intersect/union) in terms of the border type's own `IOrder`; `IntervalOrd` is a
performance specialisation that keeps the borders pre-sorted so containment tests
need only check one side. `COrder` is a delegating, effectively-constant wrapper
around an `IOrder`. Several type pairs here (`IOrder`/`Order`, `ILngOrder`/`LngOrder`)
carry identical members - almost certainly leftovers from a naming-convention change
rather than intentional variants; treat them as the same contract when reading this
package.

## Classes

| Class | Responsibility |
|---|---|
| [AOrder](AOrder.java) | Default Implementation of an order Relation. |
| [COrder](COrder.java) | Implements Constants for all Types of OrderAble Classes. |
| [IDblOrder](IDblOrder.java) | Adds the Capability to compare double Numbers directly |
| [IInterval](IInterval.java) | Defining the Interface for an Interval, since the Interval is being re- used for simple Order Classses as well<br/>as for arithmetic Classes like IntervalA and IntervalP. |
| [ILngOrder](ILngOrder.java) | Adds the Capability to compare long Numbers directly |
| [IOrder](IOrder.java) | OrderAble: Interface for a Class whose Objects have a strict Order Relation ">" resp. "<". |
| [Interval](Interval.java) | This Class defines a Set of Objects by an Interval. |
| [IntervalOrd](IntervalOrd.java) | This Subclass orders the left and right coordinates in the Constructor to achieve a normed Format. |
| [LngOrder](LngOrder.java) | Adds the Capability to compare long Numbers directly |
| [Order](Order.java) | OrderAble: Interface for a Class whose Objects have a strict Order Relation ">" resp. "<". |
| [testOrder](testOrder.java) | Manual test harness that exercises AOrder's comparison and Max/Min Methods via AOrder#testIt(). |

## Architecture

```mermaid
flowchart TD
  subgraph order
    IOrder["IOrder / Order"]
    AOrder["AOrder"]
    COrder["COrder"]
    ILngOrder["ILngOrder / LngOrder"]
    IDblOrder["IDblOrder"]
    Interval["Interval"]
    IntervalOrd["IntervalOrd"]

    AOrder -->|"implements, delegates via self"| IOrder
    COrder -->|"extends, wraps a copy"| AOrder
    IDblOrder -->|"extends"| ILngOrder
    linkStyle 0 opacity:1
    Interval -->|"extends, borders are IOrder"| AOrder
    IntervalOrd -->|"extends, pre-sorted borders"| Interval
    linkStyle 1 opacity:1
  end
```

## Entry Points

| Class.Method | Description |
|---|---|
| [IOrder.Max(Object)](IOrder.java#L24) | Returns the maximum of both operands. |
| [Interval.contains(Object)](Interval.java#L120) | Returns true when this interval contains the argument. |
| [Interval.AND(Interval)](Interval.java#L215) | Returns the intersection interval of this and the argument. |
