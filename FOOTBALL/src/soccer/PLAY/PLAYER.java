package soccer.PLAY;

import java.util.Scanner;

public class PLAYER {
    private String namePlayer;
    private int agePlayer;
    private String nationalityPlayer;
    private int goals;
    Scanner scan = new Scanner(System.in);


    public String getNamePlayer() {
        return namePlayer;
    }

    public void setNamePlayer(String namePlayer) {
        this.namePlayer = namePlayer;
    }

    public int getAgePlayer() {
        return agePlayer;
    }

    public void setAgePlayer(int agePlayer) {
        this.agePlayer = agePlayer;
    }

    public String getNationalityPlayer() {
        return nationalityPlayer;
    }

    public void setNationalityPlayer(String nationalityPlayer) {
        this.nationalityPlayer = nationalityPlayer;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public void addGoal() {
        this.goals++;
    }

    public void input_player() {
        System.out.print("Enter player name: ");
        setNamePlayer(scan.nextLine());

        System.out.print("Enter player age: ");
        setAgePlayer(scan.nextInt());
        scan.nextLine();

        System.out.print("Enter player nationality: ");
        setNationalityPlayer(scan.nextLine());

        this.goals = 0;
    }
    public void detailsPlayer() {
        System.out.println(
                "Name: " + getNamePlayer() +
                        " | Age: " + getAgePlayer() +
                        " | Nationality: " + getNationalityPlayer() +
                        " | Goals: " + getGoals()
        );
    }
}
