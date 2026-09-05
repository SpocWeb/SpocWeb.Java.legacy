---
digest:
  local-classes:
    AVector:
      mtime: '2026-09-05T12:44:29Z'
      digest: a1a96f492d44e5e982cc3b2a125fe57cbe710b95cf0326dc90ffdd8bcff693df
    AVectorStreamIn_Float:
      mtime: '2026-09-05T12:44:29Z'
      digest: b14314a9f9a48335e179bff02a82b673eef948a798c2df8f94ff853ab3e0bccf
    AVectorStreamIn_Int:
      mtime: '2026-09-05T12:44:29Z'
      digest: 40963f774241d628bd3bc7ff803575f984ad0a360a863843c6d855861dd7eae2
    HunterDouble:
      mtime: '2026-09-05T12:50:31Z'
      digest: c65db4c674eeb0aad5b3a94f9799b939ffb48b69e43471fe1cf26ce7a65e433f
    HunterFloat:
      mtime: '2026-09-05T12:49:12Z'
      digest: 7d52769deb81f0aed858aaa8e3338947d4e5e53728dd49b3c712fb7fb99a566b
    HunterInt:
      mtime: '2026-09-05T12:48:05Z'
      digest: 415b655878ed300266642513deb29d6f549d3ac2ab9d33be632ea23fa9b9f58c
    QuaternaryOp:
      mtime: '2026-09-05T12:43:24Z'
      digest: e17bd9d2447f1d6494ac58ef4dda8de77fcf827144462a1dbf3be46dc8bb2206
    VectorChar:
      mtime: '2026-09-05T13:12:10Z'
      digest: cfe644d701313d3b85823c7f096d5b9ef3aec418337c8a70a55af63d2c227a5c
    VectorCharStreamIn:
      mtime: '2026-09-05T13:12:10Z'
      digest: 6ce97631064df1ad0c3887dabb0e50d219c28125fcc58810e01c0c90fc444d99
    VectorDouble:
      mtime: '2026-09-05T16:01:01Z'
      digest: df8971a8441e0f41ab04f313b531254a0e3a935547b778146219e28762238b8e
    VectorDoubleStreamIn:
      mtime: '2026-09-05T16:01:01Z'
      digest: 9f182c959d6360d3467f896b7ce1b50c012bf6ac37cff2142326df32ff244790
    VectorFloat:
      mtime: '2026-09-05T13:49:26Z'
      digest: 17ee4d954b6a5cc8cc0ab5b97284d1c6f25145277e08f62cd4b7623962571610
    VectorFloatStreamIn:
      mtime: '2026-09-05T13:49:26Z'
      digest: 3bd91f1d650732c36a74487880786585df21ddb2b9cec3d08f658b25d6276e07
    VectorInt:
      mtime: '2026-09-05T13:35:49Z'
      digest: cb108c5ff451d39832c3b2bc694d99f9c28526080f83a8470c319be27313f573
    VectorIntStreamIn:
      mtime: '2026-09-05T13:35:49Z'
      digest: 9063223836affbf329bb9922a02b5e79c0bceed288c5038df1b0f5e35b2168b8
    VectorLong:
      mtime: '2026-09-05T13:19:50Z'
      digest: c265f18ea4c5a7a2396d8e1b740d3d00f587289128ef38c22545fc38ef3cb8b3
    VectorLongStreamIn:
      mtime: '2026-09-05T13:19:50Z'
      digest: 6ce97631064df1ad0c3887dabb0e50d219c28125fcc58810e01c0c90fc444d99
    VectorObject:
      mtime: '2026-09-05T12:45:43Z'
      digest: fe1736b2d6f2ce6fad4d629bb9dbf9ee1bbbd3396b92e5e49a91a4c798bda0e5
    VectorShort:
      mtime: '2026-09-05T13:25:39Z'
      digest: dbcc290a41569f91d584a14ca1fe3f04d1fbb2919bc03c9afe3818284f865830
    VectorShortStreamIn:
      mtime: '2026-09-05T13:25:39Z'
      digest: 6ce97631064df1ad0c3887dabb0e50d219c28125fcc58810e01c0c90fc444d99
    VectorString:
      mtime: '2026-09-05T12:55:55Z'
      digest: 1bff658a632c7ee9809d41d7fb845896dd96c84c39f66ce0d20ffce7dc676efd
  folders:
    statistic/:
      mtime: '2026-09-05T12:52:49Z'
      digest: 9c893ac4f28806b925c073db1f65eeb22b5d2174995fecc807a16f8fdce627c1
tags:
- code/growable_array
- code/quicksort
- code/order_statistic
concepts:
- Primitive-Typed Growable Vector Family
facets:
  layer: utility
  status: broken
  complexity: high
description: A family of growable, primitive-typed dynamic arrays (`VectorChar`/`Short`/`Int`/`Long`/`Float`/`Double`/`String`/`Object`), each pairing an instance-level container (capacity growth, bounds checking, item-count bookkeeping, largely inherited from `AVector`) with a large static library of array-level operations - arithmetic, min/max, linear combinations, sorting/order-statistics (`HunterInt`/`Float`/`Double`), and type-specific helpers (String parsing/padding/escaping, Object matrix-style multi-index access). `QuaternaryOp` factors out the shared add/subtract/multiply/divide/linear-combination operator used across the arithmetic methods. See `statistic/` for the correlation and hypothesis-testing layer built on top of these vectors.
---

# vector

A family of growable, primitive-typed dynamic arrays (`VectorChar`/`Short`/`Int`/`Long`/`Float`/`Double`/`String`/`Object`), each pairing an instance-level container (capacity growth, bounds checking, item-count bookkeeping, largely inherited from `AVector`) with a large static library of array-level operations - arithmetic, min/max, linear combinations, sorting/order-statistics (`HunterInt`/`Float`/`Double`), and type-specific helpers (String parsing/padding/escaping, Object matrix-style multi-index access). `QuaternaryOp` factors out the shared add/subtract/multiply/divide/linear-combination operator used across the arithmetic methods. See `statistic/` for the correlation and hypothesis-testing layer built on top of these vectors.

Several sibling classes share the same handful of copy-paste-origin bugs (see Bugs Found in the repo-root `HANDOFF.md`): `mulAt`/`divAt` overloads that delegate to `subAt` instead of the correct operation, scalar `mulAt`/`divAt` overloads that ignore their argument and operate on the vector itself, `removeAt` decrementing `itemCount` before validating the index, and `oneAt` filling with 0 instead of 1.

## Classes

| Class | Responsibility |
|---|---|
| [AVector](AVector.java) | Base class for the fixed-primitive-type vector family, providing shared capacity growth, bounds checking and<br/>item-count bookkeeping over a growable array. |
| [AVectorStreamIn_Float](AVector.java) | Iterates a floating-point vector's elements in reverse order, as a bounded stream source. |
| [AVectorStreamIn_Int](AVector.java) | Iterates an integer vector's elements in reverse order, as a bounded integer stream source. |
| [HunterDouble](HunterDouble.java) | Stateful binary-search "hunter" over a sorted double[], together with the static QuickSort, permutation,<br/>ranking and order-statistic (median/percentile) algorithms shared by the whole vector family for values<br/>requiring only an order relation. |
| [HunterFloat](HunterFloat.java) | Stateful binary-search "hunter" over a sorted float[], together with the static QuickSort, permutation,<br/>ranking and order-statistic (median/percentile) algorithms shared by the whole vector family for values<br/>requiring only an order relation. |
| [HunterInt](HunterInt.java) | Stateful binary-search "hunter" over a sorted int[], together with the static QuickSort, permutation, ranking<br/>and order-statistic (median/percentile) algorithms shared by the whole vector family for values requiring only<br/>an order relation. |
| [QuaternaryOp](QuaternaryOp.java) | Defines a binary-to-quaternary arithmetic operation (add, subtract, multiply, divide, or a linear combination)<br/>applied to up to four double operands. |
| [VectorChar](VectorChar.java) | Growable, index-addressable array of primitive char elements, plus a large library of static array-level<br/>operations (arithmetic, min/max, negation, linear combinations) shared by every method of this class and its<br/>instances alike. |
| [VectorCharStreamIn](VectorChar.java) | Iterator for the MatrixFloat Class (in reverse Order) |
| [VectorDouble](VectorDouble.java) | Provides static Methods and a dynamic Array Type for Vectors and Arrays of primitive double Numbers. |
| [VectorDoubleStreamIn](VectorDouble.java) | Iterates a VectorDouble backwards from its current Position down to Index 0. could also be substituted by any<br/>IndexStreamIn since the @see IIndexed |
| [VectorFloat](VectorFloat.java) | Provides static Methods and a dynamic Array Type for Vectors and Arrays of primitive float Numbers. |
| [VectorFloatStreamIn](VectorFloat.java) | Iterator for the VectorFloat Class (in reverse Order) |
| [VectorInt](VectorInt.java) | Provides static Methods and a dynamic Array Type for Vectors and Arrays of primitive int Numbers. |
| [VectorIntStreamIn](VectorInt.java) | Reverse-order Iterator over the Items of a VectorInt. |
| [VectorLong](VectorLong.java) | Growable, index-addressable array of primitive long elements, plus a large library of static array-level<br/>operations (arithmetic, min/max, negation, linear combinations) shared by every method of this class and its<br/>instances alike. |
| [VectorLongStreamIn](VectorLong.java) | Iterator for the MatrixFloat Class (in reverse Order) |
| [VectorObject](VectorObject.java) | Growable, index-addressable array of arbitrary Object elements, doubling as a flat backing store for 2D/3D<br/>rectangular multi-index access. |
| [VectorShort](VectorShort.java) | Growable, index-addressable array of primitive short elements, plus a large library of static array-level<br/>operations (arithmetic, min/max, negation, linear combinations, polynomial-style radix trimming and shifting)<br/>shared by every method of this class and its instances alike. |
| [VectorShortStreamIn](VectorShort.java) | Iterator for the MatrixFloat Class (in reverse Order) |
| [VectorString](VectorString.java) | Growable, index-addressable array of String elements, paired with a large static library of<br/>String/StringBuffer helpers: parsing, splitting, padding/aligning, trimming, escaping, case conversion, and<br/>array/matrix operations (column, transpose, rotate). |
