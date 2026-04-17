import os
import random
from PIL import Image

def generate_varied_edge_masks(input_dir, output_dir, alpha_threshold=200, color_mode="gradient"):
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    for filename in os.listdir(input_dir):
        if filename.lower().endswith(('.png', '.webp')):
            img_path = os.path.join(input_dir, filename)
            
            with Image.open(img_path) as img:
                img = img.convert("RGBA")
                width, height = img.size
                mask = Image.new("RGBA", (width, height), (0, 0, 0, 0))
                
                alpha = img.getchannel('A')
                pixels = alpha.load()
                mask_pixels = mask.load()

                for y in range(height):
                    for x in range(width):
                        if pixels[x, y] < alpha_threshold:
                            is_edge = False
                            for dx, dy in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
                                nx, ny = x + dx, y + dy
                                if 0 <= nx < width and 0 <= ny < height:
                                    if pixels[nx, ny] >= alpha_threshold:
                                        is_edge = True
                                        break
                            
                            if is_edge:
                                if color_mode == "random":
                                    # Generates a random bright color for every edge pixel
                                    r = random.randint(0, 10) + 20
                                    mask_pixels[x, y] = (0, 0, 0, r)
                                
                                elif color_mode == "gradient":
                                    # Generates a color based on the pixel's X/Y position
                                    r = int((x / width) * 25) + 25
                                    mask_pixels[x, y] = (0, 0, 0, r)
                                
                                else:
                                    # Default back to black if no mode is specified
                                    mask_pixels[x, y] = (0, 0, 0, 50)

                output_path = os.path.join(output_dir, filename)
                mask.save(output_path)
                print(f"Processed: {filename} with {color_mode} variation.")


# Get absolute path of the script and its directory
script_dir = os.path.dirname(os.path.abspath(__file__))

# Change CWD to script's directory
os.chdir(script_dir)


# --- Setup ---
# Your small 16x16 source images folder
input_folder = "half_timber_template"   # Update this path
# Where you want the strips saved
output_folder = "half_timber_shadow" # Update this path
# Change color_mode to "random" or "gradient"
generate_varied_edge_masks(input_folder, output_folder, color_mode="random")
