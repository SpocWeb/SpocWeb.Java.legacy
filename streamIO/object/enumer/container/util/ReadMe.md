---
digest:
  local-classes:
    Collection2Container:
      mtime: '2026-09-06T10:35:54Z'
      digest: 9a17327381edb63edb09dec182bf90297bc334db4408a4bef8025ffaf38b2099
    Container2ResultSet:
      mtime: '2026-09-06T10:35:54Z'
      digest: 28de3d97f2e27fba4c147bde4e6688aa8cce0ea2402082491d68d631c53cb9dc
    TestRunAble:
      mtime: '2026-09-06T10:35:54Z'
      digest: 3b38cdbe49f9fc0b7ccf1f4730bb148f2d12a65c11cf0120d8a307364776f3d9
    TimedEvent:
      mtime: '2026-09-06T10:35:54Z'
      digest: 47a7188673e5fc4f86b649e28ef70734dd6252848c50e257a20185ace76cdbc4
    TimedQueue:
      mtime: '2026-09-06T10:35:54Z'
      digest: ba188692aa57117f4b43e5b755b7407552a604af84fd0896f97d703a0ebb81f5
  folders: {}
tags:
- code/adapter
- code/scheduling
concepts:
- Small Adapter and Scheduling Helper Classes
facets:
  layer: utility
  status: legacy
  complexity: 4
description: 'Small adapter and scheduling helpers supporting the `container/` package: `Collection2Container` adapts any `java.util.Collection` to this codebase''s `Container` interface, `Container2ResultSet` does the reverse for JDBC (exposing an `Enumerator` as a `ResultSet`), `TestRunAble` is a minimal `Runnable` used for ad hoc concurrency tests, and `TimedEvent`/`TimedQueue` implement a queue of Runnables scheduled to fire at given timestamps.'
dv_has_:
  sub_:
    folders: 0
    files: 12
    units: 5
    facet_:
      layer_:
        utility: 5
      status_:
        legacy: 5
      complexity_:
        "4": 5
    tag_:
      code_:
        scheduling: 5
        adapter: 5
    concept_:
      small_adapter_and_scheduling_helper_classes: 5
has_sub_folders: 0
has_sub_files: 12
has_sub_units: 5
has_sub_facet_layer_utility: 5
has_sub_facet_status_legacy: 5
has_sub_facet_complexity_4: 5
has_sub_tag_code_scheduling: 5
has_sub_tag_code_adapter: 5
has_sub_concept_small_adapter_and_scheduling_helper_classes: 5
related:
  - path: ../_Matthias/Code/NET/_org.structs/maths/quantities/functions
    shared-tags: [code/adapter]
  - path: ../_Matthias/Code/NET/_std/IGraphs/DatesTimes
    shared-tags: [code/scheduling]
---

# util

Small adapter and scheduling helpers supporting the `container/` package: `Collection2Container`
adapts any `java.util.Collection` to this codebase's `Container` interface, `Container2ResultSet`
does the reverse for JDBC (exposing an `Enumerator` as a `ResultSet`), `TestRunAble` is a minimal
`Runnable` used for ad hoc concurrency tests, and `TimedEvent`/`TimedQueue` implement a queue of
Runnables scheduled to fire at given timestamps.

## Classes

| Class | Responsibility |
|---|---|
| [Collection2Container](Collection2Container.java) | Title: Collection2Container Description: Adapter Class that transforms Implementors of the Interface<br/>Collection into the Interface Container. |
| [Container2ResultSet](Container2ResultSet.java) | Bridge Class implementing the ResultSet Interface using a Container as backing This is also the Prototype for<br/>writing custom JDBC ResultSet Classes that are synchronized with Data Files in plain ASCII. |
| [TestRunAble](TestRunAble.java) | Small Test Class that prints it's Name, Number of Runs, the Time and exits |
| [TimedEvent](TimedQueue.java) | Value Object for an Event to be executed at the given TimeStamp |
| [TimedQueue](TimedQueue.java) | Implements a timed Queue, in which runnable Objects can be inserted and are executed at the given point of Time. |
