package me.fayne.anticheat.data.settings;

import lombok.Getter;
import org.bson.Document;

import java.util.UUID;

@Getter
public class PlayerLookup {
    private final UUID playerId;
    private final String checkName;
    private final String checkType;
    private int count;

    public PlayerLookup(UUID playerId, String checkName, String checkType) {
        this.playerId = playerId;
        this.checkName = checkName;
        this.checkType = checkType;
        this.count = 1;
    }

    public static PlayerLookup fromDocument(Document doc) {
        PlayerLookup lookup = new PlayerLookup(
                UUID.fromString(doc.getString("uuid")),
                doc.getString("checkName"),
                doc.getString("checkType")
        );

        lookup.count = doc.getInteger("count", 1);
        return lookup;
    }
}