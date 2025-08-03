package me.fayne.anticheat.process.processors;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import me.fayne.anticheat.data.PlayerData;
import me.fayne.anticheat.event.EventTimer;
import me.fayne.anticheat.process.Processor;
import me.fayne.anticheat.process.ProcessorInfo;
import me.fayne.anticheat.utils.PacketUtil;
import me.fayne.anticheat.utils.location.PlayerLocation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ProcessorInfo(name = "Movement")
public class MovementProcessor extends Processor {

    private EventTimer velocityTimer;
    private double velH, velY;

    private PlayerLocation to = new PlayerLocation();
    private PlayerLocation from = new PlayerLocation();
    private PlayerLocation fromFrom = new PlayerLocation();

    private double deltaX, deltaY, deltaZ, deltaXAbs, deltaZAbs, deltaYAbs, lastDeltaX, lastDeltaY, lastDeltaZ,
            lastDeltaXZ, lastDeltaYaw, lastDeltaPitch, lastDeltaYawAbs, lastDeltaPitchAbs,
            deltaXZ, deltaYaw, deltaPitch, deltaYawAbs, deltaPitchAbs;

    private boolean inLiquid, wasInLiquid;

    public boolean onGround, lastGround = false;

    private int tick, airTicks;

    public MovementProcessor(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void onPacket(PacketReceiveEvent event) {
        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_FLYING:
            case CLIENT_POSITION:
            case CLIENT_LOOK:
            case CLIENT_POSITION_LOOK: {
                getData().setTick(getData().getTick() + 1);

                WrapperPlayClientPlayerFlying flyingPacket = new WrapperPlayClientPlayerFlying(event);

                double x = flyingPacket.getLocation().getX();
                double y = flyingPacket.getLocation().getY();
                double z = flyingPacket.getLocation().getZ();

                float pitch = flyingPacket.getLocation().getPitch();
                float yaw = flyingPacket.getLocation().getYaw();

                boolean ground = flyingPacket.isOnGround();

                if (ground) {
                    this.airTicks = 0;
                } else if (this.airTicks < 20) this.airTicks++;


                if (getData().getCurrentLocation() != null) {
                    getData().setLastLastLocation(getData().getLastLocation() != null ?
                            getData().getLastLocation().clone() : getData().getCurrentLocation().clone());
                    getData().setLastLocation(getData().getCurrentLocation().clone());
                }

                PlayerLocation loc = new PlayerLocation(
                        getData().getPlayer().getWorld(),
                        x, y, z, yaw, pitch,
                        ground,
                        System.currentTimeMillis()
                );

                getData().setCurrentLocation(loc);

                this.fromFrom.setWorld(this.from.getWorld());
                this.from.setWorld(to.getWorld());
                this.to.setWorld(getData().getPlayer().getWorld());

                this.fromFrom.setClientGround(this.from.isClientGround());
                this.from.setClientGround(this.to.isClientGround());
                this.to.setClientGround(ground);

                this.lastGround = this.onGround;
                this.onGround = ground;

                this.fromFrom.setTimeStamp(this.from.getTick());
                this.from.setTimeStamp(this.to.getTick());
                this.to.setTimeStamp(this.tick);

                if (flyingPacket.hasPositionChanged()) {

                    this.fromFrom.setX(this.from.getX());
                    this.fromFrom.setY(this.from.getY());
                    this.fromFrom.setZ(this.from.getZ());

                    this.from.setX(this.to.getX());
                    this.from.setY(this.to.getY());
                    this.from.setZ(this.to.getZ());

                    this.to.setX(x);
                    this.to.setY(y);
                    this.to.setZ(z);

                    this.lastDeltaX = this.deltaX;
                    this.lastDeltaY = this.deltaY;
                    this.lastDeltaZ = this.deltaZ;

                    this.deltaY = this.to.getY() - this.from.getY();
                    this.deltaX = this.to.getX() - this.from.getX();
                    this.deltaZ = this.to.getZ() - this.from.getZ();

                    this.deltaXAbs = Math.abs(this.deltaX);
                    this.deltaZAbs = Math.abs(this.deltaZ);
                    this.deltaYAbs = Math.abs(this.deltaY);

                    this.lastDeltaXZ = this.deltaXZ;

                    this.deltaXZ = Math.hypot(this.deltaXAbs, this.deltaZAbs);
                }

                if (flyingPacket.hasRotationChanged()) {

                    this.fromFrom.setYaw(this.from.getYaw());
                    this.fromFrom.setPitch(this.from.getPitch());

                    this.from.setYaw(this.to.getYaw());
                    this.from.setPitch(this.to.getPitch());

                    this.to.setPitch(pitch);
                    this.to.setYaw(yaw);

                    this.lastDeltaYaw = this.deltaYaw;
                    this.lastDeltaPitch = this.deltaPitch;

                    this.deltaYaw = this.to.getYaw() - this.from.getYaw();
                    this.deltaPitch = this.to.getPitch() - this.from.getPitch();

                    this.lastDeltaYawAbs = this.deltaYawAbs;
                    this.lastDeltaPitchAbs = this.deltaPitchAbs;

                    this.deltaYawAbs = Math.abs(this.to.getYaw() - this.from.getYaw());
                    this.deltaPitchAbs = Math.abs(this.to.getPitch() - this.from.getPitch());

                    this.lastDeltaY = this.deltaY;
                    this.deltaY = getData().getCurrentLocation().getY() - getData().getLastLocation().getY();
                    this.deltaX = getData().getCurrentLocation().getX() - getData().getLastLocation().getX();
                    this.deltaZ = getData().getCurrentLocation().getZ() - getData().getLastLocation().getZ();
                    this.lastDeltaXZ = this.deltaXZ;
                    this.deltaXZ = Math.hypot(this.deltaX, this.deltaZ);
                }

                setInLiquid(getData().getPlayer().getLocation().getBlock().isLiquid());

                if (getData().getPlayer().isInsideVehicle()) {
                    if (getData().getVehicleTicks() < 20) {
                        getData().setVehicleTicks(getData().getVehicleTicks() + 1);
                    }
                } else {
                    if (getData().getVehicleTicks() > 0) {
                        getData().setVehicleTicks(getData().getVehicleTicks() - 1);
                    }
                }

                ++this.tick;
                break;
            }
        }
    }

    @Override
    public void onPacket(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ATTACH_ENTITY) {
            getData().getVehicleTimer().reset();
            getData().setVehicleTicks(20);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
            WrapperPlayServerEntityVelocity wrapper = new WrapperPlayServerEntityVelocity(event);
            if (wrapper.getEntityId() == getData().getPlayer().getEntityId()) {
                this.velocityTimer.reset();

                setVelH(Math.hypot(wrapper.getVelocity().getX(), wrapper.getVelocity().getZ()));
                setVelY(wrapper.getVelocity().getY());
            }
        }
    }

    @Override
    public void setupTimers(PlayerData playerData) {
        this.velocityTimer = new EventTimer(20, playerData);
        this.inLiquid = false;
        this.wasInLiquid = false;
    }

    public void setInLiquid(boolean inLiquid) {
        this.wasInLiquid = this.inLiquid;
        this.inLiquid = inLiquid;
    }
}
