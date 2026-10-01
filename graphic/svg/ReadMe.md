---
digest:
  local-classes:
    SvgApplet:
      mtime: '2026-09-05T11:48:29Z'
      digest: cd5372a949813412e28c3269508728e2136d6d10dd580b31f5fe776a330059aa
    SvgHandler:
      mtime: '2026-09-05T11:47:38Z'
      digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
  folders: {}
tags:
- code/rendering
- code/parsing
concepts:
- SVG Rendering and Parsing
facets:
  layer: utility
  status: legacy
  complexity: 3
description: 'This folder renders SVG documents through the `graphic` MVC framework rather than parsing them into an in-memory DOM. `SvgApplet` is a SAX callback target: each SVG element name it exposes as a public no-arg-return method (`svg`, `rect`, `ellipse`, `line`, `text`, `g`, ...) is invoked directly by `technology.xml.SaxDispatcher` as the document streams in, and each handler maps the element''s attributes onto an `IGraphImage` drawing call through the coordinate transform in `graphic.math2D`. It can run either as an Applet or as a standalone Frame started from `main`. `SvgHandler` is an empty placeholder, not yet wired to any of this.'
dv_has_:
  sub_:
    folders: 0
    files: 5
    units: 2
    facet_:
      layer_:
        utility: 2
      status_:
        broken: 1
        unfinished: 1
      complexity_:
        "2": 1
        "4": 1
    tag_:
      code_:
        parsing: 2
        rendering: 1
    concept_:
      empty_sax_handler_placeholder: 1
      svg_applet_renderer: 1
has_sub_folders: 0
has_sub_files: 5
has_sub_units: 2
has_sub_facet_layer_utility: 2
has_sub_facet_status_broken: 1
has_sub_facet_status_unfinished: 1
has_sub_facet_complexity_2: 1
has_sub_facet_complexity_4: 1
has_sub_tag_code_parsing: 2
has_sub_tag_code_rendering: 1
has_sub_concept_empty_sax_handler_placeholder: 1
has_sub_concept_svg_applet_renderer: 1
related:
  - path: ../_Matthias/Code/NET/_root/_projects/spocWeb.styledEditing/bracketing
    shared-tags: [code/parsing, code/rendering]
  - path: ../_Matthias/Code/NET/_root/_projects/spocWeb.styledEditing/folding
    shared-tags: [code/parsing, code/rendering]
  - path: ../_Matthias/Code/NET/_root/_projects/spocWeb.styledEditing/rendering/backGroundTasks
    shared-tags: [code/rendering]
  - path: ../_Matthias/Code/Java/streamIO/object/yaml
    shared-tags: [code/parsing]
  - path: ../_Matthias/Code/NET/_root/_projects/spocWeb.styledEditing/xml
    shared-tags: [code/parsing]
---

# svg

This folder renders SVG documents through the `graphic` MVC framework rather than
parsing them into an in-memory DOM. `SvgApplet` is a SAX callback target: each SVG
element name it exposes as a public no-arg-return method (`svg`, `rect`, `ellipse`,
`line`, `text`, `g`, ...) is invoked directly by `technology.xml.SaxDispatcher` as the
document streams in, and each handler maps the element's attributes onto an
`IGraphImage` drawing call through the coordinate transform in `graphic.math2D`. It
can run either as an Applet or as a standalone Frame started from `main`.
`SvgHandler` is an empty placeholder, not yet wired to any of this.

## Classes

| Class | Responsibility |
|---|---|
| [SvgApplet](SvgApplet.java) | Title: SvgApplet Description: Purpose: Instances of this Class can either act as Applets embedded in an<br/>AppletViewer or Browser or as a standalone Frame if started via Main. |
| [SvgHandler](SvgHandler.java) | Placeholder type reserved for future SVG element-handling logic; currently empty. |

## Entry Points

| Class.Method | Description |
|---|---|
| [SvgApplet.main](SvgApplet.java#L603) | Launches this class as a standalone Frame displaying the SVG file given as `args[0]`. |
| [SvgApplet.init](SvgApplet.java#L580) | Applet lifecycle entry point; resolves the `svgFile` Applet parameter if no URI was given. |
| [SvgApplet.paint](SvgApplet.java#L329) | Applet/Frame repaint callback that triggers parsing and drawing of the SVG document. |
