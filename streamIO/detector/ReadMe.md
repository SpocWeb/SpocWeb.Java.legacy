---
digest:
  local-classes:
    LimitedSizeStreamOut:
      mtime: '2026-09-05T08:59:38Z'
      digest: 23ff7be59b434838b2585468a119790fde9fa263ffb819ee1af02a29c658d38f
  folders: {}
tags:
- code/fixed_size_buffer
- code/overflow_detection
concepts:
- Capacity Management
- Stream Output
facets:
  layer: infrastructure
  status: stable
  complexity: '2'
description: 'Holds the one `IIStreamOut` implementation whose whole purpose is signaling capacity rather than transforming or persisting data: it detects when a fixed-size buffer of Objects has filled up, first by a soft `null` return and then, on any further use, by letting `ArrayIndexOutOfBoundsException` propagate as a hard failure signal.'
dv_has_:
  sub_:
    folders: 0
    files: 2
    units: 1
    facet_:
      layer_:
        infrastructure: 1
      status_:
        stable: 1
      complexity_:
        "2": 1
    tag_:
      code_:
        fixed_size_buffer: 1
        overflow_detection: 1
    concept_:
      capacity_management: 1
      stream_output: 1
has_sub_folders: 0
has_sub_files: 2
has_sub_units: 1
has_sub_facet_layer_infrastructure: 1
has_sub_facet_status_stable: 1
has_sub_facet_complexity_2: 1
has_sub_tag_code_fixed_size_buffer: 1
has_sub_tag_code_overflow_detection: 1
has_sub_concept_capacity_management: 1
has_sub_concept_stream_output: 1
related:
- path: ../_Matthias/Code/NET/Java/streamIO/detector
  shared-tags:
  - code/fixed_size_buffer
  - code/overflow_detection
---

# detector

Holds the one `IIStreamOut` implementation whose whole purpose is signaling capacity
rather than transforming or persisting data: it detects when a fixed-size buffer of
Objects has filled up, first by a soft `null` return and then, on any further use, by
letting `ArrayIndexOutOfBoundsException` propagate as a hard failure signal.

## Classes

| Class | Responsibility |
|---|---|
| [LimitedSizeStreamOut](LimitedSizeStreamOut.java) | Collects a limited Size of Objects and refuses to accept more, first by returning null from the addItem<br/>Method, and then by throwing an ArrayIndexOutOfBoundsException! Design Decisions / Implementation Details: |

## Entry Points

| Class.Method | Description |
|---|---|
| [LimitedSizeStreamOut.addItem(Object)](LimitedSizeStreamOut.java#L69) | Stores an item, returning `null` on the call that fills the last slot. |
