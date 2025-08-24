package soccer.event;

import soccer.PLAY.TEAM;
import java.util.ArrayList;

public class POSSESSION extends GameEvent {

    @Override
    public String gameevent() {
        return "Possession: " + getTeam().getTeamName() +
                " from " + getStartTime() +
                " to " + getEndTime() +
                " (" + duration() + " mins)";
    }

    public String getDetails() {
        return gameevent();
    }

    public static String showStatsText(ArrayList<POSSESSION> possessions, TEAM team1, TEAM team2) {
        if (possessions.isEmpty()) {
            return "No possession data recorded yet.";
        }

        int team1Time = 0;
        int team2Time = 0;
        int maxEndTime = 0;

        for (POSSESSION p : possessions) {
            TEAM t = p.getTeam();
            int d = p.duration();
            if (t == team1) team1Time += d;
            else if (t == team2) team2Time += d;

            if (p.getEndTime() > maxEndTime) {
                maxEndTime = p.getEndTime();
            }
        }

        // Use actual match time covered by possessions instead of fixed 90 mins
        int totalTime = Math.max(maxEndTime, team1Time + team2Time);

        if (totalTime == 0) {
            return "No valid possession duration recorded.";
        }

        double team1Percent = (team1Time * 100.0) / totalTime;
        double team2Percent = (team2Time * 100.0) / totalTime;

        return "===== POSSESSION STATS =====\n" +
                team1.getTeamName() + ": " + team1Time + " mins (" + String.format("%.2f", team1Percent) + "%)\n" +
                team2.getTeamName() + ": " + team2Time + " mins (" + String.format("%.2f", team2Percent) + "%)\n" +
                "Total Tracked Time: " + totalTime + " mins";
    }
}
