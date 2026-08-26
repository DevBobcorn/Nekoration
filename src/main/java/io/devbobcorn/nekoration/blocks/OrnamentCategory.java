package io.devbobcorn.nekoration.blocks;

/**
 * Category of ornament items for the Ornaments creative tab sub-filters.
 */
public enum OrnamentCategory {
    AWNING,
    EASEL_MENU,
    FURNITURE,
    CONTAINER,
    MISC;

    public String id() {
        return name().toLowerCase();
    }

    /** {@code decortype.nekoration.<id>} */
    public String descriptionId() {
        return "decortype.nekoration." + id();
    }
}
