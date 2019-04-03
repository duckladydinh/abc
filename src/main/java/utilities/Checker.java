package utilities;

import com.google.common.collect.Sets;
import model.Group;
import model.Team;

import java.util.List;

public class Checker {
    public static boolean validateGroup(Group group) {
        if (group.getSize() < Group.LOWER_TEAM_SIZE && group.getSize() > Group.UPPER_TEAM_SIZE) {
            return false;
        }
        return true;
    }

    public static boolean validateSolution(List<Group> assignedGroups, List<Team> teams) {
        int total = 0;
        for (Group group: assignedGroups) {
            validateGroup(group);
            total += group.getSize();
        }

        return total == teams.size();
    }
}
