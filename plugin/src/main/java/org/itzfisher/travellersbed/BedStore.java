package org.itzfisher.travellersbed;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

public final class BedStore {
  private final File file; private final Map<String, Record> beds = new HashMap<>();
  public BedStore(File file) { this.file=file; }
  public synchronized void load() { beds.clear(); YamlConfiguration y=YamlConfiguration.loadConfiguration(file); for (String k:y.getKeys(false)) { try { beds.put(k,new Record(UUID.fromString(y.getString(k+".world")),y.getInt(k+".x"),y.getInt(k+".y"),y.getInt(k+".z"),Material.valueOf(y.getString(k+".material")))); } catch (Exception ignored) {} } }
  public synchronized void save() { YamlConfiguration y=new YamlConfiguration(); for (var e:beds.entrySet()) { Record r=e.getValue(); String k=e.getKey(); y.set(k+".world",r.world.toString()); y.set(k+".x",r.x); y.set(k+".y",r.y); y.set(k+".z",r.z); y.set(k+".material",r.material.name()); } try { y.save(file); } catch(IOException e) { throw new IllegalStateException("Could not save bed data",e); } }
  public synchronized void add(Location l, Material m) { beds.put(key(l),new Record(l.getWorld().getUID(),l.getBlockX(),l.getBlockY(),l.getBlockZ(),m)); }
  public synchronized Record remove(Location l) { return beds.remove(key(l)); }
  public synchronized Record get(Location l) { return beds.get(key(l)); }
  public synchronized int size(){return beds.size();}
  private static String key(Location l) { return l.getWorld().getUID()+":"+l.getBlockX()+":"+l.getBlockY()+":"+l.getBlockZ(); }
  public record Record(UUID world,int x,int y,int z,Material material) {}
}
