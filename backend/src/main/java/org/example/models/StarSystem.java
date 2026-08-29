package org.example.models;

import java.math.BigDecimal;

public class StarSystem
{
    private int ID;
    private String name, starType, description, ownerUsername;
    private BigDecimal starMass, distanceFromEarth;
    private boolean isPublicSystem;

    public StarSystem() { }

    public StarSystem(int ID, String name, String starType, BigDecimal starMass, BigDecimal distanceFromEarth, String description, boolean isPublicSystem, String ownerUsername)
    {
        this.ID = ID;
        this.name = name;
        this.starType = starType;
        this.starMass = starMass;
        this.distanceFromEarth = distanceFromEarth;
        this.description = description;
        this.isPublicSystem = isPublicSystem;
        this.ownerUsername = ownerUsername;
    }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStarType() { return starType; }
    public void setStarType(String starType) { this.starType = starType; }

    public BigDecimal getStarMass() { return starMass; }
    public void setStarMass(BigDecimal starMass) { this.starMass = starMass; }

    public BigDecimal getDistanceFromEarth() { return distanceFromEarth; }
    public void setDistanceFromEarth(BigDecimal distanceFromEarth) { this.distanceFromEarth = distanceFromEarth; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isPublicSystem() { return isPublicSystem; }
    public void setPublicSystem(boolean isPublic) { this.isPublicSystem = isPublic; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public record Designator(int ID, String name) {}
}
