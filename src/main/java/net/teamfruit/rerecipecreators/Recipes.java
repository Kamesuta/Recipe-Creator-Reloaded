package net.teamfruit.rerecipecreators;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

import net.teamfruit.rerecipecreators.Recipes.CustomRecipe.FurnaceCustomRecipe;
import net.teamfruit.rerecipecreators.Recipes.CustomRecipe.ShapedCustomRecipe;
import net.teamfruit.rerecipecreators.Recipes.CustomRecipe.ShapelessCustomRecipe;
import net.teamfruit.rerecipecreators.Recipes.RecipeIngredients.FurnaceRecipeIngredients;
import net.teamfruit.rerecipecreators.Recipes.RecipeIngredients.ShapedRecipeIngredients;
import net.teamfruit.rerecipecreators.Recipes.RecipeIngredients.ShapelessRecipeIngredients;
import net.teamfruit.rerecipecreators.serialization.SerializedRecipe;

public class Recipes {
	public static abstract class CommonRecipe<T extends Recipe> {
		public abstract boolean isValid();

		public abstract void fromInventoryItem(PlayerInventory playerinv);

		public abstract void fromCraftingInventoryItem(CraftingInventory craftinv);

		public abstract void fromRecipe(T recipe);

		public abstract void fromSerializedRecipe(SerializedRecipe recipe);

		protected static ItemStack slotItem(final Inventory inv, final int slot) {
			final ItemStack itemStack = inv.getItem(slot);
			return itemStack!=null ? itemStack : new ItemStack(Material.AIR, 1);
		}
	}

	public static abstract class RecipeIngredients<T extends Recipe> extends CommonRecipe<T> {
		private final RecipeType type;

		public RecipeIngredients(final RecipeType type) {
			this.type = type;
		}

		public RecipeType getType() {
			return this.type;
		}

		public abstract void toRecipe(T appliee);

		public static RecipeIngredients<? extends Recipe> createFromRecipe(final Recipe recipe) {
			if (recipe instanceof ShapedRecipe) {
				final ShapedRecipeIngredients ingredients = new ShapedRecipeIngredients();
				ingredients.fromRecipe((ShapedRecipe) recipe);
				return ingredients;
			} else if (recipe instanceof ShapelessRecipe) {
				final ShapelessRecipeIngredients ingredients = new ShapelessRecipeIngredients();
				ingredients.fromRecipe((ShapelessRecipe) recipe);
				return ingredients;
			} else if (recipe instanceof FurnaceRecipe) {
				final FurnaceRecipeIngredients ingredients = new FurnaceRecipeIngredients();
				ingredients.fromRecipe((FurnaceRecipe) recipe);
				return ingredients;
			}
			return null;
		}

		public static class ShapedRecipeIngredients extends RecipeIngredients<ShapedRecipe> {
			public ShapedRecipeIngredients() {
				super(RecipeType.SHAPED);
			}

			private final Table<Integer, Integer, Character> shape = HashBasedTable.create();
			private Map<MaterialData, Character> ingredientUnique = Maps.newHashMap();
			private Map<Character, MaterialData> ingredients = Maps.newHashMap();

			public void setIngredient(final int x, final int y, Character key, final MaterialData ingredient) {
				if (!(0<=x&&x<3&&0<=y&&y<3))
					return;
				if (ingredient==null||ingredient.getItemType()==Material.AIR)
					return;
				final Character ukey = this.ingredientUnique.get(ingredient);
				if (ukey==null)
					this.ingredientUnique.put(ingredient, key);
				else
					key = ukey;
				this.shape.put(x, y, key);
				if (key!=null&&!Character.isWhitespace(key))
					this.ingredients.put(key, ingredient);
			}

			private Character getShapeCharacter(final int x, final int y) {
				final Character material = this.shape.get(x, y);
				if (material!=null)
					return material;
				return ' ';
			}

			public Character[][] getShapeTable() {
				final Character[][] shapes = new Character[3][3];
				for (int y = 0; y<3; y++)
					for (int x = 0; x<3; x++)
						shapes[y][x] = getShapeCharacter(x, y);
				return shapes;
			}

			public String[] getShapeString() {
				final Character[][] shape = getShapeTable();
				final String[] shapestr = new String[shape.length];
				for (int i = 0; i<shape.length; i++)
					shapestr[i] = StringUtils.join(shape[i]);
				return shapestr;
			}

			public Map<Character, MaterialData> getIngredients() {
				return Collections.unmodifiableMap(this.ingredients);
			}

			@Override
			public boolean isValid() {
				return !this.ingredients.isEmpty();
			}

			@Override
			public void toRecipe(final ShapedRecipe appliee) {
				appliee.shape(getShapeString());
				for (final Entry<Character, MaterialData> ingred : this.ingredients.entrySet()) {
					final Character key = ingred.getKey();
					if (!Character.isWhitespace(key))
						appliee.setIngredient(key, ingred.getValue());
				}
			}

			@Override
			public void fromInventoryItem(final PlayerInventory playerinv) {
				//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
				setIngredient(0, 0, 'a', slotItem(playerinv, 9).getData());
				setIngredient(1, 0, 'b', slotItem(playerinv, 10).getData());
				setIngredient(2, 0, 'c', slotItem(playerinv, 11).getData());

				setIngredient(0, 1, 'd', slotItem(playerinv, 18).getData());
				setIngredient(1, 1, 'e', slotItem(playerinv, 19).getData());
				setIngredient(2, 1, 'f', slotItem(playerinv, 20).getData());

				setIngredient(0, 2, 'g', slotItem(playerinv, 27).getData());
				setIngredient(1, 2, 'h', slotItem(playerinv, 28).getData());
				setIngredient(2, 2, 'i', slotItem(playerinv, 29).getData());
			}

			@Override
			public void fromCraftingInventoryItem(final CraftingInventory craftinv) {
				setIngredient(0, 0, 'a', slotItem(craftinv, 1).getData());
				setIngredient(1, 0, 'b', slotItem(craftinv, 2).getData());
				setIngredient(2, 0, 'c', slotItem(craftinv, 3).getData());

				setIngredient(0, 1, 'd', slotItem(craftinv, 4).getData());
				setIngredient(1, 1, 'e', slotItem(craftinv, 5).getData());
				setIngredient(2, 1, 'f', slotItem(craftinv, 6).getData());

				setIngredient(0, 2, 'g', slotItem(craftinv, 7).getData());
				setIngredient(1, 2, 'h', slotItem(craftinv, 8).getData());
				setIngredient(2, 2, 'i', slotItem(craftinv, 9).getData());
			}

			@Override
			public void fromRecipe(final ShapedRecipe recipe) {
				final Map<Character, ItemStack> ingredients = recipe.getIngredientMap();
				int y = 0;
				for (final String shapestr : recipe.getShape()) {
					int x = 0;
					for (final char key : shapestr.toCharArray()) {
						if (!Character.isWhitespace(key)) {
							final ItemStack ingredient = ingredients.get(key);
							if (ingredient!=null&&ingredient.getType()!=Material.AIR)
								setIngredient(x, y, key, ingredient.getData());
						}
						x++;
					}
					y++;
				}
			}

			@Override
			public void fromSerializedRecipe(final SerializedRecipe recipe) {
				final Recipe r = recipe.getRecipe();
				if (r instanceof ShapedRecipe)
					fromRecipe((ShapedRecipe) r);
			}

			@Override
			public boolean equals(final Object obj) {
				if (this==obj)
					return true;
				if (obj==null)
					return false;
				if (!(obj instanceof ShapedRecipeIngredients))
					return false;
				final ShapedRecipeIngredients other = (ShapedRecipeIngredients) obj;
				if (this.ingredients==null) {
					if (other.ingredients!=null)
						return false;
				} else {
					final Character[][] shape0 = getShapeTable();
					final Character[][] shape1 = other.getShapeTable();
					if (shape0.length!=shape1.length)
						return false;
					final Map<Character, MaterialData> map0 = getIngredients();
					final Map<Character, MaterialData> map1 = other.getIngredients();
					for (int i = 0; i<shape0.length; i++) {
						final Character[] shape10 = shape0[i];
						final Character[] shape11 = shape1[i];
						if (shape10.length!=shape11.length)
							return false;
						for (int j = 0; j<shape10.length; j++) {
							final MaterialData item10 = map0.get(shape10[j]);
							final MaterialData item11 = map1.get(shape11[j]);
							if (item10!=null&&item11==null||item10==null&&item11!=null)
								return false;
							if (item10!=null&&item11!=null&&!item10.equals(item11))
								return false;
						}
					}
				}
				return true;
			}
		}

		public static class TrimmedShapedRecipeIngredients extends ShapedRecipeIngredients {
			@Override
			public Character[][] getShapeTable() {
				return processShape(super.getShapeTable());
			}

			public static Character[][] processShape(final Character[][] shape) {
				int sx = 0;
				int sy = 0;
				int vx = -1;
				int vy = -1;
				for (int i = 0; i<shape.length; i++)
					for (int j = 0; j<shape[0].length; j++) {
						if (!Character.isWhitespace(shape[i][j])) {
							if (vx<0)
								vx = i;
							sx = Math.max(sx, i+1-vx);
						}
						if (!Character.isWhitespace(shape[j][i])) {
							if (vy<0)
								vy = i;
							sy = Math.max(sy, i+1-vy);
						}
					}
				if (shape.length>0&&sx==shape[0].length&&sy==shape.length)
					return shape;
				final Character[][] trimed = new Character[sx][sy];
				for (int i = 0; i<sx; i++)
					for (int j = 0; j<sy; j++)
						trimed[i][j] = shape[i+vx][j+vy];
				return trimed;
			}
		}

		public static class ShapelessRecipeIngredients extends RecipeIngredients<ShapelessRecipe> {
			public ShapelessRecipeIngredients() {
				super(RecipeType.SHAPELESS);
			}

			private List<MaterialData> ingredients = Lists.newArrayList();

			public void addIngredient(final MaterialData ingredient) {
				if (ingredient==null||ingredient.getItemType()==Material.AIR)
					return;
				this.ingredients.add(ingredient);
			}

			public List<MaterialData> getIngredients() {
				return Collections.unmodifiableList(this.ingredients);
			}

			@Override
			public boolean isValid() {
				return !this.ingredients.isEmpty();
			}

			@Override
			public void toRecipe(final ShapelessRecipe appliee) {
				for (final MaterialData data : this.ingredients)
					appliee.addIngredient(data);
			}

			@Override
			public void fromInventoryItem(final PlayerInventory playerinv) {
				//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
				addIngredient(slotItem(playerinv, 9).getData());
				addIngredient(slotItem(playerinv, 10).getData());
				addIngredient(slotItem(playerinv, 11).getData());

				addIngredient(slotItem(playerinv, 18).getData());
				addIngredient(slotItem(playerinv, 19).getData());
				addIngredient(slotItem(playerinv, 20).getData());

				addIngredient(slotItem(playerinv, 27).getData());
				addIngredient(slotItem(playerinv, 28).getData());
				addIngredient(slotItem(playerinv, 29).getData());
			}

			@Override
			public void fromCraftingInventoryItem(final CraftingInventory craftinv) {
				for (final ItemStack ingredient : craftinv.getMatrix())
					if (ingredient!=null&&ingredient.getType()!=Material.AIR)
						addIngredient(ingredient.getData());
			}

			@Override
			public void fromRecipe(final ShapelessRecipe recipe) {
				for (final ItemStack ingredient : recipe.getIngredientList())
					if (ingredient!=null&&ingredient.getType()!=Material.AIR)
						addIngredient(ingredient.getData());
			}

			@Override
			public void fromSerializedRecipe(final SerializedRecipe recipe) {
				final Recipe r = recipe.getRecipe();
				if (r instanceof ShapelessRecipe)
					fromRecipe((ShapelessRecipe) r);
			}

			@Override
			public boolean equals(final Object obj) {
				if (this==obj)
					return true;
				if (obj==null)
					return false;
				if (!(obj instanceof ShapelessRecipeIngredients))
					return false;
				final ShapelessRecipeIngredients other = (ShapelessRecipeIngredients) obj;
				if (this.ingredients==null) {
					if (other.ingredients!=null)
						return false;
				} else {
					final Multiset<MaterialData> list0 = HashMultiset.create(getIngredients());
					final Multiset<MaterialData> list1 = HashMultiset.create(other.getIngredients());
					return list0.equals(list1);
				}
				return true;
			}
		}

		public static class FurnaceRecipeIngredients extends RecipeIngredients<FurnaceRecipe> {
			public FurnaceRecipeIngredients() {
				super(RecipeType.FURNACE);
			}

			private MaterialData ingredient;

			public void setIngredient(final MaterialData input) {
				if (this.ingredient==null||this.ingredient.getItemType()==Material.AIR)
					return;
				this.ingredient = input;
			}

			public MaterialData getIngredient() {
				return this.ingredient;
			}

			@Override
			public boolean isValid() {
				return this.ingredient!=null;
			}

			@Override
			public void toRecipe(final FurnaceRecipe appliee) {
				if (this.ingredient!=null)
					appliee.setInput(this.ingredient);
			}

			@Override
			public void fromInventoryItem(final PlayerInventory playerinv) {
				setIngredient(slotItem(playerinv, 29).getData());
			}

			@Override
			public void fromCraftingInventoryItem(final CraftingInventory craftinv) {
				for (final ItemStack ingredient : craftinv.getMatrix())
					if (ingredient!=null&&ingredient.getType()!=Material.AIR)
						setIngredient(ingredient.getData());
			}

			@Override
			public void fromRecipe(final FurnaceRecipe recipe) {
				final ItemStack itemStack = recipe.getInput();
				if (itemStack!=null&&itemStack.getType()!=Material.AIR)
					setIngredient(itemStack.getData());
			}

			@Override
			public void fromSerializedRecipe(final SerializedRecipe recipe) {
				final Recipe r = recipe.getRecipe();
				if (r instanceof FurnaceRecipe)
					fromRecipe((FurnaceRecipe) r);
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime*result+(this.ingredient==null ? 0 : this.ingredient.hashCode());
				return result;
			}

			@Override
			public boolean equals(final Object obj) {
				if (this==obj)
					return true;
				if (obj==null)
					return false;
				if (!(obj instanceof FurnaceRecipeIngredients))
					return false;
				final FurnaceRecipeIngredients other = (FurnaceRecipeIngredients) obj;
				if (this.ingredient==null) {
					if (other.ingredient!=null)
						return false;
				} else if (!this.ingredient.equals(other.ingredient))
					return false;
				return true;
			}
		};
	}

	public static class RecipeResult extends CommonRecipe<Recipe> {
		private ItemStack result;
		private ItemStack craft_result;

		public RecipeResult() {
		}

		@Override
		public boolean isValid() {
			return true;
		}

		public void setResult(final ItemStack result) {
			this.result = result;
		}

		public ItemStack getResult() {
			return this.result;
		}

		public void setCraftResult(final ItemStack craft_result) {
			this.craft_result = craft_result;
		}

		public ItemStack getCraftResult() {
			return this.craft_result;
		}

		@Override
		public void fromInventoryItem(final PlayerInventory playerinv) {
			final ItemStack cresult = slotItem(playerinv, 17);
			setCraftResult(cresult);
			ItemStack result = slotItem(playerinv, 26);
			if (result==null||result.getType()==Material.AIR)
				result = cresult;
			setResult(result);
		}

		@Override
		public void fromCraftingInventoryItem(final CraftingInventory craftinv) {
			final ItemStack ingredient = craftinv.getResult();
			if (ingredient!=null&&ingredient.getType()!=Material.AIR)
				setResult(ingredient);
		}

		@Override
		public void fromRecipe(final Recipe recipe) {
			final ItemStack itemStack = recipe.getResult();
			if (itemStack!=null&&itemStack.getType()!=Material.AIR)
				setResult(itemStack);
		}

		@Override
		public void fromSerializedRecipe(final SerializedRecipe recipe) {
			fromRecipe(recipe.getRecipe());
			setCraftResult(recipe.getCraftResult());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime*result+(this.craft_result==null ? 0 : this.craft_result.hashCode());
			result = prime*result+(this.result==null ? 0 : this.result.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this==obj)
				return true;
			if (obj==null)
				return false;
			if (!(obj instanceof RecipeResult))
				return false;
			final RecipeResult other = (RecipeResult) obj;
			if (this.craft_result==null) {
				if (other.craft_result!=null)
					return false;
			} else if (!this.craft_result.equals(other.craft_result))
				return false;
			if (this.result==null) {
				if (other.result!=null)
					return false;
			} else if (!this.result.equals(other.result))
				return false;
			return true;
		}
	}

	public static abstract class CustomRecipe<T extends Recipe> {
		protected final RecipeIngredients<T> ingredients;
		protected final RecipeResult output;

		public CustomRecipe(final RecipeIngredients<T> ingredients, final RecipeResult output) {
			this.ingredients = ingredients;
			this.output = output;
		}

		public abstract T toRecipe();

		public abstract SerializedRecipe toSerializedRecipe();

		public RecipeIngredients<T> getIngredients() {
			return this.ingredients;
		}

		public RecipeResult getResult() {
			return this.output;
		}

		public static class ShapelessCustomRecipe extends CustomRecipe<ShapelessRecipe> {
			public ShapelessCustomRecipe(final ShapelessRecipeIngredients ingredients, final RecipeResult output) {
				super(ingredients, output);
			}

			@Override
			public ShapelessRecipe toRecipe() {
				if (!this.ingredients.isValid()||!this.output.isValid())
					return null;

				final ShapelessRecipe recipe = new ShapelessRecipe(this.output.getResult());
				this.ingredients.toRecipe(recipe);

				return recipe;
			}

			@Override
			public SerializedRecipe toSerializedRecipe() {
				final ShapelessRecipe recipe = toRecipe();
				if (recipe==null)
					return null;
				return new SerializedRecipe(recipe, this.output.getCraftResult());
			}
		}

		public static class ShapedCustomRecipe extends CustomRecipe<ShapedRecipe> {
			public ShapedCustomRecipe(final ShapedRecipeIngredients ingredients, final RecipeResult output) {
				super(ingredients, output);
			}

			@Override
			public ShapedRecipe toRecipe() {
				if (!this.ingredients.isValid()||!this.output.isValid())
					return null;

				final ShapedRecipe recipe = new ShapedRecipe(this.output.getResult());
				this.ingredients.toRecipe(recipe);

				return recipe;
			}

			@Override
			public SerializedRecipe toSerializedRecipe() {
				final ShapedRecipe recipe = toRecipe();
				if (recipe==null)
					return null;
				return new SerializedRecipe(recipe, this.output.getCraftResult());
			}

			protected Character[][] processShape(final Character[][] shape) {
				return shape;
			}
		}

		public static class FurnaceCustomRecipe extends CustomRecipe<FurnaceRecipe> {
			public FurnaceCustomRecipe(final FurnaceRecipeIngredients ingredients, final RecipeResult output) {
				super(ingredients, output);
			}

			@Override
			public FurnaceRecipe toRecipe() {
				if (!this.ingredients.isValid()||!this.output.isValid())
					return null;

				final FurnaceRecipe recipe = new FurnaceRecipe(this.output.getResult(), ((FurnaceRecipeIngredients) this.ingredients).getIngredient());

				return recipe;
			}

			@Override
			public SerializedRecipe toSerializedRecipe() {
				final FurnaceRecipe recipe = toRecipe();
				if (recipe==null)
					return null;
				return new SerializedRecipe(recipe, this.output.getCraftResult());
			}
		}
	}

	public static void loadRecipes(final ChatOutput output) {
		output.sendMessage(ChatColor.YELLOW+"[RecipeCreator] Loading Recipe Files");
		ReRecipeCreators.instance.recipestorage.load();
		final List<Recipe> recipes = Lists.newArrayList();
		for (final SerializedRecipe r : ReRecipeCreators.instance.recipestorage.getRecipes().values())
			recipes.add(r.getRecipe());
		ReRecipeCreators.instance.reciperegistrar.setRecipes(recipes);
		output.sendMessage(ChatColor.YELLOW+"[RecipeCreator] Done Loading Recipe Files! ("+recipes.size()+" Recipes)");
	}

	public static void add(final ChatOutput output, final SerializedRecipe recipe, final String name, final String[] alias) {
		if (ReRecipeCreators.instance.recipestorage.getRecipe(name)!=null) {
			output.sendMessage(ChatColor.RED+"A recipe with that name already exists");
			return;
		}

		recipe.getAlias().addAll(Arrays.asList(alias));
		ReRecipeCreators.instance.recipestorage.putRecipe(name, recipe);
		ReRecipeCreators.instance.reciperegistrar.addRecipe(recipe.getRecipe());

		output.sendMessage(ChatColor.GREEN+"Shapeless recipe '"+name+"' created");
	}

	public static boolean removeRecipe(final ChatOutput output, final String name) {
		final SerializedRecipe remove = ReRecipeCreators.instance.recipestorage.getRecipe(name);
		if (remove!=null)
			if (ReRecipeCreators.instance.recipestorage.removeRecipe(name)) {
				ReRecipeCreators.instance.reciperegistrar.removeRecipe(remove.getRecipe());
				return true;
			}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Recipe> T resultRecipe(final T recipe, final ItemStack itemStack) {
		final RecipeResult result = new RecipeResult();
		result.setResult(itemStack);
		if (recipe instanceof ShapedRecipe) {
			final ShapedRecipeIngredients ingredients = new ShapedRecipeIngredients();
			ingredients.fromRecipe((ShapedRecipe) recipe);
			return (T) new ShapedCustomRecipe(ingredients, result).toRecipe();
		} else if (recipe instanceof ShapelessRecipe) {
			final ShapelessRecipeIngredients ingredients = new ShapelessRecipeIngredients();
			ingredients.fromRecipe((ShapelessRecipe) recipe);
			return (T) new ShapelessCustomRecipe(ingredients, result).toRecipe();
		} else if (recipe instanceof FurnaceRecipe) {
			final FurnaceRecipeIngredients ingredients = new FurnaceRecipeIngredients();
			ingredients.fromRecipe((FurnaceRecipe) recipe);
			return (T) new FurnaceCustomRecipe(ingredients, result).toRecipe();
		}
		return recipe;
	}

	public static boolean recipeEquals(final Recipe recipe1, final Recipe recipe2) {
		return resultEquals(recipe1, recipe2)&&ingredientEquals(recipe1, recipe2);
	}

	public static boolean resultEquals(final Recipe recipe1, final Recipe recipe2) {
		if (recipe1==recipe2)
			return true;
		if (recipe1==null&&recipe2==null)
			return true;
		if (recipe1==null||recipe2==null)
			return false;
		if (recipe1.getResult().isSimilar(recipe2.getResult()))
			return true;
		return false;
	}

	public static boolean ingredientEquals(final Recipe recipe1, final Recipe recipe2) {
		if (recipe1==recipe2)
			return true;
		if (recipe1==null&&recipe2==null)
			return true;
		if (recipe1==null||recipe2==null)
			return false;
		if (recipe1 instanceof ShapedRecipe&&recipe2 instanceof ShapedRecipe) {
			final ShapedRecipeIngredients ing1 = new ShapedRecipeIngredients();
			ing1.fromRecipe((ShapedRecipe) recipe1);
			final ShapedRecipeIngredients ing2 = new ShapedRecipeIngredients();
			ing2.fromRecipe((ShapedRecipe) recipe2);
			return ing1.equals(ing2);
		} else if (recipe1 instanceof ShapelessRecipe&&recipe2 instanceof ShapelessRecipe) {
			final ShapelessRecipeIngredients ing1 = new ShapelessRecipeIngredients();
			ing1.fromRecipe((ShapelessRecipe) recipe1);
			final ShapelessRecipeIngredients ing2 = new ShapelessRecipeIngredients();
			ing2.fromRecipe((ShapelessRecipe) recipe2);
			return ing1.equals(ing2);
		} else if (recipe1 instanceof FurnaceRecipe&&recipe2 instanceof FurnaceRecipe) {
			final FurnaceRecipeIngredients ing1 = new FurnaceRecipeIngredients();
			ing1.fromRecipe((FurnaceRecipe) recipe1);
			final FurnaceRecipeIngredients ing2 = new FurnaceRecipeIngredients();
			ing2.fromRecipe((FurnaceRecipe) recipe2);
			return ing1.equals(ing2);
		}
		return false;
	}

	public static boolean ingredientEquals(final Recipe recipe1, final CraftingInventory recipe2) {
		if (recipe1==recipe2)
			return true;
		if (recipe1==null&&recipe2==null)
			return true;
		if (recipe1==null||recipe2==null)
			return false;
		if (recipe1 instanceof ShapedRecipe) {
			final ShapedRecipeIngredients ing1 = new ShapedRecipeIngredients();
			ing1.fromRecipe((ShapedRecipe) recipe1);
			final ShapedRecipeIngredients ing2 = new ShapedRecipeIngredients();
			ing2.fromCraftingInventoryItem(recipe2);
			return ing1.equals(ing2);
		} else if (recipe1 instanceof ShapelessRecipe&&recipe2 instanceof ShapelessRecipe) {
			final ShapelessRecipeIngredients ing1 = new ShapelessRecipeIngredients();
			ing1.fromRecipe((ShapelessRecipe) recipe1);
			final ShapelessRecipeIngredients ing2 = new ShapelessRecipeIngredients();
			ing2.fromCraftingInventoryItem(recipe2);
			return ing1.equals(ing2);
		} else if (recipe1 instanceof FurnaceRecipe&&recipe2 instanceof FurnaceRecipe) {
			final FurnaceRecipeIngredients ing1 = new FurnaceRecipeIngredients();
			ing1.fromRecipe((FurnaceRecipe) recipe1);
			final FurnaceRecipeIngredients ing2 = new FurnaceRecipeIngredients();
			ing2.fromCraftingInventoryItem(recipe2);
			return ing1.equals(ing2);
		}
		return false;
	}
}