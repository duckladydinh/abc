package model;

public class Team {
    // should be unique
    private final String name;
    private final Location location;

    public Team(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
