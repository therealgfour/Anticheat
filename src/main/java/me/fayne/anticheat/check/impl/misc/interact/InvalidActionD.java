package me.fayne.anticheat.check.impl.misc.interact;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import me.fayne.anticheat.check.Check;
import me.fayne.anticheat.check.CheckData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CheckData(name = "InvalidAction", description = "Checks for the delay between shooting a bow", punishmentVL = 5, type = "D")
public class InvalidActionD extends Check {

    private final Map<UUID, Long> bowUseStart = new HashMap<>();

    @Override
    public void onPacket(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            ItemStack item = getPlayerData().getPlayer().getItemInHand();
            if (item != null && item.getType() == Material.BOW)
                bowUseStart.put(getPlayerData().getPlayer().getUniqueId(), System.currentTimeMillis());
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging place = new WrapperPlayClientPlayerDigging(event);
            if (place.getAction() == DiggingAction.RELEASE_USE_ITEM) {
                ItemStack item = getPlayerData().getPlayer().getItemInHand();
                if (item != null && item.getType() == Material.BOW) {
                    Long start = bowUseStart.remove(getPlayerData().getPlayer().getUniqueId());

                    if (start != null) {
                        long now = System.currentTimeMillis();
                        long duration = now - start;

                        if (duration < 20) {
                            this.fail("using fast bow. duration=" + duration);
                        }
                    }
                }
            }
        }
    }
}