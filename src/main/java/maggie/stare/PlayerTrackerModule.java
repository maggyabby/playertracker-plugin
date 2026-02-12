package maggie.stare;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.render.EventRender2D;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.client.api.utils.RotationUtils;
import org.rusherhack.core.event.subscribe.Subscribe;


public class PlayerTrackerModule extends ToggleableModule {

    Player whoToFollow = null;

    public PlayerTrackerModule() {
        super("PlayerTracker", "Lock eyes on players that are moving around and make your gaze follow them", ModuleCategory.MOVEMENT);
    }

    @Subscribe
    private void onFrameRender(EventRender2D event) {
        if (mc.player == null || mc.level == null) return;
        if (whoToFollow == null || !mc.level.players().contains(whoToFollow)) {
            ChatUtils.print("Target out of render distance");
            this.setToggled(false);
            return;
        }

        float[] rots = RotationUtils.getRotations(whoToFollow.getEyePosition());
        mc.player.setYRot(rots[0]);
        mc.player.setXRot(rots[1]);
        mc.player.yRotO = rots[0];
        mc.player.xRotO = rots[1];

    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.level == null) return;

        Vec3 selfEyePos = mc.player.getEyePosition();
        Vec3 selfEyeVector = mc.player.getViewVector(1.0F).normalize();

        double bestDist = Double.MAX_VALUE;
        Player best = null;

        for (Player player : mc.level.players()) {
            if (player == mc.player) continue;

            Vec3 playerPos = player.getEyePosition();
            double t = playerPos.subtract(selfEyePos).dot(selfEyeVector);
            if (t <= 0) continue;

            Vec3 closestPoint = selfEyePos.add(selfEyeVector.scale(t));
            double dist = playerPos.distanceTo(closestPoint);

            if (dist < bestDist) {
                bestDist = dist;
                best = player;
            }
        }

        if (best != null) {
            whoToFollow = best;
            RusherHackAPI.getNotificationManager().info("Locked on to " + best.getGameProfile().getName());
        } else {
            RusherHackAPI.getNotificationManager().warn("No players in render distance");
            this.setToggled(false);
        }
    }

}
