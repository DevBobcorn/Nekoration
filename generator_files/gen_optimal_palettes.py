import numpy as np
from PIL import Image
from scipy.optimize import linear_sum_assignment

def find_optimal_color_mapping(img1_path: str, img2_path: str, output_path: str = "mapped_output.png"):
    """
    Finds an optimal mapping from N colors in Image 1 to N colors in Image 2
    that minimizes the sum of squared color distances across all pixels.
    """
    # 1. Load images and convert to RGB numpy arrays
    img1 = Image.open(img1_path).convert("RGB")
    img2 = Image.open(img2_path).convert("RGB")
    
    arr1 = np.array(img1, dtype=np.float64)
    arr2 = np.array(img2, dtype=np.float64)
    
    assert arr1.shape == arr2.shape, "Images must be of the same dimensions."
    
    # Flatten spatial dimensions: shape becomes (H * W, 3)
    pixels1 = arr1.reshape(-1, 3)
    pixels2 = arr2.reshape(-1, 3)
    
    # 2. Identify unique colors from Image 1 (N <= 10)
    # Using np.unique on structured views to group RGB tuples efficiently
    unique_colors, labels1 = np.unique(pixels1, axis=0, return_inverse=True)
    N = len(unique_colors)
    print(f"Found {N} distinct colors in Image 1.")
    
    # 3. Build Cost Matrix C of shape (N, N)
    # For a target candidate color c_j assigned to source color c_i:
    # We want to find the best candidate colors from Image 2.
    # The optimal mapped color for region i in Image 1 is simply the AVERAGE
    # color of the corresponding pixels in Image 2.
    
    # Compute the average target color for each of the N region masks
    target_mean_colors = np.zeros((N, 3), dtype=np.float64)
    counts = np.zeros(N, dtype=np.int64)
    
    for i in range(N):
        mask = (labels1 == i)
        counts[i] = np.sum(mask)
        if counts[i] > 0:
            target_mean_colors[i] = np.mean(pixels2[mask], axis=0)

    # If we want to select N unique target colors from Image 2's palette directly:
    unique_target_colors, _ = np.unique(pixels2, axis=0, return_inverse=True)
    M = len(unique_target_colors)
    
    # Compute cost to map source color i to target candidate color j:
    # Sum of squared errors across all pixels belonging to source color i.
    # Cost = count_i * || mean_target_i - target_color_j ||^2
    cost_matrix = np.zeros((N, M), dtype=np.float64)
    for i in range(N):
        diffs = unique_target_colors - target_mean_colors[i]
        squared_distances = np.sum(diffs ** 2, axis=1)
        cost_matrix[i, :] = counts[i] * squared_distances

    # 4. Solve the assignment problem using Hungarian Algorithm
    # If M > N, it picks the N best distinct colors from Image 2.
    row_ind, col_ind = linear_sum_assignment(cost_matrix)
    
    # 5. Build mapping dictionary and reconstructed image
    color_map = {}
    mapped_pixels = np.zeros_like(pixels1)
    
    for src_idx, tgt_idx in zip(row_ind, col_ind):
        src_color = tuple(unique_colors[src_idx].astype(np.uint8))
        tgt_color = unique_target_colors[tgt_idx].astype(np.uint8)
        color_map[src_color] = tuple(tgt_color)
        
        # Assign mapped color to pixels
        mapped_pixels[labels1 == src_idx] = tgt_color

    print("\nOptimal Color Mapping (Img1 -> Img2):")
    for src, tgt in color_map.items():
        print(f"  RGB {src}  --->  RGB {tgt}")

    # Save output image
    result_arr = mapped_pixels.reshape(arr1.shape).astype(np.uint8)
    result_img = Image.fromarray(result_arr)
    result_img.save(output_path)
    print(f"\nMapped output saved to: {output_path}")

# Example Usage
if __name__ == "__main__":
    find_optimal_color_mapping("./wool_textures/white_wool_simplified.png", "./wool_textures/pink_wool.png", "output.png")
