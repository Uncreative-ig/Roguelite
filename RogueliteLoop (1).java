import java.util.*;

public class RogueliteLoop {
    private Player player;
    private Scanner scanner;
    private Random random;
    private EmotionManager emotionManager;
    private int battlesCompleted;
    private int totalBattles = 12; // Increased for 4 biomes (3 battles each)
    
    // Biome system
    private List<Biome> biomes;
    private int currentBiomeIndex = 0;
    private Biome currentBiome;
    private Weather currentWeather;
    private BattleEvent currentBattleEvent;
    
    // Post-battle buff tracking
    private int battleTranceStacks = 0;
    private EmotionCard primedEmotion = null;
    private boolean riskyBargainActive = false;
    
    public RogueliteLoop(Player player) {
        this.player = player;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.emotionManager = new EmotionManager(player);
        this.battlesCompleted = 0;
        this.biomes = Biome.createAllBiomes();
        this.currentBiome = biomes.get(0);
    }
    
    public void startRun() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║   WELCOME TO THE EMOTION ROGUELITE   ║");
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("\nThe world has grown cold and empty.");
        System.out.println("Emotions have been stolen from everyone...");
        System.out.println("Including you.");
        System.out.println("\nSurvive " + totalBattles + " battles to uncover the truth!");
        System.out.println("Reclaim your emotions along the way.\n");
        
        while (battlesCompleted < totalBattles && player.isAlive()) {
            // Change biome every 3 battles
            if (battlesCompleted > 0 && battlesCompleted % 3 == 0) {
                currentBiomeIndex++;
                if (currentBiomeIndex < biomes.size()) {
                    currentBiome = biomes.get(currentBiomeIndex);
                    System.out.println("\n╔══════════════════════════════════════╗");
                    System.out.println("║    ENTERING NEW BIOME                ║");
                    System.out.println("╔══════════════════════════════════════╗");
                    System.out.println("\n" + currentBiome.getName());
                    System.out.println(currentBiome.getDescription());
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                }
            }
            
            // Chance for mystery encounter (30% chance before each battle)
            if (random.nextInt(10) < 3) {
                MysteryEncounter encounter = MysteryEncounter.getRandomForBiome(
                    currentBiome.getName(), random);
                if (encounter != null) {
                    encounter.trigger(player, emotionManager, scanner);
                }
            }
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("BATTLE " + (battlesCompleted + 1) + " of " + totalBattles);
            System.out.println("Location: " + currentBiome.getName());
            System.out.println("=".repeat(50));
            
            // CHANGED: 50% heal instead of full (makes choices matter)
            int healAmount = player.maxHealth / 2;
            player.health = Math.min(player.maxHealth, player.health + healAmount);
            System.out.println(player.getName() + " heals for " + healAmount + " HP before battle!");
            System.out.println("Current HP: " + player.health + "/" + player.maxHealth);
            
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
                
                // Apply risky bargain pre-charge
                if (riskyBargainActive) {
                    List<EmotionCard> activeEmotions = emotionManager.getActiveEmotions();
                    int emotionsPreCharged = 0;
                    for (EmotionCard emotion : activeEmotions) {
                        if (emotionsPreCharged < 2) {
                            emotion.addCharge(2);
                            emotionsPreCharged++;
                        }
                    }
                    System.out.println("Risky Bargain: 2 emotions start with 2 charge!");
                    riskyBargainActive = false;
                }
            } else {
                System.out.println("\nYou have no emotions yet. You must fight without them...");
            }
            
            // Set weather and battle event
            String weatherType = currentBiome.getRandomWeather(random);
            currentWeather = new Weather(weatherType, "");
            currentWeather.setActive(true);
            
            String eventType = currentBiome.getRandomBattleEvent(random);
            currentBattleEvent = new BattleEvent(eventType, getEventDescription(eventType));
            currentBattleEvent.setActive(true);
            
            System.out.println("\n--- Battle Conditions ---");
            System.out.println("Weather: " + weatherType + " (" + currentWeather.getEffectDescription() + ")");
            
            // Generate enemy from current biome
            Enemy enemy = generateEnemy();
            
            // Start battle with weather and event
            EmotionBattle battle = new EmotionBattle(player, enemy, emotionManager, 
                                                     currentWeather, currentBattleEvent);
            boolean won = battle.start();
            
            if (!won) {
                System.out.println("\n╔══════════════════════════════════════╗");
                System.out.println("║          DEFEAT - RUN ENDED          ║");
                System.out.println("╔══════════════════════════════════════╗");
                System.out.println("Battles completed: " + battlesCompleted);
                System.out.println("You collapsed in: " + currentBiome.getName());
                return;
            }
            
            battlesCompleted++;
            emotionManager.onBattleWon();
            
            // Give XP AFTER battle
            System.out.println("\n" + "=".repeat(50));
            int xpReward = battle.getXPReward();
            System.out.println("XP Gained: " + xpReward);
            player.gainXP(xpReward, scanner);
            System.out.println("=".repeat(50));
            
            // Check for victory
            if (battlesCompleted >= totalBattles) {
                System.out.println("\n╔══════════════════════════════════════╗");
                System.out.println("║        VICTORY - RUN COMPLETE!       ║");
                System.out.println("╔══════════════════════════════════════╗");
                System.out.println("You've conquered all " + totalBattles + " battles!");
                System.out.println("But the journey to reclaim all emotions continues...");
                return;
            }
            
            // Post-battle choices
            presentPostBattleChoices();
        }
    }
    
    private Enemy generateEnemy() {
        // Get enemy from current biome's pool
        String enemyName = currentBiome.getRandomEnemy(random);
        
        // Get the template from existing enemy arrays
        Enemy template = null;
        
        // Search through all enemy arrays for this name
        Enemy[] pools = {
            Enemy.getDefaultEnemies(),
            Enemy.getLvl2Enemies(),
            Enemy.getModerateEnemies(),
            Enemy.getLvl4Enemies(),
            Enemy.getMBossEnemies()
        };
        
        for (Enemy[] pool : pools) {
            for (Enemy e : pool) {
                if (e.getName().equals(enemyName)) {
                    template = e;
                    break;
                }
            }
            if (template != null) break;
        }
        
        // Fallback to Goblin if not found
        if (template == null) {
            template = Enemy.getDefaultEnemies()[0];
        }
        
        // Scale enemy stats based on player level (percentage-based)
        int scaledHP = (int)(template.maxHealth * (1 + player.getLevel() * 0.12));
        int scaledAtk = template.getAttack() + player.getLevel();
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
    
    private String getEventDescription(String eventName) {
        // Return descriptions for battle events
        switch(eventName) {
            case "Crumbling Walls": return "Old walls provide cover. Defense abilities are stronger.";
            case "Broken Fountain": return "A dried fountain. First heal is doubled.";
            case "Town Square": return "Open space allows for better multi-hit attacks.";
            case "Abandoned Market": return "Scattered supplies provide small healing.";
            
            case "Lava Pools": return "Molten rock everywhere. Attacking is risky.";
            case "Burning Ground": return "Everything burns hotter here.";
            case "Dust Devil": return "Swirling winds disrupt timing.";
            case "Scorched Ruins": return "The heat makes everyone more aggressive.";
            
            case "Ice Patches": return "Slippery ice can freeze combatants.";
            case "Frozen Lake": return "The ice reflects damage back.";
            case "Icicle Ceiling": return "Icicles fall periodically.";
            case "Snow Drift": return "Deep snow hinders movement.";
            
            case "Narrow Passage": return "Limited space for maneuvering.";
            case "Bottomless Pit": return "Fall and be stunned by the impact.";
            case "Crystal Formation": return "Crystals amplify emotional energy.";
            case "Ancient Altar": return "A mysterious altar grants power.";
            
            default: return "A standard battlefield.";
        }
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
            case 7: return "Fortify";
            case 8: return "Emotional Mastery";
            case 9: return "Fortune's Favor";
            default: return "Unknown";
        }
    }
    
    private String getChoiceDescription(int choice) {
        switch(choice) {
            case 0: return "Unlock a new emotion card";
            case 1: return "Restore 40% of your HP";
            case 2: return "Reset all emotion cooldowns";
            case 3: return "Increase a random skill's power by 15%";
            case 4: return "Start next battle with one emotion at 3/5 charge";
            case 5: return "Gain +3 attack for the next 2 battles";
            case 6: return "Lose 15% HP but start next battle with 2 emotions at 2/5 charge";
            case 7: return "Gain +2 defense for next battle";
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
                    System.out.println("All emotions already unlocked! Restoring 30% HP instead.");
                    player.heal(player.maxHealth * 30 / 100);
                }
                break;
                
            case 1: // Quick Rest
                int healAmount = player.maxHealth * 40 / 100;
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
                riskyBargainActive = true;
                System.out.println("You'll start next battle with 2 emotions at 2/5 charge!");
                break;
                
            case 7: // Fortify
                player.applyBuff("defense", 2, 1);
                System.out.println("You fortify your defenses! +2 defense for next battle!");
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
