import os
from PIL import Image
import colorsys

# 15 Minecraft dye colors (excluding white) with vibrant HSL values
VIBRANT_PALETTE = {
    "orange": (0.08, 0.95, 0.80),
    "magenta": (0.83, 0.60, 0.88),
    "light_blue": (0.55, 0.60, 0.85),
    "yellow": (0.13, 0.95, 0.85),
    "lime": (0.22, 0.60, 0.85),
    "pink": (0.95, 0.90, 0.88),
    "gray": (0.0, 0.0, 0.60),
    "light_gray": (0.0, 0.0, 0.80),
    "cyan": (0.48, 0.70, 0.88),
    "purple": (0.75, 0.50, 0.88),
    "blue": (0.62, 0.90, 0.88),
    "brown": (0.04, 0.35, 0.55),
    "green": (0.33, 0.45, 0.75),
    "red": (0.0, 0.95, 0.82),
    "black": (0.0, 0.0, 0.40)
}

def recolor_texture(input_path="white.png"):
    if not os.path.exists(input_path):
        raise FileNotFoundError(f"Could not find '{input_path}' in the script directory.")

    base_img = Image.open(input_path).convert("RGBA")
    directory = os.path.dirname(os.path.abspath(input_path))

    for color_name, (h, s, target_l) in VIBRANT_PALETTE.items():
        colored_img = base_img.copy()
        pixels = colored_img.load()

        for y in range(colored_img.height):
            for x in range(colored_img.width):
                r, g, b, a = pixels[x, y]
                
                if a == 0:
                    continue

                r_norm, g_norm, b_norm = r / 255.0, g / 255.0, b / 255.0
                _, orig_l, _ = colorsys.rgb_to_hls(r_norm, g_norm, b_norm)
                
                final_l = orig_l * target_l
                new_r, new_g, new_b = colorsys.hls_to_rgb(h, final_l, s)

                pixels[x, y] = (
                    int(new_r * 255),
                    int(new_g * 255),
                    int(new_b * 255),
                    a
                )

        output_path = os.path.join(directory, f"{color_name}.png")
        colored_img.save(output_path)
        print(f"Saved: {output_path}")

# Set cwd to file directory
script_dir = os.path.dirname(os.path.abspath(__file__))
os.chdir(script_dir)

if __name__ == "__main__":
    recolor_texture()
