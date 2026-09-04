This note documents the data mapping between the original version(v1) of Nekoration and the remaster version(v2). Always check under `reference/nekoration-1.19` directory to see the exact original implementation when there's anything unclear.

All Identifiers(Ids or Resource Locations) in this note should all be prefixed with mod namespace `nekoration:`, which is omitted below.

## Block Connections

The serialized values of the `horizontal_connection` and `vertical_connection` block properties are unchanged between v1 and v2. Preserve these properties when the upgraded block supports the same connection direction. The values describe the block's position in a connected run as follows:

|Value|Horizontal Connection|Vertical Connection|
|-----|---------------------|-------------------|
|s0|Single block|Single block|
|d0|Left block of a pair; connected on the right|Bottom block of a pair; connected above|
|d1|Right block of a pair; connected on the left|Top block of a pair; connected below|
|t0|Left end of a run; connected on the right|Bottom end of a run; connected above|
|t1|Middle of a run; connected on both sides|Middle of a run; connected above and below|
|t2|Right end of a run; connected on the left|Top end of a run; connected below|

Unlike v1, v2 determines connections from neighbors on both sides instead of using the configured horizontal or vertical placement order. If connection states are recalculated while upgrading, assign the values in the table from the complete connected run. A run of more than three blocks uses `t0` for its first block, `t2` for its last block and `t1` for every block between them.

### Frame Blocks (Window Frame Blocks in v1)

The dyeable Window Frame Blocks from v1 are Cement Frame Blocks in v2. Convert their `level` property to the v2 `color` property using the [Colors](#colors) table, and preserve `facing`.

|v1 Block Id|v2 Block Id|Connection Data|
|-----------|-----------|---------------|
|window_top|cement_frame_peak|Preserve `horizontal_connection`|
|window_sill|cement_frame_sill|Preserve `horizontal_connection`|
|window_frame with `frame_part=top`|cement_frame_head|Remove `frame_part`, `left` and `right`; recalculate `horizontal_connection` from adjacent converted frame-head blocks|
|window_frame with `frame_part=middle`|cement_frame_side|Remove `frame_part`, `left` and `right`; set `frame_connection` as described below|
|window_frame with `frame_part=bottom`|cement_frame_sill|Remove `frame_part`, `left` and `right`; recalculate `horizontal_connection` from adjacent converted frame-sill blocks|

For a v1 `window_frame` with `frame_part=middle`, convert the two Boolean edge properties to the v2 `frame_connection` property. The left/right names are reversed because the v1 properties named the rendered edge while the v2 values name the side to which the frame connects.

|v1 `left`|v1 `right`|v2 `frame_connection`|
|---------|----------|---------------------|
|true|false|right|
|false|true|left|
|true|true|both|
|false|false|both|

The v1 `frame_part` property is represented by the selected v2 block id and should then be discarded. V2 frame-side blocks occupy the full block height. The `false`/`false` combination has no direct v2 equivalent, so use the default `both` state.

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

## Cement Blocks (known as Stone Blocks in v1)

Stone Blocks in v1 are reimplemented as Cement Blocks. Note that in v1 the dye color is stored in the `level` block property of a blockstate, while in v2 they're moved to a block property called `color` as `EnumNekoColor` instead of a number(still serialized as a number though).

Not to be confused with Stone Blocks in v2, those are not relavant to Cement Blocks.

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

Wooden Blocks also have a massive change of block ids and block properties. In v2, the Wood Type is no longer treated as dye color and is no longer distinguished by block property `level`, but instead by a part of their block ids. A few exceptions are `table`, `round_table` and `chair` blocks, which already use separate ids for different Wood Types in v1.

When upgrading data for all Wooden Blocks with `level` property, take the `level` value from the old block data and use this lookup table to determine the wood_type_id(Some `level` values map to a same wood_type_id):

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

Half-Timber Blocks additionally used to have a secondary color block property which is stored as a number in `age` property in v1, and moved to `color` property and stored as `EnumNekoColor`(similar to Cement Blocks) in v2.

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
|{Wood Type} Arm Chair|arm_chair|{Wood Type} Armchair|{wood_type_id}_armchair|
|{Wood Type} Bench|bench|{Wood Type} Bench|{wood_type_id}_bench|
|{Wood Type} Drawer|drawer|{Wood Type} Drawer|{wood_type_id}_drawer|
|{Wood Type} Cabinet|cabinet|{Wood Type} Cabinet|{wood_type_id}_cabinet|
|{Wood Type} Chest of Drawers|drawer_chest|{Wood Type} Drawer Chest|{wood_type_id}_drawer_chest|
|{Wood Type} Cupboard|cupboard|{Wood Type} Cupboard|{wood_type_id}_cupboard|
|{Wood Type} Shelf|shelf|{Wood Type} Cupboard|{wood_type_id}_cupboard|
|{Wood Type} Wall Shelf|wall_shelf|{Wood Type} Wall Shelf|{wood_type_id}_wall_shelf|

## Lamp Posts, Candle Holders and Flower Baskets

In v2, the material part(`iron`, `gold` or `quartz`) of these block ids moved from a suffix to a prefix. The item ids follow their block ids in both versions.

|Old Name|Old Id|New Name|New Id|
|--------|------|--------|------|
|Iron Lamp Post|lamp_post_iron|Iron Lamp Post|iron_lamp_post|
|Gold Lamp Post|lamp_post_gold|Gold Lamp Post|gold_lamp_post|
|Quartz Lamp Post|lamp_post_quartz|Quartz Lamp Post|quartz_lamp_post|
|Iron Candle Holder|candle_holder_iron|Iron Candle Holder|iron_candle_holder|
|Gold Candle Holder|candle_holder_gold|Gold Candle Holder|gold_candle_holder|
|Quartz Candle Holder|candle_holder_quartz|Quartz Candle Holder|quartz_candle_holder|
|Hanging Plants|flower_basket_iron|Hanging Plants|iron_flower_basket|
|Hanging Plants|flower_basket_gold|Hanging Plants|gold_flower_basket|
|Hanging Plants|flower_basket_quartz|Hanging Plants|quartz_flower_basket|

All block properties are carried over between v1 and v2, with two exceptions on Candle Holders:

- They store their dye color in the `level` block property in v1, while v2 uses the `color` property as `EnumNekoColor`(like Cement Blocks). Convert their `level` property to the v2 `color` property using the [Colors](#colors) table.
- Their flame level is stored in the `age` integer property in v1, while v2 uses a dedicated `flame` enum property. Convert their `age` value as follows:

|v1 `age`|v2 `flame`|
|--------|----------|
|0|none|
|1|flame|
|2|soul_flame|
|3|firework|

All properties of Lamp Posts are unchanged.

## Awnings

In v2, the word `short` moved from a suffix to a prefix of the Short Awning block ids. The item ids follow their block ids in both versions. Full Awning block ids are unchanged.

|Old Name|Old Id|New Name|New Id|
|--------|------|--------|------|
|{Color} Awning|awning_pure|{Color} Awning|awning_pure|
|{Color} Stripe Awning|awning_stripe|{Color} Stripe Awning|awning_stripe|
|Short {Color} Awning|awning_pure_short|Short {Color} Awning|short_awning_pure|
|Short {Color} Stripe Awning|awning_stripe_short|Short {Color} Stripe Awning|short_awning_stripe|

All block properties are carried over between v1 and v2, with one exception:

- They store their dye color in the `level` block property in v1, while v2 uses the `color` property as `EnumNekoColor`(like Cement Blocks). Convert their `level` property to the v2 `color` property using the [Colors](#colors) table.

The `facing` property is preserved on all Awning blocks, and the `bottom` property(end cap) is preserved on full Awning blocks. Short Awning blocks have no `bottom` property in either version.

## Quartz Doors

Doors in v1 used numbered ids; in v2 they are named after their quartz pattern, with the word `tall` moving from a suffix to a prefix. The item ids follow their block ids in both versions.

|Old Name|Old Id|New Name|New Id|
|--------|------|--------|------|
|Quartz Door|door_1|Quartz Door|quartz_door|
|Chiseled Quartz Door|door_2|Chiseled Quartz Door|chiseled_quartz_door|
|Quartz Bricks Door|door_3|Quartz Bricks Door|quartz_bricks_door|
|Tall Quartz Door|door_tall_1|Tall Quartz Door|tall_quartz_door|
|Tall Chiseled Quartz Door|door_tall_2|Tall Chiseled Quartz Door|tall_chiseled_quartz_door|
|Tall Quartz Bricks Door|door_tall_3|Tall Quartz Bricks Door|tall_quartz_bricks_door|

All doors are dyeable in both versions, and every door block keeps its own dye color(two halves for regular doors, three segments for tall doors), so convert each block's color separately:

- They store their dye color in the `level` block property in v1, while v2 uses the `color` property as `EnumNekoColor`(like Cement Blocks). Convert their `level` property to the v2 `color` property using the [Colors](#colors) table.
- The `facing`, `hinge`, `open` and `powered` properties are carried over between v1 and v2 on all doors.

Tall Doors changed from two block positions to three:

- In v1 a Tall Door occupies two blocks(rendered three blocks tall by a 32-high model on the upper half) and uses the vanilla `half` property. In v2 it occupies three blocks distinguished by the `segment` property(`lower`, `middle`, `upper`). The v2 `half` property is redundant and kept in sync(`lower` for the lower segment, `upper` for the other two); derive it from `segment` instead of copying it from v1 data.
- When upgrading, the v1 lower block becomes the v2 `segment=lower` block at the same position and keeps its dye color. The v1 upper block becomes the v2 `segment=middle` block at the same position, and a new `segment=upper` block is placed one above it; both take the v1 upper block's dye color, since the v1 upper half rendered both sections.
- The block above a v1 Tall Door's upper half must be empty or replaceable for the new upper segment.

Door item stacks only need their id renamed: v1 door items carried no dye color data. In v2, breaking a door drops an item that remembers the dye color of the lower half / segment it was broken from, and placing it restores that color.
