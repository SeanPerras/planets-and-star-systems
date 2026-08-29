package org.example.models;

import java.math.BigDecimal;

public class Planet
{
    private int ID;
    private String name, type, description, moons, ownerUsername;
    private BigDecimal mass, distanceFromStar;
    private boolean publicPlanet;
    private Integer systemID;

    public Planet() { }

    public Planet(int ID, String name, String type, BigDecimal mass, BigDecimal distanceFromStar,
                  String description, String moons, boolean publicPlanet, String ownerUsername, Integer systemID)
    {
        this.ID = ID;
        this.name = name;
        this.type = type;
        this.mass = mass;
        this.distanceFromStar = distanceFromStar;
        this.description = description;
        this.moons = moons;
        this.publicPlanet = publicPlanet;
        this.ownerUsername = ownerUsername;
        this.systemID = systemID;
    }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getMass() { return mass; }
    public void setMass(BigDecimal mass) { this.mass = mass; }

    public BigDecimal getDistanceFromStar() { return distanceFromStar; }
    public void setDistanceFromStar(BigDecimal distanceFromStar) { this.distanceFromStar = distanceFromStar; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMoons() { return moons; }
    public void setMoons(String moons) { this.moons = moons; }

    public boolean isPublicPlanet() { return publicPlanet; }
    public void setPublicPlanet(boolean publicPlanet) { this.publicPlanet = publicPlanet; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public Integer getSystemID() { return systemID; }
    public void setSystemID(Integer systemID) { this.systemID = systemID; }
}
