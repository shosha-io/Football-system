package soccer.event;

import soccer.PLAY.TEAM;
import java.util.ArrayList;

public class MATCH {

    private TEAM team1;
    private TEAM team2;
    private ArrayList<POSSESSION> possessions = new ArrayList<>();
    private ArrayList<GOAL> goals = new ArrayList<>();

    public MATCH(TEAM t1, TEAM t2) {
        this.team1 = t1;
        this.team2 = t2;
    }

    public TEAM getTeam1() { return team1; }
    public TEAM getTeam2() { return team2; }
    public ArrayList<POSSESSION> getPossessions() { return possessions; }
    public ArrayList<GOAL> getGoals() { return goals; }

    public void addPossession(POSSESSION p) {
        possessions.add(p);
    }


    public void addGoal(GOAL g) {
        goals.add(g);
        g.getPlayer().addGoal();

        if (g.getTeam() == team1) {
            team1.setTeam_goal(team1.getTeam_goal() + 1);
        } else if (g.getTeam() == team2) {
            team2.setTeam_goal(team2.getTeam_goal() + 1);
        }
    }

    public String getScore() {
        int t1Goals = team1.getTeam_goal();
        int t2Goals = team2.getTeam_goal();
        return team1.getTeamName() + " " + t1Goals + " - " + t2Goals + " " + team2.getTeamName();
    }


    public String getStats() {
        return POSSESSION.showStatsText(possessions, team1, team2);
    }

    public String getMatchEvents() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== MATCH EVENTS: ").append(getMatchName()).append(" =====\n");

        if (goals.isEmpty() && possessions.isEmpty()) {
            sb.append("No events recorded yet.\n");
        } else {
            for (GOAL g : goals) {
                sb.append(g.gameevent()).append("\n");
            }
            for (POSSESSION p : possessions) {
                sb.append(p.gameevent()).append("\n");
            }
        }

        sb.append("\nFinal Score: ").append(getScore()).append("\n");
        return sb.toString();
    }

    // Match Name
    public String getMatchName() {
        return team1.getTeamName() + " vs " + team2.getTeamName();
    }
}
