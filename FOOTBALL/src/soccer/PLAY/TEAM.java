package soccer.PLAY;

import java.util.ArrayList;
import java.util.Scanner;

public class TEAM {
    private String teamName;
    private ArrayList<PLAYER>players=new ArrayList<>();
    private  int team_points=0;
    private  int team_goal=0;
Scanner scan=new Scanner(System.in);

    public int getTeam_goal()
    {
        return team_goal;
    }

    public void setTeam_goal(int team_goal) {
        this.team_goal = team_goal;
    }

    public int getTeam_points() {
        return team_points;
    }

    public void setTeam_points(int team_points) {
        this.team_points = team_points;
    }

    public void add_players(PLAYER player)
     {

         players.add(player);
     }

    public String getTeamName()
    {
        return teamName;
    }

    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
    }

    public ArrayList<PLAYER> getPlayers()
    {
        return players;
    }
    public void input_team() {
        System.out.print("Enter team name: ");
        setTeamName(scan.nextLine());

        System.out.print("Enter number of players: ");
        int numPlayers =scan.nextInt();
        scan.nextLine();
        for (int i = 1; i<= numPlayers; i++)
        {
            System.out.println("\n--- Enter details for Player " + i + " ---");
            PLAYER p = new PLAYER();
            p.input_player();
            players.add(p);
        }
    }
    public void team_info()
    {
        System.out.println("Team name : "+getTeamName());
        System.out.println("Team name: " + getTeamName());
        System.out.println("Team points: " + getTeam_points());
        System.out.println("Team goals: " + getTeam_goal());
        System.out.println("Players:");
        for (PLAYER p : players) {
            p.detailsPlayer();
        }

    }

}
