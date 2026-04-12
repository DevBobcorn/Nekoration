#!/usr/bin/env python3
"""
Generate Half-Timber *pillar connection* block models from flat ``p<n>`` bases.

For each wood and ``n`` in ``{0, 1, 2}``, reads::

    half_timber_<wood>_p<n>.json

with parent ``nekoration:block/half_timber/half_timber`` and writes five variants::

    half_timber_<wood>_p<n>_d0.json
    half_timber_<wood>_p<n>_d1.json
    half_timber_<wood>_p<n>_t0.json
    half_timber_<wood>_p<n>_t1.json
    half_timber_<wood>_p<n>_t2.json

Each variant uses parent ``nekoration:block/half_timber/half_timber_pillar``,
``side`` / ``overlay`` = base paths + ``_d0`` / ``_d1`` / ``_t0`` / ``_t1`` / ``_t2``,
and ``end`` / ``end_overlay`` = original base ``side`` / ``overlay`` (suffix stripped).

Examples::

    py -3 scripts/generate_half_timber_pillar_variants.py --dry-run
    py -3 scripts/generate_half_timber_pillar_variants.py --write
    py -3 scripts/generate_half_timber_pillar_variants.py --write --wood oak

Requires Python 3.10+ (stdlib only).
"""

from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path

SCRIPT_DIR = Path(__file__).resolve().parent
REPO_ROOT = SCRIPT_DIR.parent

DEFAULT_MODELS_ROOT = (
    REPO_ROOT / "src/main/resources/assets/nekoration/models/block/half_timber"
)

BASE_PARENT = "nekoration:block/half_timber/half_timber"
PILLAR_PARENT = "nekoration:block/half_timber/half_timber_pillar"

# Must match io.devbobcorn.nekoration.blocks.HalfTimberWood folder names
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

PILLAR_N = (0, 1, 2)
VARIANT_SUFFIXES = ("_d0", "_d1", "_t0", "_t1", "_t2")


def build_variant(base_side: str, base_overlay: str, suffix: str) -> dict:
    return {
        "parent": PILLAR_PARENT,
        "textures": {
            "side": base_side + suffix,
            "overlay": base_overlay + suffix,
            "end": base_side,
            "end_overlay": base_overlay,
        },
    }


def process_base(path: Path, dry_run: bool) -> int:
    """Return number of files written or that would be written."""
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as e:
        print(f"SKIP (read error) {path}: {e}", file=sys.stderr)
        return 0

    parent = data.get("parent")
    if parent != BASE_PARENT:
        print(f"SKIP (parent not {BASE_PARENT!r}) {path}", file=sys.stderr)
        return 0

    tex = data.get("textures")
    if not isinstance(tex, dict) or "side" not in tex or "overlay" not in tex:
        print(f"SKIP (missing textures.side/overlay) {path}", file=sys.stderr)
        return 0

    base_side = tex["side"]
    base_overlay = tex["overlay"]
    if not isinstance(base_side, str) or not isinstance(base_overlay, str):
        print(f"SKIP (side/overlay not strings) {path}", file=sys.stderr)
        return 0

    stem = path.stem  # e.g. half_timber_oak_p0 (block model id, not registry block id)
    count = 0
    for suffix in VARIANT_SUFFIXES:
        out_path = path.with_name(f"{stem}{suffix}.json")
        payload = build_variant(base_side, base_overlay, suffix)
        text = json.dumps(payload, indent=4) + "\n"
        if dry_run:
            print(f"would write {out_path}")
        else:
            out_path.write_text(text, encoding="utf-8")
            print(f"wrote {out_path}")
        count += 1
    return count


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--models-root",
        type=Path,
        default=DEFAULT_MODELS_ROOT,
        help="Half-timber models folder (contains per-wood subdirs)",
    )
    parser.add_argument(
        "--wood",
        action="append",
        dest="woods",
        metavar="ID",
        help="Only this wood (repeatable). Default: all woods with folders",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Print actions only",
    )
    parser.add_argument(
        "--write",
        action="store_true",
        help="Write JSON files (required to change disk)",
    )
    args = parser.parse_args()

    if not args.write and not args.dry_run:
        parser.error("pass --write to apply, or --dry-run to preview")

    dry_run = args.dry_run or not args.write
    root: Path = args.models_root.resolve()
    if not root.is_dir():
        print(f"models root not found: {root}", file=sys.stderr)
        return 1

    woods = args.woods if args.woods else list(WOOD_IDS)
    for w in woods:
        if w not in WOOD_IDS:
            print(f"unknown wood {w!r} (not in WOOD_IDS)", file=sys.stderr)
            return 1

    total = 0
    for wood in woods:
        wood_dir = root / wood
        if not wood_dir.is_dir():
            if args.woods:
                print(f"wood folder missing: {wood_dir}", file=sys.stderr)
            continue
        for n in PILLAR_N:
            base_name = f"half_timber_{wood}_p{n}.json"
            base_path = wood_dir / base_name
            if not base_path.is_file():
                continue
            total += process_base(base_path, dry_run)

    if total == 0:
        print("no base models processed (expected half_timber_<wood>_p0|p1|p2.json)", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
