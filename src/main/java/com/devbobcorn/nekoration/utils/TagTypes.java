package com.devbobcorn.nekoration.utils;

import net.minecraft.nbt.*;
/**
 * Created by TGG on 21/02/2020. Just used to make the code more readable due to
 * obfuscated names Once the mapping is updated to something nicer, we can
 * remove...
 */
public class TagTypes {
    public static final byte LONG_NBT_ID = LongTag.valueOf(0).getId();
    public static final byte INT_NBT_ID = IntTag.valueOf(0).getId();
    public static final byte SHORT_NBT_ID = ShortTag.valueOf((short) 0).getId();
    public static final byte BYTE_NBT_ID = ByteTag.valueOf((byte) 0).getId();
    public static final byte FLOAT_NBT_ID = FloatTag.valueOf(0).getId();
    public static final byte DOUBLE_NBT_ID = DoubleTag.valueOf(0).getId();
    public static final byte STRING_NBT_ID = StringTag.valueOf("").getId();

    private static byte[] dummyByteArray = { 0 };
    private static int[] dummyIntArray = { 0 };
    private static long[] dummyLongArray = { 0 };
    public static final byte BYTE_ARRAY_NBT_ID = new ByteArrayTag(dummyByteArray).getId();
    public static final byte INT_ARRAY_NBT_ID = new IntArrayTag(dummyIntArray).getId();
    public static final byte LONG_ARRAY_NBT_ID = new LongArrayTag(dummyLongArray).getId();
    public static final byte LIST_NBT_ID = new ListTag().getId();
    public static final byte COMPOUND_NBT_ID = new CompoundTag().getId();
}
