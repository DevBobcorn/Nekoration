from pathlib import Path

import numpy as np
from PIL import Image
from scipy.optimize import linear_sum_assignment

SOURCE_IMAGE = Path("plank_textures/grayscale_planks.png")
TARGET_IMAGE_DIR = Path("plank_textures")
OUTPUT_DIR = Path("plank_palettes")


def find_optimal_color_mapping(source_path: Path, target_path: Path) -> np.ndarray:
    """
    Finds an optimal mapping from N colors in the source image to N colors in
    the target image that minimizes the sum of squared color distances across
    all pixels. Returns the N mapped colors ordered like the source palette.
    """
    # 1. Load images and convert to RGB numpy arrays
    arr1 = np.array(Image.open(source_path).convert("RGB"), dtype=np.float64)
    arr2 = np.array(Image.open(target_path).convert("RGB"), dtype=np.float64)

    assert arr1.shape == arr2.shape, "Images must be of the same dimensions."

    # Flatten spatial dimensions: shape becomes (H * W, 3)
    pixels1 = arr1.reshape(-1, 3)
    pixels2 = arr2.reshape(-1, 3)

    # 2. Identify unique colors from the source image (N <= 10)
    unique_colors, labels1 = np.unique(pixels1, axis=0, return_inverse=True)
    N = len(unique_colors)

    # 3. The optimal mapped color for region i in the source image is simply
    # the AVERAGE color of the corresponding pixels in the target image.
    target_mean_colors = np.zeros((N, 3), dtype=np.float64)
    counts = np.zeros(N, dtype=np.int64)
    for i in range(N):
        mask = labels1 == i
        counts[i] = np.sum(mask)
        if counts[i] > 0:
            target_mean_colors[i] = np.mean(pixels2[mask], axis=0)

    # 4. Cost to map source color i to target candidate color j:
    # Cost = count_i * || mean_target_i - target_color_j ||^2
    unique_target_colors, _ = np.unique(pixels2, axis=0, return_inverse=True)
    M = len(unique_target_colors)
    cost_matrix = np.zeros((N, M), dtype=np.float64)
    for i in range(N):
        diffs = unique_target_colors - target_mean_colors[i]
        cost_matrix[i, :] = counts[i] * np.sum(diffs ** 2, axis=1)

    # 5. Solve the assignment problem using the Hungarian Algorithm
    row_ind, col_ind = linear_sum_assignment(cost_matrix)

    # 6. Build mapping dictionary and the resulting palette
    color_map = {}
    mapped_colors = np.zeros((N, 3), dtype=np.uint8)
    for src_idx, tgt_idx in zip(row_ind, col_ind):
        src_color = tuple(unique_colors[src_idx].astype(np.uint8))
        tgt_color = tuple(unique_target_colors[tgt_idx].astype(np.uint8))
        color_map[src_color] = tgt_color
        mapped_colors[src_idx] = unique_target_colors[tgt_idx]

    print(f"\nOptimal Color Mapping ({source_path.name} -> {target_path.name}):")
    for src, tgt in color_map.items():
        print(f"  RGB {src}  --->  RGB {tgt}")

    return mapped_colors


def save_palette_strip(colors: np.ndarray, output_path: Path):
    """Saves the colors as an Nx1 color stripe PNG."""
    Image.fromarray(colors.reshape(1, -1, 3)).save(output_path)


def main():
    script_dir = Path(__file__).resolve().parent
    source_path = script_dir / SOURCE_IMAGE
    target_dir = script_dir / TARGET_IMAGE_DIR
    output_dir = script_dir / OUTPUT_DIR

    if not source_path.is_file():
        raise ValueError(f"Source image not found: {source_path}")

    target_paths = sorted(
        child
        for child in target_dir.iterdir()
        if child.is_file() and child.suffix.lower() == ".png" and child != source_path
    )
    if not target_paths:
        raise ValueError(f"No target images found in directory: {target_dir}")

    output_dir.mkdir(parents=True, exist_ok=True)

    print(f"Source image: {source_path.name}")
    print(f"Target image count: {len(target_paths)}")

    for target_path in target_paths:
        colors = find_optimal_color_mapping(source_path, target_path)
        output_path = output_dir / f"{target_path.stem.removesuffix('_planks')}.png"
        save_palette_strip(colors, output_path)
        print(f"Palette saved to: {output_path} ({len(colors)}x1)")


if __name__ == "__main__":
    main()
