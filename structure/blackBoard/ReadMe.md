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
  complexity: 2
description: 'Defines the generic Blackboard-pattern Contract (`IKnowledge`: `check()`/`update()`) that any number of independent, rule-based Knowledge Sources can implement to cooperatively fill in an incomplete shared Data Structure. The `triangle` Subsystem is the concrete Application, solving Triangle Geometry.'
dv_has_:
  sub_:
    folders: 1
    files: 17
    units: 9
    facet_:
      layer_:
        domain: 9
      status_:
        legacy: 9
      complexity_:
        "2": 8
        "3": 1
    tag_:
      code_:
        rule_based_validation: 7
        blackboard_pattern: 3
        "2d_geometry": 2
    concept_:
      angle_angle_angle_rule: 1
      angle_side_angle_rule: 1
      knowledge_source_interface: 1
      rule_based_triangle_solver: 1
      side_angle_side_rule: 1
      side_side_angle_rule: 1
      side_side_side_rule: 1
      triangle_knowledge_source_base: 1
      triangle_value_object: 1
has_sub_folders: 1
has_sub_files: 17
has_sub_units: 9
has_sub_facet_layer_domain: 9
has_sub_facet_status_legacy: 9
has_sub_facet_complexity_2: 8
has_sub_facet_complexity_3: 1
has_sub_tag_code_rule_based_validation: 7
has_sub_tag_code_blackboard_pattern: 3
has_sub_tag_code_2d_geometry: 2
has_sub_concept_angle_angle_angle_rule: 1
has_sub_concept_angle_side_angle_rule: 1
has_sub_concept_knowledge_source_interface: 1
has_sub_concept_rule_based_triangle_solver: 1
has_sub_concept_side_angle_side_rule: 1
has_sub_concept_side_side_angle_rule: 1
has_sub_concept_side_side_side_rule: 1
has_sub_concept_triangle_knowledge_source_base: 1
has_sub_concept_triangle_value_object: 1
related:
  - path: ../_Matthias/Code/NET/_org.structs/TriangleBlackboard
    shared-tags: [code/blackboard_pattern]
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
