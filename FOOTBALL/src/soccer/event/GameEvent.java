package soccer.event;

import soccer.PLAY.PLAYER;
import soccer.PLAY.TEAM;

public abstract class GameEvent {
    private TEAM team;
    private PLAYER player;
    private int startTime;
    private int endTime;

    public TEAM getTeam() {
        return team;
    }

    public void setTeam(TEAM team) {
        this.team = team;
    }

    public PLAYER getPlayer() {
        return player;
    }

    public void setPlayer(PLAYER player) {
        this.player = player;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int duration() {
        return endTime - startTime;
    }
    public abstract String gameevent();
}
