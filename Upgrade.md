This note documents the data mapping between the original version(v1) of Nekoration and the remaster version(v2).
Always check under `reference/nekoration-1.19` directory to see the exact original implementation when there's anything unclear.

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

Wooden Blocks also have a massive change of block ids and block properties. In v2, the wood type is no longer treated as dye color and is no longer distinguished by block property `level`, but instead by a part of their block ids.

When upgrading data for all Wooden Blocks, take the `level` property from the old block data and use this lookup table to determine the Wood Type Id(Some `level` values share a same Wood Type Id):

|Dye Color|`level` Property Value|Wood Type Id|
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

When upgrading `half_timber_pillar_p0`, `half_timber_pillar_p1` and `half_timber_pillar_p2` to the new version, turn them into `{Wood Type Id}_half_timber_p0`, `{Wood Type Id}_half_timber_p1` and `{Wood Type Id}_half_timber_p0` respectively, and preserve the `vertical_connection` block property. `half_timber_p0`, `half_timber_p1` and `half_timber_p2` from old data will also be turned into `{Wood Type Id}_half_timber_p0`, `{Wood Type Id}_half_timber_p1` and `{Wood Type Id}_half_timber_p0`, and have their `vertical_connection` property set to `s0`.

Block id mapping:

|Old Name|Old Id|New Name|New Id|
|--------|------|--------|------|
|{WoodType} {Color} Half Timber|half_timber_p0|{Color} {WoodType} Half-Timber|{Wood Type Id}_half_timber_p0|
|{WoodType} Slash {Color} Half Timber|half_timber_p1|{Color} Bend Sinister {WoodType} Half-Timber|{Wood Type Id}_half_timber_p1|
|...|...|...|...|
|{WoodType} Double {Color} Half Timber|half_timber_p9|{Color} Double {WoodType} Half-Timber|{Wood Type Id}_half_timber_p9|
|{WoodType} {Color} Half Timber Pillar|half_timber_pillar_p0|{Color} {WoodType} Half-Timber|{Wood Type Id}_half_timber_p0|
|{WoodType} Slash {Color} Half Timber Pillar|half_timber_pillar_p1|{Color} Bend Sinister {WoodType} Half-Timber|{Wood Type Id}_half_timber_p1|
|...|...|...|...|
|{WoodType} Simple Window|window_simple|Simple {WoodType} Window|{Wood Type Id}_window_simple|
|...|...|...|...|
