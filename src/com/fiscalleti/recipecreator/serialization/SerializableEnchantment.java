package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;

import org.bukkit.enchantments.Enchantment;

/**
 * A serializable Enchantment
 * @author NuclearW
 */
public class SerializableEnchantment implements Serializable {
    private static final long serialVersionUID = 8973856768102665381L;

    private final int id;

    public SerializableEnchantment(Enchantment enchantment) {
        this.id = enchantment.getId();
    }

    public Enchantment unbox() {
        return Enchantment.getById(this.id);
    }
}
