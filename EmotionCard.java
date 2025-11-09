import java.util.*;

public class EmotionCard {
    private String name;
    private String description;
    private int chargeTicks;
    private int maxChargeTicks;
    private int cooldown;
    private int maxCooldown;
    private boolean isActive;
    private boolean isUnlocked;
    private String chargeType; // How this emotion charges
    
    // Effect parameters
    private int effectDuration;
    private int currentEffectTurns;
    
    public EmotionCard(String name, String description, String chargeType, int maxChargeTicks, int maxCooldown, int effectDuration) {
        this.name = name;
        this.description = description;
        this.chargeType = chargeType;
        this.chargeTicks = 0;
        this.maxChargeTicks = maxChargeTicks;
        this.cooldown = 0;
        this.maxCooldown = maxCooldown;
        this.effectDuration = effectDuration;
        this.currentEffectTurns = 0;
        this.isActive = false;
        this.isUnlocked = false;
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getChargeType() { return chargeType; }
    public int getChargeTicks() { return chargeTicks; }
    public int getMaxChargeTicks() { return maxChargeTicks; }
    public int getCooldown() { return cooldown; }
    public boolean isActive() { return isActive; }
    public boolean isUnlocked() { return isUnlocked; }
    public boolean isFullyCharged() { return chargeTicks >= maxChargeTicks; }
    public boolean isReady() { return cooldown == 0 && !isActive; }
    public int getCurrentEffectTurns() { return currentEffectTurns; }
    
    // Setters
    public void setUnlocked(boolean unlocked) { this.isUnlocked = unlocked; }
    public void setMaxChargeTicks(int ticks) { this.maxChargeTicks = ticks; }
    
    // Charge management
    public void addCharge(int amount) {
        if (cooldown == 0 && !isActive) {
            chargeTicks = Math.min(chargeTicks + amount, maxChargeTicks);
            if (isFullyCharged()) {
                System.out.println(">>> " + name + " is fully charged! <<<");
            }
        }
    }
    
    public void resetCharge() {
        chargeTicks = 0;
    }
    
    public void resetCooldown() {
        cooldown = 0;
    }
    
    // Activation
    public void activate(Player player, Enemy enemy) {
        if (!isFullyCharged() || !isReady()) return;
        
        isActive = true;
        currentEffectTurns = effectDuration;
        System.out.println("\n*** EMOTION ACTIVATED: " + name + " ***");
        System.out.println(description);
        
        applyEffect(player, enemy);
        
        chargeTicks = 0;
        cooldown = maxCooldown;
    }
    
    private void applyEffect(Player player, Enemy enemy) {
        switch(name) {
            case "Anger":
                // Halve cooldowns for 3 turns
                player.halveAllCooldowns();
                break;
                
            case "Pride":
                // Boost attack damage for 2 turns
                player.applyBuff("attack", 8, effectDuration);
                break;
                
            case "Fear":
                // Slow enemy attacks
                enemy.increaseAllSkillCooldowns(2);
                enemy.applyDeBuff("attack", 5, effectDuration);
                break;
                
            case "Joy":
                // Heal 100% and buff all stats
                player.heal(player.maxHealth);
                player.applyBuff("both", 6, effectDuration);
                break;
                
            case "Sadness":
                // Never miss and land crits
                player.setCritChance(1.0, effectDuration);
                player.setNeverMiss(true, effectDuration);
                break;
                
            case "Confusion":
                // Convert all debuffs to buffs
                player.convertDebuffsToBuffs();
                break;
                
            case "Ashamed":
                // Slow everyone's cooldowns except player
                enemy.increaseAllSkillCooldowns(3);
                break;
                
            case "Bored":
                // Damage both sides for 50%
                int playerDmg = player.maxHealth / 2;
                int enemyDmg = enemy.maxHealth / 2;
                player.takeDamage(playerDmg, null);
                enemy.takeDamage(enemyDmg, null);
                break;
                
            case "Goofy":
                // Give 2 random buffs
                player.applyRandomBuffs(2);
                break;
                
            case "Hope":
                // Survive at 1 HP with buffs
                player.setHopeMode(true, effectDuration);
                player.applyBuff("both", 10, effectDuration);
                break;
        }
    }
    
    // Update per turn
    public void updateEffect(Player player) {
        if (isActive && currentEffectTurns > 0) {
            currentEffectTurns--;
            if (currentEffectTurns == 0) {
                deactivate(player);
            }
        }
        
        if (cooldown > 0) {
            cooldown--;
        }
    }
    
    private void deactivate(Player player) {
        isActive = false;
        System.out.println("(" + name + " effect has ended)");
        
        // Clean up specific effects
        if (name.equals("Sadness")) {
            player.setNeverMiss(false, 0);
        } else if (name.equals("Hope")) {
            player.setHopeMode(false, 0);
        }
    }
    
    public void displayStatus() {
        if (isActive) {
            System.out.print(name + " [ACTIVE: " + currentEffectTurns + " turns] ");
        } else if (cooldown > 0) {
            System.out.print(name + " [CD: " + cooldown + "] ");
        } else {
            System.out.print(name + " [" + chargeTicks + "/" + maxChargeTicks + "] ");
        }
    }
    
    // Static factory method to create all emotion cards
    public static List<EmotionCard> createAllEmotions() {
        List<EmotionCard> emotions = new ArrayList<>();
        
        emotions.add(new EmotionCard("Anger", "Halves all cooldowns for 3 turns", 
            "damage_taken", 5, 4, 3));
        emotions.add(new EmotionCard("Pride", "Boosts attack damage for 2 turns", 
            "no_damage_taken", 5, 4, 2));
        emotions.add(new EmotionCard("Fear", "Slows enemy attacks", 
            "below_half_health", 1, 4, 3));
        emotions.add(new EmotionCard("Joy", "Heal 100% and buff all stats for 3 turns", 
            "win_battles", 3, 4, 3));
        emotions.add(new EmotionCard("Sadness", "Never miss and land crits for 3 turns", 
            "miss_attack", 5, 4, 3));
        emotions.add(new EmotionCard("Confusion", "Convert debuffs to buffs", 
            "debuffed", 5, 4, 1));
        emotions.add(new EmotionCard("Ashamed", "Slow enemy cooldowns for 2 turns", 
            "no_damage_dealt", 2, 4, 2));
        emotions.add(new EmotionCard("Bored", "Damage both sides for 50%", 
            "no_combat_damage", 5, 4, 1));
        emotions.add(new EmotionCard("Goofy", "Give 2 random buffs", 
            "rng_action", 5, 4, 1));
        emotions.add(new EmotionCard("Hope", "Survive at 1 HP with massive buffs", 
            "losing_badly", 1, 4, 3));
        
        return emotions;
    }
}