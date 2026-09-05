---
digest:
  local-classes:
    ScalarFlowConnection:
      mtime: '2026-09-05T10:23:44Z'
      digest: 2df7c06106969ce72eeb9b9bc07e56945b35d2cc9a8a9e6c4ca31c5bf7293a06
  folders:
    push/:
      mtime: '2026-09-05T10:24:25Z'
      digest: fd5b4aef935ecb8d9863243632fc66090f66743c5ff6dca5164535a684b1fcc9
tags:
- code/scalar_operation
concepts:
- Dataflow
facets:
  layer: domain
  status: stable
  complexity: low
description: 'This folder models continuous scalar flow between nodes as a simple 1st-order ODE system: `ScalarFlowConnection` links a source and a target `IFloat` value with an `IDoubleMetric` function that computes the flow rate between them from their current values only (not their rate of change), and moves that amount from source to target on each `run()`. A static registry collects every connection so `ScalarFlowConnection.update()` can advance the whole network in one sweep, each call performing one discrete-transport step rather than a continuous integration. The `push` subfolder is an unrelated, general-purpose push-based dataflow/pipeline framework (routing, fan-in/fan-out, join stages) that happens to share this package root.'
---

# flow

This folder models continuous scalar flow between nodes as a simple 1st-order ODE system:
`ScalarFlowConnection` links a source and a target `IFloat` value with an `IDoubleMetric`
function that computes the flow rate between them from their current values only (not their
rate of change), and moves that amount from source to target on each `run()`. A static registry
collects every connection so `ScalarFlowConnection.update()` can advance the whole network in
one sweep, each call performing one discrete-transport step rather than a continuous
integration. The `push` subfolder is an unrelated, general-purpose push-based dataflow/pipeline
framework (routing, fan-in/fan-out, join stages) that happens to share this package root.

## Entry Points

| Class.Method | Description |
|---|---|
| `ScalarFlowConnection.ScalarFlowConnection(IFloat, IFloat, IDoubleMetric)` | Creates and registers a new flow connection between a source and target node. |
| `ScalarFlowConnection.update()` | Advances every registered connection by one discrete flow step. |
| `ScalarFlowConnection.run()` | Performs one discrete transport step for this single connection. |

## Classes

| Class | Responsibility |
|---|---|
| [ScalarFlowConnection](ScalarFlowConnection.java) | Title: ScalarFlowConnection Description: Purpose: Represents a Flow between two scalar Nodes. |
