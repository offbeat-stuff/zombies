Main project files

├── data - registry stuff
│   └── ZBiomeTags.java 
        - Tags which are used in the project
        - TODO: move datagen exclusives to datagen
├── DebugCommands.java
├── difficulty
│   ├── DifficultyCalculations.java
│   ├── ExtendedDifficulty.java
│   └── ItemAttributeUtils.java
├── entity
│   ├── ExtendedZombieEntity.java
│   └── ZombieVariants.java
├── loot_table
│   ├── condition
│   │   ├── TimeCheckLootCondition.java
│   │   └── ZombieLootConditionTypes.java
│   ├── function
│   │   ├── EnchantmentProviderLootFunction.java
│   │   └── ZombieLootFunctionTypes.java
│   └── number_provider
│       ├── LuckLootNumberProvider.java
│       └── ZombieLootNumberProviderTypes.java
├── math
│   ├── IntRange.java
│   ├── RandomRange.java
│   └── RandomUtils.java
├── mixin
│   ├── EntityTypeMixin.java
│   ├── ServerChunkManagerMixin.java
│   └── ServerWorldMixin.java
├── spawning
│   ├── provider
│   │   ├── PosRangeProvider.java
│   │   └── SpawnPosProvider.java
│   ├── SpawnerProvider.java
│   ├── SpawnProvider.java
│   ├── SpawnUtils.java
│   ├── ZombieApocalypse.java
│   ├── ZombieDensityMap.java
│   └── ZombieNbtUtils.java
├── ZombieDatapacks.java
├── ZombieGamerules.java
└── Zombies.java


Mob variants api

├── mixin
│   ├── EntityConversionTypeInvoker.java
│   ├── LivingEntityAccessor.java
│   ├── LivingEntityMixin.java
│   └── MobEntityMixin.java
├── registry
│   ├── ZombieRegistries.java
│   └── ZombieRegistryKeys.java
├── spawn_conditions
│   ├── AllOfSpawnCondition.java
│   ├── DaySpawnCondition.java
│   ├── NegateSpawnCondition.java
│   ├── NightSpawnCondition.java
│   ├── PrecipitationSpawnCondition.java
│   ├── RainingSpawnCondition.java
│   ├── ThunderingSpawnCondition.java
│   └── ZombieSpawnConditions.java
└── variant
    ├── effect
    │   ├── LivingEffect.java
    │   ├── MobEffect.java
    │   ├── pair
    │   │   ├── AllOfMobEffect.java
    │   │   ├── ConditionalSpawnEffect.java
    │   │   ├── ConvertToEntityTypeEffect.java
    │   │   ├── DefaultMobEffect.java
    │   │   ├── HealFromDamage.java
    │   │   ├── RandomMobEffect.java
    │   │   ├── SingleMobEffect.java
    │   │   ├── StatusMobEffect.java
    │   │   └── SwapMobEffect.java
    │   ├── single
    │   │   ├── AttributeModifierEffect.java
    │   │   ├── DefaultAttributeEffect.java
    │   │   ├── DefaultLivingEffect.java
    │   │   ├── ExplosionEffect.java
    │   │   ├── FreezeEffect.java
    │   │   ├── IgniteEffect.java
    │   │   ├── SpawnEffectCloudEffect.java
    │   │   ├── SpawnParticleEffect.java
    │   │   └── StatusLivingEffect.java
    │   └── util
    │       └── StatusEffectInstanceBuilder.java
    ├── MobAttachments.java
    └── MobVariant.java