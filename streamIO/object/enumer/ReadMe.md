---
digest:
  local-classes:
    AChangeStreamIn:
      mtime: '2026-09-05T21:33:29Z'
      digest: f62b9c32e9e9c63f0a0b81e1a089b78f7e93db8984323bcc75db1937d0911c24
    AEnumerator:
      mtime: '2026-09-05T21:53:43Z'
      digest: 10450af99c2fec1d6c45a9be67f492c2e149d3ba4915cb860708986a8e820468
    AIndexEnumerator:
      mtime: '2026-09-05T21:36:33Z'
      digest: 0a62fdc5296cea1d83ca9c0078c9897b2cce17c7c2829d74236f308be77086ca
    APipe:
      mtime: '2026-09-05T21:30:43Z'
      digest: d3ebd301370cc743df274956f1ea84b0f76a5830601286f30a74cd361101e801
    AReverseEnumerator:
      mtime: '2026-09-05T21:33:01Z'
      digest: af51682e89e55b21855a344655050c59d03b6a3989f253b8cc4121921e6367aa
    ArrayEnum:
      mtime: '2026-09-05T21:38:36Z'
      digest: 3859ca9aa0359b09300aa2a0688d153a65c38f7a614a003e5f3647be44df89d1
    ArrayEnumDbl:
      mtime: '2026-09-05T21:53:24Z'
      digest: 24b4d8a59662056707bdbdb63cc8453a2be6d6529fcc50ca515fe49b2c7c705e
    ArrayEnumPrim:
      mtime: '2026-09-05T21:34:49Z'
      digest: fb4d6fe6b39e60e01ad08fdee64116a05d91284adb7eb03b76e6a88dbe007f0e
    ArrayEnumPrimitive:
      mtime: '2026-09-05T21:34:50Z'
      digest: fb4d6fe6b39e60e01ad08fdee64116a05d91284adb7eb03b76e6a88dbe007f0e
    CachePipe:
      mtime: '2026-09-05T21:53:26Z'
      digest: 9fa3deabb7baeff9c93f6d2129fefe83bb7c0533713499a401f712b7071339cd
    ChangeIterator:
      mtime: '2026-09-05T10:13:31Z'
      digest: bb7445fb24284adb59223d5ff25979fc82669b82762d401626a6c10257deccc2
    DblListItem:
      mtime: '2026-09-05T21:34:53Z'
      digest: ae55386183a37611d7449d9821730d4bcfcd5e7254f2c6f598594637c670ad37
    Enumerator:
      mtime: '2026-09-05T10:13:31Z'
      digest: 766e59904437217b74c7cb892c956185d1a158722bdcff94c95353395fee1faa
    Enumerator2Enumeration:
      mtime: '2026-09-05T21:31:26Z'
      digest: d3f104d6a4e715679147baa131c48162292d84bd92513f74bc17bbc2c0547874
    Enumerator2Iterator:
      mtime: '2026-09-05T21:53:27Z'
      digest: d3b0efa6c4007ace33e693100aaa2d2a972e1286b6b9d2baa46737dfcc8b8cf5
    FilterEnumerator:
      mtime: '2026-09-05T21:33:34Z'
      digest: bded2fef9c2b70d3e33bbd6f8763dbbfa8a1dc929d11aa1701bc8791b69c1210
    IAlterAble:
      mtime: '2026-09-05T10:13:32Z'
      digest: 861cb79eb40fd79f404506398d263b0cb9373629e0ddfa54a6c1a29b1b87e9a0
    IChangeAble:
      mtime: '2026-09-05T10:13:32Z'
      digest: f32eb16f1bcdb69111c6dd4b02020b7a1036906c5519bd8e7759f80836c62703
    IVersioned:
      mtime: '2026-09-05T10:13:32Z'
      digest: bf18bd883dbe82d85a574cd7c634964e3d603916774b2a1622b47dbde14f101d
    IndexEnumerator:
      mtime: '2026-09-05T10:13:32Z'
      digest: 3f324ca550a83d63b17d89214881fb042ca16b4eaf49b3c40f904da182af9d91
    Iterator2Enumerator:
      mtime: '2026-09-05T21:55:05Z'
      digest: d630c06ebe5cd022b57fee8039437f14aa45aa5ca15b5d24c0ec385c7b277b21
    ListItem:
      mtime: '2026-09-05T21:38:01Z'
      digest: e8427491b44314beeaeb87e495150776b6165473caedfa336276b7553809448d
    PipeSplitter:
      mtime: '2026-09-05T21:33:40Z'
      digest: 356b00589bf89284b585100cb114fc6ddd117f9c5f5b2a7c08ca310071e31e9c
    ReverseEnumerator:
      mtime: '2026-09-05T21:33:10Z'
      digest: d68bc0ebb8f0c9bb7b2c5f8647bbe121696d367977c045c9d1505b0924c2c3a0
  folders:
    container/:
      mtime: '2026-09-05T21:55:08Z'
      digest: 3022d91be3a158285e57fa8091b71ee4d6f2db767c46d84eda0c07b060f40199
tags:
- code/enumerator
- code/iterator_adapter
concepts:
- Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections
facets:
  layer: utility
  status: legacy
  complexity: high
description: 'Enumerator/Iterator layer for the `streamIO.object` package: the `Enumerator` interface plus its abstract base (`AEnumerator`) and reverse-iteration counterpart (`AReverseEnumerator`), concrete implementations over arrays (`ArrayEnum`, `ArrayEnumDbl`, `ArrayEnumPrim`), linked structures (`ListItem`, `DblListItem`), and asynchronous pipes (`APipe`, `CachePipe`, `PipeSplitter`). Also provides bridges to and from `java.util.Iterator` (`Iterator2Enumerator`, `Enumerator2Iterator`, `Enumerator2Enumeration`) so this codebase''s custom streaming abstractions can interoperate with standard Java collections. `container/` (documented separately) supplies the concrete storage structures - HashContainer, TreeMap, Array, Relation, etc. - that these Enumerators iterate over.'
---

# enumer

Enumerator/Iterator layer for the `streamIO.object` package: the `Enumerator` interface plus its
abstract base (`AEnumerator`) and reverse-iteration counterpart (`AReverseEnumerator`), concrete
implementations over arrays (`ArrayEnum`, `ArrayEnumDbl`, `ArrayEnumPrim`), linked structures
(`ListItem`, `DblListItem`), and asynchronous pipes (`APipe`, `CachePipe`, `PipeSplitter`). Also
provides bridges to and from `java.util.Iterator` (`Iterator2Enumerator`, `Enumerator2Iterator`,
`Enumerator2Enumeration`) so this codebase's custom streaming abstractions can interoperate with
standard Java collections. `container/` (documented separately) supplies the concrete storage
structures - HashContainer, TreeMap, Array, Relation, etc. - that these Enumerators iterate over.

## Classes

| Class | Responsibility |
|---|---|
| [AChangeStreamIn](AChangeStreamIn.java) | Title: AModStreamIn.java Description: TODO: Describes the Purpose / Responsibilities of this Class, not it's<br/>Implementation. |
| [AEnumerator](AEnumerator.java) | Abstract Enumerator Class Design Decisions: If Enumerators are inner Classes they implicitly know their outer<br/>Object, which saves handing over 'this'. |
| [AIndexEnumerator](AIndexEnumerator.java) | Title: AIndexEnumerator Description: Implements some Methods that are defined inherently Base Class for<br/>several Array and ResultSet Enumerators. |
| [APipe](APipe.java) | This Class models a Pipe which allows more flexible, asynchronous Communication between two Threads than<br/>'streamIO.Monitor': To facilitate continuous Operation on both Sides without Memory Overload, this Class also<br/>has a MaxCapacity. |
| [AReverseEnumerator](AReverseEnumerator.java) | Implements the possible abstract reverse Enumerator Operations |
| [ArrayEnum](ArrayEnum.java) | Title: ArrayEnum Description: Enumerator Class for any Type of Array except for primitive Types |
| [ArrayEnumDbl](ArrayEnumDbl.java) | Title: ArrayEnumDbl Description: Enumerator Class for Arrays of Type double |
| [ArrayEnumPrim](ArrayEnumPrim.java) | Title: PrimArrayEnumerator Description: Enumerator Class for any Type of Array, also primitive Types<br/>Copyright: Copyright (c) Matthias Heuer Company: personal |
| [ArrayEnumPrimitive](ArrayEnumPrimitive.java) | Title: PrimArrayEnumerator Description: Enumerator Class for any Type of Array, also primitive Types<br/>Copyright: Copyright (c) Matthias Heuer Company: personal |
| [CachePipe](CachePipe.java) | This Class models an asynchronous Pipe which allows more flexible, asynchronous Communication between two<br/>Threads than 'streamIO.Monitor': To facilitate continuous Operation on both Sides without Memory Overload,<br/>this Class also has a MaxCapacity. |
| [ChangeIterator](ChangeIterator.java) | Title: ModStreamIn.java Description: Defines the Interface for a streamIO that can be modified in it's<br/>Contents, but not structurally. |
| [DblListItem](DblListItem.java) | This is the Class of the List Items for single linked Lists. |
| [Enumerator](Enumerator.java) | Interface for an Enumerator through a Collection or on a streamIO. |
| [Enumerator2Enumeration](Enumerator2Enumeration.java) | Title: Iterator2Enumeration.java Description: Bridge Class (Filter) from StreamIn to Enumeration The Opposite<br/>Direction is implemented in Enumeration2StreamIn. |
| [Enumerator2Iterator](Enumerator2Iterator.java) | Title: StreamIn2Enumeration.java Description: Bridge Class (Filter) from StreamIn to Enumeration The Opposite<br/>Direction is implemented in Enumeration2StreamIn Known SubClasses: Copyright: Copyright (c) Matthias Heuer<br/>Company: personal Created on 06-03-2001, 12:40 AM |
| [FilterEnumerator](FilterEnumerator.java) | Prototype for a Filter working on an Input-streamIO of Objects Overwrites ALL Methods with Passes to the<br/>Parent Enumerator. |
| [IAlterAble](IAlterAble.java) | Title: AlterAble.java Description: This Interface is implemented by Containers supporting the addItem() and<br/>removeItem() Method and / or an Enumerator Iterator. |
| [IChangeAble](IChangeAble.java) | ChangeAble.java This Interface is implemented by Containers supporting the replaceItem() Method and / or a<br/>ModStreamIn Iterator to indicate a Change or Addition in the contained Elements. |
| [IVersioned](IVersioned.java) | Interface for versioned Objects or Classes. |
| [IndexEnumerator](IndexEnumerator.java) | Abstract Class for an indexed Access. |
| [Iterator2Enumerator](Iterator2Enumerator.java) | Title: Enumeration2StreamIn.java Description: Bridge Class (Filter) from Enumeration to StreamIn The Opposite<br/>Direction is implemented in Iterator2Enumeration. |
| [ListItem](ListItem.java) | This is the Class of the List Items for singly linked Lists and upward navigable Trees (Hierarchies), although<br/>you can also build Cycles in the Object Graph! It should never be visble to the User directly, because it is<br/>always hidden by the List Object. |
| [PipeSplitter](PipeSplitter.java) | Title: PipeSplitter Description: Purpose: Splits a Pipe up into a StreamIn and a StreamOut Interface / joins<br/>both into a Pipe The Reverse is not necessary, because a Pipe can be directly used with both Interfaces. |
| [ReverseEnumerator](ReverseEnumerator.java) | Interface for an iterator through a Collection Design Decisions: The Enumerator Object is singled out from the<br/>Container Classes, because it is very probable, that you have several Enumerators running over the same Object<br/>at the same time! Therefore the Interface is renamed from 'iterAble' to 'Enumerator'. |

## Subsystems

| Folder | Domain Role | Entry Point |
|---|---|---|
| `container/` | Concrete storage containers (Array, HashContainer, TreeMap, Relation) and their Enumerators | `AContainer` |
