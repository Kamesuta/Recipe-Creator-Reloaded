package com.fiscalleti.recipecreator.serialization;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class IS2SIS {
    public static SerializableItemStack[] TO_SIS(ItemStack[] itemStacks){
        SerializableItemStack[] cb = new SerializableItemStack[itemStacks.length];
        int i = 0;
        for(ItemStack is : itemStacks){
            cb[i] = new SerializableItemStack((is == null) ? new ItemStack(Material.AIR) : is);
            i++;
        }
        return cb;
    }
}
