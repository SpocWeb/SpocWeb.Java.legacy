---
digest:
  local-classes:
    SubTreeMap:
      mtime: '2026-09-05T21:52:51Z'
      digest: 68f30542c3af75aaa58f38a538247b3178ea4964a17403bf67dc37bc0c60c212
    SubTreeMapEntryIterator:
      mtime: '2026-09-05T21:52:14Z'
      digest: 70c37df321b95e7dbcdd8646be5660837f09972c5cd136192a6d463e521f8b3a
    TreeEntryIterator:
      mtime: '2026-09-05T21:52:11Z'
      digest: c20d7a621311a4d4fdcdab471fe5bfd3cbf78e6624f1d4143652ad3a8ac437e2
    TreeEntrySet:
      mtime: '2026-09-05T21:48:21Z'
      digest: 047b1824b070c841294e9842f5dc8ef49b20b2b4cd05e4378e3f66d4ad5f230e
    TreeEntrySetView:
      mtime: '2026-09-05T21:52:08Z'
      digest: b141718a2ebc86e7e78128ffbd703f3fbc2b2ad5d89543c600d970eebedfe7a1
    TreeKeyIterator:
      mtime: '2026-09-05T21:52:15Z'
      digest: f3703e9a56090fe040ee08946c8f7a440f87f77a2a1099946426bf900c2aba0e
    TreeKeySet:
      mtime: '2026-09-05T21:47:59Z'
      digest: 9ef1f058bea615692d7b1e72e90fad28a17b7022bedde103597e4263598e9009
    TreeMap:
      mtime: '2026-09-05T21:43:50Z'
      digest: 19b46efacd2fe7498d15efc7e30e8cfbae1fa96a627b9ac2ec87128303fba810
    TreeMapEntry:
      mtime: '2026-09-05T21:52:34Z'
      digest: 93db5e92ca8fbdbd207549a5bbae3e4d11e2961987dee52a51151abf25b14dd6
    TreeValueCollection:
      mtime: '2026-09-05T21:48:09Z'
      digest: a5f61aa57c4eba075082259c574cc36d49d73633f082858f95427a06d5955c6e
    TreeValueIterator:
      mtime: '2026-09-05T21:52:17Z'
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
