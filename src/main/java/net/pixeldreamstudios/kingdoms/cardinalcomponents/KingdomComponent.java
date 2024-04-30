package net.pixeldreamstudios.kingdoms.cardinalcomponents;

import net.minecraft.nbt.CompoundTag;
import net.pixeldreamstudios.kingdoms.Kingdoms;

public class KingdomComponent implements TeamComponent {
    private int team = Kingdoms.NO_TEAM;

//    @Override
//    public String getTeam() {
//        return team;
//    }
//
//    @Override
//    public String setTeam(String team) {
//        return this.team = team;
//    }

    @Override
    public int getTeam() {
        return this.team;
    }

    @Override
    public void setTeam(int team) {
        this.team = team;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.team = tag.getInt("teamTag");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("teamTag", this.team);
    }
}
