---
language: doc
digest:
  local-units: {}
  folders: {}
tags: [code/vendored_code, code/nlp]
concepts: [Natural Language Processing]
facet-layer: reference
facet-status: stable
facet-complexity: 1
dv_has_:
  sub_:
    folders: 0
    files: 1
    units: 1
    tag_:
      code_:
        java_library: 1
        text_processing_library: 1
        vendored_code: 1
has_sub_folders: 0
has_sub_files: 1
has_sub_units: 1
has_sub_tag_code_java_library: 1
has_sub_tag_code_text_processing_library: 1
has_sub_tag_code_vendored_code: 1
---

# _NaturalLang

Java natural-language tooling kept for reference; at present it holds one vendored project,
documented from outside by its sidecar [LanguageTool.md](LanguageTool.md), and no own code.

## Vendored Resources

| Resource | Version | License | Upstream | Why it is here |
|---|---|---|---|---|
| `LanguageTool` | unknown | unknown in this checkout (upstream: LGPL-2.1) | <https://github.com/languagetool-org/languagetool> | Style and grammar proofreading engine for more than 20 languages; [sidecar](LanguageTool.md). |
