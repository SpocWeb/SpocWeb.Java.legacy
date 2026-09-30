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
  complexity: 2
description: 'This folder models continuous scalar flow between nodes as a simple 1st-order ODE system: `ScalarFlowConnection` links a source and a target `IFloat` value with an `IDoubleMetric` function that computes the flow rate between them from their current values only (not their rate of change), and moves that amount from source to target on each `run()`. A static registry collects every connection so `ScalarFlowConnection.update()` can advance the whole network in one sweep, each call performing one discrete-transport step rather than a continuous integration. The `push` subfolder is an unrelated, general-purpose push-based dataflow/pipeline framework (routing, fan-in/fan-out, join stages) that happens to share this package root.'
dv_has_:
  sub_:
    folders: 1
    files: 33
    units: 15
    facet_:
      layer_:
        domain: 15
      status_:
        stable: 13
        broken: 1
        legacy: 1
      complexity_:
        "2": 12
        "3": 3
    tag_:
      code_:
        producer_consumer: 8
        adapter_pattern: 5
        multicast: 2
        null_object: 1
        scalar_operation: 1
    concept_:
      pipeline: 14
      dataflow: 15
has_sub_folders: 1
has_sub_files: 33
has_sub_units: 15
has_sub_facet_layer_domain: 15
has_sub_facet_status_stable: 13
has_sub_facet_status_broken: 1
has_sub_facet_status_legacy: 1
has_sub_facet_complexity_2: 12
has_sub_facet_complexity_3: 3
has_sub_tag_code_producer_consumer: 8
has_sub_tag_code_adapter_pattern: 5
has_sub_tag_code_multicast: 2
has_sub_tag_code_null_object: 1
has_sub_tag_code_scalar_operation: 1
has_sub_concept_pipeline: 14
has_sub_concept_dataflow: 15
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
