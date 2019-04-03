import model.Group;
import model.Team;
import solution.AbstractSolution;
import solution.GeometryNeighborSolution;
import utilities.Metrics;
import utilities.Checker;

import java.io.FileNotFoundException;
import java.util.List;

import static utilities.DataTool.inputData;
import static utilities.DataTool.outputData;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        args = new String[] {"input.txt", "output.txt"};
        String inputFileName = args[0].trim();
        String outputFileName = args[1].trim();
        new Main().solve(inputFileName, outputFileName);
    }

    public void solve(String inputFileName, String outputFileName) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        List<Team> teams = inputData(inputFileName);

        AbstractSolution solution = new GeometryNeighborSolution();
        List<Group> assignedGroups = solution.solve(teams);
        Checker.validateSolution(assignedGroups, teams);

        System.out.println("MinMax = " + Metrics.measureSolutionMinMaxDistance(assignedGroups));
        System.out.println("TOTAL = " + Metrics.measureSolutionTotalDistance(assignedGroups));

        outputData(outputFileName, assignedGroups);
    }
}
