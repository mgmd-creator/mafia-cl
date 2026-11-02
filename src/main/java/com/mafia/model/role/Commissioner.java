package com.mafia.model.role;
import java.util.Objects;
import com.mafia.model.Player;
import com.mafia.model.Team;


public class Commissioner extends Role {

    public Commissioner() {
        super("Комиссар", Team.CIVILIANS);
    }



    @Override
    public NightActionResult perform(Player actor, Player target) {
        Player checked = Objects.requireNonNull(target, "Комиссар должен выбрать цель");
        return NightActionResult.check(actor, checked, checked.getRole().getTeam());
    }

}