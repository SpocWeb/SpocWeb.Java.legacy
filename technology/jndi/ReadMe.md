---
digest:
  local-classes:
    CmdLnBrowser:
      mtime: '2026-09-05T11:10:56Z'
      digest: 4b8f5fad08596c8bac6db2d669d3d32095c8ef20bb09dbe01763ce2f0bf8db86
    InitCtx:
      mtime: '2026-09-05T10:13:32Z'
      digest: 4fa79c755e69786f2f156e7d7ba4ec7ce7cd37b207e725c31f1ed2628faac9fc
  folders: {}
tags:
- code/directory_services
concepts:
- Naming and Directory Services
facets:
  layer: infrastructure
  status: legacy
  complexity: 2
description: 'Small demonstrations of the Java Naming and Directory Interface (JNDI): acquiring an `InitialContext` against a file-system JNDI provider, and browsing/manipulating it interactively with Unix-like commands (`cd`, `ls`, `mv`, `mkdir`, `rmdir`, `cat`).'
dv_has_:
  sub_:
    folders: 0
    files: 5
    units: 2
    facet_:
      layer_:
        test: 1
        utility: 1
      status_:
        legacy: 2
      complexity_:
        "2": 2
    tag_:
      code_:
        directory_services: 2
    concept_:
      command_line_jndi_browser: 1
      jndi_context_demo: 1
has_sub_folders: 0
has_sub_files: 5
has_sub_units: 2
has_sub_facet_layer_test: 1
has_sub_facet_layer_utility: 1
has_sub_facet_status_legacy: 2
has_sub_facet_complexity_2: 2
has_sub_tag_code_directory_services: 2
has_sub_concept_command_line_jndi_browser: 1
has_sub_concept_jndi_context_demo: 1
---

# jndi

Small demonstrations of the Java Naming and Directory Interface (JNDI): acquiring an
`InitialContext` against a file-system JNDI provider, and browsing/manipulating it
interactively with Unix-like commands (`cd`, `ls`, `mv`, `mkdir`, `rmdir`, `cat`).

## Classes

| Class | Responsibility |
|---|---|
| [CmdLnBrowser](CmdLnBrowser.java) | Hierarchical JNDI Browser operated on the Command line using Unix like Commands cd ls mv mkdir |
| [InitCtx](InitCtx.java) | Demonstrates acquiring an Initial Context for accessing a JNDI Server. |

## Entry Points

| Class.Method | Description |
|---|---|
| [CmdLnBrowser.main(String[])](CmdLnBrowser.java#L237) | Starts an interactive command-line JNDI browsing session. |
| [InitCtx.main(String[])](InitCtx.java#L47) | Demonstrates acquiring an Initial Context and prints the result. |
