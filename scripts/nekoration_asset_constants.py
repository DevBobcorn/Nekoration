"""
Shared ids for Nekoration asset generators (keep in sync with Java registration).

Half-timber block/item registry paths: ``{wood}_half_timber_p0..p9``,
``{wood}_half_timber_pillar_p0..2`` (block models may still live under
``models/block/half_timber/<wood>/half_timber_<wood>_p*``).
"""

MOD_ID = "nekoration"

# io.devbobcorn.nekoration.blocks.HalfTimberWood
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

# DyeableBlock vertical connection property values
VERTICAL_CONNECTIONS = ("s0", "d0", "d1", "t0", "t1", "t2")

# io.devbobcorn.nekoration.NekoColors.EnumNekoColor serialized names
# WindowRegistration.WindowVariant id segment (<wood>_window_<variant>)
WINDOW_VARIANTS = (
    "simple",
    "arch",
    "cross",
    "shade",
    "lancet",
)

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
