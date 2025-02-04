package net.forcemaster_rpg.custom.custom_spells;

import net.forcemaster_rpg.effect.Effects;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.spell_engine.api.spell.event.CustomSpellHandler;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.utils.SoundHelper;
import net.spell_engine.utils.TargetHelper;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static net.forcemaster_rpg.ForcemasterClassMod.MOD_ID;

public class CustomSpells {
    public static void register() {

        int speed_belial_smashing = 4;
        double knockup_burstcrack = 0.35;
        double knockup_burstcrack_stonehand = 0.65;

        /////FORCEMASTER_SPELLS
        /// BURSTCRACK
        CustomSpellHandler.register(Identifier.of(MOD_ID, "burstcrack"), (data) -> {
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            var spellEntry = SpellRegistry.from(data1.caster().getWorld()).getEntry(Identifier.of(MOD_ID, "burstcrack")).orElse(null);
            var spell = spellEntry.value();
            Spell.Impact[] impacts = spell.impact;

            Predicate<Entity> selectionPredicate = (target2) -> {
                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, data1.caster(), target2)
                );
            };
            if (!data1.caster().getWorld().isClient) {
                List<Entity> list = data1.caster().getWorld().getOtherEntities(data1.caster(), data1.caster().getBoundingBox().expand(spell.range), selectionPredicate);
                for (Entity entity : list) {
                    SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity, spellEntry,impacts ,data1.impactContext());
                    SoundHelper.playSound(data1.caster().getWorld(), entity, spell.impact[0].sound);
                    Vec3d currentMovement = entity.getVelocity();
                    entity.setVelocity(currentMovement.x, currentMovement.y + knockup_burstcrack, currentMovement.z);
                    entity.velocityModified = true;
                    if (data1.caster().hasStatusEffect(Effects.STONE_HAND.registryEntry)) {
                        SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity, spellEntry,impacts ,data1.impactContext());
                        SoundHelper.playSound(data1.caster().getWorld(), entity, spell.impact[0].sound);
                        Vec3d currentMovement2 = entity.getVelocity();
                        entity.setVelocity(currentMovement2.x, currentMovement2.y + knockup_burstcrack_stonehand, currentMovement2.z);
                        entity.velocityModified = true;

                    }
                }
            }
            return true;
        });
        /// BELIAL SMASHING
        CustomSpellHandler.register(Identifier.of(MOD_ID, "belial_smashing"), (data) -> {
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            var spellEntry = SpellRegistry.from(data1.caster().getWorld()).getEntry(Identifier.of(MOD_ID, "belial_smashing")).orElse(null);
            var spell = spellEntry.value();
            Spell.Impact[] impacts = spell.impact;

            List<Entity> list = TargetHelper.targetsFromRaycast(data1.caster(), spell.range, Objects::nonNull);
            if (!data1.caster().getWorld().isClient) {
                data1.caster().velocityDirty = true;
                data1.caster().velocityModified = true;
                Vec3d rotationVector = data1.caster().getRotationVector();
                Vec3d velocity = data1.caster().getVelocity();
                data1.caster().addVelocity(rotationVector.x * 0.1 + (rotationVector.x * 2.5 - velocity.x) * speed_belial_smashing, 0, rotationVector.z * 0.1 + (rotationVector.z * 2.5 - velocity.z) * speed_belial_smashing);
                for (Entity entity : list) {
                    SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity, spellEntry,impacts ,data1.impactContext());
                    Vec3d currentMovement2 = entity.getVelocity();
                    entity.setVelocity(currentMovement2.x, currentMovement2.y + 0.6f, currentMovement2.z);
                }
            }
            return true;
        });

    }
}
