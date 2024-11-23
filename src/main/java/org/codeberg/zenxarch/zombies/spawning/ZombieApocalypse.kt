package org.codeberg.zenxarch.zombies.spawning

import net.minecraft.entity.Entity
import net.minecraft.nbt.NbtCompound
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Difficulty
import net.minecraft.world.World
import org.codeberg.zenxarch.zombies.DEBUG
import org.codeberg.zenxarch.zombies.ZombieGamerules
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity
import org.codeberg.zenxarch.zombies.entity.ZombieRegistry
import java.util.*
import java.util.stream.Stream

class ZombieApocalypse(private var world: ServerWorld) {
    private var zombieCount: Map<BlockPos, Int>? = null

    private fun spawnZombie(zombie: ExtendedZombieEntity) {
        zombie.target = world.getClosestPlayer(
            zombie.x,
            zombie.y,
            zombie.z,
            64.0,
            EntityPredicates.VALID_LIVING_ENTITY.and(EntityPredicates.EXCEPT_SPECTATOR)
        )
        zombie.initialize(this.world)
        world.spawnEntityAndPassengers(zombie)
    }

    private fun spawnZombie(pos: BlockPos) {
        ZombieRegistry.selectTemplate(world.getRandom(), world.getBiome(pos))
            .flatMap { ZombieRegistry.newZombie(world, it, pos) }
            .ifPresent { zombie: ExtendedZombieEntity -> this.spawnZombie(zombie) }
    }

    fun spawnZombiesAt(playerPos: BlockPos, positions: List<BlockPos>?) {
        val difficulty = ExtendedDifficulty(this.world, playerPos)
        val toSpawn = difficulty.maxZombies
        if (toSpawn <= zombieCount!!.getOrDefault(playerPos, 0)) return

        if (positions != null) {
            SpawnProvider.giveSpawnPositions(world, playerPos, positions, toSpawn)
                .ifPresent { pos: BlockPos -> this.spawnZombie(pos) }
        }
    }

    fun countZombies(positions: List<BlockPos>): Map<BlockPos, Int> {
        val result = HashMap<BlockPos, Int>()
        for (pos in positions) result[pos] = 0
        for (entity in world.iterateEntities()) {
            if (entity !is ExtendedZombieEntity) continue
            for (pos in positions) if (entity.getBlockPos().isWithinDistance(pos, 128.0)) result[pos] =
                result[pos]!! + 1
        }
        return result
    }

    private fun spawnCenters(): Stream<BlockPos> {
        return players(this.world).stream().map { obj: ServerPlayerEntity -> obj.blockPos }
    }

    private fun debugCheck(zombieCount: Map<BlockPos, Int>) {
        for (player in players(this.world)) {
            val pos = player.blockPos
            val difficulty = (ExtendedDifficulty(world, pos).clampedLocalDifficulty * 100).toInt()
            val zcount = zombieCount.getOrDefault(pos, 0)
            player.sendMessage(Text.of("$difficulty : $zcount"), true)
        }
    }

    fun spawn(world: ServerWorld, spawnMonsters: Boolean) {
        this.world = world
        if (!spawnMonsters || this.world.difficulty == Difficulty.PEACEFUL
            || !this.world.gameRules.getBoolean(ZombieGamerules.DO_ZOMBIE_SPAWNING)
        ) {
            return
        }

        val positions = spawnCenters().toList()
        this.zombieCount = countZombies(positions)

        if (DEBUG) debugCheck(zombieCount!!)

        for (v in positions) {
            spawnZombiesAt(v, positions)
        }
    }

    companion object {
        @JvmStatic
        fun players(world: ServerWorld)=
            world.getPlayers(
                EntityPredicates.VALID_LIVING_ENTITY.and(EntityPredicates.EXCEPT_SPECTATOR)
            )

        @JvmStatic
        fun isApocalypticWorld(world: ServerWorld)=
            world.registryKey == World.OVERWORLD

        const val ZOMBIE_ID_KEY: String = "zenxarch_zombie_id"

        private fun loadFromNbt(world: World?, nbt: NbtCompound, id: String): Entity =
            ExtendedZombieEntity(world, ZombieRegistry.getTemplate(id)).apply {
                readNbt(nbt)
            }

        @JvmStatic
        fun loadFromNbt(nbt: NbtCompound, world: World?) =
            Optional.of(nbt.getString(ZOMBIE_ID_KEY)).filter(String::isNotBlank).map { loadFromNbt(world,nbt,it) }
    }
}
