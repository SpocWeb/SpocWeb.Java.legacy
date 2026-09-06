---
digest:
  local-classes:
    SubTreeMap:
      mtime: '2026-09-06T10:35:54Z'
      digest: ff1ec4b00791a6fd86d96935d3d60ec941f7d171066fca8d90bc87509b285329
    SubTreeMapEntryIterator:
      mtime: '2026-09-06T10:35:54Z'
      digest: 86960c5e7d2734565b1e7e02b77230a9b998c1baadc59cfaff3b4fc885ccf469
    TreeEntryIterator:
      mtime: '2026-09-06T10:35:54Z'
      digest: 72f97cbf280b793065fc7ac0ea2d998ad7aa4f92acf6a3b1559cab2eddf89e2b
    TreeEntrySet:
      mtime: '2026-09-06T10:35:54Z'
      digest: e6b9cc4f5ef071ee0d9a834f39b94043d6b4ca8d6c463cf63077fe16681672eb
    TreeEntrySetView:
      mtime: '2026-09-06T10:35:54Z'
      digest: 26de5f9b913d3cfbb7023c2b0fa85e0878f154e3ac437e120004c2628744e080
    TreeKeyIterator:
      mtime: '2026-09-06T10:35:54Z'
      digest: f3703e9a56090fe040ee08946c8f7a440f87f77a2a1099946426bf900c2aba0e
    TreeKeySet:
      mtime: '2026-09-06T10:35:54Z'
      digest: b2f89fb13b37a522bfb92feffa4b771eb171f5a7dc4b00bc42f340117da116f5
    TreeMap:
      mtime: '2026-09-06T10:35:54Z'
      digest: 2107bc6602323144c2a2562c451301184029ed0fb382f0180dc049ecbdeae311
    TreeMapEntry:
      mtime: '2026-09-06T10:35:54Z'
      digest: 974c5453e2ccf1df6fc3640a7d47bff57cb7dcf27adfefedef19185a7652759c
    TreeValueCollection:
      mtime: '2026-09-06T10:35:54Z'
      digest: cde638e072cb24abd4a6c0dd16b9a45e6a1bd9b2dc97f324004d34e1e3a6e78a
    TreeValueIterator:
      mtime: '2026-09-06T10:35:54Z'
      digest: 5a002f0121b1b73947b42df72dab64993a9731590067774c18a4d52a86659039
  folders: {}
tags:
- code/red_black_tree
- code/iterator_pattern
concepts:
- Red-Black Tree Backed Sorted Map Implementation
facets:
  layer: utility
  status: legacy
  complexity: high
description: Red-Black tree implementation of `SortedMap` (`TreeMap`, entries as `TreeMapEntry`) plus the Set and Collection views `java.util.Map` expects (`TreeKeySet`, `TreeValueCollection`, `TreeEntrySet` and their Iterators). `SubTreeMap` and `TreeEntrySetView`/`SubTreeMapEntryIterator` implement the key-range views returned by `headMap()`/`tailMap()`/`subMap()`, backed by the same underlying `TreeMap` so mutations on either side are reflected in the other. `TreeMapEntry` also extends `container.IndexAssociation`, the lightweight Entry base shared with the hash-table implementation in the parent `container/` package.
---

# tree

Red-Black tree implementation of `SortedMap` (`TreeMap`, entries as `TreeMapEntry`) plus the Set
and Collection views `java.util.Map` expects (`TreeKeySet`, `TreeValueCollection`, `TreeEntrySet`
and their Iterators). `SubTreeMap` and `TreeEntrySetView`/`SubTreeMapEntryIterator` implement the
key-range views returned by `headMap()`/`tailMap()`/`subMap()`, backed by the same underlying
`TreeMap` so mutations on either side are reflected in the other. `TreeMapEntry` also extends
`container.IndexAssociation`, the lightweight Entry base shared with the hash-table implementation
in the parent `container/` package.

## Classes

| Class | Responsibility |
|---|---|
| [SubTreeMap](SubTreeMap.java) | Title: Description: Purpose: This Class represents a sorted Sub-Map, i.e. Subset of a sorted Map including the<br/>'left' Value, but excluding the 'right' Value. |
| [SubTreeMapEntryIterator](SubTreeMapEntryIterator.java) | Iterates over the entries of a TreeMap bounded above by an excluded key, as used by SubTreeMap to expose a<br/>key-range view. |
| [TreeEntryIterator](TreeEntryIterator.java) | TreeMap Iterator. |
| [TreeEntrySet](TreeEntrySet.java) | Set implementation based on a sorted Tree. |
| [TreeEntrySetView](TreeEntrySetView.java) | An java.util.Set view over the entries of a SubTreeMap's key range, backed by the underlying TreeMap so<br/>mutations on either side are reflected in the other. |
| [TreeKeyIterator](TreeKeyIterator.java) | Iterates over the keys of a TreeMap, in ascending key order. |
| [TreeKeySet](TreeKeySet.java) | A java.util.Set view over the keys of a TreeMap, backed by it so mutations on either side are reflected in the other. |
| [TreeMap](TreeMap.java) | Red-Black tree based implementation of the SortedMap interface. |
| [TreeMapEntry](TreeMapEntry.java) | Universal, lightweight, final and flexible Implementation for Top Performance usable for HashMaps, Trees,<br/>linked Lists, Graphs etc. No Overhead like IOrderAble etc. |
| [TreeValueCollection](TreeValueCollection.java) | A java.util.Collection view over the values of a TreeMap, backed by it so mutations on either side are<br/>reflected in the other. |
| [TreeValueIterator](TreeValueIterator.java) | Iterates over the values of a TreeMap, in ascending key order. |
