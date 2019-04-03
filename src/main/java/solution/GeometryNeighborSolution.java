package solution;

import com.google.common.collect.Lists;
import model.Group;
import model.Team;
import org.apache.commons.lang3.Validate;

import java.util.Comparator;
import java.util.List;

import static utilities.DataTool.getRNG;
import static utilities.DataTool.moveTeamFromList;
import static utilities.Metrics.distance;
import static utilities.Metrics.marginDistanceIfAdded;

public class GeometryNeighborSolution implements AbstractSolution {

    @Override
    public List<Group> solve(List<Team> teams) {
        teams.sort(Comparator.comparing(x -> x.getLocation().getX()));

        List<Group> groups = Lists.newArrayList();
        int teamLimit = Group.LOWER_TEAM_SIZE;

        while (!teams.isEmpty() && teams.size() >= Group.LOWER_TEAM_SIZE) {
            Group group = new Group();

            int firstMember = findFirstMemberTeam(teams);
            moveTeamFromList(teams, firstMember, group);

            while (group.getSize() < teamLimit && !teams.isEmpty()) {
                int closest = 0;
                final int choice = getRNG().nextInt(3);
                if (choice == 0) {
                    closest = findClosestTeamToAll(group, teams);
                } else if (choice == 1) {
                    closest = findClosestTeamToCenter(group, teams);
                } else {
                    closest = findClosestTeamToMedianCenter(group, teams);
                }
                Validate.isTrue(closest != -1);
                moveTeamFromList(teams, closest, group);
            }

            groups.add(group);
        }
        Lists.reverse(teams);

        distributeRemainingTeams(teams, groups);
        return groups;
    }

    private int findFirstMemberTeam(List<Team> teams) {
        return 0;
    }

    private void distributeRemainingTeams(List<Team> teams, List<Group> groups) {
        for (Team team: teams) { // only if not empty
            long minCost = (long) 1e15;
            int bestGroup = -1;
            for (int i = 0; i < groups.size(); ++i) {
                Group group = groups.get(i);
                long cost = marginDistanceIfAdded(group, team);
                if (minCost > cost) {
                    minCost = cost;
                    bestGroup = i;
                }
            }

            Validate.inclusiveBetween(0, groups.size() - 1, bestGroup, "Impossible, there got to be a group here!");
            Group group = groups.get(bestGroup);
            group.addMemberTeam(team);
        }
        teams.clear();
    }

    private int findClosestTeamToAll(Group group, List<Team> teams) {
        long minimalDistance = (long) 1e15;
        int closest = -1;

        for (int i = 0; i < teams.size(); ++i) {
            long res = marginDistanceIfAdded(group, teams.get(i));

            if (minimalDistance > res) {
                minimalDistance = res;
                closest = i;
            }
        }

        return closest;
    }

    private int findClosestTeamToCenter(Group group, List<Team> teams) {
        long minimalDistance = (long) 1e15;
        int closest = -1;

        for (int i = 0; i < teams.size(); ++i) {
            Team team = teams.get(i);
            long res = distance(group.getCenter(), team.getLocation());

            if (minimalDistance > res) {
                minimalDistance = res;
                closest = i;
            }
        }

        return closest;
    }

    private int findClosestTeamToMedianCenter(Group group, List<Team> teams) {
        long minimalDistance = (long) 1e15;
        int closest = -1;

        for (int i = 0; i < teams.size(); ++i) {
            Team team = teams.get(i);
            long res = distance(group.getMedianCenter(), team.getLocation());

            if (minimalDistance > res) {
                minimalDistance = res;
                closest = i;
            }
        }

        return closest;
    }
}
