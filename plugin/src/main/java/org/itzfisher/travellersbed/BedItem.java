package org.itzfisher.travellersbed;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.List;

public final class BedItem {
  private final NamespacedKey key;
  public BedItem(TravellersBedPlugin plugin) { key = new NamespacedKey(plugin, "traveller_bed"); }
  public ItemStack create(Material material) {
    if (!isVanillaBed(material)) throw new IllegalArgumentException("Material is not a bed: " + material);
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(Component.text("Traveller's Bed", NamedTextColor.AQUA));
    meta.lore(List.of(Component.text("Rest anywhere.", NamedTextColor.GRAY), Component.text("Your home awaits.", NamedTextColor.GRAY), Component.text("Sleeping never changes your spawn.", NamedTextColor.GRAY)));
    meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
    meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    item.setItemMeta(meta); return item;
  }
  public boolean isTraveller(ItemStack item) { return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE); }
  public static boolean isVanillaBed(Material material) {
    if (material == null) return false;
    return switch (material) {
      case WHITE_BED, ORANGE_BED, MAGENTA_BED, LIGHT_BLUE_BED, YELLOW_BED, LIME_BED, PINK_BED, GRAY_BED,
           LIGHT_GRAY_BED, CYAN_BED, PURPLE_BED, BLUE_BED, BROWN_BED, GREEN_BED, RED_BED, BLACK_BED -> true;
      default -> false;
    };
  }
}
