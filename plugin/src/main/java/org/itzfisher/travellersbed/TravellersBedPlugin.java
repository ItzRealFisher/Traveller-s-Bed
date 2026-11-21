package org.itzfisher.travellersbed;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent.Cause;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.*;

public final class TravellersBedPlugin extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {
  private BedItem items; private BedStore store;
  @Override public void onEnable() {
    saveDefaultConfig(); items=new BedItem(this); store=new BedStore(new File(getDataFolder(),"beds.yml")); store.load();
    getServer().getPluginManager().registerEvents(this,this); Objects.requireNonNull(getCommand("travellerbed")).setExecutor(this); getCommand("travellerbed").setTabCompleter(this); registerRecipes();
  }
  @Override public void onDisable(){ if(store!=null) store.save(); }
  private void registerRecipes() {
    Iterator<Recipe> it=getServer().recipeIterator(); while(it.hasNext()) { Recipe r=it.next(); if(r instanceof Keyed k && k.getKey().getNamespace().equals(getName().toLowerCase(Locale.ROOT))) it.remove(); }
    if(!getConfig().getBoolean("crafting.enabled",true)) return;
    for(Material bed: Material.values()) if(BedItem.isVanillaBed(bed)) { NamespacedKey key=new NamespacedKey(this,"craft_"+bed.name().toLowerCase(Locale.ROOT)); ShapelessRecipe recipe=new ShapelessRecipe(key,items.create(bed)); recipe.addIngredient(new RecipeChoice.MaterialChoice(bed)); recipe.addIngredient(new RecipeChoice.MaterialChoice(Material.OAK_LOG)); getServer().addRecipe(recipe); }
  }
  @EventHandler(ignoreCancelled=true) public void prepareCraft(PrepareItemCraftEvent e) {
    if(!getConfig().getBoolean("crafting.enabled",true) || !e.getView().getPlayer().hasPermission("travellersbed.craft")) return;
    ItemStack result=e.getInventory().getResult(); if(result==null || !items.isTraveller(result)) return;
    for(ItemStack i:e.getInventory().getMatrix()) if(items.isTraveller(i)) { e.getInventory().setResult(null); return; }
  }
  @EventHandler(ignoreCancelled=true) public void place(BlockPlaceEvent e) {
    if(!items.isTraveller(e.getItemInHand())) return;
    Location foot=foot(e.getBlockPlaced()); if(foot==null) return; store.add(foot,e.getItemInHand().getType()); store.save();
  }
  @EventHandler(ignoreCancelled=true) public void preventSpawn(PlayerSetSpawnEvent e) {
    if(!getConfig().getBoolean("bed.prevent-spawn-setting",true) || e.getCause()!=Cause.BED || e.getLocation()==null) return;
    if(isTravellerBlock(e.getLocation().getBlock())) e.setCancelled(true);
  }
  @EventHandler(ignoreCancelled=true) public void breakBed(BlockBreakEvent e) {
    Location foot=foot(e.getBlock()); if(foot==null) return; BedStore.Record r=store.get(foot); if(r==null) return;
    store.remove(foot); store.save(); e.setDropItems(false);
    if(e.getPlayer().getGameMode()!=GameMode.CREATIVE && getConfig().getBoolean("drops.survival",true)) e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(),items.create(r.material()));
  }
  @EventHandler(ignoreCancelled=true) public void explosion(BlockExplodeEvent e) { handleExplosion(e.blockList(),e.getBlock().getWorld()); }
  @EventHandler(ignoreCancelled=true) public void entityExplosion(EntityExplodeEvent e) { handleExplosion(e.blockList(),e.getLocation().getWorld()); }
  private void handleExplosion(List<Block> blocks, World world) { for(Block b:new ArrayList<>(blocks)) { Location foot=foot(b); if(foot==null) continue; BedStore.Record r=store.remove(foot); if(r!=null && getConfig().getBoolean("drops.explosion-drops",true)) world.dropItemNaturally(b.getLocation(),items.create(r.material())); } store.save(); }
  private boolean isTravellerBlock(Block block) { Location l=foot(block); return l!=null && store.get(l)!=null; }
  private Location foot(Block b) { if(!BedItem.isVanillaBed(b.getType()) || !(b.getBlockData() instanceof Bed data)) return null; return data.getPart()==Bed.Part.FOOT ? b.getLocation() : b.getRelative(data.getFacing().getOppositeFace()).getLocation(); }
  @Override public boolean onCommand(CommandSender s,Command c,String l,String[] a) {
    if(a.length==0 || a[0].equalsIgnoreCase("help")) { s.sendMessage("/travellerbed give <player> [amount] [colour], reload, info, debug"); return true; }
    if(a[0].equalsIgnoreCase("reload")) { if(!permit(s,"travellersbed.reload"))return true; reloadConfig(); registerRecipes(); s.sendMessage("Traveller's Bed configuration reloaded."); return true; }
    if(a[0].equalsIgnoreCase("info")) { if(!permit(s,"travellersbed.info"))return true; s.sendMessage("Traveller's Bed "+getDescription().getVersion()+" | Paper "+getServer().getMinecraftVersion()+" | recipes: "+getConfig().getBoolean("crafting.enabled")+" | tracked: "+store.size()); return true; }
    if(a[0].equalsIgnoreCase("give")) { if(!permit(s,"travellersbed.give") || a.length<2)return true; Player p=Bukkit.getPlayerExact(a[1]); if(p==null){s.sendMessage("Player not found.");return true;} int n=1; try{if(a.length>2)n=Math.max(1,Math.min(64,Integer.parseInt(a[2])));}catch(NumberFormatException x){s.sendMessage("Amount must be 1-64.");return true;} Material m=Material.RED_BED; if(a.length>3) try{m=Material.valueOf(a[3].toUpperCase(Locale.ROOT)+"_BED");}catch(IllegalArgumentException x){s.sendMessage("Unknown bed colour.");return true;} ItemStack i=items.create(m);i.setAmount(n); Map<Integer,ItemStack> left=p.getInventory().addItem(i); left.values().forEach(v->p.getWorld().dropItemNaturally(p.getLocation(),v)); return true; }
    if(a[0].equalsIgnoreCase("debug")) { if(!permit(s,"travellersbed.debug")||!(s instanceof Player p))return true; Block b=p.getTargetBlockExact(6); s.sendMessage(b!=null && isTravellerBlock(b)?"Traveller's Bed: "+foot(b):"No tracked Traveller's Bed targeted.");return true;} return true;
  }
  private boolean permit(CommandSender s,String p){if(s.hasPermission(p))return true;s.sendMessage(ChatColor.translateAlternateColorCodes('&',getConfig().getString("messages.no-permission","&cNo permission.")));return false;}
  @Override public List<String> onTabComplete(CommandSender s,Command c,String a,String[] args){if(args.length==1)return List.of("give","reload","info","debug","help");if(args.length==2&&args[0].equalsIgnoreCase("give"))return null;if(args.length==4&&args[0].equalsIgnoreCase("give"))return Arrays.stream(Material.values()).filter(BedItem::isVanillaBed).map(m->m.name().replace("_BED","").toLowerCase(Locale.ROOT)).toList();return List.of();}
}
