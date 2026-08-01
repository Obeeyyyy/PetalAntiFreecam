package com.boggy.petalantifreecam.packet;

import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.chunk.TileEntity;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.SingletonPalette;

public final class ChunkMasker {

    private static final int SECTION_HEIGHT = 16;
    private final AirBlockStateIds airBlockStateIds;
    private final SingletonPalette airPalette;

    public ChunkMasker(AirBlockStateIds airBlockStateIds) {
        this.airBlockStateIds = airBlockStateIds;
        this.airPalette = new SingletonPalette(airBlockStateIds.air());
    }

    public Column mask(Column column, int minimumWorldY, int hideBlocksBelowY) {
        BaseChunk[] sections = column.getChunks();

        int hiddenHeight = Math.clamp(hideBlocksBelowY - minimumWorldY, 0, sections.length * SECTION_HEIGHT);
        int completelyHiddenSections = hiddenHeight / SECTION_HEIGHT;
        int hiddenLayersInNextSection = hiddenHeight % SECTION_HEIGHT;

        for (int sectionIndex = 0; sectionIndex < completelyHiddenSections; sectionIndex++) {
            clearSection(sections[sectionIndex]);
        }

        if (hiddenLayersInNextSection > 0 && completelyHiddenSections < sections.length) {
            clearLayers(sections[completelyHiddenSections], hiddenLayersInNextSection);
        }

        TileEntity[] tileEntities = column.getTileEntities();
        TileEntity[] visibleTileEntities = filterTileEntities(tileEntities, hideBlocksBelowY);

        if (visibleTileEntities == tileEntities) {
            return column;
        }

        return new Column(
                column.getX(),
                column.getZ(),
                column.isFullChunk(),
                sections,
                visibleTileEntities,
                column.getHeightmaps()
        );
    }

    @SuppressWarnings("DataFlowIssue") // for some reason IntelliJ thinks blockData.storage is non-null?
    private void clearSection(BaseChunk section) {
        if (!(section instanceof Chunk_v1_18 modernSection)) {
            return;
        }

        DataPalette blockData = modernSection.getChunkData();
        blockData.palette = airPalette;
        blockData.storage = null;
        modernSection.setBlockCount(0);
        modernSection.setFluidCount(0);
    }

    private void clearLayers(BaseChunk section, int layerCount) {
        if (!(section instanceof Chunk_v1_18 modernSection)) {
            return;
        }

        DataPalette blockData = modernSection.getChunkData();
        int removedBlocks = 0;
        for (int y = 0; y < layerCount; y++) {
            for (int z = 0; z < SECTION_HEIGHT; z++) {
                for (int x = 0; x < SECTION_HEIGHT; x++) {
                    int blockStateId = blockData.get(x, y, z);
                    if (!airBlockStateIds.contains(blockStateId)) {
                        removedBlocks++;
                    }
                    blockData.set(x, y, z, airBlockStateIds.air());
                }
            }
        }
        modernSection.setBlockCount(Math.max(0, modernSection.getBlockCount() - removedBlocks));
    }

    private TileEntity[] filterTileEntities(TileEntity[] tileEntities, int hideBlocksBelowY) {
        int visibleCount = 0;
        for (TileEntity tileEntity : tileEntities) {
            if (tileEntity.getY() >= hideBlocksBelowY) {
                visibleCount++;
            }
        }
        if (visibleCount == tileEntities.length) {
            return tileEntities;
        }

        TileEntity[] visibleTileEntities = new TileEntity[visibleCount];
        int destinationIndex = 0;
        for (TileEntity tileEntity : tileEntities) {
            if (tileEntity.getY() >= hideBlocksBelowY) {
                visibleTileEntities[destinationIndex++] = tileEntity;
            }
        }
        return visibleTileEntities;
    }
}
