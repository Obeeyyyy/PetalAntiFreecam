package com.boggy.petalantifreecam.packet;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;

public record AirBlockStateIds(int air, int caveAir, int voidAir) {

    public static AirBlockStateIds forVersion(ClientVersion version) {
        return new AirBlockStateIds(
                globalId(version, StateTypes.AIR),
                globalId(version, StateTypes.CAVE_AIR),
                globalId(version, StateTypes.VOID_AIR)
        );
    }

    public boolean contains(int blockStateId) {
        return blockStateId == air || blockStateId == caveAir || blockStateId == voidAir;
    }

    private static int globalId(ClientVersion version, StateType stateType) {
        return WrappedBlockState.getDefaultState(version, stateType).getGlobalId();
    }
}
