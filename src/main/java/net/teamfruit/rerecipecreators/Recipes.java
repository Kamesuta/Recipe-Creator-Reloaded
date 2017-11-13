package net.teamfruit.rerecipecreators;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import net.teamfruit.rerecipecreators.Recipes.RecipeBuilder.FurnaceRecipeBuilder.FurnaceRecipeIngredients;
import net.teamfruit.rerecipecreators.Recipes.RecipeBuilder.ShapedRecipeBuilder.ShapedRecipeIngredients;
import net.teamfruit.rerecipecreators.Recipes.RecipeBuilder.ShapelessRecipeBuilder.ShapelessRecipeIngredients;
import net.teamfruit.rerecipecreators.serialization.SerializedRecipe;

public class Recipes {
	public static abstract class RecipeBuilder {
		protected final Player player;

		public RecipeBuilder(final Player player) {
			this.player = player;
		}

		public RecipeResult toResult() {
			final RecipeResult output = new RecipeResult();
			output.fromInventoryItem(this.player);
			return output;
		}

		public abstract RecipeIngredients<? extends Recipe> toIngredients();

		public abstract Recipe toRecipe();

		public abstract SerializedRecipe toSerializedRecipe();

		public static abstract class PlayerRecipe<T extends Recipe> {
			public abstract boolean isValid();

			public abstract void fromInventoryItem(Player player);

			public abstract void fromRecipe(T recipe);

			protected static ItemStack slotItem(final Player player, final int slot) {
				final ItemStack itemStack = player.getInventory().getItem(slot);
				return itemStack!=null ? itemStack : new ItemStack(Material.AIR, 1);
			}
		}

		public static abstract class RecipeIngredients<T extends Recipe> extends PlayerRecipe<T> {
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
			};
		}

		public static class RecipeResult extends PlayerRecipe<Recipe> {
			private ItemStack result;

			public RecipeResult() {
			}

			@Override
			public boolean isValid() {
				return this.result!=null;
			}

			public void setResult(final ItemStack result) {
				if (result==null||result.getType()==Material.AIR)
					return;
				this.result = result;
			}

			public ItemStack getResult() {
				return this.result;
			}

			@Override
			public void fromInventoryItem(final Player player) {
				setResult(slotItem(player, 17));
			}

			public static RecipeResult createFromRecipe(final Recipe recipe) {
				final RecipeResult result = new RecipeResult();
				result.fromRecipe(recipe);
				return result;
			}

			@Override
			public void fromRecipe(final Recipe recipe) {
				final ItemStack itemStack = recipe.getResult();
				if (itemStack!=null&&itemStack.getType()!=Material.AIR)
					setResult(itemStack);
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
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
				if (this.result==null) {
					if (other.result!=null)
						return false;
				} else if (!this.result.equals(other.result))
					return false;
				return true;
			}
		}

		public static class ShapelessRecipeBuilder extends RecipeBuilder {
			public ShapelessRecipeBuilder(final Player player) {
				super(player);
			}

			@Override
			public ShapelessRecipeIngredients toIngredients() {
				final ShapelessRecipeIngredients ingredients = new ShapelessRecipeIngredients();
				ingredients.fromInventoryItem(this.player);
				return ingredients;
			}

			@Override
			public ShapelessRecipe toRecipe() {
				final ShapelessRecipeIngredients ingredients = toIngredients();
				final RecipeResult output = toResult();

				if (!ingredients.isValid()||!output.isValid())
					return null;

				final ShapelessRecipe recipe = new ShapelessRecipe(output.getResult());
				ingredients.toRecipe(recipe);

				return recipe;
			}

			@Override
			public SerializedRecipe toSerializedRecipe() {
				final ShapelessRecipe recipe = toRecipe();
				if (recipe==null)
					return null;
				return new SerializedRecipe(recipe);
			}

			public static class ShapelessRecipeIngredients extends RecipeIngredients<ShapelessRecipe> {
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
				public void fromInventoryItem(final Player player) {
					//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
					addIngredient(slotItem(player, 9).getData());
					addIngredient(slotItem(player, 10).getData());
					addIngredient(slotItem(player, 11).getData());

					addIngredient(slotItem(player, 18).getData());
					addIngredient(slotItem(player, 19).getData());
					addIngredient(slotItem(player, 20).getData());

					addIngredient(slotItem(player, 27).getData());
					addIngredient(slotItem(player, 28).getData());
					addIngredient(slotItem(player, 29).getData());
				}

				@Override
				public void fromRecipe(final ShapelessRecipe recipe) {
					for (final ItemStack ingredient : recipe.getIngredientList())
						if (ingredient!=null&&ingredient.getType()!=Material.AIR)
							addIngredient(ingredient.getData());
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
						final List<MaterialData> list0 = getIngredients();
						final List<MaterialData> list1 = other.getIngredients();
						final int list0size = list0.size();
						if (list0size!=list1.size())
							return false;
						for (int i = 0; i<list0size; i++) {
							final MaterialData item10 = list0.get(i);
							final MaterialData item11 = list1.get(i);
							if (item10.equals(item11))
								return false;
						}
					}
					return true;
				}
			}
		}

		public static class ShapedRecipeBuilder extends RecipeBuilder {
			public ShapedRecipeBuilder(final Player player) {
				super(player);
			}

			@Override
			public ShapedRecipeIngredients toIngredients() {
				final ShapedRecipeIngredients ingredients = new ShapedRecipeIngredients();
				ingredients.fromInventoryItem(this.player);
				return ingredients;
			}

			@Override
			public ShapedRecipe toRecipe() {
				final ShapedRecipeIngredients ingredients = toIngredients();
				final RecipeResult output = toResult();

				if (!ingredients.isValid()||!output.isValid())
					return null;

				final ShapedRecipe recipe = new ShapedRecipe(output.getResult());
				ingredients.toRecipe(recipe);

				return recipe;
			}

			@Override
			public SerializedRecipe toSerializedRecipe() {
				final ShapedRecipe recipe = toRecipe();
				if (recipe==null)
					return null;
				return new SerializedRecipe(recipe);
			}

			protected Character[][] processShape(final Character[][] shape) {
				return shape;
			}

			public static class ShapedRecipeIngredients extends RecipeIngredients<ShapedRecipe> {
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
				public void fromInventoryItem(final Player player) {
					//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
					setIngredient(0, 0, 'a', slotItem(player, 9).getData());
					setIngredient(1, 0, 'b', slotItem(player, 10).getData());
					setIngredient(2, 0, 'c', slotItem(player, 11).getData());

					setIngredient(0, 1, 'd', slotItem(player, 18).getData());
					setIngredient(1, 1, 'e', slotItem(player, 19).getData());
					setIngredient(2, 1, 'f', slotItem(player, 20).getData());

					setIngredient(0, 2, 'g', slotItem(player, 27).getData());
					setIngredient(1, 2, 'h', slotItem(player, 28).getData());
					setIngredient(2, 2, 'i', slotItem(player, 29).getData());
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
								if (item10.equals(item11))
									return false;
							}
						}
					}
					return true;
				}
			}
		}

		public static class TrimmedShapedRecipeBuilder extends ShapedRecipeBuilder {
			public TrimmedShapedRecipeBuilder(final Player player) {
				super(player);
			}

			@Override
			public ShapedRecipeIngredients toIngredients() {
				final TrimmedShapedRecipeIngredients ingredients = new TrimmedShapedRecipeIngredients();
				ingredients.fromInventoryItem(this.player);
				return ingredients;
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
		}

		public static class FurnaceRecipeBuilder extends RecipeBuilder {
			public FurnaceRecipeBuilder(final Player player) {
				super(player);
			}

			@Override
			public FurnaceRecipeIngredients toIngredients() {
				final FurnaceRecipeIngredients ingredients = new FurnaceRecipeIngredients();
				ingredients.fromInventoryItem(this.player);
				return ingredients;
			}

			@Override
			public FurnaceRecipe toRecipe() {
				final FurnaceRecipeIngredients ingredients = toIngredients();
				final RecipeResult output = toResult();

				if (!ingredients.isValid()||!output.isValid())
					return null;

				final FurnaceRecipe recipe = new FurnaceRecipe(output.getResult(), ingredients.getIngredient());

				return recipe;
			}

			@Override
			public SerializedRecipe toSerializedRecipe() {
				final FurnaceRecipe recipe = toRecipe();
				if (recipe==null)
					return null;
				return new SerializedRecipe(recipe);
			}

			public static class FurnaceRecipeIngredients extends RecipeIngredients<FurnaceRecipe> {
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
				public void fromInventoryItem(final Player player) {
					setIngredient(slotItem(player, 29).getData());
				}

				@Override
				public void fromRecipe(final FurnaceRecipe recipe) {
					final ItemStack itemStack = recipe.getInput();
					if (itemStack!=null&&itemStack.getType()!=Material.AIR)
						setIngredient(itemStack.getData());
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

	public static void add(final ChatOutput output, final String name, final SerializedRecipe recipe) {
		if (ReRecipeCreators.instance.recipestorage.getRecipe(name)!=null) {
			output.sendMessage(ChatColor.RED+"A recipe with that name already exists");
			return;
		}

		ReRecipeCreators.instance.recipestorage.putRecipe(name, recipe);
		ReRecipeCreators.instance.reciperegistrar.addRecipe(recipe.getRecipe());

		output.sendMessage(ChatColor.GREEN+"Shapeless recipe '"+name+"' created");
	}

	public static boolean removeRecipe(final ChatOutput output, final String name) {
		final Recipe remove = ReRecipeCreators.instance.recipestorage.getRecipe(name).getRecipe();
		if (remove!=null)
			if (ReRecipeCreators.instance.recipestorage.removeRecipe(name)) {
				ReRecipeCreators.instance.reciperegistrar.removeRecipe(remove);
				return true;
			}
		return false;
	}
}
