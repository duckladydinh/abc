package model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class Group {
    public static final int LOWER_TEAM_SIZE = 16;
    public static final int UPPER_TEAM_SIZE = 20;

    protected List<Team> teams = Lists.newArrayList();
    protected long sumX = 0;
    protected long sumY = 0;

    public List<Team> getTeams() {
        return teams;
    }

    public Team getTeam(int i) {
        return teams.get(i);
    }

    public int getSize() {
        return teams.size();
    }

    public void addMemberTeam(Team team) {
        teams.add(team);
        sumX += team.getLocation().getX();
        sumY += team.getLocation().getY();
    }

    public void removeMemberTeam(Team team) {
        teams.add(team);
        sumX -= team.getLocation().getX();
        sumY -= team.getLocation().getY();
    }

    public Location getCenter() {
        return new Location((int) (sumX / getSize()), (int) (sumY / getSize()));
    }

    public Location getMedianCenter() {
        Set<Integer> allXs = Sets.newTreeSet();
        Set<Integer> allYs = Sets.newTreeSet();

        for (Team team: teams) {
            allXs.add(team.getLocation().getX());
            allYs.add(team.getLocation().getY());
        }

        List<Integer> listXs = Lists.newArrayList(allXs);
        List<Integer> listYs = Lists.newArrayList(allYs);
        Location center = new Location(listXs.get(listXs.size() / 2), listYs.get(listYs.size() / 2));
        return center;
    }
}
