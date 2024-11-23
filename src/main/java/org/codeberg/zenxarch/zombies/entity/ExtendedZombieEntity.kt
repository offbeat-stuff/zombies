package org.codeberg.zenxarch.zombies.entity

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityData
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import net.minecraft.world.WorldView
import org.codeberg.zenxarch.zombies.ZombieGamerules
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty
import org.codeberg.zenxarch.zombies.spawning.SpawnerProvider
import org.codeberg.zenxarch.zombies.spawning.ZombieApocalypse

class ExtendedZombieEntity(world: World?, private val template: ZombieTemplate) : ZombieEntity(world) {
    override fun burnsInDaylight(): Boolean {
        return when (world) {
            is ServerWorld -> (world as ServerWorld).gameRules.getBoolean(ZombieGamerules.ZOMBIES_BURN_IN_DAYLIGHT)
            else -> false
        }
    }

    protected fun getExtentedDifficulty(serverWorld: ServerWorldAccess): ExtendedDifficulty {
        return ExtendedDifficulty(serverWorld.toServerWorld(), this.blockPos)
    }

    fun initialize(world: ServerWorldAccess) {
        this.initialize(world, world.getLocalDifficulty(this.blockPos), SpawnReason.NATURAL, null)
    }

    override fun initialize(
        world: ServerWorldAccess,
        difficulty: LocalDifficulty,
        spawnReason: SpawnReason,
        entityData: EntityData?
    ): EntityData? {
        val entityData = ZombieData(false, false)
        return super.initialize(world, difficulty, spawnReason, entityData)
    }

    override fun initEquipment(random: Random, unused: LocalDifficulty) {}

    override fun updateEnchantments(
        world: ServerWorldAccess, random: Random, unused: LocalDifficulty
    ) {
        val difficulty = getExtentedDifficulty(world)

        template.initEquipment(world.toServerWorld(), this, difficulty, random)
    }

    override fun damage(world: ServerWorld, source: DamageSource, amount: Float): Boolean {
        if (!super.damage(world, source, amount)) return false
        if (world !is SpawnerProvider) return false
        for (spawner in world.spawners) spawner.spawn(world, true)
        return true
    }

    override fun tryAttack(world: ServerWorld, target: Entity): Boolean {
        val result = super.tryAttack(world, target)
        if (result && target is LivingEntity) {
            template.onAttackEffect().run(world, this, target)
        }
        return result
    }

    override fun onKilledBy(adversary: LivingEntity?) {
        if (world is ServerWorld) template.onKillEffect().run(world as ServerWorld, this, adversary)
        super.onKilledBy(adversary)
    }

    override fun tick() {
        if (world is ServerWorld) template.onTickEffect().run(world as ServerWorld, this, null)
        super.tick()
    }

    override fun applyAttributeModifiers(chanceMultiplier: Float) {
        super.applyAttributeModifiers(chanceMultiplier)
        val spawnAttribute = this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS)
        spawnAttribute!!.baseValue = 0.0
        spawnAttribute.removeModifier(Identifier.ofVanilla("leader_zombie_bonus"))
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putString(ZombieApocalypse.ZOMBIE_ID_KEY, ZombieRegistry.getId(this.template))
    }

    override fun canSpawn(world: WorldView): Boolean {
        return world.doesNotIntersectEntities(this)
                && world.isSpaceEmpty(this)
                && (this.canSpawnAsReinforcementInFluid() || !world.containsFluid(this.boundingBox))
    }
}
