package org.itzfisher.travellersbed;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RecipeRulesTest {
  @Test void recognisesAllSixteenVanillaBedColours() {
    var beds=java.util.Arrays.stream(Material.values()).filter(BedItem::isVanillaBed).toList();
    assertEquals(16,beds.size());
    beds.forEach(bed -> assertEquals(bed,RecipeRules.outputColour(List.of(Material.OAK_LOG,bed))));
  }
  @Test void acceptsIngredientsInEitherOrder() { assertTrue(RecipeRules.isValidIngredients(List.of(Material.RED_BED,Material.OAK_LOG))); assertTrue(RecipeRules.isValidIngredients(List.of(Material.OAK_LOG,Material.RED_BED))); }
  @Test void rejectsExtraOrInvalidIngredients() { assertFalse(RecipeRules.isValidIngredients(List.of(Material.RED_BED,Material.OAK_LOG,Material.DIRT))); assertFalse(RecipeRules.isValidIngredients(List.of(Material.RED_BED,Material.SPRUCE_LOG))); }
  @Test void ordinaryMaterialDetectionDoesNotUseNames() { assertFalse(BedItem.isVanillaBed(Material.OAK_LOG)); assertTrue(BedItem.isVanillaBed(Material.BLACK_BED)); }
}
