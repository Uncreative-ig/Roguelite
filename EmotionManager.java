import java.util.*;

public class EmotionManager {
    private List<EmotionCard> allEmotions;
    private List<EmotionCard> activeEmotions;
    private Player player;
    
    // Tracking for charge conditions
    private int consecutiveTurnsDamageDealt = 0;  // NEW: For Pride
    private int consecutiveBattlesWon = 0;
    
    public EmotionManager(Player player) {
        this.player = player;
        this.allEmotions = EmotionCard.createAllEmotions();
        this.activeEmotions = new ArrayList<>();
        
        // CHANGED: Start with 0 emotions - must unlock them all
        // Player begins completely emotionless
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
            activeEmotions.addAll(unlocked);
            System.out.println("Equipped all unlocked emotions for battle!");
            return;
        }
        
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
    
    // FIXED: Improved charge tracking
    public void onDamageTaken(int damage) {
        consecutiveTurnsDamageDealt = 0;  // Reset Pride counter
        chargeEmotion("damage_taken", 1);
        
        // Check if below half health
        if (player.health <= player.maxHealth / 2) {
            chargeEmotion("below_half_health", 1);
        }
    }
    
    // NEW: Track when player deals damage (for Pride)
    public void onDamageDealt() {
        consecutiveTurnsDamageDealt++;
        if (consecutiveTurnsDamageDealt >= 2) {
            chargeEmotion("damage_dealt", 1);
        }
    }
    
    // REMOVED: onNoDamageTaken (Pride no longer uses this)
    
    public void onMissedAttack() {
        chargeEmotion("miss_attack", 1);
    }
    
    public void onDebuffApplied() {
        chargeEmotion("debuffed", 1);
    }
    
    // REMOVED: onNoDamageDealt (Ashamed no longer uses this)
    
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
    
    // NEW: Charge Ashamed when fighting at low health
    public void checkLowHealthCombat() {
        if (player.health <= player.maxHealth / 3) {
            chargeEmotion("low_health_combat", 1);
        }
    }
    
    // NEW: Charge Bored when at full health
    public void checkFullHealth() {
        if (player.health >= player.maxHealth) {
            chargeEmotion("full_health", 1);
        }
    }
    
    // REMOVED: onTurnNoCombatDamage and resetTurnTracking
    
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
    
    // Reset cooldowns
    public void resetAllCooldowns() {
        for (EmotionCard e : allEmotions) {
            e.resetCooldown();
        }
    }
    
    // Reduce charge requirement
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
