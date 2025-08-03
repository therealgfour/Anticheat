package me.fayne.anticheat.database;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import me.fayne.anticheat.data.settings.PlayerLookup;
import me.fayne.anticheat.data.settings.PlayerSettings;
import me.fayne.anticheat.utils.LoggerUtil;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoDB {
    private final String uri;
    private final String databaseName;
    private MongoClient client;
    @Getter
    private MongoDatabase database;
    private MongoCollection<Document> playerSettingsCollection;
    private MongoCollection<Document> playerLookupCollection;

    public MongoDB(String uri, String databaseName) {
        this.uri = uri;
        this.databaseName = databaseName;
    }

    public void connect() {
        try {
            this.client = MongoClients.create(uri);
            this.database = client.getDatabase(databaseName);
            this.playerSettingsCollection = database.getCollection("vca_player_data");
            this.playerLookupCollection = database.getCollection("vca_player_lookup");

            playerLookupCollection.createIndex(new Document("uuid", 1));
            playerLookupCollection.createIndex(new Document("timestamp", -1));

            playerSettingsCollection.createIndex(new Document("uuid", 1));

            LoggerUtil.info("Connected to MongoDB");
        } catch (Exception e) {
            LoggerUtil.error("Failed to connect to MongoDB", e);
            throw new RuntimeException("Failed to connect to MongoDB", e);
        }
    }

    public CompletableFuture<PlayerSettings> getPlayerSettings(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Document doc = playerSettingsCollection
                        .find(Filters.eq("uuid", playerId.toString()))
                        .first();

                if (doc == null) {
                    return new PlayerSettings(playerId);
                }

                return PlayerSettings.fromDocument(doc);
            } catch (Exception e) {
                LoggerUtil.error("Failed to load settings for " + playerId, e);
                return new PlayerSettings(playerId);
            }
        });
    }

    public CompletableFuture<Void> savePlayerSettings(UUID playerId, PlayerSettings settings) {
        return CompletableFuture.runAsync(() -> {
            try {
                Document doc = settings.toDocument();
                playerSettingsCollection.updateOne(
                        Filters.eq("uuid", playerId.toString()),
                        new Document("$set", doc),
                        new UpdateOptions().upsert(true)
                );
            } catch (Exception e) {
                LoggerUtil.error("Failed to save settings for " + playerId, e);
                throw new RuntimeException("Failed to save player settings", e);
            }
        });
    }

    public void savePlayerLookup(PlayerLookup newLookup) {
        CompletableFuture.runAsync(() -> {
            try {
                playerLookupCollection.updateOne(
                        Filters.and(
                                Filters.eq("uuid", newLookup.getPlayerId().toString()),
                                Filters.eq("checkName", newLookup.getCheckName()),
                                Filters.eq("checkType", newLookup.getCheckType())
                        ),
                        new Document("$inc", new Document("count", 1))
                                .append("$set", new Document("lastTimestamp", System.currentTimeMillis())),
                        new UpdateOptions().upsert(true)
                );
            } catch (Exception e) {
                LoggerUtil.error("Failed to save lookup for " + newLookup.getPlayerId(), e);
            }
        });
    }

    public CompletableFuture<List<PlayerLookup>> getPlayerLookup(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            List<PlayerLookup> lookups = new ArrayList<>();
            try (MongoCursor<Document> cursor = playerLookupCollection
                    .find(Filters.eq("uuid", playerId.toString()))
                    .sort(Sorts.descending("lastTimestamp"))
                    .iterator()) {

                while (cursor.hasNext()) {
                    lookups.add(PlayerLookup.fromDocument(cursor.next()));
                }
            } catch (Exception e) {
                LoggerUtil.error("Failed to load lookups for " + playerId, e);
            }
            return lookups;
        });
    }

    public void close() {
        client.close();
    }
}
