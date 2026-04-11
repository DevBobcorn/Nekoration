#!/usr/bin/env python3
"""
Port Nekoration resource assets from legacy layouts (e.g. 1.16.5) to NeoForge 1.21+ layouts.

Primary use: expand half-timber blocks from ``half_timber_p0`` into per-wood ids
``half_timber_<wood>_p0`` matching ``HalfTimberWood`` / ``HalfTimberRegistration``.

Examples
--------
Dry run (print actions only)::

    python scripts/port_nekoration_assets.py half-timber \\
        --reference reference/nekoration-1.16.5/src/main/resources \\
        --output src/main/resources

Apply writes::

    python scripts/port_nekoration_assets.py half-timber \\
        --reference reference/nekoration-1.16.5/src/main/resources \\
        --output src/main/resources --write

Generate only en_us block name entries (merge into existing lang file)::

    python scripts/port_nekoration_assets.py lang --output src/main/resources/assets/nekoration/lang/en_us.json --write

Bulk string replace inside JSON under assets (e.g. after manual path tweaks)::

    python scripts/port_nekoration_assets.py rewrite-json assets/nekoration --find OLD --replace NEW --write

Migrate blockstate variant keys from legacy ``level=0..15`` to ``color=<enum>`` (matches ``DyeableBlock``)::

    python scripts/port_nekoration_assets.py migrate-color --blockstates src/main/resources/assets/nekoration/blockstates --write

Requires Python 3.10+ (stdlib only). On Windows, use ``py -3 scripts/port_nekoration_assets.py ...`` if ``python`` is not on PATH.
"""

from __future__ import annotations

import argparse
import json
import re
import shutil
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Any

MOD_ID = "nekoration"

# Must match io.devbobcorn.nekoration.blocks.HalfTimberWood
WOOD_IDS = (
    "oak",
    "spruce",
    "birch",
    "jungle",
    "acacia",
    "dark_oak",
    "mangrove",
    "cherry",
    "bamboo",
    "crimson",
    "warped",
)

WOOD_TITLE = {
    "oak": "Oak",
    "spruce": "Spruce",
    "birch": "Birch",
    "jungle": "Jungle",
    "acacia": "Acacia",
    "dark_oak": "Dark Oak",
    "mangrove": "Mangrove",
    "cherry": "Cherry",
    "bamboo": "Bamboo",
    "crimson": "Crimson",
    "warped": "Warped",
}

VERTICAL_CONNECTIONS = ("s0", "d0", "d1", "t0", "t1", "t2")

# Must match io.devbobcorn.nekoration.NekoColors.EnumNekoColor order (NBT id 0..15 → serialized name)
NEKO_COLOR_NAMES = (
    "black",
    "blue",
    "brown",
    "cyan",
    "gray",
    "green",
    "light_blue",
    "light_gray",
    "lime",
    "magenta",
    "orange",
    "pink",
    "purple",
    "red",
    "white",
    "yellow",
)

# Legacy en_us pattern names (p0..p9 then pillar p0..p2) — first %s was wood tint, second plaster; we fold wood into the id.
PANEL_LABEL_EN = (
    "%s Half Timber",
    "%s Slash Half Timber",
    "%s Backslash Half Timber",
    "%s Bi-Slash Half Timber",
    "%s Bi-Backslash Half Timber",
    "%s Center Half Timber",
    "%s Cross Half Timber",
    "%s Diamond Half Timber",
    "%s Checkered Half Timber",
    "%s Double Half Timber",
)
PILLAR_LABEL_EN = (
    "%s Half Timber Pillar",
    "%s Slash Half Timber Pillar",
    "%s Backslash Half Timber Pillar",
)


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


def _copy_file(src: Path, dst: Path, write: bool) -> None:
    if not write:
        print(f"[dry-run] copy {src} -> {dst}")
        return
    dst.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(src, dst)
    print(f"copied {src} -> {dst}")


def _panel_blockstate_model_path(wood: str, pattern_index: int) -> str:
    return f"{MOD_ID}:block/half_timber/{wood}/half_timber_{wood}_p{pattern_index}"


def _pillar_blockstate_model_path(wood: str, pattern_index: int, conn_suffix: str | None) -> str:
    base = f"{MOD_ID}:block/half_timber/{wood}/half_timber_{wood}_p{pattern_index}"
    if conn_suffix:
        return f"{base}_{conn_suffix}"
    return base


def build_panel_blockstate(wood: str, pattern_index: int) -> dict[str, Any]:
    variants = {
        f"color={NEKO_COLOR_NAMES[i]}": {"model": _panel_blockstate_model_path(wood, pattern_index)}
        for i in range(len(NEKO_COLOR_NAMES))
    }
    return {"variants": variants}


def build_pillar_blockstate(wood: str, pillar_slot: int) -> dict[str, Any]:
    """
    pillar_slot: which pillar block (0,1,2) — uses pattern p0, p1, or p2 model sets like legacy.
    """
    p = pillar_slot
    variants: dict[str, Any] = {}
    for i in range(len(NEKO_COLOR_NAMES)):
        cname = NEKO_COLOR_NAMES[i]
        for vc in VERTICAL_CONNECTIONS:
            key = f"color={cname},vertical_connection={vc}"
            if vc == "s0":
                variants[key] = {"model": _pillar_blockstate_model_path(wood, p, None)}
            else:
                variants[key] = {"model": _pillar_blockstate_model_path(wood, p, vc)}
    return {"variants": variants}


MODEL_NAME_RE = re.compile(
    r"^half_timber_(p\d+)((?:_[dt]\d)?)\.json$"
)  # half_timber_p0.json, half_timber_p0_d0.json
BASE_MODEL_RE = re.compile(r"^half_timber\.json$")


@dataclass
class PortConfig:
    reference_resources: Path
    output_resources: Path
    write: bool
    """If True, duplicate overlay textures per wood; if False, only duplicate when missing."""
    texture_dupe: bool
    """Also place back (plaster) textures under half_timber_back/<wood>/ (duplicate from legacy flat files)."""
    dupe_back_textures: bool


def port_half_timber(cfg: PortConfig) -> None:
    ref_assets = _assets_root(cfg.reference_resources)
    out_assets = _assets_root(cfg.output_resources)

    ref_ht_models = ref_assets / "models" / "block" / "half_timber"
    if not ref_ht_models.is_dir():
        print(f"error: missing reference models dir: {ref_ht_models}", file=sys.stderr)
        sys.exit(1)

    # Shared parent model (unchanged path so all children keep the same parent string)
    base_model = ref_ht_models / "half_timber.json"
    if base_model.is_file():
        _copy_file(base_model, out_assets / "models" / "block" / "half_timber" / "half_timber.json", cfg.write)

    # Optional full-column pillar geometry parent (legacy); not referenced by default blockstates.
    pillar_base = ref_ht_models / "half_timber_pillar.json"
    if pillar_base.is_file():
        ptext = pillar_base.read_text(encoding="utf-8")
        for wood in WOOD_IDS:
            dst = out_assets / "models" / "block" / "half_timber" / wood / f"half_timber_{wood}_pillar.json"
            out_body = ptext.replace(
                f"{MOD_ID}:block/half_timber/halftimber_frame_",
                f"{MOD_ID}:block/half_timber/{wood}/halftimber_frame_",
            )
            if cfg.dupe_back_textures:
                out_body = out_body.replace(
                    f"{MOD_ID}:block/half_timber_back/halftimber_frame_",
                    f"{MOD_ID}:block/half_timber_back/{wood}/halftimber_frame_",
                )
            if not cfg.write:
                print(f"[dry-run] pillar base -> {dst.relative_to(out_assets)} ({wood})")
            else:
                dst.parent.mkdir(parents=True, exist_ok=True)
                dst.write_text(out_body, encoding="utf-8")
                print(f"wrote {dst}")

    # Port each block model variant
    for src in sorted(ref_ht_models.glob("half_timber*.json")):
        if src.name == "half_timber.json":
            continue
        m = MODEL_NAME_RE.match(src.name)
        if not m:
            print(f"skip unrecognized model name: {src.name}")
            continue
        pattern_part = m.group(1)  # p0, p1, ...
        conn_suffix = m.group(2) or ""  # "", "_d0", "_t1", ...

        text = src.read_text(encoding="utf-8")
        for wood in WOOD_IDS:
            new_name = f"half_timber_{wood}_{pattern_part}{conn_suffix}.json"
            dst_dir = out_assets / "models" / "block" / "half_timber" / wood
            dst = dst_dir / new_name

            # Texture paths: wood-specific overlay folder; optional wood-specific back
            out_body = text
            out_body = out_body.replace(
                f"{MOD_ID}:block/half_timber/halftimber_frame_",
                f"{MOD_ID}:block/half_timber/{wood}/halftimber_frame_",
            )
            if cfg.dupe_back_textures:
                out_body = out_body.replace(
                    f"{MOD_ID}:block/half_timber_back/halftimber_frame_",
                    f"{MOD_ID}:block/half_timber_back/{wood}/halftimber_frame_",
                )

            if not cfg.write:
                print(f"[dry-run] model {src.name} -> {dst.relative_to(out_assets)} ({wood})")
            else:
                dst_dir.mkdir(parents=True, exist_ok=True)
                dst.write_text(out_body, encoding="utf-8")
                print(f"wrote {dst}")

    # Blockstates: panels p0..p9
    for p in range(10):
        for wood in WOOD_IDS:
            bs = build_panel_blockstate(wood, p)
            rel = Path("blockstates") / f"half_timber_{wood}_p{p}.json"
            _dump_json(out_assets / rel, bs, cfg.write)

    # Blockstates: pillars (legacy pillar_pK uses pattern pK)
    for slot in range(3):
        for wood in WOOD_IDS:
            bs = build_pillar_blockstate(wood, slot)
            rel = Path("blockstates") / f"half_timber_{wood}_pillar_p{slot}.json"
            _dump_json(out_assets / rel, bs, cfg.write)

    # Item models
    for p in range(10):
        for wood in WOOD_IDS:
            name = f"half_timber_{wood}_p{p}.json"
            data = {"parent": f"{MOD_ID}:block/half_timber/{wood}/half_timber_{wood}_p{p}"}
            _dump_json(out_assets / "models" / "item" / name, data, cfg.write)

    for slot in range(3):
        for wood in WOOD_IDS:
            name = f"half_timber_{wood}_pillar_p{slot}.json"
            data = {
                "parent": f"{MOD_ID}:block/half_timber/{wood}/half_timber_{wood}_p{slot}",
            }
            _dump_json(out_assets / "models" / "item" / name, data, cfg.write)

    # Textures: duplicate legacy flat files into per-wood folders (artists replace with real wood variants)
    ref_tex_ht = ref_assets / "textures" / "block" / "half_timber"
    ref_tex_back = ref_assets / "textures" / "block" / "half_timber_back"
    if ref_tex_ht.is_dir():
        patterns = list(ref_tex_ht.glob("*.png")) + list(ref_tex_ht.glob("*.mcmeta"))
        for wood in WOOD_IDS:
            dst_ht = out_assets / "textures" / "block" / "half_timber" / wood
            for f in patterns:
                target = dst_ht / f.name
                if target.exists() and not cfg.texture_dupe:
                    continue
                _copy_file(f, target, cfg.write)
    else:
        print(f"note: no reference textures at {ref_tex_ht} (skip texture copy)")

    if cfg.dupe_back_textures and ref_tex_back.is_dir():
        backs = list(ref_tex_back.glob("*.png")) + list(ref_tex_back.glob("*.mcmeta"))
        for wood in WOOD_IDS:
            dst_b = out_assets / "textures" / "block" / "half_timber_back" / wood
            for f in backs:
                _copy_file(f, dst_b, cfg.write)


def generate_lang_entries() -> dict[str, str]:
    out: dict[str, str] = {}
    for wood in WOOD_IDS:
        title = WOOD_TITLE[wood]
        for p in range(10):
            key = f"block.{MOD_ID}.half_timber_{wood}_p{p}"
            out[key] = PANEL_LABEL_EN[p] % title
        for slot in range(3):
            key = f"block.{MOD_ID}.half_timber_{wood}_pillar_p{slot}"
            out[key] = PILLAR_LABEL_EN[slot] % title
    return out


def cmd_lang(args: argparse.Namespace) -> None:
    path = Path(args.output)
    write = bool(args.write)
    new_entries = generate_lang_entries()
    if path.is_file():
        existing = json.loads(path.read_text(encoding="utf-8"))
        if not isinstance(existing, dict):
            print("error: lang file must be a JSON object", file=sys.stderr)
            sys.exit(1)
        merged = dict(existing)
        merged.update(new_entries)
        data = merged
    else:
        data = new_entries
    _dump_json(path, dict(sorted(data.items())), write)


def cmd_migrate_level_to_color(args: argparse.Namespace) -> None:
    """Rewrite variant keys level=N → color=<NEKO_COLOR_NAMES[N]> in blockstate JSON files."""
    blockstates = Path(args.blockstates)
    write = bool(args.write)
    if not blockstates.is_dir():
        print(f"error: not a directory: {blockstates}", file=sys.stderr)
        sys.exit(1)

    def repl(match: re.Match[str]) -> str:
        idx = int(match.group(1))
        if not 0 <= idx < len(NEKO_COLOR_NAMES):
            raise ValueError(f"level index out of range: {idx}")
        return f"color={NEKO_COLOR_NAMES[idx]}"

    pattern = re.compile(r"level=(\d+)")
    for path in sorted(blockstates.glob("*.json")):
        text = path.read_text(encoding="utf-8")
        if "level=" not in text:
            continue
        try:
            new_text = pattern.sub(repl, text)
        except ValueError as e:
            print(f"error in {path}: {e}", file=sys.stderr)
            sys.exit(1)
        if new_text == text:
            continue
        if not write:
            print(f"[dry-run] migrate {path.name}")
        else:
            path.write_text(new_text, encoding="utf-8")
            print(f"migrated {path}")


def cmd_rewrite_json(args: argparse.Namespace) -> None:
    root = Path(args.root)
    find = args.find
    replace = args.replace
    write = bool(args.write)
    if not root.is_dir():
        print(f"error: not a directory: {root}", file=sys.stderr)
        sys.exit(1)
    for path in sorted(root.rglob("*.json")):
        text = path.read_text(encoding="utf-8")
        if find not in text:
            continue
        new_text = text.replace(find, replace)
        if not write:
            print(f"[dry-run] rewrite {path}")
        else:
            path.write_text(new_text, encoding="utf-8")
            print(f"rewrote {path}")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = parser.add_subparsers(dest="command", required=True)

    p_ht = sub.add_parser("half-timber", help="Port half-timber models, blockstates, items, textures")
    p_ht.add_argument("--reference", type=Path, required=True, help="Legacy src/main/resources root")
    p_ht.add_argument("--output", type=Path, required=True, help="Target src/main/resources root")
    p_ht.add_argument("--write", action="store_true", help="Apply changes (default is dry-run)")
    p_ht.add_argument(
        "--texture-dupe",
        action="store_true",
        help="Overwrite per-wood textures even if destination already exists",
    )
    p_ht.add_argument(
        "--dupe-back-textures",
        action="store_true",
        help="Also emit half_timber_back/<wood>/ copies and rewrite model side paths to match",
    )

    p_lang = sub.add_parser("lang", help="Emit / merge en_us block translation keys for half-timber ids")
    p_lang.add_argument(
        "--output",
        type=Path,
        required=True,
        help="Path to en_us.json (merged if file exists)",
    )
    p_lang.add_argument("--write", action="store_true")

    p_rw = sub.add_parser("rewrite-json", help="Replace a literal string in all JSON under a folder")
    p_rw.add_argument("root", type=Path, help="Folder to scan recursively")
    p_rw.add_argument("--find", required=True)
    p_rw.add_argument("--replace", required=True)
    p_rw.add_argument("--write", action="store_true")

    p_mig = sub.add_parser(
        "migrate-color",
        help="In blockstates/*.json, replace level=0..15 variant keys with color=<nekocolor> (DyeableBlock)",
    )
    p_mig.add_argument(
        "--blockstates",
        type=Path,
        required=True,
        help="Directory containing blockstate JSON (e.g. assets/nekoration/blockstates)",
    )
    p_mig.add_argument("--write", action="store_true")

    args = parser.parse_args()

    if args.command == "half-timber":
        cfg = PortConfig(
            reference_resources=args.reference,
            output_resources=args.output,
            write=bool(args.write),
            texture_dupe=bool(args.texture_dupe),
            dupe_back_textures=bool(args.dupe_back_textures),
        )
        port_half_timber(cfg)
        if not cfg.write:
            print("\nDry run only. Re-run with --write to create files.")
    elif args.command == "lang":
        cmd_lang(args)
        if not args.write:
            print("\nDry run only. Re-run with --write.")
    elif args.command == "rewrite-json":
        cmd_rewrite_json(args)
        if not args.write:
            print("\nDry run only. Re-run with --write.")
    elif args.command == "migrate-color":
        cmd_migrate_level_to_color(args)
        if not args.write:
            print("\nDry run only. Re-run with --write.")


if __name__ == "__main__":
    main()
