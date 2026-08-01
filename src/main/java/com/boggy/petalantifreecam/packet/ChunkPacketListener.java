package com.boggy.petalantifreecam.packet;

import com.boggy.petalantifreecam.config.AntiFreecamSettings;
import com.boggy.petalantifreecam.config.ConfigurationManager;
import com.boggy.petalantifreecam.player.PlayerVisibilityManager;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;

public final class ChunkPacketListener implements PacketListener {

    private final PlayerVisibilityManager visibilityService;
    private final ConfigurationManager configurationManager;
    private final ChunkMasker chunkMasker;

    public ChunkPacketListener(PlayerVisibilityManager visibilityService, ConfigurationManager configurationManager, ChunkMasker chunkMasker) {
        this.visibilityService = visibilityService;
        this.configurationManager = configurationManager;
        this.chunkMasker = chunkMasker;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.CHUNK_DATA) return;
        if (!visibilityService.isMasking(event.getUser().getUUID())) return;

        AntiFreecamSettings settings = configurationManager.current();
        int minimumWorldY = event.getUser().getMinWorldHeight();

        if (settings.hideBlocksBelowY() <= minimumWorldY) return;

        WrapperPlayServerChunkData chunkPacket = new WrapperPlayServerChunkData(event);
        Column maskedColumn = chunkMasker.mask(chunkPacket.getColumn(), minimumWorldY, settings.hideBlocksBelowY());
        chunkPacket.setColumn(maskedColumn);

        event.markForReEncode(true);
    }
}