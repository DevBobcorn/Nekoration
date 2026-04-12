"""
Tint template PNGs per wood type (multiply blend) for half-timber and window textures.

Templates live under ``scripts/generator_files/``; output goes under
``src/main/resources/assets/nekoration/textures/block/`` (per-wood subfolders).
"""

import glob
import os

from PIL import Image, ImageChops


# (template_dir_relative_to_script, output_dir_relative_to_script — per-wood subfolders created under output)
TEXTURE_JOBS = (
    ("./generator_files/half_timber_template/", "../src/main/resources/assets/nekoration/textures/block/half_timber/"),
    ("./generator_files/window_template/", "../src/main/resources/assets/nekoration/textures/block/window/"),
)

wood_type_colors = {
    "oak": 0xB9955B,
    "spruce": 0x886541,
    "birch": 0xE8D699,
    "jungle": 0xB38564,
    "acacia": 0xB4653A,
    "dark_oak": 0x5C3C1B,
    "mangrove": 0x753630,
    "cherry": 0xE2B2AC,
    "bamboo": 0xC1AD50,
    "crimson": 0x873468,
    "warped": 0x389A99,
}


def tint_rgba_multiply(img: Image.Image, rgb: int) -> Image.Image:
    """Multiply RGB by tint; alpha unchanged (Minecraft-style plank tint)."""
    img = img.convert("RGBA")
    r = (rgb >> 16) & 0xFF
    g = (rgb >> 8) & 0xFF
    b = rgb & 0xFF
    c_r, c_g, c_b, c_a = img.split()
    tint_r = Image.new("L", img.size, r)
    tint_g = Image.new("L", img.size, g)
    tint_b = Image.new("L", img.size, b)
    out_r = ImageChops.multiply(c_r, tint_r)
    out_g = ImageChops.multiply(c_g, tint_g)
    out_b = ImageChops.multiply(c_b, tint_b)
    return Image.merge("RGBA", (out_r, out_g, out_b, c_a))


def main():
    # Set cwd to file directory
    script_dir = os.path.dirname(os.path.abspath(__file__))
    os.chdir(script_dir)

    for source, target in TEXTURE_JOBS:
        pattern = os.path.join(source, "*.png")
        matches = glob.glob(pattern)
        if not matches:
            print(f"Skip (no PNGs): {source}")
            continue
        print(f"Source: {source} -> {target}")
        for file_path in matches:
            org_name = os.path.basename(file_path)
            print(f"  Processing: {org_name}")
            with Image.open(file_path) as src:
                for wood_type, color in wood_type_colors.items():
                    out_dir = os.path.join(target, wood_type)
                    os.makedirs(out_dir, exist_ok=True)
                    out_path = os.path.join(out_dir, org_name)
                    tinted = tint_rgba_multiply(src, color)
                    tinted.save(out_path)


if __name__ == "__main__":
    main()
