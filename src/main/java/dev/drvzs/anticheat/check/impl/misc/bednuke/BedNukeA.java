package dev.drvzs.anticheat.check.impl.misc.bednuke;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import dev.drvzs.anticheat.check.Check;
import dev.drvzs.anticheat.check.CheckData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@CheckData(name = "BedNuke", punishmentVL = 5, description = "Checks for bed nuke", experimental = true)
public class BedNukeA extends Check {
    private final Map<Block, Long> lastChecked = new HashMap<>();

    @Override
    public void onPacket(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.PLAYER_DIGGING) return;

        if (isCancellable())
            return; // TODO: check if the chunk is loaded to fix "asynchronous entity added" exception from spigot's AsyncCatcher

        WrapperPlayClientPlayerDigging dig = new WrapperPlayClientPlayerDigging(event);
        if (dig.getAction() != DiggingAction.START_DIGGING) return;

        Player player = getPlayerData().getPlayer();
        Block block = player.getWorld().getBlockAt(
                dig.getBlockPosition().getX(),
                dig.getBlockPosition().getY(),
                dig.getBlockPosition().getZ()
        );

        if (block.getType() != Material.BED_BLOCK) return;

        // we check once per second per block to prevent lag
        long now = System.currentTimeMillis();
        if (now - lastChecked.getOrDefault(block, 0L) < 1000) {
            return;
        }
        lastChecked.put(block, now);

        lastChecked.entrySet().removeIf(entry -> now - entry.getValue() > 5000);

        org.bukkit.block.BlockState state = block.getState();
        org.bukkit.material.Bed bed = (org.bukkit.material.Bed) state.getData();

        Block otherPart = findOtherBedPart(block, bed);
        if (otherPart == null) return;

        boolean isTopCovered = isBlocked(block, BlockFace.UP) && isBlocked(otherPart, BlockFace.UP);
        if (!isTopCovered)
            return;

        if (isFullySurrounded(block) && isFullySurrounded(otherPart)) this.fail("bed nuke");
    }

    private Block findOtherBedPart(Block block, org.bukkit.material.Bed bed) {
        Block other = block.getRelative(bed.getFacing());
        if (other.getType() == Material.BED_BLOCK) {
            return other;
        }

        other = block.getRelative(bed.getFacing().getOppositeFace());
        if (other.getType() == Material.BED_BLOCK) {
            return other;
        }
        return null;
    }

    private boolean isBlocked(Block block, BlockFace face) {
        Block relative = block.getRelative(face);
        Material type = relative.getType();
        return type.isSolid() || type == Material.WATER || type == Material.STATIONARY_WATER || type == Material.LAVA || type == Material.STATIONARY_LAVA;
    }

    private boolean isFullySurrounded(Block block) {
        for (BlockFace face : new BlockFace[]{
                BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.DOWN
        }) {
            if (!isBlocked(block, face)) {
                return false;
            }
        }
        return true;
    }
}