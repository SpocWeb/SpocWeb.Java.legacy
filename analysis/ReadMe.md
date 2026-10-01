---
digest:
  local-classes:
    Party:
      mtime: '2026-09-05T09:41:23Z'
      digest: c73587adeb298614978ad38910b866b3db0f8caddc639ad15415cd4e086a9088
    PartyAssociation:
      mtime: '2026-09-05T09:41:43Z'
      digest: 686dcc6cf09668487e5badc26701d89cc694d12fa0d8919781d0be6d9c80fdcc
    PartyType:
      mtime: '2026-09-05T09:41:32Z'
      digest: c52a48c5aec8a9a545656f88051af1b3a7a5b0e7cd1f502d52ce6a9b54f8f00f
    PartyTypeAssociation:
      mtime: '2026-09-05T09:41:53Z'
      digest: f0e4687069327c454a1fd566d7de614559ef76762181c1e75c9056d455bb97c1
    Responsibility:
      mtime: '2026-09-05T09:42:02Z'
      digest: d7b8fc30157dbe4a7bad405a5a8549f7a8c4e7a7ce60cb8f78966f6ee6328a3a
    ResponsibilityType:
      mtime: '2026-09-05T09:42:10Z'
      digest: 3b1c35081a451e31a0dc51c25c9e6c084be890ee61770154afab91da89fccb20
  folders: {}
tags:
- code/domain_model
- code/type_system
concepts:
- Domain Model
- Relationship Modelling
facets:
  layer: domain
  status: stable
  complexity: '2'
description: 'A pure-interface implementation of Martin Fowler''s Party/Responsibility analysis pattern: `Party` (a Person or Organization, possibly composite) has a dynamically assignable `PartyType`, and `Responsibility` generalizes hierarchical relationships between Parties (parent/child, org membership, etc.) beyond a single fixed hierarchy. Each operational concept has a Knowledge-Level counterpart that constrains it: `PartyAssociation` pairs two `Party`s while `PartyTypeAssociation` pairs the corresponding `PartyType`s, and `ResponsibilityType` maintains the allowed `PartyTypeAssociation`s for a `Responsibility`. No implementation of these interfaces exists in this folder.'
dv_has_:
  sub_:
    folders: 0
    files: 14
    units: 6
    facet_:
      layer_:
        domain: 6
      status_:
        stable: 6
      complexity_:
        "2": 6
    tag_:
      code_:
        type_system: 6
        domain_model: 6
    concept_:
      relationship_modelling: 6
      "Technology\\IT\\Software\\SW~Programming\\Domain-Driven_Design.md": 6
has_sub_folders: 0
has_sub_files: 14
has_sub_units: 6
has_sub_facet_layer_domain: 6
has_sub_facet_status_stable: 6
has_sub_facet_complexity_2: 6
has_sub_tag_code_type_system: 6
has_sub_tag_code_domain_model: 6
has_sub_concept_relationship_modelling: 6
has_sub_concept_technology_it_software_sw_programming_domain_driven_design_md: 6
related:
- path: ../_Matthias/Code/NET/_std/SpecBuilderPattern/Models
  shared-tags:
  - code/domain_model
- path: ../_Matthias/Code/NET/_core/UnitTestProject1/Models
  shared-tags:
  - code/domain_model
---

# analysis

A pure-interface implementation of Martin Fowler's Party/Responsibility analysis pattern:
`Party` (a Person or Organization, possibly composite) has a dynamically assignable
`PartyType`, and `Responsibility` generalizes hierarchical relationships between Parties
(parent/child, org membership, etc.) beyond a single fixed hierarchy. Each operational
concept has a Knowledge-Level counterpart that constrains it: `PartyAssociation` pairs two
`Party`s while `PartyTypeAssociation` pairs the corresponding `PartyType`s, and
`ResponsibilityType` maintains the allowed `PartyTypeAssociation`s for a `Responsibility`.
No implementation of these interfaces exists in this folder.

## Classes

| Class | Responsibility |
|---|---|
| [Party](Party.java) | Defines the Interface for a Party (see Fowler: 'Analysis Patterns'). |
| [PartyAssociation](PartyAssociation.java) | Defines the Interface for a directed Parent/Child Association between two Parties. |
| [PartyType](PartyType.java) | Defines the Interface for a Party (i.e. Group of Agents) Type. |
| [PartyTypeAssociation](PartyTypeAssociation.java) | Defines the Interface for a directed Parent/Child Association between two PartyTypes, i.e. an allowed<br/>Responsibility at the Knowledge Level. |
| [Responsibility](Responsibility.java) | Defines the Interface for a Responsibility, a generalized Parent/Child Association between two Parties, typed<br/>by a ResponsibilityType. |
| [ResponsibilityType](ResponsibilityType.java) | Defines the Interface for a Responsibility Type. |

## Architecture

```mermaid
flowchart TD
  subgraph analysis
    Party["Party"]
    PartyType["PartyType"]
    PartyAssociation["PartyAssociation"]
    PartyTypeAssociation["PartyTypeAssociation"]
    Responsibility["Responsibility"]
    ResponsibilityType["ResponsibilityType"]

    Party -->|"typed by"| PartyType
    linkStyle 0 opacity:1
    PartyAssociation -->|"pairs two"| Party
    linkStyle 1 opacity:1
    PartyTypeAssociation -->|"pairs two"| PartyType
    linkStyle 2 opacity:1
    Responsibility -->|"extends"| PartyAssociation
    linkStyle 3 opacity:1
    Responsibility -->|"typed by"| ResponsibilityType
    linkStyle 4 opacity:1
    ResponsibilityType -->|"constrains via"| PartyTypeAssociation
    linkStyle 5 opacity:1
  end
```

## Entry Points

| Class.Method | Description |
|---|---|
| [Party.getpartyType()](Party.java#L44) | Returns this Party's dynamically assignable Type. |
| [Responsibility.getresponsibilityType()](Responsibility.java#L44) | Returns this Responsibility's dynamically assignable Type. |
| [ResponsibilityType.getAllowedAssociations()](ResponsibilityType.java#L52) | Returns the allowed Associations between Parent and Child PartyTypes. |
