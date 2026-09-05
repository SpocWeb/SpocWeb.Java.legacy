---
digest:
  local-classes:
    AIndexer:
      mtime: '2026-09-05T16:41:04Z'
      digest: 0c441140a5630e8953cc549451e9b2f5ad29863bc5aaa89f1bafae6684e93bf9
    IDirectAccess:
      mtime: '2026-09-05T10:13:18Z'
      digest: 3552dba47de0dde794e9322634f6149077361c94aa9c208a9f965b56c3766a49
    IDirectRead:
      mtime: '2026-09-05T10:13:18Z'
      digest: 2a444a35de2f179068c3b71382f09c0561160454c5c4766335b5bb5986b277f8
    IIndex:
      mtime: '2026-09-05T16:41:09Z'
      digest: 05dcf91b42651492158fa027ecab49c1e7302b950a6978a4fa76e3f21da4515c
    IIndexAble:
      mtime: '2026-09-05T10:13:18Z'
      digest: 51f4b68f3a6b50b8072b75def9482f2f5940fda732e5ee9b24d332bc80668ae1
    IIndexer:
      mtime: '2026-09-05T16:41:14Z'
      digest: e5482723b6c75030aff5401b3885900ed4a968a94d7bf2c310f3c278e02c1690
    IndexComparator:
      mtime: '2026-09-05T16:41:20Z'
      digest: dc4c8c47c6824b4e517e5defc113c304a0222519fa16c80c50270a1e8eda30e6
    IndexEntry:
      mtime: '2026-09-05T16:41:40Z'
      digest: 67d73bb3aed59ac84546d912deba46de725f87c9887f2f99467afa783225a34b
    Indexer:
      mtime: '2026-09-05T16:42:07Z'
      digest: c92ab749c929f17cc380356f2d058929556461eb36e1ab1258bfda8675c8cd47
  folders: {}
tags:
- code/indexing
concepts:
- Indexing Abstractions
facets:
  layer: utility
  status: legacy
  complexity: medium
description: 'Indexing abstractions for random-access collections: `IDirectAccess`/`IDirectRead` mark a collection as directly addressable by integer index, `IIndex`/`IIndexer`/`AIndexer` build and query a sortable index over such a collection (with a comparator via `IndexComparator`), and `IndexEntry` pairs an index position with its underlying value.'
---

# index

Indexing abstractions for random-access collections: `IDirectAccess`/`IDirectRead` mark a collection as directly addressable by integer index, `IIndex`/`IIndexer`/`AIndexer` build and query a sortable index over such a collection (with a comparator via `IndexComparator`), and `IndexEntry` pairs an index position with its underlying value.

## Classes

| Class | Responsibility |
|---|---|
| [AIndexer](AIndexer.java) | Abstract IIndexer that adds a bulk #update(ResultSet, int[]) helper for indexing a JDBC ResultSet by one or<br/>more columns. |
| [IDirectAccess](IDirectAccess.java) | Defines the Interface for random Read/Write Access to a Container. |
| [IDirectRead](IDirectRead.java) | Defines the Interface for random Read Access to a Container. |
| [IIndex](IIndex.java) | Title: Description: Purpose: Purpose / Responsibilities of this Class Design Decisions / Implementation<br/>Details: If similar Classes exist (e.g. Polymorphism), characterize the specific Differences to compare these. |
| [IIndexAble](IIndexAble.java) | This Interface specifies that the Index of this Object in a Storage can be stored within the Object to be able<br/>to directly access it. |
| [IIndexer](IIndexer.java) | Title: Description: Purpose: Purpose / Responsibilities of this Class Design Decisions / Implementation<br/>Details: If similar Classes exist (e.g. Polymorphism), characterize the specific Differences to compare these. |
| [IndexComparator](IndexComparator.java) | Allows to use a Comparator with IndexElement Objects. |
| [IndexEntry](IndexEntry.java) | Stores an Object Reference together with an Index. |
| [Indexer](Indexer.java) | Implements an Index, which efficiently maps Objects or Strings to Numbers and can return the Values or their<br/>Index in sorted Order as Arrays in O(N). |
