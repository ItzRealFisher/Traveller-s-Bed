package org.itzfisher.travellersbed;

import org.bukkit.Material;
import java.util.Collection;

/** Pure recipe validation used by crafting tests and future UI integrations. */
public final class RecipeRules {
  private RecipeRules() {}
  public static boolean isValidIngredients(Collection<Material> ingredients) {
    if (ingredients.size()!=2) return false;
    long logs=ingredients.stream().filter(m->m==Material.OAK_LOG).count();
    long beds=ingredients.stream().filter(BedItem::isVanillaBed).count();
    return logs==1 && beds==1;
  }
  public static Material outputColour(Collection<Material> ingredients) {
    if(!isValidIngredients(ingredients)) throw new IllegalArgumentException("Expected exactly one bed and one oak log");
    return ingredients.stream().filter(BedItem::isVanillaBed).findFirst().orElseThrow();
  }
}
