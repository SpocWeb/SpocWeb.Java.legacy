# Java

Personal Java codebase, accumulated from the JDK 1.2-6 era onward and still organised as a
single flat source tree rather than a build-tool project.
There is no `pom.xml`, `build.gradle` or `Makefile`: the tree carries Eclipse `.project`/
`.classpath` metadata only, and compiled `.class` files sit alongside their sources
(ignored via `.gitignore`).

TODO: LLM — replace this paragraph with the domain narrative once Pass 3 has run over the
subsystems below. The structure and counts here are measured; the per-folder domain roles
are deliberately left blank rather than guessed from folder names.

## Subsystems

Measured `.java` file counts per top-level folder, deepest content included.
The Domain Role column is filled in by `update-readme --subsystems` once each folder has its
own `ReadMe.md`.

| Folder | Files | Domain Role |
|---|--:|---|
| `streamIO/` | 674 | TODO: LLM |
| `function/` | 204 | TODO: LLM |
| `graphic/` | 131 | TODO: LLM |
| `math/` | 84 | TODO: LLM |
| `structure/` | 52 | TODO: LLM |
| `tester/` | 49 | TODO: LLM |
| `technology/` | 41 | TODO: LLM |
| `synch/` | 32 | TODO: LLM |
| `graphs/` | 31 | TODO: LLM |
| `asynch/` | 28 | TODO: LLM |
| `knowledge/` | 26 | TODO: LLM |
| `stringOp/` | 16 | TODO: LLM |
| `tools/` | 16 | TODO: LLM |
| `aspect/` | 15 | TODO: LLM |
| `flow/` | 14 | TODO: LLM |
| `reflect/` | 12 | TODO: LLM |
| `sound/` | 10 | TODO: LLM |
| `analysis/` | 6 | TODO: LLM |
| `swing/` | 3 | TODO: LLM |
| `persistences/` | 2 | TODO: LLM |

Nine further `.java` files sit directly in this root folder; they are listed in the
`## Classes` table once `update-readme` has run here.

## Documentation Status

Documentation is generated bottom-up by the `/Java.ReadMeGenerator` skill and its companion
CLI, whose source lives outside this repository at
`D:/_/_AI/skills/Java.ReadMeGenerator/ReadMeGenerator/`.

| Pass | Scope | Status |
|---|---|---|
| 1 — method Javadoc | 1,455 files | not started |
| 2 — class Javadoc + docstate | 1,455 files | not started |
| 3 — folder `ReadMe.md` | 136 folders | `tools/` only |
| 4-7 — tags, vocabulary, index | whole tree | not started |

Per-type staleness bookkeeping is stored in a machine-owned `<!-- docstate ... -->` block
inside each type's Javadoc comment; never hand-edit its `pass`, `mtime`, `digest` or `stale`
fields. Progress across sessions is tracked in `HANDOFF.md` once a multi-folder run starts.

## Build

There is no build tooling in this repository, so this ReadMe intentionally carries no
Quick Start section. Compilation, when needed, is driven from the Eclipse project metadata
or by invoking `javac` against the relevant package folder directly.
