---
digest:
  local-classes:
    IKnowledge:
      mtime: '2026-09-05T11:22:29Z'
      digest: bd8ced382336dd2dde59895be8c9e275b103740fa115a8ceb93b007d2c560e60
  folders:
    triangle/:
      mtime: '2026-09-05T11:23:47Z'
      digest: 1f57ada579fabd50203d3b371945ffcfa2262117f6cc44ee706065f8dbf330ee
tags:
- code/blackboard_pattern
concepts:
- Blackboard Architecture
facets:
  layer: domain
  status: legacy
  complexity: low
description: 'Defines the generic Blackboard-pattern Contract (`IKnowledge`: `check()`/`update()`) that any number of independent, rule-based Knowledge Sources can implement to cooperatively fill in an incomplete shared Data Structure. The `triangle` Subsystem is the concrete Application, solving Triangle Geometry.'
---

# blackBoard

Defines the generic Blackboard-pattern Contract (`IKnowledge`: `check()`/`update()`) that any
number of independent, rule-based Knowledge Sources can implement to cooperatively fill in an
incomplete shared Data Structure. The `triangle` Subsystem is the concrete Application, solving
Triangle Geometry.

## Classes

| Class | Responsibility |
|---|---|
| [IKnowledge](IKnowledge.java) | Declares the Blackboard-pattern Contract every Knowledge Source implements: whether it can currently<br/>contribute (#check()) and how it applies that contribution (#update()). |

## Subsystems

| Folder | Domain Role | Entry Point |
|---|---|---|
| `triangle/` | Implements a Blackboard-pattern solver for `Triangle` geometry: `Triangle` holds up to six | `ATriangleKnowledge` |
