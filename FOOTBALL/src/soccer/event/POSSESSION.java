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
        int team1Time = 0;
        int team2Time = 0;

        for (POSSESSION p : possessions) {
            TEAM t = p.getTeam();
            int d = p.duration();
            if (t == team1) team1Time += d;
            else if (t == team2) team2Time += d;
        }

        int totalTime = 90;
        double team1Percent = (team1Time * 100.0) / totalTime;
        double team2Percent = (team2Time * 100.0) / totalTime;

        return "===== POSSESSION STATS (90 mins) =====\n" +
                team1.getTeamName() + " had " + team1Time + " mins (" + team1Percent + "%)\n" +
                team2.getTeamName() + " had " + team2Time + " mins (" + team2Percent + "%)\n";
    }
}
