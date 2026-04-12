#!/usr/bin/env python3
"""
Generate Minecraft JSON assets for per-wood dyeable blocks (blockstates, block models, item models).

Primary target: ``<wood>_window_<variant>`` (see ``WindowRegistration``) — ``vertical_connection`` only
(non-dyeable); one texture family per variant (e.g. ``window_simple`` from ``window_simple*.png``).

Run from repo root or from ``scripts/`` (script sets cwd to its directory).

Examples
--------
Dry run::

    python scripts/generate_wooden_block_assets.py window

Write files (templates under generator_files/window_template; tinted textures in assets)::

    python scripts/generate_wooden_block_assets.py window --write

Regenerate only blockstates for half-timber pillars (models must already exist)::

    python scripts/generate_wooden_block_assets.py half-timber-pillars --write

Regenerate only blockstates for half-timber bases p0..p9::

    python scripts/generate_wooden_block_assets.py half-timber-bases --write

Requires Python 3.10+ (stdlib only).
"""

from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path
from typing import Any

_SCRIPT_DIR = Path(__file__).resolve().parent
if str(_SCRIPT_DIR) not in sys.path:
    sys.path.insert(0, str(_SCRIPT_DIR))

from nekoration_asset_constants import MOD_ID, NEKO_COLOR_NAMES, VERTICAL_CONNECTIONS, WINDOW_VARIANTS, WOOD_IDS


def _assets_root(resources: Path) -> Path:
    return resources / "assets" / MOD_ID


def _dump_json(path: Path, data: Any, write: bool) -> None:
    text = json.dumps(data, indent=4, ensure_ascii=False) + "\n"
    if not write:
        print(f"[dry-run] would write {path} ({len(text)} bytes)")
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, encoding="utf-8")
    print(f"wrote {path}")


def _collect_texture_stems(template_dir: Path, style: str) -> set[str]:
    """Basenames without .png for files like ``window_simple.png``, ``window_simple_t0.png``."""
    stems: set[str] = set()
    if not template_dir.is_dir():
        return stems
    for p in template_dir.glob("*.png"):
        name = p.stem
        if name == style or name.startswith(style + "_"):
            stems.add(name)
    return stems


def _pick_texture_stem(style: str, vertical_connection: str, stems: set[str]) -> str:
    """Map vertical_connection to a template stem; fall back so generation always works."""

    def pick(candidates: list[str]) -> str:
        for c in candidates:
            if c in stems:
                return c
        return style

    if vertical_connection == "s0":
        return pick([style])
    if vertical_connection == "d0":
        return pick([f"{style}_d0", f"{style}_t0", style])
    if vertical_connection == "d1":
        return pick([f"{style}_d1", f"{style}_t2", style])
    if vertical_connection == "t0":
        return pick([f"{style}_t0", style])
    if vertical_connection == "t1":
        return pick([f"{style}_t1", style])
    if vertical_connection == "t2":
        return pick([f"{style}_t2", style])
    return style


WINDOW_PARENT_JSON: dict[str, Any] = {
    "parent": "block/block",
    "render_type": "minecraft:cutout",
    "textures": {
        "particle": "#side",
        "side": "minecraft:block/white_stained_glass",
        "overlay": "minecraft:block/oak_planks",
    },
    "elements": [
        {
            "from": [0, 0, 0],
            "to": [16, 16, 16],
            "faces": {
                "down": {"uv": [0, 0, 16, 16], "texture": "#side", "cullface": "down"},
                "up": {"uv": [0, 0, 16, 16], "texture": "#side", "cullface": "up"},
                "north": {"uv": [0, 0, 16, 16], "texture": "#side", "cullface": "north"},
                "south": {"uv": [0, 0, 16, 16], "texture": "#side", "cullface": "south"},
                "west": {"uv": [0, 0, 16, 16], "texture": "#side", "cullface": "west"},
                "east": {"uv": [0, 0, 16, 16], "texture": "#side", "cullface": "east"},
            },
        },
        {
            "from": [0, 0, 0],
            "to": [16, 16, 16],
            "faces": {
                "down": {"uv": [0, 0, 16, 16], "texture": "#overlay", "cullface": "down"},
                "up": {"uv": [0, 0, 16, 16], "texture": "#overlay", "cullface": "up"},
                "north": {"uv": [0, 0, 16, 16], "texture": "#overlay", "cullface": "north"},
                "south": {"uv": [0, 0, 16, 16], "texture": "#overlay", "cullface": "south"},
                "west": {"uv": [0, 0, 16, 16], "texture": "#overlay", "cullface": "west"},
                "east": {"uv": [0, 0, 16, 16], "texture": "#overlay", "cullface": "east"},
            },
        },
    ],
}


def _window_block_model_id(wood: str, variant: str, vertical_connection: str) -> str:
    base = f"{MOD_ID}:block/window/{wood}/{variant}/window_{wood}_{variant}"
    if vertical_connection == "s0":
        return base
    return f"{base}_{vertical_connection}"


def build_window_blockstate(wood: str, variant: str) -> dict[str, Any]:
    return {
        "variants": {
            f"vertical_connection={vc}": {"model": _window_block_model_id(wood, variant, vc)}
            for vc in VERTICAL_CONNECTIONS
        }
    }


def _half_timber_base_model_path(wood: str, pattern_index: int) -> str:
    return f"{MOD_ID}:block/half_timber/{wood}/half_timber_{wood}_p{pattern_index}"


def build_half_timber_base_blockstate(wood: str, pattern_index: int) -> dict[str, Any]:
    """Same layout as port_nekoration_assets.build_base_blockstate (p0..p9)."""
    variants = {
        f"color={NEKO_COLOR_NAMES[i]}": {"model": _half_timber_base_model_path(wood, pattern_index)}
        for i in range(len(NEKO_COLOR_NAMES))
    }
    return {"variants": variants}


def build_half_timber_pillar_blockstate(wood: str, pillar_slot: int) -> dict[str, Any]:
    """Same layout as port_nekoration_assets.build_pillar_blockstate (p0..p2)."""

    def model_path(wood_inner: str, p: int, conn_suffix: str | None) -> str:
        base = f"{MOD_ID}:block/half_timber/{wood_inner}/half_timber_{wood_inner}_p{p}"
        if conn_suffix:
            return f"{base}_{conn_suffix}"
        return base

    p = pillar_slot
    variants: dict[str, Any] = {}
    for cname in NEKO_COLOR_NAMES:
        for vc in VERTICAL_CONNECTIONS:
            key = f"color={cname},vertical_connection={vc}"
            if vc == "s0":
                variants[key] = {"model": model_path(wood, p, None)}
            else:
                variants[key] = {"model": model_path(wood, p, vc)}
    return {"variants": variants}


def cmd_window(args: argparse.Namespace) -> None:
    resources = Path(args.resources).resolve()
    out_assets = _assets_root(resources)
    write = bool(args.write)
    template_dir = (Path(args.template_dir) if args.template_dir else _SCRIPT_DIR / "generator_files" / "window_template")
    template_dir = template_dir.resolve()

    parent_path = out_assets / "models" / "block" / "window" / "window.json"
    _dump_json(parent_path, WINDOW_PARENT_JSON, write)

    for wood in WOOD_IDS:
        for variant in WINDOW_VARIANTS:
            style = f"window_{variant}"
            stems = _collect_texture_stems(template_dir, style)
            if not stems:
                print(
                    f"warning: no PNGs matching style {style!r} under {template_dir} — stems fall back to {style!r} only.",
                    file=sys.stderr,
                )
                stems.add(style)

            model_dir = out_assets / "models" / "block" / "window" / wood / variant
            base_name = f"{wood}_window_{variant}"
            for vc in VERTICAL_CONNECTIONS:
                stem = _pick_texture_stem(style, vc, stems)
                side_tex = f"{MOD_ID}:block/window/{wood}/{stem}"
                end_tex = f"{MOD_ID}:block/window/{wood}/window_top"
                model_name = f"{base_name}.json" if vc == "s0" else f"{base_name}_{vc}.json"
                body = {
                    "parent": f"{MOD_ID}:block/window/window",
                    "textures": {"side": side_tex, "end": end_tex},
                }
                _dump_json(model_dir / model_name, body, write)

            bs = build_window_blockstate(wood, variant)
            _dump_json(out_assets / "blockstates" / f"{base_name}.json", bs, write)

            item_body = {"parent": _window_block_model_id(wood, variant, "s0")}
            _dump_json(out_assets / "models" / "item" / f"{base_name}.json", item_body, write)

    if not write:
        print("\nDry run only. Re-run with --write.")
    else:
        print(
            "\nNote: maintain matching grayscale files under textures/block/window_back/ for each stem "
            "(e.g. window_simple_t0.png)."
        )


def cmd_half_timber_pillars(args: argparse.Namespace) -> None:
    resources = Path(args.resources).resolve()
    out_assets = _assets_root(resources)
    write = bool(args.write)
    for slot in range(3):
        for wood in WOOD_IDS:
            bs = build_half_timber_pillar_blockstate(wood, slot)
            name = f"{wood}_half_timber_pillar_p{slot}.json"
            _dump_json(out_assets / "blockstates" / name, bs, write)
    if not write:
        print("\nDry run only. Re-run with --write.")


def cmd_half_timber_bases(args: argparse.Namespace) -> None:
    resources = Path(args.resources).resolve()
    out_assets = _assets_root(resources)
    write = bool(args.write)
    for p in range(10):
        for wood in WOOD_IDS:
            bs = build_half_timber_base_blockstate(wood, p)
            name = f"{wood}_half_timber_p{p}.json"
            _dump_json(out_assets / "blockstates" / name, bs, write)
    if not write:
        print("\nDry run only. Re-run with --write.")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = parser.add_subparsers(dest="command", required=True)

    p_win = sub.add_parser(
        "window",
        help="Emit <wood>_window_<variant> blockstates (vertical_connection only), models, item models",
    )
    p_win.add_argument(
        "--resources",
        type=Path,
        default=_SCRIPT_DIR.parent / "src" / "main" / "resources",
        help="Path to src/main/resources",
    )
    p_win.add_argument(
        "--template-dir",
        type=Path,
        default=None,
        help="Folder of template PNGs (default: scripts/generator_files/window_template)",
    )
    p_win.add_argument("--write", action="store_true")
    p_win.set_defaults(func=cmd_window)

    p_ht = sub.add_parser(
        "half-timber-pillars",
        help="Rewrite <wood>_half_timber_pillar_p0..2 blockstates only (models unchanged)",
    )
    p_ht.add_argument(
        "--resources",
        type=Path,
        default=_SCRIPT_DIR.parent / "src" / "main" / "resources",
        help="Path to src/main/resources",
    )
    p_ht.add_argument("--write", action="store_true")
    p_ht.set_defaults(func=cmd_half_timber_pillars)

    p_hp = sub.add_parser(
        "half-timber-bases",
        help="Rewrite <wood>_half_timber_p0..p9 blockstates only (models unchanged)",
    )
    p_hp.add_argument(
        "--resources",
        type=Path,
        default=_SCRIPT_DIR.parent / "src" / "main" / "resources",
        help="Path to src/main/resources",
    )
    p_hp.add_argument("--write", action="store_true")
    p_hp.set_defaults(func=cmd_half_timber_bases)

    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
