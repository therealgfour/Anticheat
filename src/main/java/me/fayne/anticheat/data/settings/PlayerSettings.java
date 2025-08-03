package me.fayne.anticheat.data.settings;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.UUID;

@Getter
@Setter
public class PlayerSettings {
    private final UUID playerId;
    private boolean alertsEnabled;

    public PlayerSettings(UUID playerId) {
        this.playerId = playerId;
        this.alertsEnabled = true;
    }

    public static PlayerSettings fromDocument(Document doc) {
        PlayerSettings settings = new PlayerSettings(UUID.fromString(doc.getString("uuid")));
        settings.setAlertsEnabled(doc.getBoolean("alerts", true));
        return settings;
    }

    public Document toDocument() {
        return new Document("uuid", playerId.toString())
                .append("alerts", alertsEnabled);
    }
}
