---
digest:
  local-classes:
    ATestCase:
      mtime: '2026-09-05T09:19:35Z'
      digest: e5b7732d0250b9556bba1a79d4509a41d72c3a11e269372a2e5c260cee9611ad
    ITestCase:
      mtime: '2026-09-05T09:19:21Z'
      digest: d39834cc28b3803e82cedccd6046e9a8b5d4b0180129c7354539528e3499d2f8
    TestCollection:
      mtime: '2026-09-05T09:19:40Z'
      digest: 88dee787283c86215e596233f8c60efcab7e72958aba601b05e96a34b542e516
  folders: {}
tags:
- code/test_harness
- code/composite_pattern
- code/reflection_based_dispatch
concepts:
- Testing
- Reflection
facets:
  layer: test
  status: broken
  complexity: 3
description: A small, dependency-free test harness predating JUnit's presence in this codebase. `ITestCase` defines the composite contract (`setUp`/`tearDown`/`runTest`); `ATestCase` implements it by reflectively discovering and invoking every public no-argument `test...()` method on a Class or Object, so a subclass need only write plain `testXxx()` methods; and `TestCollection` composes multiple `ITestCase`s into one, running them depth-first before its own reflective tests.
dv_has_:
  sub_:
    folders: 0
    files: 8
    units: 3
    facet_:
      layer_:
        test: 3
      status_:
        stable: 2
        broken: 1
      complexity_:
        "2": 2
        "3": 1
    tag_:
      code_:
        test_harness: 3
        reflection_based_dispatch: 1
        composite_pattern: 1
    concept_:
      testing: 3
      composite_pattern: 1
      reflection: 1
has_sub_folders: 0
has_sub_files: 8
has_sub_units: 3
has_sub_facet_layer_test: 3
has_sub_facet_status_stable: 2
has_sub_facet_status_broken: 1
has_sub_facet_complexity_2: 2
has_sub_facet_complexity_3: 1
has_sub_tag_code_test_harness: 3
has_sub_tag_code_reflection_based_dispatch: 1
has_sub_tag_code_composite_pattern: 1
has_sub_concept_testing: 3
has_sub_concept_composite_pattern: 1
has_sub_concept_reflection: 1
---

# testing

A small, dependency-free test harness predating JUnit's presence in this codebase.
`ITestCase` defines the composite contract (`setUp`/`tearDown`/`runTest`); `ATestCase`
implements it by reflectively discovering and invoking every public no-argument
`test...()` method on a Class or Object, so a subclass need only write plain `testXxx()`
methods; and `TestCollection` composes multiple `ITestCase`s into one, running them
depth-first before its own reflective tests.

**Known defect** (see `## Bugs Found` in the repository root `HANDOFF.md`):
`ATestCase`'s reflective test runner logs the wrapping `InvocationTargetException`
instead of the test method's actual thrown exception, obscuring the real failure cause.

## Classes

| Class | Responsibility |
|---|---|
| [ATestCase](ATestCase.java) | Abstract Test Case, defaults the setUp() and tearDown() Methods to empty Methods, and drives Tests either<br/>directly, or reflectively over every public no-argument test...() Method of a Class or Object. |
| [ITestCase](ITestCase.java) | Defines the Interface for a (composite) Test Case that is to be run automatically. |
| [TestCollection](TestCollection.java) | Composite Pattern typed for Test Cases: runs a nested Collection of ITestCases depth-first before running its<br/>own reflective Tests. |

## Architecture

```mermaid
flowchart TD
  subgraph testing
    ITestCase["ITestCase (interface)"]
    ATestCase["ATestCase"]
    TestCollection["TestCollection"]

    ATestCase -->|"implements"| ITestCase
    linkStyle 0 opacity:1
    TestCollection -->|"extends"| ATestCase
    linkStyle 1 opacity:1
    TestCollection -->|"composes many"| ITestCase
    linkStyle 2 opacity:1
  end
```

## Entry Points

| Class.Method | Description |
|---|---|
| [ITestCase.runTest(IIStreamOut, IIStreamOut)](ITestCase.java#L59) | Runs this Test Case, reporting Failures and Errors to the given Handlers. |
| [ATestCase.test(ITestCase)](ATestCase.java#L96) | Runs every reflective test...() Method on the given Object. |
| [TestCollection.addTestCase(ITestCase)](TestCollection.java#L49) | Adds a nested Test Case to this composite Test. |
