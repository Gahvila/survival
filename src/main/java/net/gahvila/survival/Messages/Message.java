package net.gahvila.survival.Messages;

public enum Message {
    NO_PERMISSION("Sinulla ei ole oikeuksia tuohon."),
    TELEPORT_NOT_POSSIBLE("<red>Et voi teleportata juuri nyt."),
    WARP_SETWARP_CANCELED("Peruit warpin luomisen.");

    private final String text;

    Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}