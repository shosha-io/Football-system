package soccer.event;

public class KICKOFF extends GameEvent {
    @Override
    public String gameevent() {
        return "Kickoff by " + getTeam().getTeamName() +
                " at " + getStartTime() + " minutes.";
    }
}
