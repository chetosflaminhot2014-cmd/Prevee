#!/usr/bin/env python3
"""Convert a GLB into a Bedrock cube-based geometry approximation.

This deliberately voxelizes the source model because Bedrock entity geometry is
cube-based. Original rigging/animation is not preserved; material base colors
are preserved as a simple generated atlas.
"""
import json
import math
import subprocess
from pathlib import Path

import numpy as np
import trimesh
from PIL import Image, ImageDraw

ROOT = Path("addons/Gato-Terror")
GLB = Path("cat_in_the_hat.glb")
GEO = ROOT / "GatoTerrorRP/models/entity/gato_terror.geo.json"
TEX = ROOT / "GatoTerrorRP/textures/entity/gato.png"
TARGET_HEIGHT = 2.8
MAX_VOXELS_HIGH = 28
MAX_CUBES = 9000

if not GLB.exists():
    raise SystemExit(f"Missing source model: {GLB}")

loaded = trimesh.load(GLB, force="scene")
if isinstance(loaded, trimesh.Trimesh):
    scene = trimesh.Scene(loaded)
else:
    scene = loaded

parts = []
for node in scene.graph.nodes_geometry:
    transform, geom_name = scene.graph.get(node)
    mesh = scene.geometry[geom_name].copy()
    if not isinstance(mesh, trimesh.Trimesh) or len(mesh.faces) == 0:
        continue
    mesh.apply_transform(transform)
    material = getattr(getattr(mesh, "visual", None), "material", None)
    rgba = None
    if material is not None:
        for attr in ("baseColorFactor", "main_color"):
            value = getattr(material, attr, None)
            if value is not None:
                arr = np.asarray(value).reshape(-1)
                if len(arr) >= 3:
                    rgba = [int(max(0, min(255, round(float(c) * (255 if float(c) <= 1 else 1))))) for c in arr[:4]]
                    break
    if rgba is None:
        try:
            arr = np.asarray(mesh.visual.vertex_colors)
            if arr.size:
                rgba = [int(v) for v in np.median(arr.reshape(-1, 4), axis=0)]
        except Exception:
            pass
    if rgba is None:
        rgba = [180, 180, 180, 255]
    if len(rgba) == 3:
        rgba.append(255)
    parts.append((mesh, tuple(rgba)))

if not parts:
    raise SystemExit("The GLB contains no triangle meshes.")

all_bounds = np.vstack([mesh.bounds for mesh, _ in parts])
extent = all_bounds[1] - all_bounds[0]
if not np.all(np.isfinite(extent)) or float(extent.max()) <= 0:
    raise SystemExit("Invalid model bounds.")
pitch = float(extent[1]) / MAX_VOXELS_HIGH if extent[1] > 0 else float(extent.max()) / MAX_VOXELS_HIGH
pitch = max(pitch, float(extent.max()) / 44.0)
palette = []
voxels = {}
for mesh, rgba in parts:
    if rgba not in palette:
        palette.append(rgba)
    material_index = palette.index(rgba)
    try:
        vox = mesh.voxelized(pitch=pitch)
        points = np.asarray(vox.points)
    except Exception as exc:
        print(f"Skipping mesh that could not be voxelized: {exc}")
        continue
    if not len(points):
        continue
    for point in points:
        # Normalize the source model into a 2.8-block tall entity, with its
        # feet at y=0. GLB axes are retained as far as possible; Y is up.
        normalized = (
            (point[0] - all_bounds[0, 0]) / pitch,
            (point[1] - all_bounds[0, 1]) / pitch,
            (point[2] - all_bounds[0, 2]) / pitch,
        )
        key = tuple(int(round(v)) for v in normalized)
        voxels.setdefault(key, material_index)

if not voxels:
    raise SystemExit("Voxelization produced no cubes.")
if len(voxels) > MAX_CUBES:
    raise SystemExit(f"Model too dense ({len(voxels)} cubes); lower resolution before conversion.")

# Use one 16x16 color swatch per source material. All six faces of each cube
# point at its swatch; this preserves material colors but not original UV maps.
tile = 16
columns = 8
rows = max(1, math.ceil(len(palette) / columns))
atlas = Image.new("RGBA", (columns * tile, rows * tile), (255, 0, 255, 255))
draw = ImageDraw.Draw(atlas)
for i, rgba in enumerate(palette):
    x, y = (i % columns) * tile, (i // columns) * tile
    draw.rectangle((x, y, x + tile - 1, y + tile - 1), fill=rgba)
TEX.parent.mkdir(parents=True, exist_ok=True)
atlas.save(TEX)

def face_uv(material_index):
    x, y = (material_index % columns) * tile, (material_index // columns) * tile
    return {"uv": [x, y], "uv_size": [tile, tile]}

cubes = []
for (x, y, z), mat_index in sorted(voxels.items(), key=lambda item: (item[0][1], item[0][0], item[0][2])):
    cubes.append({
        "origin": [round((x - extent[0] / (2 * pitch)) * pitch / extent[1] * TARGET_HEIGHT, 4),
                   round(y * pitch / extent[1] * TARGET_HEIGHT, 4),
                   round((z - extent[2] / (2 * pitch)) * pitch / extent[1] * TARGET_HEIGHT, 4)],
        "size": [round(pitch / extent[1] * TARGET_HEIGHT, 4)] * 3,
        "uv": {
            "north": face_uv(mat_index), "south": face_uv(mat_index),
            "east": face_uv(mat_index), "west": face_uv(mat_index),
            "up": face_uv(mat_index), "down": face_uv(mat_index)
        }
    })

geometry = {
    "format_version": "1.12.0",
    "minecraft:geometry": [{
        "description": {
            "identifier": "geometry.gato_terror",
            "texture_width": columns * tile,
            "texture_height": rows * tile,
            "visible_bounds_width": 3.0,
            "visible_bounds_height": 3.5,
            "visible_bounds_offset": [0, 1.6, 0]
        },
        "bones": [{
            "name": "root",
            "pivot": [0, 0, 0],
            "cubes": cubes
        }]
    }]
}
GEO.parent.mkdir(parents=True, exist_ok=True)
GEO.write_text(json.dumps(geometry, separators=(",", ":")), encoding="utf-8")
print(f"Converted {GLB}: {len(parts)} meshes, {len(palette)} material colors, {len(cubes)} cubes.")
print(f"Geometry: {GEO}; texture: {TEX}")
