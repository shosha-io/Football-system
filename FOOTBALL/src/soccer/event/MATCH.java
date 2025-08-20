package soccer.event;

import soccer.PLAY.TEAM;

import java.util.ArrayList;

public class MATCH {

        private TEAM team1;
        private TEAM team2;
        private ArrayList<POSSESSION> possessions = new ArrayList<>();

        public MATCH(TEAM t1, TEAM t2) {
            this.team1 = t1;
            this.team2 = t2;
        }

        public TEAM getTeam1() { return team1; }
        public TEAM getTeam2() { return team2; }
        public ArrayList<POSSESSION> getPossessions() { return possessions; }

        public void addPossession(POSSESSION p) {
            possessions.add(p);
        }

        public String getStats() {
            return POSSESSION.showStatsText(possessions, team1, team2);
        }

        public String getMatchName() {
            return team1.getTeamName() + " vs " + team2.getTeamName();
        }
    }


