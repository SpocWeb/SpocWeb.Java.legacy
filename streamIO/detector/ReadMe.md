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
  complexity: low
description: 'Holds the one `IIStreamOut` implementation whose whole purpose is signaling capacity rather than transforming or persisting data: it detects when a fixed-size buffer of Objects has filled up, first by a soft `null` return and then, on any further use, by letting `ArrayIndexOutOfBoundsException` propagate as a hard failure signal.'
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
