package net.pixeldreamstudios.kingdoms.cardinalcomponents;

import net.minecraft.nbt.CompoundTag;

public class KingdomComponent implements TeamComponent {
    private String team = "no_team";

    @Override
    public String getTeam() {
        return team;
    }

    @Override
    public String setTeam(String team) {
        return this.team = team;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.team = tag.getString("teamTag");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putString("teamTag", this.team);
    }
}
