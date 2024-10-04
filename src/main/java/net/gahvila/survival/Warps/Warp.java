package net.gahvila.survival.Warps;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

public class Warp {

    private String name;
    private UUID owner;
    private String ownerName;
    private Integer uses;
    private Long creationDate;
    private Location location;
    private String color;

    private Material customItem;

    public Warp(String name, UUID owner, String ownerName, Integer uses, Long creationDate, Location location, String color) {
        this.name = name;
        this.owner = owner;
        this.ownerName = ownerName;
        this.uses = uses;
        this.creationDate = creationDate;
        this.location = location;
        this.color = color;
        this.customItem = Material.DIRT;
    }

    public Warp(String name, UUID owner, String ownerName, Integer uses, Long creationDate, Location location, String color, Material customItem) {
        this.name = name;
        this.owner = owner;
        this.ownerName = ownerName;
        this.uses = uses;
        this.creationDate = creationDate;
        this.location = location;
        this.color = color;
        this.customItem = customItem;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Integer getUses() {
        return uses;
    }

    public void setUses(Integer uses) {
        this.uses = uses;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Material getCustomItem() {
        return customItem;
    }

    public void setCustomItem(Material customItem) {
        this.customItem = customItem;
    }
}