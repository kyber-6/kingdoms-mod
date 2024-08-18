package net.pixeldreamstudios.kingdoms.cardinalcomponents;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface TeamComponent extends Component {
    int getTeam();
    void setTeam(int team);

    boolean isKing();
    void setKing(boolean isKing);

    boolean isTeamRespawnAllowed();
    void setTeamRespawnAllowed(boolean allowed);
}
