# Technical design

The custom item uses a namespaced persistent-data marker, never a display-name check. Each colour gets a Paper `ShapelessRecipe`; crafting preparation rejects marked Traveller's Beds as inputs. Placed beds are indexed by world UUID and foot coordinates in `plugins/TravellersBed/beds.yml`. `PlayerSetSpawnEvent` is cancelled only for a tracked custom bed with cause `BED`, leaving vanilla sleep and normal bed respawns intact. Breaking or explosions remove the record and recreate the marked item.
