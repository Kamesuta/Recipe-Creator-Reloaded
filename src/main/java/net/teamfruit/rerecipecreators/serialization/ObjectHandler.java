package net.teamfruit.rerecipecreators.serialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ObjectHandler {
	public static final Gson gson = new GsonBuilder()
			.registerTypeAdapterFactory(new ItemStackTypeAdapter())
			.create();

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

	public static class ItemStackTypeAdapter implements TypeAdapterFactory {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
			final Class<? super T> rawType = type.getRawType();
			if (!ItemStack.class.isAssignableFrom(rawType))
				return null;

			final Gson gson2 = new GsonBuilder()
					.registerTypeAdapterFactory(new TypedMapTypeAdapter())
					.create();

			final TypeAdapter<Map> mapadapter = gson2.getAdapter(Map.class);

			return (TypeAdapter<T>) new TypeAdapter<ItemStack>() {
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

						final ItemMeta itemMeta = value.getItemMeta();
						final Map<String, Object> meta = Maps.newHashMap(itemMeta.serialize());
						if (!meta.isEmpty()) {
							meta.put("==", ConfigurationSerialization.getAlias(itemMeta.getClass()));
							out.name("meta");
							mapadapter.write(out, meta);
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
					Map<String, Object> meta = null;
					while (in.hasNext()) {
						final String line = in.nextName();
						if (line.equals("type"))
							type = Material.getMaterial(in.nextString());
						else if (line.equals("damage"))
							damage = (short) in.nextInt();
						else if (line.equals("amount"))
							amount = in.nextInt();
						else if (line.equals("meta"))
							meta = mapadapter.read(in);
					}
					in.endObject();

					final ItemStack result = new ItemStack(type, amount, damage);
					if (meta!=null)
						result.setItemMeta((ItemMeta) ConfigurationSerialization.deserializeObject(meta));

					return result;
				}
			};
		}
	}

	public static class TypedMapTypeAdapter implements TypeAdapterFactory {
		@SuppressWarnings({ "unchecked" })
		@Override
		public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
			final Class<? super T> rawType = type.getRawType();
			if (!Map.class.isAssignableFrom(rawType))
				return null;

			final TypeAdapter<JsonElement> jsonadapter = gson.getAdapter(JsonElement.class);

			return (TypeAdapter<T>) new TypeAdapter<Map<String, Object>>() {
				@Override
				public void write(final JsonWriter out, final Map<String, Object> value) throws IOException {
					if (value==null)
						out.nullValue();
					else {
						out.beginObject();
						for (final Entry<String, Object> entry : value.entrySet()) {
							out.name(entry.getKey());
							final Object obj = entry.getValue();
							if (obj==null)
								out.nullValue();
							else if (obj instanceof Integer)
								out.value((Integer) obj);
							else if (obj instanceof Boolean)
								out.value((Boolean) obj);
							else if (obj instanceof String)
								out.value((String) obj);
							else if (obj instanceof Map) {
								out.beginObject();
								out.name("data");
								write(out, (Map<String, Object>) obj);
								out.name("type").value("map");
								out.endObject();
							} else {
								out.beginObject();
								out.name("data");
								final Class<?> clazz = obj.getClass();
								((TypeAdapter<Object>) gson.getAdapter(clazz)).write(out, obj);
								out.name("class").value(clazz.getName());
								out.endObject();
							}
						}
						out.endObject();
					}
				}

				@Override
				public Map<String, Object> read(final JsonReader in) throws IOException {
					final Map<String, Object> result = Maps.newHashMap();
					in.beginObject();
					while (in.hasNext()) {
						final String name = in.nextName();
						switch (in.peek()) {
							case NULL:
								result.put(name, null);
								break;
							case NUMBER:
								result.put(name, in.nextInt());
								break;
							case BOOLEAN:
								result.put(name, in.nextBoolean());
								break;
							case STRING:
								result.put(name, in.nextString());
								break;
							case BEGIN_OBJECT:
								in.beginObject();
								String type = null;
								String clazz = null;
								JsonElement data = null;
								while (in.hasNext()) {
									final String tag = in.nextName();
									if (tag.equals("type"))
										type = in.nextString();
									else if (tag.equals("class"))
										clazz = in.nextString();
									else if (tag.equals("data"))
										data = jsonadapter.read(in);
								}
								if (data!=null)
									if (StringUtils.equals(type, "map"))
										result.put(name, fromJsonTree(data));
									else if (!StringUtils.isEmpty(clazz))
										try {
											final Class<?> clazz1 = Class.forName(clazz);
											result.put(name, gson.getAdapter(clazz1).fromJsonTree(data));
										} catch (final Exception e) {
											e.printStackTrace();
										}
								in.endObject();
								break;
							default:
								break;
						}
					}
					in.endObject();
					return result;
				}
			};
		}
	}
}
