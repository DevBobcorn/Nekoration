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

### Window Plants

The `window_plant` block id is unchanged between v1 and v2. Convert its `level` property to the v2 `color` property using the [Colors](#colors) table, and preserve `horizontal_connection` and `facing`.

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

The same table applies to the `color` NBT byte tag of dyeable item stacks (see [Item Stacks](#item-stacks)) and to the v1 `age` property of Half-Timber Blocks, since v1 stored dye colors with one shared ordinal everywhere.

## Cement Blocks (known as Stone Blocks in v1)

Stone Blocks in v1 are reimplemented as Cement Blocks. Note that in v1 the dye color is stored in the `level` block property of a blockstate, while in v2 they're moved to a block property called `color` as `EnumNekoColor` instead of a number(still serialized as a number though).

Not to be confused with Stone Blocks in v2, those are not relavant to Cement Blocks.

When upgrading the data, turn `stone_frame`, `stone_frame_bottom`, `stone_pillar`, `stone_doric`, `stone_ionic`, `stone_corinthian` and `stone_pillar_bottom` all into `paneled_cement`. For these blocks whose id ends with `_bottom` from v1 data, set their `vertical_connection` property set to `s0` in new block data.

`stone_base` becomes `cement`, which is also vertically connected, so preserve its `vertical_connection` property. `stone_base_bottom` becomes the non-connected `trimmed_cement`, so drop its connection data. The v1 pots have no properties besides `level`.

**Beware of id collisions:** v2 also registers new, non-dyeable natural stone blocks named `stone_base`, `stone_pot` and `stone_planter` (per-stone variants of the new Stone Blocks family). These are unrelated to the v1 blocks of the same names; always remap the v1 ids to the Cement Block ids instead of keeping them.

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
|{Color} Stone Pot|stone_pot|{Color} Cement Pot|cement_pot|
|{Color} Stone Planter|stone_planter|{Color} Cement Planter|cement_planter|

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

Half-Timber Blocks additionally used to have a secondary color block property which is stored as a number in `age` property in v1, and moved to `color` property and stored as `EnumNekoColor`(similar to Cement Blocks) in v2. The `age` value uses the v1 dye color ordinal, so convert it with the [Colors](#colors) table.

When upgrading `half_timber_pillar_p0`, `half_timber_pillar_p1` and `half_timber_pillar_p2` to the new version, turn them into `{wood_type_id}_half_timber_p0`, `{wood_type_id}_half_timber_p1` and `{wood_type_id}_half_timber_p2` respectively, and preserve the `vertical_connection` block property. `half_timber_p0`, `half_timber_p1` and `half_timber_p2` from old data will also be turned into `{wood_type_id}_half_timber_p0`, `{wood_type_id}_half_timber_p1` and `{wood_type_id}_half_timber_p2`, and have their `vertical_connection` property set to `s0`.

`shelf` from v1 should be turned into `{wood_type_id}_cupboard` in v2, the content items should be kept.

`easel_menu` and `easel_menu_white` are merged and split(by Wood Type) into `{wood_type_id}_easel_menu`, with 16 dye colors distinguished by the `color` block property: the regular v1 `easel_menu` had a black board, so give it `color=black`; `easel_menu_white` gets `color=white`.

The v1 Window Blocks (`window_simple`, `window_arch`, `window_cross`, `window_shade` and `window_lancet`) carry a `vertical_connection` property, but their v2 counterparts have no block properties at all; discard `vertical_connection` when upgrading them.

Other than the removed `level` property, the remaining properties of Wooden Blocks are carried over: `facing` on Chairs, Armchairs, Benches, Drawers, Cabinets, Drawer Chests, Cupboards, Shelves, Wall Shelves and Easel Menus; `open` on Drawers, Cabinets, Drawer Chests, Cupboards, Shelves and Wall Shelves; `bottom` on Cupboards and Shelves; `horizontal_connection` on Benches and Wall Shelves. Tables, Round Tables, Glass Tables and Round Glass Tables have no other properties.

Block id mapping:

|Old Name|Old Id|New Name|New Id|
|--------|------|--------|------|
|{Wood Type} {Color} Half Timber|half_timber_p0|{Color} {Wood Type} Half-Timber|{wood_type_id}_half_timber_p0|
|{Wood Type} Slash {Color} Half Timber|half_timber_p1|{Color} Bend Sinister {Wood Type} Half-Timber|{wood_type_id}_half_timber_p1|
|{Wood Type} Backslash {Color} Half Timber|half_timber_p2|{Color} Bend {Wood Type} Half-Timber|{wood_type_id}_half_timber_p2|
|{Wood Type} Bi-Slash {Color} Half Timber|half_timber_p3|{Color} Double Bend Sinister {Wood Type} Half-Timber|{wood_type_id}_half_timber_p3|
|{Wood Type} Bi-Backslash {Color} Half Timber|half_timber_p4|{Color} Double Bend {Wood Type} Half-Timber|{wood_type_id}_half_timber_p4|
|{Wood Type} Center {Color} Half Timber|half_timber_p5|{Color} Center {Wood Type} Half-Timber|{wood_type_id}_half_timber_p5|
|{Wood Type} Cross {Color} Half Timber|half_timber_p6|{Color} Roundel {Wood Type} Half-Timber|{wood_type_id}_half_timber_p6|
|{Wood Type} Diamond {Color} Half Timber|half_timber_p7|{Color} Saltire {Wood Type} Half-Timber|{wood_type_id}_half_timber_p7|
|{Wood Type} Checkered {Color} Half Timber|half_timber_p8|{Color} Pale {Wood Type} Half-Timber|{wood_type_id}_half_timber_p8|
|{Wood Type} Double {Color} Half Timber|half_timber_p9|{Color} Double {Wood Type} Half-Timber|{wood_type_id}_half_timber_p9|
|{Wood Type} {Color} Half Timber Pillar|half_timber_pillar_p0|{Color} {Wood Type} Half-Timber|{wood_type_id}_half_timber_p0|
|{Wood Type} Slash {Color} Half Timber Pillar|half_timber_pillar_p1|{Color} Bend Sinister {Wood Type} Half-Timber|{wood_type_id}_half_timber_p1|
|{Wood Type} Backslash {Color} Half Timber Pillar|half_timber_pillar_p2|{Color} Bend {Wood Type} Half-Timber|{wood_type_id}_half_timber_p2|
|{Wood Type} Simple Window|window_simple|Simple {Wood Type} Window|{wood_type_id}_window_simple|
|{Wood Type} Arch Window|window_arch|Arch {Wood Type} Window|{wood_type_id}_window_arch|
|{Wood Type} Cross Window|window_cross|Cross {Wood Type} Window|{wood_type_id}_window_cross|
|{Wood Type} Shade Window|window_shade|Shade {Wood Type} Window|{wood_type_id}_window_shade|
|{Wood Type} Lancet Window|window_lancet|Lancet {Wood Type} Window|{wood_type_id}_window_lancet|
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

## Pumpkin Furniture

`pumpkin_table` and `pumpkin_chair` keep their ids and properties(`facing` on the chair) between v1 and v2, so no conversion is needed for them.

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

## Item Stacks

Item stacks found in inventories(chests, item frames, dropped items, etc.) need their ids renamed like their blocks, and dyeable items also carry color data in NBT that must be converted. In v1 the color is a `color` byte tag on the stack, in v2 it is a `color` byte tag inside the stack's custom data component.

- Dyeable items whose v1 `color` tag holds a dye color(Cement family, Frame Blocks, Window Plants, Awning blocks, Candle Holders, Stone Pots and Planters): convert the tag value with the [Colors](#colors) table, and rename the id like the block.
- A `window_frame` item has no `frame_part` data from which to select one of the three v2 blocks, so convert it to `cement_frame_side`.
- Dyeable wooden items whose v1 `color` tag holds a wood type(Windows, Glass Tables, Armchairs, Benches, Drawers, Cabinets, Drawer Chests, Cupboards, Shelves, Wall Shelves): use the tag value with the wood type lookup table to build the new id, then drop the tag. When the tag is absent, default to `dark_oak`(index 0).
- Easel Menu items follow the wooden rule for their wood type, and additionally set the new `color` tag to `black` for `easel_menu` and `white` for `easel_menu_white`.
- Half-Timber items carry two tags: `color_0`(wood type, builds the new id) and `color_1`(dye color, becomes the new `color` tag via the [Colors](#colors) table).
- Door items and the per-wood Table, Round Table and Chair items carried no color data; only rename their ids.

## Block Entities

The block entity type ids `cabinet`, `item_display` and `easel_menu` are unchanged between v1 and v2, so the saved block entity data(container contents, easel menu text and items) survives as long as the upgraded block at that position still belongs to the same entity type:

- `cabinet`: v1 Drawers, Cabinets and Drawer Chests become their `{wood_type_id}` counterparts, which are all still `cabinet` entity blocks.
- `item_display`: v1 Cupboards, Shelves and Wall Shelves become `{wood_type_id}` Cupboards(v1 Shelves included, since `{wood_type_id}_cupboard` is an `item_display` entity block) and Wall Shelves.
- `easel_menu`: v1 Easel Menus become `{wood_type_id}` Easel Menus.

The v1 `phonograph`, `custom` and `prismap_table` block entity types have no v2 counterpart yet(see below).

## Not Yet Ported

The following v1 features have no v2 implementation yet, so their data cannot be upgraded and would be lost when loading an old save; the auto upgrader should skip them(and keep a report) until they are ported:

- Phonograph: the `phonograph` block and its block entity.
- Custom Block: the `custom` block, its block entity and the itemless `dream_was_taken` block.
- Prismap Table: the `prismap_table` block and its block entity.
- Items: the Paw and Paw Tweak items(`paw`, `paw_up`, `paw_down`, `paw_left`, `paw_right`, `paw_near`, `paw_far`, `paw_15`, `paw_90`), `arrow_hint`, `palette`, `camera`, `painting` and `wallpaper`.
- Entity types: `painting` and `wallpaper`(v1 saves may contain saved entities of these types; only the `seat` entity type is ported, with an unchanged id).

## Won't be Ported

The following v1 features are never present in a release, and won't be implemented in v2; the auto upgrader should skip them:

- Thin Stone Blocks: `stone_bottom_thin`, `stone_pillar_thin`, `stone_doric_thin`, `stone_ionic_thin` and `stone_corinthian_thin`(dyeable; the four pillar variants also have `vertical_connection`).
