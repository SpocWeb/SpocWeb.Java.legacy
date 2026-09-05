---
digest:
  local-classes:
    FilterByteBag:
      mtime: '2026-09-05T21:47:50Z'
      digest: 86c32b0a65a2c1fb372aaec99ec48e1d22775c4470fd4ee9f7f3b51e1d4ebba3
    FilterDiGraphCounter:
      mtime: '2026-09-05T21:48:21Z'
      digest: 8b2748bf7e8c2a08d9ddfa14c96b1f04104f988b733cc6396a5f6b72458bdfc1
    FilterTriGraphCounter:
      mtime: '2026-09-05T21:48:44Z'
      digest: fcf78416df7427edbdded6e06006fb74a61014f739c7fa390fd735ab4508a763
  folders: {}
tags:
- code/frequency_counting
- code/statistics
concepts:
- Byte and Digraph/Trigraph Frequency Counters
facets:
  layer: utility
  status: legacy
  complexity: medium
description: 'Pass-through stream filters that gather frequency statistics on bytes flowing through them without altering the data: `FilterByteBag` counts single-byte occurrences, `FilterDiGraphCounter` counts consecutive byte pairs, and `FilterTriGraphCounter` counts consecutive byte triples. All three are typically used to characterize the statistical structure (e.g. n-gram entropy) of a byte stream such as compressed or encrypted output, or natural-language text.'
---

# stats

Pass-through stream filters that gather frequency statistics on bytes flowing through them
without altering the data: `FilterByteBag` counts single-byte occurrences, `FilterDiGraphCounter`
counts consecutive byte pairs, and `FilterTriGraphCounter` counts consecutive byte triples. All
three are typically used to characterize the statistical structure (e.g. n-gram entropy) of a
byte stream such as compressed or encrypted output, or natural-language text.

## Classes

| Class | Responsibility |
|---|---|
| [FilterByteBag](FilterByteBag.java) | This Filter also implement a Bag ("MonoGraphCounter") that counts the Occurrences of single Bytes in a Stream. |
| [FilterDiGraphCounter](FilterDiGraphCounter.java) | This DiGraphCounter counts the Occurrences of Byte Pairs in a Stream. |
| [FilterTriGraphCounter](FilterTriGraphCounter.java) | This DiGraphCounter counts the Occurrences of Byte Triples in a Stream. |
