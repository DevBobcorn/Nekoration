This note documents the data mapping between the original version(v1) of Nekoration and the remaster version(v2). Always check under `reference/nekoration-1.19` directory to see the exact original implementation when there's anything unclear.

All Identifiers(Ids or Resource Locations) in this note should all be prefixed with mod namespace `nekoration:`, which is omitted below.

## Colors
The ordinal of dye color enum has changed from v1 to v2. This is used in block properties:

|Color|Old Index|New Index|
|--------|------|--------|
|Black|0|3|
|Blue|1|12|
|Brown|2|4|
|Cyan|3|10|
|Gray|4|2|
|Green|5|9|
|Light Blue|6|11|
|Light Gray|7|1|
|Lime|8|8|
|Magenta|9|14|
|Orange|10|6|
|Pink|11|15|
|Purple|12|13|
|Red|13|5|
|White|14|0|
|Yellow|15|7|

## Cement Blocks (known as Stone Blocks in old version)

Stone Blocks in v1 are rebranded as Cement Blocks. Note that in v1 the dye color is stored in the `level` block property of a blockstate, while in v2 they're moved to a block property called `color` as `EnumNekoColor` instead of a number(still serialized as a number though).

Not to be confused with Stone Blocks in v2, those are not relavant to Stone Blocks.

When upgrading the data, turn `stone_frame`, `stone_frame_bottom`, `stone_pillar`, `stone_doric`, `stone_ionic`, `stone_corinthian` and `stone_pillar_bottom` all into `paneled_cement`. For these blocks whose id ends with `_bottom` from v1 data, set their `vertical_connection` property set to `s0` in new block data.

Block id mapping:

|Old Name|Old Id|New Name|New Id|
|--------|------|--------|------|
|{Color} Stone Base|stone_base|{Color} Cement|cement|
|{Color} Stone Base Bottom|stone_base_bottom|{Color} Trimmed Cement|trimmed_cement|
|{Color} Stone Frame|stone_frame|{Color} Paneled Cement|paneled_cement|
|{Color} Stone Frame Bottom|stone_frame_bottom|{Color} Paneled Cement|paneled_cement|
|{Color} Stone Pillar|stone_pillar|{Color} Paneled Cement|paneled_cement|
|{Color} Doric Stone Pillar|stone_doric|{Color} Paneled Cement|paneled_cement|
|{Color} Ionic Stone Pillar|stone_ionic|{Color} Paneled Cement|paneled_cement|
|{Color} Corinthian Stone Pillar|stone_corinthian|{Color} Paneled Cement|paneled_cement|
|{Color} Stone Pillar Bottom|stone_pillar_bottom|{Color} Paneled Cement|paneled_cement|
|{Color} Layered Stone|stone_layered|{Color} Layered Cement|layered_cement|

## Wooden Blocks

Wooden Blocks also have a massive change of block ids and block properties. In v2, the wood type is no longer treated as dye color and is no longer distinguished by block property `level`, but instead by a part of their block ids. A few exceptions are `table`, `round_table` and `chair` blocks, which already use separate ids for different Wood Types in v1.

When upgrading data for all Wooden Blocks, take the `level` property from the old block data and use this lookup table to determine the wood_type_id(Some `level` values share a same wood_type_id):

|Dye Color|`level` Property Value|wood_type_id|
|---------|----------------------|---------|
|Black|0|dark_oak|
|Blue|1|warped|
|Brown|2|spruce|
|Cyan|3|warped|
|Gray|4|jungle|
|Green|5|warped|
|Light Blue|6|warped|
|Light Gray|7|oak|
|Lime|8|warped|
|Magenta|9|crimson|
|Orange|10|acacia|
|Pink|11|cherry|
|Purple|12|mangrove|
|Red|13|mangrove|
|White|14|birch|
|Yellow|15|birch|

Half-Timber Blocks additionally have a secondary color block property which is stored as a number in `age` property in v1, and moved to `color` property and stored as `EnumNekoColor`(similar to Cement Blocks).

When upgrading `half_timber_pillar_p0`, `half_timber_pillar_p1` and `half_timber_pillar_p2` to the new version, turn them into `{wood_type_id}_half_timber_p0`, `{wood_type_id}_half_timber_p1` and `{wood_type_id}_half_timber_p2` respectively, and preserve the `vertical_connection` block property. `half_timber_p0`, `half_timber_p1` and `half_timber_p2` from old data will also be turned into `{wood_type_id}_half_timber_p0`, `{wood_type_id}_half_timber_p1` and `{wood_type_id}_half_timber_p2`, and have their `vertical_connection` property set to `s0`.

`shelf` from v1 should be turned into `{wood_type_id}_cupboard` in v2, the content items should be kept.

`easel_menu` and `easel_menu_white` are merged and split(by Wood Type) into `{wood_type_id}_easel_menu`, with 16 dye colors distinguished by the `color` block property.

Block id mapping:

|Old Name|Old Id|New Name|New Id|
|--------|------|--------|------|
|{Wood Type} {Color} Half Timber|half_timber_p0|{Color} {Wood Type} Half-Timber|{wood_type_id}_half_timber_p0|
|{Wood Type} Slash {Color} Half Timber|half_timber_p1|{Color} Bend Sinister {Wood Type} Half-Timber|{wood_type_id}_half_timber_p1|
|...|...|...|...|
|{Wood Type} Double {Color} Half Timber|half_timber_p9|{Color} Double {Wood Type} Half-Timber|{wood_type_id}_half_timber_p9|
|{Wood Type} {Color} Half Timber Pillar|half_timber_pillar_p0|{Color} {Wood Type} Half-Timber|{wood_type_id}_half_timber_p0|
|{Wood Type} Slash {Color} Half Timber Pillar|half_timber_pillar_p1|{Color} Bend Sinister {Wood Type} Half-Timber|{wood_type_id}_half_timber_p1|
|...|...|...|...|
|{Wood Type} Simple Window|window_simple|Simple {Wood Type} Window|{wood_type_id}_window_simple|
|...|...|...|...|
|{Wood Type} Easel Menu|easel_menu|{Color} {Wood Type} Easel Menu|{wood_type_id}_easel_menu|
|{Wood Type} White Easel Menu|easel_menu_white|{Color} {Wood Type} Easel Menu|{wood_type_id}_easel_menu|
|{Wood Type} Table|{wood_type_id}_table|{Wood Type} Table|{wood_type_id}_table|
|{Wood Type} Round Table|{wood_type_id}_round_table|{Wood Type} Round Table|{wood_type_id}_round_table|
|{Wood Type} Chair|{wood_type_id}_chair|{Wood Type} Chair|{wood_type_id}_chair|
|{Wood Type} Glass Table|glass_table|{Wood Type} Table|{wood_type_id}_glass_table|
|{Wood Type} Round Glass Table|glass_round_table|{Wood Type} Round Glass Table|{wood_type_id}_round_glass_table|
|{Wood Type} Arm Chair|arm_chair|{Wood Type} Arm Chair|{wood_type_id}_arm_chair|
|{Wood Type} Bench|bench|{Wood Type} Bench|{wood_type_id}_bench|
|{Wood Type} Drawer|drawer|{Wood Type} Drawer|{wood_type_id}_drawer|
|{Wood Type} Cabinet|cabinet|{Wood Type} Cabinet|{wood_type_id}_cabinet|
|{Wood Type} Chest of Drawers|drawer_chest|{Wood Type} Drawer Chest|{wood_type_id}_drawer_chest|
|{Wood Type} Cupboard|cupboard|{Wood Type} Cupboard|{wood_type_id}_cupboard|
|{Wood Type} Shelf|shelf|{Wood Type} Cupboard|{wood_type_id}_cupboard|
|{Wood Type} Wall Shelf|wall_shelf|{Wood Type} Wall Shelf|{wood_type_id}_wall_shelf|
