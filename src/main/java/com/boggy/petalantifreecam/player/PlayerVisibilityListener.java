package com.boggy.petalantifreecam.player;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public final class PlayerVisibilityListener implements Listener {

    private final PlayerVisibilityManager visibilityService;

    public PlayerVisibilityListener(PlayerVisibilityManager visibilityService) {
        this.visibilityService = visibilityService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        visibilityService.track(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        visibilityService.untrack(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        updateWhenHeightChanges(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        updateWhenHeightChanges(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        visibilityService.update(event.getPlayer(), event.getPlayer().getLocation());
    }

    private void updateWhenHeightChanges(PlayerMoveEvent event) {
        Location destination = event.getTo();
        if (event.getFrom().getWorld().equals(destination.getWorld()) &&
                event.getFrom().getBlockY() == destination.getBlockY()) return;

        visibilityService.update(event.getPlayer(), destination);
    }
}
