---
digest:
  local-classes:
    Coord2DMouseController:
      mtime: '2026-09-05T11:47:54Z'
      digest: f0d75e45c453366785563fe3edb13b37f7fbbbeeb3dc50fa7c5aae47f45411ad
    Coordinates2D:
      mtime: '2026-09-05T10:13:18Z'
      digest: cf33bac07ba34b0e08a5dba84932c0265ca3be9a345411556418a2f1e6dedd14
    LinCoordMap:
      mtime: '2026-09-05T10:13:18Z'
      digest: 3fda585440159ab2a2346f7794433d083a2714fd82e5efd42343cb7318bbe6a5
    Map2DModel:
      mtime: '2026-09-05T11:46:15Z'
      digest: 7b8c895665b46e590f6ca1d88d70c61752b7a86cf4a3c2d5006cdb7ff59b553b
    Map2DMouseController:
      mtime: '2026-09-05T11:47:45Z'
      digest: 231c7c777e625ae0ffaf8c8c12b67ca86ea126ab6b8da56a0fc13b62d9554700
    Map2DPainter:
      mtime: '2026-09-05T11:47:31Z'
      digest: ad2faa50b2972364a7ec522aeddf5a6d57be02a10fe3e4262d7cbd506a446cc3
    Raster:
      mtime: '2026-09-05T11:48:46Z'
      digest: 9c2f6786741937b4361f97403658f9e97a8f1b69e91cbf055909eb389d0800fd
    testMathGraph2:
      mtime: '2026-09-05T10:13:18Z'
      digest: ca70060b98fcdb176f190e34b3efef9ea972144f260259e503affe0cd6f87fdd
  folders: {}
tags:
- code/coordinate_transform
- code/raster_generation
- code/view_model
concepts:
- 2D Graph Visualization (MVC)
facets:
  layer: domain
  status: legacy
  complexity: medium
description: Provides affine coordinate mapping between a 2D data range and a Graphics2D target area, plus the model/painter/controller triad that displays and interactively edits a set of mapped points and edges. `LinCoordMap` and `Coordinates2D` do the per-axis and combined-axis affine transforms; `Map2DModel`, `Map2DPainter` and `Map2DMouseController` build an MVC layer for graphs of 2D points on top of that transform, while `Raster` and `Coord2DMouseController` supply supporting raster-generation and view-panning utilities. `testMathGraph2` is a standalone applet/demo exercising the package against real map/star data files.
---

# math2D

Provides affine coordinate mapping between a 2D data range and a Graphics2D target area, plus
the model/painter/controller triad that displays and interactively edits a set of mapped
points and edges. `LinCoordMap` and `Coordinates2D` do the per-axis and combined-axis affine
transforms; `Map2DModel`, `Map2DPainter` and `Map2DMouseController` build an MVC layer for
graphs of 2D points on top of that transform, while `Raster` and `Coord2DMouseController`
supply supporting raster-generation and view-panning utilities. `testMathGraph2` is a
standalone applet/demo exercising the package against real map/star data files.

## Classes

| Class | Responsibility |
|---|---|
| [Coord2DMouseController](Coord2DMouseController.java) | Controls drag-and-drop panning of the view pane's Coordinates2D, with shrink on single click and enlarge on<br/>double click. |
| [Coordinates2D](Coordinates2D.java) | This Class encapsulates 2dim Mapping in each Dimension separately. |
| [LinCoordMap](LinCoordMap.java) | This Class encapsulates affine Mapping between two 1D Coordinate Systems. |
| [Map2DModel](Map2DModel.java) | Holds the original 2D point/edge data alongside its mapped Point2D positions and the Coordinates2D transform<br/>used to derive them, allowing points to be added and moved interactively via the GUI. |
| [Map2DMouseController](Map2DMouseController.java) | Handles mouse interaction with a Map2DModel: adds or removes points and edges on double-click, and drags<br/>either a single focused point or the whole canvas. |
| [Map2DPainter](Map2DPainter.java) | Displays, translates, scales and edits a Map2DModel, re-generating the mapped points whenever the underlying<br/>edges have changed. |
| [Raster](Raster.java) | This Class defines Routines that display the Results of numerical Calculations with 2D Graphics. |
| [testMathGraph2](testMathGraph2.java) | This class reads PARAM tags from its HTML host page and sets the color and label properties of the applet. |

## Architecture

```mermaid
flowchart TD
  subgraph math2D
    LinCoordMap["LinCoordMap - 1D affine map"]
    Coordinates2D["Coordinates2D - 2D affine map"]
    Map2DModel["Map2DModel - point/edge data"]
    Map2DPainter["Map2DPainter"]
    Map2DMouseController["Map2DMouseController"]
    Coord2DMouseController["Coord2DMouseController"]
    Raster["Raster"]

    Coordinates2D -->|"delegates per axis to"| LinCoordMap
    Map2DModel -->|"stores"| Coordinates2D
    Map2DPainter -->|"displays/edits"| Map2DModel
    Map2DPainter -->|"installs"| Map2DMouseController
    linkStyle 4 opacity:1
    Map2DMouseController -->|"edits points/trafo of"| Map2DModel
    Coord2DMouseController -->|"pans"| Coordinates2D
  end
```

## Entry Points

| Class.Method | Description |
|---|---|
| [Map2DPainter.draw(IGraphText)](Map2DPainter.java#L139) | Recomputes mapped points from edges when they changed, then paints the graph. |
| [Map2DPainter.addDefaultControllers(IController)](Map2DPainter.java#L168) | Wires up the mouse and key controllers for interactive editing. |
| [Map2DModel.addPoint(float, float, String)](Map2DModel.java#L223) | Adds a labeled point to the model, mapped through the current coordinate transform. |
| [Coordinates2D.mapPt(Point2D, float, float)](Coordinates2D.java#L87) | Maps a data-space point into target/display coordinates. |
| [testMathGraph2.main(String[])](testMathGraph2.java#L208) | Standalone entry point that runs the demo applet. |
