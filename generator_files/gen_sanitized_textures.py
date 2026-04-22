import cv2
import numpy as np
import os
from sklearn.neighbors import NearestNeighbors

def limit_colors_to_palette(image_dir, palette_path, output_dir):
    # 1. Load and prepare the palette
    palette_img = cv2.imread(palette_path, cv2.IMREAD_UNCHANGED)
    if palette_img is None:
        print("Error: Palette image not found.")
        return

    # Handle palette with or without alpha channel
    if palette_img.shape[2] == 4:
        palette_colors = palette_img[:, :, :3].reshape(-1, 3)
    else:
        palette_colors = palette_img.reshape(-1, 3)

    # Initialize KNN to find the single closest color
    nn = NearestNeighbors(n_neighbors=1, algorithm='ball_tree').fit(palette_colors)

    # 2. Prepare output directory
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    # 3. Process images
    valid_extensions = ('.png', '.jpg', '.jpeg', '.bmp')
    for filename in os.listdir(image_dir):
        if filename.lower().endswith(valid_extensions):
            img_path = os.path.join(image_dir, filename)
            img = cv2.imread(img_path, cv2.IMREAD_UNCHANGED)

            if img is None:
                continue

            # Ensure image has alpha channel
            if img.shape[2] == 3:
                # Add full alpha if missing
                alpha_channel = np.full((img.shape[0], img.shape[1], 1), 255, dtype=np.uint8)
                img = np.concatenate([img, alpha_channel], axis=2)

            h, w, c = img.shape
            pixels = img.reshape(-1, 4)

            # Prepare output pixel array
            output_pixels = np.zeros_like(pixels)

            # Mask for alpha < 200
            alpha_mask = pixels[:, 3] < 200
            # Set fully transparent pixels
            output_pixels[alpha_mask] = [0, 0, 0, 0]

            # Mask for alpha >= 200
            opaque_mask = ~alpha_mask
            if np.any(opaque_mask):
                # Only process opaque pixels
                opaque_pixels = pixels[opaque_mask][:, :3]
                distances, indices = nn.kneighbors(opaque_pixels)
                mapped_colors = palette_colors[indices.flatten()]
                # Set mapped color with full alpha
                output_pixels[opaque_mask, :3] = mapped_colors
                output_pixels[opaque_mask, 3] = 255

            # Reshape back to image
            output_img = output_pixels.reshape(h, w, 4)

            # Save as PNG to preserve alpha
            cv2.imwrite(os.path.join(output_dir, filename), output_img)
            print(f"Processed: {filename}")


# Get absolute path of the script and its directory
script_dir = os.path.dirname(os.path.abspath(__file__))

# Change CWD to script's directory
os.chdir(script_dir)


# --- Configuration ---
IMAGE_DIRECTORY = 'container_template_raw'  # Folder containing your images
PALETTE_IMAGE = 'plank_palettes/grayscale.png'      # Your Nx1 palette image
OUTPUT_DIRECTORY = 'container_template'        # Where to save the results

limit_colors_to_palette(IMAGE_DIRECTORY, PALETTE_IMAGE, OUTPUT_DIRECTORY)
