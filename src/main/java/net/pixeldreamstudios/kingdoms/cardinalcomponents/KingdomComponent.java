package net.pixeldreamstudios.kingdoms.cardinalcomponents;

import net.minecraft.nbt.CompoundTag;
import net.pixeldreamstudios.kingdoms.Kingdoms;

public class KingdomComponent implements TeamComponent {
    private int team = Kingdoms.NO_KINGDOM;
    private boolean isKing = false;
    private boolean teamRespawnAllowed = true;

    @Override
    public int getTeam() {
        return this.team;
    }

    @Override
    public void setTeam(int team) {
        this.team = team;
    }

    @Override
    public boolean isKing() {
        return this.isKing;
    }

    @Override
    public void setKing(boolean isKing) {
        this.isKing = isKing;
    }

    @Override
    public boolean isTeamRespawnAllowed() {
        return this.teamRespawnAllowed;
    }

    @Override
    public void setTeamRespawnAllowed(boolean allowed) {
        this.teamRespawnAllowed = allowed;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.team = tag.getInt("teamTag");
        this.isKing = tag.getBoolean("isKing");
        this.teamRespawnAllowed = tag.getBoolean("teamRespawnAllowed");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("teamTag", this.team);
        tag.putBoolean("isKing", this.isKing);
        tag.putBoolean("teamRespawnAllowed", this.teamRespawnAllowed);
    }
}
