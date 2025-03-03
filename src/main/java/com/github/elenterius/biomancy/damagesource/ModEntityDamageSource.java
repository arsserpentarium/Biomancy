package com.github.elenterius.biomancy.damagesource;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class ModEntityDamageSource extends EntityDamageSource {

	public String statusProc;
	private float attackStrength;

	public ModEntityDamageSource(String damageCause, String statusProcIn, @Nullable Entity damageSourceEntityIn, float attackStrengthIn) {
		super(damageCause, damageSourceEntityIn);
		attackStrength = attackStrengthIn;
		statusProc = statusProcIn;
	}

	public float getAttackStrength() {
		return attackStrength;
	}

	public void updateAttackStrength(float damageAmount) {
		attackStrength = damageAmount / attackStrength;
	}

	@Override
	public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn) {
		ItemStack itemstack = damageSourceEntity instanceof LivingEntity ? ((LivingEntity) damageSourceEntity).getHeldItemMainhand() : ItemStack.EMPTY;
		String str = "death.attack." + BiomancyMod.MOD_ID + "." + damageType;
		ITextComponent entityDisplayName = damageSourceEntity != null ? damageSourceEntity.getDisplayName() : new StringTextComponent("Foobar").setStyle(Style.EMPTY.setObfuscated(true));
		return !itemstack.isEmpty() && itemstack.hasDisplayName() ? new TranslationTextComponent(str + ".item", entityLivingBaseIn.getDisplayName(), entityDisplayName, itemstack.getTextComponent()) : new TranslationTextComponent(str, entityLivingBaseIn.getDisplayName(), entityDisplayName);
	}

}
