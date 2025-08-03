package me.fayne.anticheat.action.punish;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum PunishmentType {

    CONSOLE_BAN("You have been banned for cheating!"),
    USER_KICK("You have been kicked for {reason} by {user}!");

    @Getter
    private String desc;

}
