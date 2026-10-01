---
digest:
  local-classes:
    Memento:
      mtime: '2026-09-04T16:35:47Z'
      digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    Originator:
      mtime: '2026-09-04T16:35:47Z'
      digest: c02e6618cf7aea70cfe5b8ccaa564438d0dfa633b2e08d3540be19c46ec8ea77
  folders: {}
tags:
- code/state_snapshot
- code/marker_interface
concepts:
- Memento Pattern
facets:
  layer: infrastructure
  status: stable
  complexity: 2
description: 'A two-interface expression of the Memento pattern, kept deliberately minimal: an object that can snapshot and restore its own state, and an opaque token standing for one such snapshot.'
dv_has_:
  sub_:
    folders: 0
    files: 5
    units: 2
    facet_:
      layer_:
        infrastructure: 2
      status_:
        stable: 2
      complexity_:
        "2": 2
    tag_:
      code_:
        state_snapshot: 2
        marker_interface: 1
        interface_contract: 1
    concept_:
      memento_pattern: 2
has_sub_folders: 0
has_sub_files: 5
has_sub_units: 2
has_sub_facet_layer_infrastructure: 2
has_sub_facet_status_stable: 2
has_sub_facet_complexity_2: 2
has_sub_tag_code_state_snapshot: 2
has_sub_tag_code_marker_interface: 1
has_sub_tag_code_interface_contract: 1
has_sub_concept_memento_pattern: 2
related:
  - path: ../_Matthias/Code/NET/org.structs/iMathExpression
    shared-tags: [code/marker_interface]
  - path: ../_Matthias/Code/NET/_SpocWeb.Root/_std/SpocWeb.Basics/Interfaces/categories
    shared-tags: [code/marker_interface]
---

# mementos

A two-interface expression of the Memento pattern, kept deliberately minimal:
an object that can snapshot and restore its own state, and an opaque token standing for
one such snapshot.

The point of the pair is access control rather than data transfer.
`Memento` declares no members at all, so a caretaker holding one can pass it around and
hand it back but cannot read or forge its contents; only the originator that produced it
knows the concrete type and can unpack it.
Java's one-public-type-per-file rule is why the two live in separate files despite being a
single concept — a constraint the source comments call out explicitly.

Nothing in `tools/` implements these interfaces yet; they exist as the reusable contract
for classes elsewhere that need rollback without exposing their internals.

## Classes

| Class | Responsibility |
|---|---|
| [Memento](Memento.java) | Marker Interface identifying an opaque Snapshot of an Originator's internal State. |
| [Originator](Originator.java) | Defines the Interface for capturing and restoring an Object's own State via a Memento. |

## Architecture

```mermaid
flowchart TD
  subgraph mementos
    Originator["Originator - snapshots own state"]
    Memento["Memento - opaque token"]
    Caretaker["Caretaker (external) - stores tokens"]

    Originator -->|"getState() creates"| Memento
    linkStyle 0 opacity:1
    Memento -->|"setState() consumes"| Originator
    linkStyle 1 opacity:1
    Caretaker -.->|"holds, cannot read"| Memento
    linkStyle 2 opacity:1
  end
```

## Entry Points

| Class.Method | Description |
|---|---|
| [Originator.getState()](Originator.java#L38) | Captures the current internal state into a fresh Memento. |
| [Originator.setState(Memento)](Originator.java#L45) | Restores the state captured in a Memento this same Originator produced. |
