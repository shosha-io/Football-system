package soccer.PLAY;

import java.util.Scanner;

public class RESULTGAME {
    public void recordMatch(TEAM team1, TEAM team2)
    {
        Scanner scan=new Scanner(System.in);

        System.out.print(" goals scored by " + team1.getTeamName() + ": ");
        int goals1 = scan.nextInt();

        System.out.print("goals scored by " + team2.getTeamName() + ": ");
        int goals2 = scan.nextInt();

        team1.setTeam_goal(team1.getTeam_goal() + goals1);
        team2.setTeam_goal(team2.getTeam_goal() + goals2);

        if (goals1 > goals2) {
            team1.setTeam_points(team1.getTeam_points() + 3);
        } else if (goals1 < goals2) {
            team2.setTeam_points(team2.getTeam_points() + 3);
        } else {
            team1.setTeam_points(team1.getTeam_points() + 1);
            team2.setTeam_points(team2.getTeam_points() + 1);
        }
    }
}
