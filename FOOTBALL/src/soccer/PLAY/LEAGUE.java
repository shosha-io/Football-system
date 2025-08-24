package soccer.PLAY;

import soccer.event.MATCH;
import java.util.ArrayList;
import java.util.Scanner;

public class LEAGUE {
    private String name_of_league;
    private ArrayList<TEAM> teams = new ArrayList<>();
    private ArrayList<MATCH> matches = new ArrayList<>();

    public String getName_of_league() {
        return name_of_league;
    }

    public void setName_of_league(String name_of_league) {
        this.name_of_league = name_of_league;
    }

    public ArrayList<TEAM> getTeams() {
        return teams;
    }

    public ArrayList<MATCH> getMatches() {
        return matches;
    }


    public void addTeam(TEAM team) {
        teams.add(team);
    }

    public void addMatch(MATCH m) {
        matches.add(m);
    }


    public void showTeamPlayers(String teamName) {
        boolean found = false;
        for (TEAM team : teams) {
            if (team.getTeamName().equalsIgnoreCase(teamName)) {
                team.team_info();
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Team not found.");
        }
    }

    public void showLeague() {
        System.out.println("\n=== League: " + getName_of_league() + " ===");
        if (teams.isEmpty()) {
            System.out.println("No teams in this league.");
        } else {
            for (TEAM t : teams) {
                t.team_info();
                System.out.println();
            }
        }
    }


    public static LEAGUE chooseLeague(ArrayList<LEAGUE> leagues, Scanner scan) {
        if (leagues.isEmpty()) {
            System.out.println("No leagues available.");
            return null;
        }
        for (int i = 0; i < leagues.size(); i++) {
            System.out.println((i + 1) + ". " + leagues.get(i).getName_of_league());
        }
        System.out.print("Choose league (1-" + leagues.size() + "): ");
        int choice = scan.nextInt();
        scan.nextLine();
        return leagues.get(choice - 1);
    }


    public static TEAM chooseTeam(LEAGUE league, Scanner scan) {
        if (league.getTeams().isEmpty()) {
            System.out.println("No teams available in this league. Add a team first.");
            return null;
        }
        for (int i = 0; i < league.getTeams().size(); i++) {
            System.out.println((i + 1) + ". " + league.getTeams().get(i).getTeamName());
        }
        System.out.print("Choose team (1-" + league.getTeams().size() + "): ");
        int choice = scan.nextInt();
        scan.nextLine();
        if (choice < 1 || choice > league.getTeams().size()) {
            System.out.println("Invalid choice.");
            return null;
        }
        return league.getTeams().get(choice - 1);
    }
}
