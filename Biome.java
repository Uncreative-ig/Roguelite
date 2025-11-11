import java.util.*;

public class Biome {
    private String name;
    private String description;
    private List<String> weatherTypes;
    private List<String> battleEvents;
    private List<String> enemyPool;
    
    public Biome(String name, String description) {
        this.name = name;
        this.description = description;
        this.weatherTypes = new ArrayList<>();
        this.battleEvents = new ArrayList<>();
        this.enemyPool = new ArrayList<>();
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getWeatherTypes() { return weatherTypes; }
    public List<String> getBattleEvents() { return battleEvents; }
    public List<String> getEnemyPool() { return enemyPool; }
    
    public void addWeather(String weather) { weatherTypes.add(weather); }
    public void addBattleEvent(String event) { battleEvents.add(event); }
    public void addEnemy(String enemy) { enemyPool.add(enemy); }
    
    // Get random weather for this biome
    public String getRandomWeather(Random random) {
        if (weatherTypes.isEmpty()) return "Clear";
        return weatherTypes.get(random.nextInt(weatherTypes.size()));
    }
    
    // Get random battle event for this biome
    public String getRandomBattleEvent(Random random) {
        if (battleEvents.isEmpty()) return "None";
        return battleEvents.get(random.nextInt(battleEvents.size()));
    }
    
    // Get random enemy from this biome's pool
    public String getRandomEnemy(Random random) {
        if (enemyPool.isEmpty()) return "Goblin";
        return enemyPool.get(random.nextInt(enemyPool.size()));
    }
    
    // Create all 4 biomes
    public static List<Biome> createAllBiomes() {
        List<Biome> biomes = new ArrayList<>();
        
        // ===== BIOME 1: ABANDONED VILLAGE =====
        Biome village = new Biome("Abandoned Village", 
            "A once-thriving settlement now empty and silent. Hollow shells of homes stand as reminders of what was lost.");
        
        // Weather
        village.addWeather("Fog");
        village.addWeather("Overcast");
        village.addWeather("Light Rain");
        
        // Battle Events
        village.addBattleEvent("Crumbling Walls");
        village.addBattleEvent("Broken Fountain");
        village.addBattleEvent("Town Square");
        village.addBattleEvent("Abandoned Market");
        
        // Enemies (Levels 1-2)
        village.addEnemy("Goblin");
        village.addEnemy("Berserker");
        village.addEnemy("Skeleton");
        village.addEnemy("Bats");
        village.addEnemy("Firecraker");
        village.addEnemy("Bomber");
        
        biomes.add(village);
        
        // ===== BIOME 2: SCORCHED WASTELAND =====
        Biome wasteland = new Biome("Scorched Wasteland",
            "Cracked earth stretches endlessly. The heat is oppressive, and anger lingers in the air like smoke.");
        
        // Weather
        wasteland.addWeather("Heatwave");
        wasteland.addWeather("Ash Storm");
        wasteland.addWeather("Sandstorm");
        
        // Battle Events
        wasteland.addBattleEvent("Lava Pools");
        wasteland.addBattleEvent("Burning Ground");
        wasteland.addBattleEvent("Dust Devil");
        wasteland.addBattleEvent("Scorched Ruins");
        
        // Enemies (Levels 2-3)
        wasteland.addEnemy("Firecraker");
        wasteland.addEnemy("Bomber");
        wasteland.addEnemy("Berserker");
        wasteland.addEnemy("Archers");
        wasteland.addEnemy("Mini Pekka");
        wasteland.addEnemy("Dart Goblin");
        
        biomes.add(wasteland);
        
        // ===== BIOME 3: FROZEN HOLLOW =====
        Biome frozen = new Biome("Frozen Hollow",
            "An eerie silence blankets this frozen valley. The cold numbs both body and spirit.");
        
        // Weather
        frozen.addWeather("Blizzard");
        frozen.addWeather("Freezing Wind");
        frozen.addWeather("Heavy Snow");
        
        // Battle Events
        frozen.addBattleEvent("Ice Patches");
        frozen.addBattleEvent("Frozen Lake");
        frozen.addBattleEvent("Icicle Ceiling");
        frozen.addBattleEvent("Snow Drift");
        
        // Enemies (Levels 3-4)
        frozen.addEnemy("Skeleton");
        frozen.addEnemy("Dart Goblin");
        frozen.addEnemy("Archers");
        frozen.addEnemy("Mini Pekka");
        frozen.addEnemy("Goblin Machine");
        frozen.addEnemy("Dark Prince");
        
        biomes.add(frozen);
        
        // ===== BIOME 4: TWILIGHT DEPTHS =====
        Biome depths = new Biome("Twilight Depths",
            "Shadows twist and writhe in this underground expanse. Fear and desperation echo off the stone walls.");
        
        // Weather
        depths.addWeather("Darkness");
        depths.addWeather("Echoing Winds");
        depths.addWeather("Tremors");
        
        // Battle Events
        depths.addBattleEvent("Narrow Passage");
        depths.addBattleEvent("Bottomless Pit");
        depths.addBattleEvent("Crystal Formation");
        depths.addBattleEvent("Ancient Altar");
        
        // Enemies (Levels 4-5)
        depths.addEnemy("Archer Queen");
        depths.addEnemy("Dark Prince");
        depths.addEnemy("Goblin Machine");
        depths.addEnemy("Mega Knight");
        depths.addEnemy("Boss Bandit");
        depths.addEnemy("Golem");
        
        biomes.add(depths);
        
        return biomes;
    }
}
