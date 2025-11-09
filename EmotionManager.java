import java.util.*;

public class EmotionManager {
    private List<EmotionCard> allEmotions;
    private List<EmotionCard> activeEmotions; // Emotions equipped for current battle
    private Player player;
    
    // Tracking for charge conditions
    private int noDamageTurns = 0;
    private int noCombatDamageTurns = 0;
    private int consecutiveBattlesWon = 0;
    
    public EmotionManager(Player player) {
        this.player = player;
        this.allEmotions = EmotionCard.createAllEmotions();
        this.activeEmotions = new ArrayList<>();
        
        // Start with Anger unlocked
        allEmotions.get(0).setUnlocked(true);
    }
    
    // Getters
    public List<EmotionCard> getAllEmotions() { return allEmotions; }
    public List<EmotionCard> getActiveEmotions() { return activeEmotions; }
    public List<EmotionCard> getUnlockedEmotions() {
        List<EmotionCard> unlocked = new ArrayList<>();
        for (EmotionCard e : allEmotions) {
            if (e.isUnlocked()) unlocked.add(e);
        }
        return unlocked;
    }
    
    public int getUnlockedCount() {
        return getUnlockedEmotions().size();
    }
    
    // Unlock system
    public void unlockNextEmotion() {
        for (EmotionCard e : allEmotions) {
            if (!e.isUnlocked()) {
                e.setUnlocked(true);
                System.out.println("\n*** NEW EMOTION UNLOCKED: " + e.getName() + " ***");
                System.out.println(e.getDescription());
                System.out.println("Charges: " + e.getChargeType());
                return;
            }
        }
        System.out.println("All emotions already unlocked!");
    }
    
    public boolean hasLockedEmotions() {
        for (EmotionCard e : allEmotions) {
            if (!e.isUnlocked()) return true;
        }
        return false;
    }
    
    // Pre-battle emotion selection
    public void selectEmotionsForBattle(Scanner scanner) {
        activeEmotions.clear();
        List<EmotionCard> unlocked = getUnlockedEmotions();
        
        if (unlocked.isEmpty()) {
            System.out.println("No emotions unlocked yet!");
            return;
        }
        
        if (unlocked.size() <= 3) {
            // If 3 or fewer, use all
            activeEmotions.addAll(unlocked);
            System.out.println("Equipped all unlocked emotions for battle!");
            return;
        }
        
        // Choose 2, get 1 random
        System.out.println("\n=== SELECT EMOTIONS FOR BATTLE ===");
        System.out.println("Choose 2 emotions (you'll get 1 random as well):");
        
        for (int i = 0; i < unlocked.size(); i++) {
            EmotionCard e = unlocked.get(i);
            System.out.println((i + 1) + ". " + e.getName() + " - " + e.getDescription());
        }
        
        System.out.print("\nFirst emotion choice: ");
        int choice1 = scanner.nextInt() - 1;
        if (choice1 >= 0 && choice1 < unlocked.size()) {
            activeEmotions.add(unlocked.get(choice1));
        }
        
        System.out.print("Second emotion choice: ");
        int choice2 = scanner.nextInt() - 1;
        if (choice2 >= 0 && choice2 < unlocked.size() && choice2 != choice1) {
            activeEmotions.add(unlocked.get(choice2));
        }
        
        // Add random third
        Random rand = new Random();
        EmotionCard random;
        do {
            random = unlocked.get(rand.nextInt(unlocked.size()));
        } while (activeEmotions.contains(random));
        
        activeEmotions.add(random);
        System.out.println("Random emotion: " + random.getName());
        System.out.println("\nEmotions equipped for battle!");
    }
    
    // Charge tracking
    public void onDamageTaken(int damage) {
        noDamageTurns = 0;
        chargeEmotion("damage_taken", 1);
        
        // Check if below half health
        if (player.health <= player.maxHealth / 2) {
            chargeEmotion("below_half_health", 1);
        }
        
        // Check if losing badly (below 30% health and enemy above 70%)
        // This check happens in battle context
    }
    
    public void onNoDamageTaken() {
        noDamageTurns++;
        if (noDamageTurns >= 1) {
            chargeEmotion("no_damage_taken", 1);
        }
    }
    
    public void onMissedAttack() {
        chargeEmotion("miss_attack", 1);
    }
    
    public void onDebuffApplied() {
        chargeEmotion("debuffed", 1);
    }
    
    public void onNoDamageDealt() {
        chargeEmotion("no_damage_dealt", 1);
    }
    
    public void onRNGAction() {
        chargeEmotion("rng_action", 1);
    }
    
    public void onBattleWon() {
        consecutiveBattlesWon++;
        if (consecutiveBattlesWon >= 3) {
            chargeEmotion("win_battles", 1);
            consecutiveBattlesWon = 0;
        }
    }
    
    public void onTurnNoCombatDamage() {
        noCombatDamageTurns++;
        if (noCombatDamageTurns >= 2) {
            chargeEmotion("no_combat_damage", 1);
        }
    }
    
    public void resetTurnTracking() {
        noCombatDamageTurns = 0;
    }
    
    public void checkLosingBadly(int playerHP, int playerMaxHP, int enemyHP, int enemyMaxHP) {
        double playerPercent = (double)playerHP / playerMaxHP;
        double enemyPercent = (double)enemyHP / enemyMaxHP;
        
        if (playerPercent < 0.25 && enemyPercent > 0.70) {
            chargeEmotion("losing_badly", 1);
        }
    }
    
    private void chargeEmotion(String chargeType, int amount) {
        for (EmotionCard e : activeEmotions) {
            if (e.getChargeType().equals(chargeType)) {
                e.addCharge(amount);
            }
        }
    }
    
    // Auto-activate fully charged emotions
    public void checkAndActivateEmotions(Enemy enemy) {
        for (EmotionCard e : activeEmotions) {
            if (e.isFullyCharged() && e.isReady()) {
                e.activate(player, enemy);
            }
        }
    }
    
    // Update all emotions per turn
    public void updateEmotions() {
        for (EmotionCard e : activeEmotions) {
            e.updateEffect(player);
        }
    }
    
    // Display status
    public void displayEmotionStatus() {
        if (activeEmotions.isEmpty()) return;
        
        System.out.print("Emotions: ");
        for (EmotionCard e : activeEmotions) {
            e.displayStatus();
        }
        System.out.println();
    }
    
    // Reset cooldowns (for post-battle choice)
    public void resetAllCooldowns() {
        for (EmotionCard e : allEmotions) {
            e.resetCooldown();
        }
    }
    
    // Reduce charge requirement (for post-battle choice)
    public void reduceChargeRequirement(String emotionName) {
        for (EmotionCard e : allEmotions) {
            if (e.getName().equalsIgnoreCase(emotionName)) {
                e.setMaxChargeTicks(Math.max(1, e.getMaxChargeTicks() - 1));
                System.out.println(emotionName + " now charges faster!");
                return;
            }
        }
    }
}