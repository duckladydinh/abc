package utilities;

import model.Group;
import model.Location;
import model.Team;

import java.util.List;

public class Metrics {
    public static long measureGroupTotalDistance(Group group) {
        long total = 0;
        for (int i = 0; i < group.getSize(); ++i) {
            for (int j = i + 1; j < group.getSize(); ++j) {
                total += distance(group.getTeam(i).getLocation(), group.getTeam(j).getLocation());
            }
        }
        return total;
    }

    public static long measureGroupMinMaxDistance(Group group) {
        long maks = 0;
        for (int i = 0; i < group.getSize(); ++i) {
            long total = 0;
            for (int j = 0; j < group.getSize(); ++j) {
                total += distance(group.getTeam(i), group.getTeam(j));
            }
            maks = Math.max(maks, total);
        }
        return maks;
    }

    public static long measureSolutionMinMaxDistance(List<Group> assignedGroups) {
        long maks = 0;
        for (Group group: assignedGroups) {
            maks = Math.max(maks, Metrics.measureGroupMinMaxDistance(group));
        }
        return maks;
    }

    public static long measureSolutionTotalDistance(List<Group> assignedGroups) {
        long total = 0;
        for (Group group: assignedGroups) {
            total += Metrics.measureGroupTotalDistance(group);
        }
        return total;
    }


    public static long marginDistanceIfAdded(Group group, Team addedTeam) {
        Group singleGroup = new Group();
        singleGroup.addMemberTeam(addedTeam);
        return distance(group, singleGroup);
    }

    public static long marginDistanceIfRemove(Group group, Team removedTeam) {
        Group singleGroup = new Group();
        singleGroup.addMemberTeam(removedTeam);
        return distance(group, singleGroup) - distance(singleGroup, singleGroup);
    }



    public static long distance(Location A, Location B) {
        return (long) Math.sqrt(squaredDistance(A, B)) + 1;
    }

    private static Long squaredDistance(Location A, Location B) {
        return ((long) A.getX() - B.getX()) * (A.getX() - B.getX()) + ((long) A.getY() - B.getY()) * (A.getY() - B.getY());
    }

    private static long distance(Team A, Team B) {
        return distance(A.getLocation(), B.getLocation());
    }

    private static long distance(Group A, Group B) {
        long total = 0L;

        for (Team a: A.getTeams()) {
            for (Team b: B.getTeams()) {
                total += distance(a, b);
            }
        }

        return total;
    }


}
