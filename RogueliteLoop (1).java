import java.util.*;

public class RogueliteLoop {
    private Player player;
    private Scanner scanner;
    private Random random;
    private EmotionManager emotionManager;
    private int battlesCompleted;
    private int totalBattles = 10; // Number of battles in a run
    
    // Post-battle buff tracking
    private int battleTranceStacks = 0;
    private EmotionCard primedEmotion = null;
    
    public RogueliteLoop(Player player) {
        this.player = player;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.emotionManager = new EmotionManager(player);
        this.battlesCompleted = 0;
    }
    
    public void startRun() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║   WELCOME TO THE EMOTION ROGUELITE   ║");
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("\nSurvive " + totalBattles + " battles using the power of emotions!");
        
        while (battlesCompleted < totalBattles && player.isAlive()) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("BATTLE " + (battlesCompleted + 1) + " of " + totalBattles);
            System.out.println("=".repeat(50));
            
            // Pre-battle healing
            player.resetHealth();
            System.out.println(player.getName() + " is healed to full before the battle!");
            
            // Apply battle trance buff if active
            if (battleTranceStacks > 0) {
                player.applyBuff("attack", 3, 2);
                battleTranceStacks--;
                System.out.println("Battle Trance active! (" + battleTranceStacks + " battles remaining)");
            }
            
            // Emotion selection
            if (emotionManager.getUnlockedCount() > 0) {
                emotionManager.selectEmotionsForBattle(scanner);
                
                // Apply primed emotion if exists
                if (primedEmotion != null) {
                    primedEmotion.addCharge(3);
                    System.out.println(primedEmotion.getName() + " starts pre-charged!");
                    primedEmotion = null;
                }
            }
            
            // Generate enemy based on progress
            Enemy enemy = generateEnemy();
            
            // Start battle
            EmotionBattle battle = new EmotionBattle(player, enemy, emotionManager);
            boolean won = battle.start();
            
            if (!won) {
                System.out.println("\n╔══════════════════════════════════════╗");
                System.out.println("║          DEFEAT - RUN ENDED          ║");
                System.out.println("╔══════════════════════════════════════╗");
                System.out.println("Battles completed: " + battlesCompleted);
                return;
            }
            
            battlesCompleted++;
            emotionManager.onBattleWon();
            
            // Give XP AFTER battle, BEFORE post-battle choices
            System.out.println("\n" + "=".repeat(50));
            int xpReward = battle.getXPReward();
            System.out.println("XP Gained: " + xpReward);
            player.gainXP(xpReward, scanner);  // Level up prompts happen here!
            System.out.println("=".repeat(50));
            
            // Check for victory
            if (battlesCompleted >= totalBattles) {
                System.out.println("\n╔══════════════════════════════════════╗");
                System.out.println("║        VICTORY - RUN COMPLETE!       ║");
                System.out.println("╔══════════════════════════════════════╗");
                System.out.println("You conquered all " + totalBattles + " battles!");
                return;
            }
            
            // Post-battle choices (don't heal!)
            presentPostBattleChoices();
        }
    }
    
    private Enemy generateEnemy() {
        // Scale difficulty based on progress
        int progress = battlesCompleted;
        Enemy[] pool;
        
        if (progress < 2) {
            pool = Enemy.getDefaultEnemies();
        } else if (progress < 4) {
            pool = Enemy.getLvl2Enemies();
        } else if (progress < 7) {
            pool = Enemy.getModerateEnemies();
        } else if (progress < 9) {
            pool = Enemy.getLvl4Enemies();
        } else {
            pool = Enemy.getMBossEnemies();
        }
        
        Enemy template = pool[random.nextInt(pool.length)];
        
        // Scale enemy stats based on player level
        int scaledHP = template.maxHealth + (player.getLevel() * 10);
        int scaledAtk = template.getAttack() + (player.getLevel());
        int scaledDef = template.defense + (player.getLevel() / 2);
        
        return new Enemy(
            template.getName(),
            template.getLevel(),
            scaledHP,
            scaledAtk,
            scaledDef,
            template.getXpReward(),
            SkillManager.getEnemySkillsFor(template.getName())
        );
    }
    
    private void presentPostBattleChoices() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("POST-BATTLE CHOICES");
        System.out.println("=".repeat(50));
        System.out.println("Current HP: " + player.health + "/" + player.maxHealth);
        
        // Generate 3 random choices from pool of 10
        List<Integer> choicePool = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            choicePool.add(i);
        }
        Collections.shuffle(choicePool);
        
        int[] choices = new int[3];
        for (int i = 0; i < 3; i++) {
            choices[i] = choicePool.get(i);
        }
        
        // Display choices
        for (int i = 0; i < 3; i++) {
            System.out.println((i + 1) + ". " + getChoiceName(choices[i]));
            System.out.println("   " + getChoiceDescription(choices[i]));
        }
        
        System.out.print("\nSelect your choice (1-3): ");
        int selection = scanner.nextInt() - 1;
        
        if (selection >= 0 && selection < 3) {
            applyChoice(choices[selection]);
        }
    }
    
    private String getChoiceName(int choice) {
        switch(choice) {
            case 0: return "Emotional Discovery";
            case 1: return "Quick Rest";
            case 2: return "Emotional Release";
            case 3: return "Skill Enhancement";
            case 4: return "Emotional Priming";
            case 5: return "Battle Trance";
            case 6: return "Risky Bargain";
            case 7: return "Scout Ahead";
            case 8: return "Emotional Mastery";
            case 9: return "Fortune's Favor";
            default: return "Unknown";
        }
    }
    
    private String getChoiceDescription(int choice) {
        switch(choice) {
            case 0: return "Unlock a new emotion card";
            case 1: return "Restore 30% of your HP";
            case 2: return "Reset all emotion cooldowns";
            case 3: return "Increase a random skill's power by 15%";
            case 4: return "Start next battle with one emotion at 3/5 charge";
            case 5: return "Gain +3 attack for the next 2 battles";
            case 6: return "Lose 15% HP but start next battle with 2 emotions at 2/5 charge";
            case 7: return "Choose the difficulty of your next enemy";
            case 8: return "Permanently reduce one emotion's charge requirement";
            case 9: return "Receive a random powerful effect";
            default: return "";
        }
    }
    
    private void applyChoice(int choice) {
        System.out.println();
        
        switch(choice) {
            case 0: // Emotional Discovery
                if (emotionManager.hasLockedEmotions()) {
                    emotionManager.unlockNextEmotion();
                } else {
                    System.out.println("All emotions already unlocked! Restoring 20% HP instead.");
                    player.heal(player.maxHealth / 5);
                }
                break;
                
            case 1: // Quick Rest
                int healAmount = player.maxHealth * 30 / 100;
                player.heal(healAmount);
                break;
                
            case 2: // Emotional Release
                emotionManager.resetAllCooldowns();
                System.out.println("All emotion cooldowns have been reset!");
                break;
                
            case 3: // Skill Enhancement
                List<Skills> skills = player.getSkills();
                if (!skills.isEmpty()) {
                    Skills skill = skills.get(random.nextInt(skills.size()));
                    int oldPower = skill.getBasePower();
                    int newPower = (int)(oldPower * 1.15);
                    skill.setBasePower(newPower);
                    System.out.println(skill.getName() + " upgraded from " + oldPower + " to " + newPower + " power!");
                }
                break;
                
            case 4: // Emotional Priming
                List<EmotionCard> unlocked = emotionManager.getUnlockedEmotions();
                if (!unlocked.isEmpty()) {
                    primedEmotion = unlocked.get(random.nextInt(unlocked.size()));
                    System.out.println(primedEmotion.getName() + " will start pre-charged next battle!");
                }
                break;
                
            case 5: // Battle Trance
                battleTranceStacks = 2;
                System.out.println("You enter a battle trance! +3 attack for 2 battles!");
                break;
                
            case 6: // Risky Bargain
                int damage = player.maxHealth * 15 / 100;
                player.takeDamage(damage, null);
                System.out.println("You'll start next battle with 2 emotions pre-charged!");
                // This is handled in the battle start
                break;
                
            case 7: // Scout Ahead
                System.out.println("Choose next enemy difficulty:");
                System.out.println("1. Easy (lower stats)");
                System.out.println("2. Medium (normal stats)");
                System.out.println("3. Hard (higher stats)");
                // This would need additional implementation
                System.out.println("(Feature coming soon - receiving +2 defense for 1 battle instead)");
                player.applyBuff("defense", 2, 1);
                break;
                
            case 8: // Emotional Mastery
                List<EmotionCard> unlockedEmotions = emotionManager.getUnlockedEmotions();
                if (unlockedEmotions.isEmpty()) {
                    System.out.println("No emotions unlocked yet!");
                    break;
                }
                System.out.println("Choose an emotion to master:");
                for (int i = 0; i < unlockedEmotions.size(); i++) {
                    EmotionCard e = unlockedEmotions.get(i);
                    System.out.println((i + 1) + ". " + e.getName() + " [" + e.getMaxChargeTicks() + " ticks]");
                }
                int emChoice = scanner.nextInt() - 1;
                if (emChoice >= 0 && emChoice < unlockedEmotions.size()) {
                    emotionManager.reduceChargeRequirement(unlockedEmotions.get(emChoice).getName());
                }
                break;
                
            case 9: // Fortune's Favor
                int fortune = random.nextInt(5);
                System.out.println("Fortune smiles upon you...");
                if (fortune == 0) {
                    player.heal(player.maxHealth / 2);
                    System.out.println("Massive heal! +50% HP");
                } else if (fortune == 1) {
                    player.attack += 3;
                    System.out.println("Permanent +3 attack!");
                } else if (fortune == 2) {
                    player.defense += 2;
                    System.out.println("Permanent +2 defense!");
                } else if (fortune == 3) {
                    player.gainXP(100, scanner);
                    System.out.println("Gained 100 XP!");
                } else {
                    player.heal(player.maxHealth / 4);
                    System.out.println("Decent heal! +25% HP");
                }
                break;
        }
    }
}
