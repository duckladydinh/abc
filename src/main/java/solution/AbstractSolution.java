package solution;

import model.Group;
import model.Team;

import java.util.List;

public interface AbstractSolution {
    List<Group> solve(List<Team> teams);
}
