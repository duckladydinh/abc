package utilities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import model.Group;
import model.Location;
import model.Team;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SplittableRandom;

public class DataTool {
    private static SplittableRandom rng = new SplittableRandom();

    public static SplittableRandom getRNG() {
        return rng;
    }

    public static Map<Team, Integer> transformSolution(List<Group> assignedGroups) {
        Map<Team, Integer> assignedTeams = Maps.newTreeMap(Comparator.comparing(Team::getName));

        for (int groupNo = 0; groupNo < assignedGroups.size(); ++groupNo) {
            Group group = assignedGroups.get(groupNo);
            for (Team team: group.getTeams()) {
                assignedTeams.put(team, groupNo + 1);
            }
        }

        return assignedTeams;
    }


    public static List<Team> inputData(String inputFileName) {
        final String delimeter = ",";
        final Integer[] columns = {0, 2, 3};

        List<Team> teams = Lists.newArrayList();
        File inputFile = new File(inputFileName);

        Scanner scanner;
        try {
            scanner = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] values = line.trim().split(delimeter);

            Team team = new Team(
                    values[columns[0]],
                    new Location(
                            Integer.parseInt(values[columns[1]]),
                            Integer.parseInt(values[columns[2]])));
            teams.add(team);
        }

        return teams;
    }

    public static void outputData(String outputFileName, List<Group> assignedGroups) {
        File outputFile = new File(outputFileName);
        PrintWriter writer;

        try {
            writer = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Map<Team, Integer> assignedTeams = DataTool.transformSolution(assignedGroups);

        List<Integer> X = Lists.newArrayList();
        List<Integer> Y = Lists.newArrayList();
        List<Integer> Z = Lists.newArrayList();
        assignedTeams.forEach((key, value) -> {
            X.add(key.getLocation().getX());
            Y.add(key.getLocation().getY());
            Z.add(value);
            writer.println(key.getLocation().getX() + " " + key.getLocation().getY() + " " + value);
        });

        writer.flush();
        writer.close();
    }

    public static void moveTeamFromGroup(Group fromGroup, int teamIndex, Group toGroup) {
        Team team = fromGroup.getTeam(teamIndex);
        toGroup.addMemberTeam(team);
        fromGroup.removeMemberTeam(team);
    }

    public static void moveTeamFromList(List<Team> fromTeams, int teamIndex, Group toGroup) {
        toGroup.addMemberTeam(fromTeams.get(teamIndex));
        fromTeams.remove(teamIndex);
    }
}
