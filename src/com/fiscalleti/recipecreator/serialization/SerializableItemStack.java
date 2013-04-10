package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * A serializable ItemStack
 */
public class SerializableItemStack implements Serializable {
    private static final long serialVersionUID = 729890133797629669L;

    private final int type, amount;
    private final short damage;

    private final Map<SerializableEnchantment, Integer> enchants;

    public SerializableItemStack(ItemStack item) {
        this.type = item.getTypeId();
        this.amount = item.getAmount();
        this.damage = item.getDurability();

        this.enchants = new HashMap<SerializableEnchantment, Integer>();

        Map<Enchantment, Integer> enchantments = item.getEnchantments();

        for(Enchantment enchantment : enchantments.keySet()) {
            this.enchants.put(new SerializableEnchantment(enchantment), enchantments.get(enchantment));
        }
    }

    public ItemStack unbox() {
        ItemStack item = new ItemStack(type, amount, damage);

        HashMap<Enchantment, Integer> map = new HashMap<Enchantment, Integer>();

        for(SerializableEnchantment cEnchantment : enchants.keySet()) {
            map.put(cEnchantment.unbox(), this.enchants.get(cEnchantment));
        }

        item.addUnsafeEnchantments(map);

        return item;
    }
}
