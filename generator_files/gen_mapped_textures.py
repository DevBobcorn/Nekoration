from pathlib import Path
from PIL import Image

IMAGE_SUFFIXES = {".png", ".jpg", ".jpeg", ".bmp", ".webp"}

# --- Config (no CLI needed) ---
TARGET_PALETTE_DIR = Path("plank_palettes")
SOURCE_PALETTE_FILENAME = "grayscale.png"
SOURCE_IMAGE_DIR = Path("half_timber_template")
OUTPUT_DIR = Path("../src/generated/resources/assets/nekoration/textures/block/half_timber")


def load_palette(path: Path):
    with Image.open(path) as image:
        palette = image.convert("RGBA")

    if palette.height != 1:
        raise ValueError(
            f"Palette '{path}' must be <color_count>x1. Found {palette.width}x{palette.height}."
        )

    return list(palette.getdata()), palette.width


def build_color_mapping(palette_a_path: Path, palette_b_path: Path):
    colors_a, width_a = load_palette(palette_a_path)
    colors_b, width_b = load_palette(palette_b_path)

    if width_a != width_b:
        raise ValueError(
            f"Palette sizes must match. A has {width_a} colors, B has {width_b} colors."
        )

    mapping = {}
    for index, (color_a, color_b) in enumerate(zip(colors_a, colors_b)):
        if color_a in mapping and mapping[color_a] != color_b:
            raise ValueError(
                f"Ambiguous mapping at palette index {index}: color {color_a} "
                f"was already mapped to {mapping[color_a]}, now maps to {color_b}."
            )
        mapping[color_a] = color_b

    return mapping


def load_source_image(source_path: Path):
    with Image.open(source_path) as source_image:
        source_rgba = source_image.convert("RGBA")
    return source_rgba.size, list(source_rgba.getdata())


def remap_pixels(source_pixels: list, mapping: dict):
    unmapped = set()
    mapped_pixels = []

    for pixel in source_pixels:
        # Keep fully transparent pixels unchanged and skip palette mapping.
        if pixel[3] == 0:
            mapped_pixels.append(pixel)
            continue

        if pixel not in mapping:
            unmapped.add(pixel)
            continue

        mapped_pixels.append(mapping[pixel])

    if unmapped:
        preview = ", ".join(str(c) for c in list(unmapped)[:10])
        raise ValueError(
            "Source image contains colors not present in palette A. "
            f"Unmapped color count: {len(unmapped)}. Example(s): {preview}"
        )

    return mapped_pixels


def save_mapped_image(image_size: tuple, mapped_pixels: list, output_path: Path):
    output_image = Image.new("RGBA", image_size)
    output_image.putdata(mapped_pixels)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_image.save(output_path)


def collect_images(path: Path):
    if not path.is_dir():
        raise ValueError(f"Directory does not exist: {path}")

    files = sorted(
        [
            child
            for child in path.iterdir()
            if child.is_file() and child.suffix.lower() in IMAGE_SUFFIXES
        ]
    )
    if not files:
        raise ValueError(f"No image files found in directory: {path}")

    return files


def get_source_palette(target_palette_dir: Path):
    source_palette = target_palette_dir / SOURCE_PALETTE_FILENAME
    if not source_palette.is_file():
        raise ValueError(
            f"Configured source palette not found: {source_palette}. "
            "Update SOURCE_PALETTE_FILENAME in the script config."
        )
    return source_palette


def build_output_path(output_root: Path, source_image: Path, target_palette: Path):
    target_dir = output_root / target_palette.stem
    target_dir.mkdir(parents=True, exist_ok=True)
    return target_dir / source_image.name


def main():
    script_dir = Path(__file__).resolve().parent
    target_palette_dir = script_dir / TARGET_PALETTE_DIR
    source_image_dir = script_dir / SOURCE_IMAGE_DIR
    output_dir = script_dir / OUTPUT_DIR

    target_palettes = collect_images(target_palette_dir)
    source_palette = get_source_palette(target_palette_dir)
    target_palettes = [p for p in target_palettes if p != source_palette]
    source_images = collect_images(source_image_dir)

    if not target_palettes:
        raise ValueError("No target palettes found after excluding source palette.")

    output_dir.mkdir(parents=True, exist_ok=True)

    print(f"Source palette: {source_palette.name}")
    print(f"Target palette count: {len(target_palettes)}")
    print(f"Source image count: {len(source_images)}")

    for source_image in source_images:
        image_size, source_pixels = load_source_image(source_image)
        for target_palette in target_palettes:
            mapping = build_color_mapping(source_palette, target_palette)
            mapped_pixels = remap_pixels(source_pixels, mapping)
            output_path = build_output_path(output_dir, source_image, target_palette)
            save_mapped_image(image_size, mapped_pixels, output_path)
            print(f"Mapped '{source_image.name}' with '{target_palette.name}' -> '{output_path}'")


if __name__ == "__main__":
    main()
