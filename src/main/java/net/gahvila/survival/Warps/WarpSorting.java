package net.gahvila.survival.Warps;

public enum WarpSorting {
    ALPHABETICAL("Aakkosjärjestys"),
    REVERSE_ALPHABETICAL("Käänteinen aakkosjärjestys"),
    NEWEST_WARP("Uusin luomispäivä"),
    OLDEST_WARP("Vanhin luomispäivä");

    private final String displayName;

    WarpSorting(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
