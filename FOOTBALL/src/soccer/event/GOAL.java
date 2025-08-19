package soccer.event;

public class GOAL extends GameEvent {
    @Override
    public String gameevent() {
        return " GOAL by " + getPlayer().getNamePlayer() +
                " for " + getTeam().getTeamName() +
                " at " + getStartTime() + " minutes!";
    }
}
