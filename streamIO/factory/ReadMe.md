---
digest:
  local-classes:
    FactoryByClass:
      mtime: '2026-09-05T09:06:15Z'
      digest: 0e3e8c5748ccde039fcbefef77c261c8fa507b364a7cdf104c9dd28c2b099b80
    FactoryByPrototype:
      mtime: '2026-09-05T09:06:26Z'
      digest: 7c5a248552a32f2ecf0d2b79051f4e31047176293281134fc09db2bd28cc332f
  folders: {}
tags:
- code/factory_pattern
- code/reflection
- code/cloneable_pattern
concepts:
- Object Instantiation
- Prototype Pattern
facets:
  layer: infrastructure
  status: stable
  complexity: '2'
description: 'Two `IFactory` implementations that create a new Object from an existing one, differing only in how faithfully the new instance reproduces the original: `FactoryByClass` copies only the type, via reflection, while `FactoryByPrototype` copies the data too, via an explicit `ICopy` contract that stands in for Java''s protected `clone()`.'
dv_has_:
  sub_:
    folders: 0
    files: 4
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
        factory_pattern: 2
        cloneable_pattern: 1
        reflection: 1
    concept_:
      object_instantiation: 2
      prototype_pattern: 2
has_sub_folders: 0
has_sub_files: 4
has_sub_units: 2
has_sub_facet_layer_infrastructure: 2
has_sub_facet_status_stable: 2
has_sub_facet_complexity_2: 2
has_sub_tag_code_factory_pattern: 2
has_sub_tag_code_cloneable_pattern: 1
has_sub_tag_code_reflection: 1
has_sub_concept_object_instantiation: 2
has_sub_concept_prototype_pattern: 2
related:
- path: ../_Matthias/Code/NET/Java/streamIO/factory
  shared-tags:
  - code/cloneable_pattern
  - code/factory_pattern
- path: ../_Matthias/Code/NET/_root/db/Query/SpocDb/SpocDb/Internal/DbManagement
  shared-tags:
  - code/factory_pattern
  - code/reflection
---

# factory

Two `IFactory` implementations that create a new Object from an existing one, differing
only in how faithfully the new instance reproduces the original: `FactoryByClass` copies
only the type, via reflection, while `FactoryByPrototype` copies the data too, via an
explicit `ICopy` contract that stands in for Java's protected `clone()`.

## Classes

| Class | Responsibility |
|---|---|
| [FactoryByClass](FactoryByClass.java) | Creates a new, uninitialized instance of the same Class as a given prototype Object, using reflection rather<br/>than copying. |
| [FactoryByPrototype](FactoryByPrototype.java) | Creates a new Object by copying a prototype Object's data, not only its Class. |

## Architecture

```mermaid
flowchart TD
  subgraph factory
    FactoryByClass["FactoryByClass"]
    FactoryByPrototype["FactoryByPrototype"]
  end

  FactoryByClass -->|"reflection, type only"| Object1["new Object"]
  linkStyle 0 opacity:1
  FactoryByPrototype -->|"ICopy.Copy(), type + data"| Object2["new Object"]
  linkStyle 1 opacity:1
```

## Entry Points

| Class.Method | Description |
|---|---|
| [FactoryByClass.nextItem()](FactoryByClass.java#L87) | Creates a new instance of the captured Class via reflection. |
| [FactoryByPrototype.nextItem()](FactoryByPrototype.java#L69) | Creates a new Object by copying the prototype. |
