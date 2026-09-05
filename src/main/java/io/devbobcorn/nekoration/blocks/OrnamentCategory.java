package io.devbobcorn.nekoration.blocks;

/**
 * Category of ornament items for the Ornaments creative tab sub-filters.
 */
public enum OrnamentCategory {
    POTS_AND_PLANTERS,
    WINDOW_ATTACHMENT,
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
