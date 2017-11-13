package net.teamfruit.rerecipecreators.serialization;

import static com.google.gson.stream.JsonToken.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import guava10.com.google.common.collect.Maps;

public class ObjectHandler {
	public static final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(ItemStack.class, new ItemStackTypeAdapter()).create();

	public static <T> void write(final File file, final Class<T> clazz, final T c) throws IOException, JsonIOException {
		JsonWriter writer = null;
		try {
			writer = new JsonWriter(new FileWriter(file));
			writer.setIndent("\t");
			gson.toJson(c, clazz, writer);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public static <T> T read(final File file, final Class<T> clazz) throws FileNotFoundException, JsonIOException, JsonSyntaxException {
		JsonReader reader = null;
		try {
			reader = new JsonReader(new FileReader(file));
			return gson.fromJson(reader, clazz);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	public static class ItemStackTypeAdapter extends TypeAdapter<ItemStack> {
		@Override
		public void write(final JsonWriter out, final ItemStack value) throws IOException {
			if (value==null)
				out.nullValue();
			else {
				out.beginObject();

				out.name("type").value(value.getType().name());

				final int durability = value.getDurability();
				if (durability!=0)
					out.name("damage").value(durability);

				final int amount = value.getAmount();
				if (amount!=1)
					out.name("amount").value(amount);

				final Map<Enchantment, Integer> enchants = value.getEnchantments();
				if (!enchants.isEmpty()) {
					out.name("enchants");
					out.beginObject();
					for (final Entry<Enchantment, Integer> entry : enchants.entrySet())
						out.name(entry.getKey().getName()).value(entry.getValue());
					out.endObject();
				}

				out.endObject();
			}
		}

		@Override
		public ItemStack read(final JsonReader in) throws IOException {
			in.beginObject();
			Material type = null;
			short damage = 0;
			int amount = 1;
			Map<Enchantment, Integer> enchants = null;
			while (in.hasNext()) {
				final String line = in.nextName();
				if (line.equals("type"))
					type = Material.getMaterial(in.nextString());
				else if (line.equals("damage"))
					damage = (short) in.nextInt();
				else if (line.equals("amount"))
					amount = in.nextInt();
				else if (line.equals("enchants")) {
					enchants = Maps.newHashMap();
					in.beginObject();
					while (in.peek()!=END_OBJECT) {
						final Enchantment enchant = Enchantment.getByName(in.nextName());
						if (enchant!=null)
							enchants.put(enchant, in.nextInt());
					}
					in.endObject();
				}
			}
			in.endObject();

			final ItemStack result = new ItemStack(type, amount, damage);
			if (enchants!=null)
				result.addEnchantments(enchants);

			return result;
		}
	}
}
