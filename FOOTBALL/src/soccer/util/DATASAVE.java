package soccer.util;

import soccer.PLAY.LEAGUE;
import soccer.PLAY.TEAM;
import soccer.PLAY.PLAYER;

import java.io.*;
import java.util.ArrayList;

public class DATASAVE {

    // Save league to JSON-like text
    public static void saveLeague(LEAGUE league, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("{");
            writer.println("  \"league\": \"" + league.getName_of_league() + "\",");
            writer.println("  \"teams\": [");

            ArrayList<TEAM> teams = league.getTeams();
            for (int i = 0; i < teams.size(); i++) {
                TEAM t = teams.get(i);
                writer.println("    {");
                writer.println("      \"name\": \"" + t.getTeamName() + "\",");
                writer.println("      \"points\": " + t.getTeam_points() + ",");
                writer.println("      \"goals\": " + t.getTeam_goal() + ",");
                writer.println("      \"players\": [");

                ArrayList<PLAYER> players = t.getPlayers();
                for (int j = 0; j < players.size(); j++) {
                    PLAYER p = players.get(j);
                    writer.println("        {");
                    writer.println("          \"name\": \"" + p.getNamePlayer() + "\",");
                    writer.println("          \"age\": " + p.getAgePlayer() + ",");
                    writer.println("          \"nationality\": \"" + p.getNationalityPlayer() + "\"");
                    writer.print("        }");
                    if (j < players.size() - 1) writer.println(",");
                    else writer.println();
                }

                writer.println("      ]");
                writer.print("    }");
                if (i < teams.size() - 1) writer.println(",");
                else writer.println();
            }

            writer.println("  ]");
            writer.println("}");
            System.out.println("✅ League saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load league from JSON-like text (very simple parser)
    public static String loadLeagueFile(String filename) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
