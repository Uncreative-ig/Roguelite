import java.util.*;

public class MysteryEncounter {
    private String name;
    private String description;
    private String biomeType; // Which biome this encounter appears in
    
    public MysteryEncounter(String name, String description, String biomeType) {
        this.name = name;
        this.description = description;
        this.biomeType = biomeType;
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getBiomeType() { return biomeType; }
    
    // Trigger the encounter
    public void trigger(Player player, EmotionManager emotionManager, Scanner scanner) {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║        MYSTERY ENCOUNTER             ║");
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("\n" + name);
        System.out.println(description);
        
        // Each encounter type has different choices
        switch(name) {
            // ===== ABANDONED VILLAGE ENCOUNTERS =====
            case "Hollow Merchant":
                encounterHollowMerchant(player, scanner);
                break;
            case "Fading Memories":
                encounterFadingMemories(player, emotionManager, scanner);
                break;
            case "Abandoned Home":
                encounterAbandonedHome(player, scanner);
                break;
                
            // ===== SCORCHED WASTELAND ENCOUNTERS =====
            case "Smoldering Campfire":
                encounterSmolderingCampfire(player, scanner);
                break;
            case "Rage Monument":
                encounterRageMonument(player, emotionManager, scanner);
                break;
            case "Burnt Soldier":
                encounterBurntSoldier(player, scanner);
                break;
                
            // ===== FROZEN HOLLOW ENCOUNTERS =====
            case "Frozen Statue":
                encounterFrozenStatue(player, emotionManager, scanner);
                break;
            case "Ice Cave":
                encounterIceCave(player, scanner);
                break;
            case "Numb Wanderer":
                encounterNumbWanderer(player, emotionManager, scanner);
                break;
                
            // ===== TWILIGHT DEPTHS ENCOUNTERS =====
            case "Whispering Shadows":
                encounterWhisperingShadows(player, emotionManager, scanner);
                break;
            case "Ancient Shrine":
                encounterAncientShrine(player, emotionManager, scanner);
                break;
            case "Lost Soul":
                encounterLostSoul(player, scanner);
                break;
                
            // ===== UNIVERSAL ENCOUNTERS =====
            case "Emotional Crossroads":
                encounterEmotionalCrossroads(player, emotionManager, scanner);
                break;
            case "Memory Fragment":
                encounterMemoryFragment(player);
                break;
            case "Strange Rift":
                encounterStrangeRift(player, scanner);
                break;
        }
        
        System.out.println("\nYou continue your journey...\n");
    }
    
    // ===== ABANDONED VILLAGE ENCOUNTERS =====
    
    private void encounterHollowMerchant(Player player, Scanner scanner) {
        System.out.println("\nA figure sits at a stall, staring blankly at their wares.");
        System.out.println("They speak in a monotone: 'Buy... or don't... it doesn't matter...'");
        System.out.println("\nWhat do you do?");
        System.out.println("1. Buy supplies (Lose 10 HP, gain +5 defense for 3 battles)");
        System.out.println("2. Try to help them remember joy (Gain nothing, but maybe it matters?)");
        System.out.println("3. Leave quietly");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            player.takeDamage(10, null);
            player.applyBuff("defense", 5, 3);
            System.out.println("\nYou purchase sturdy armor. The merchant doesn't react.");
        } else if (choice == 2) {
            System.out.println("\nYou share a memory of a time you felt joy...");
            System.out.println("For a moment, their eyes seem less empty.");
            System.out.println("'Thank... you...' they whisper.");
            player.heal(20);
            System.out.println("\nKindness has its own reward.");
        } else {
            System.out.println("\nYou walk away from the hollow merchant.");
        }
    }
    
    private void encounterFadingMemories(Player player, EmotionManager emotionManager, Scanner scanner) {
        System.out.println("\nYou find a journal on the ground. Its pages describe powerful emotions.");
        System.out.println("Reading it makes you feel... something.");
        System.out.println("\nWhat do you focus on?");
        System.out.println("1. Pages about anger and determination");
        System.out.println("2. Pages about sadness and reflection");
        System.out.println("3. Pages about joy and hope");
        
        int choice = scanner.nextInt();
        System.out.println("\nThe memories resonate within you...");
        
        // Give random emotion charge boost
        List<EmotionCard> active = emotionManager.getActiveEmotions();
        if (!active.isEmpty()) {
            EmotionCard emotion = active.get(new Random().nextInt(active.size()));
            emotion.addCharge(2);
            System.out.println(emotion.getName() + " grows stronger from the memories!");
        }
    }
    
    private void encounterAbandonedHome(Player player, Scanner scanner) {
        System.out.println("\nYou enter an abandoned house. Photos on the wall show smiling faces.");
        System.out.println("But now the house is silent and empty.");
        System.out.println("\nWhat do you do?");
        System.out.println("1. Rest here (Heal 30% HP)");
        System.out.println("2. Search for supplies (50% chance: find item OR trigger trap)");
        System.out.println("3. Leave immediately");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            int heal = player.maxHealth * 30 / 100;
            player.heal(heal);
            System.out.println("\nYou rest in the quiet home, finding brief peace.");
        } else if (choice == 2) {
            Random rand = new Random();
            if (rand.nextBoolean()) {
                player.attack += 2;
                System.out.println("\nYou found an old weapon! +2 permanent attack!");
            } else {
                player.takeDamage(25, null);
                System.out.println("\nThe floor collapsed! You take damage from the fall.");
            }
        } else {
            System.out.println("\nYou leave the empty home behind.");
        }
    }
    
    // ===== SCORCHED WASTELAND ENCOUNTERS =====
    
    private void encounterSmolderingCampfire(Player player, Scanner scanner) {
        System.out.println("\nA campfire still burns despite the desolation.");
        System.out.println("Something about the flames feels... angry.");
        System.out.println("\nWhat do you do?");
        System.out.println("1. Sit by the fire (Take 10 damage, gain +3 attack for 2 battles)");
        System.out.println("2. Extinguish the flames (Heal 15 HP, lose +2 attack for 1 battle)");
        System.out.println("3. Walk past it");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            player.takeDamage(10, null);
            player.applyBuff("attack", 3, 2);
            System.out.println("\nThe fire burns you, but its rage fuels your own!");
        } else if (choice == 2) {
            player.heal(15);
            player.applyDeBuff("attack", 2, 1);
            System.out.println("\nYou put out the flames. The silence is peaceful, but your resolve weakens.");
        } else {
            System.out.println("\nYou leave the fire to burn alone.");
        }
    }
    
    private void encounterRageMonument(Player player, EmotionManager emotionManager, Scanner scanner) {
        System.out.println("\nA cracked monument towers before you. Fury seems to emanate from it.");
        System.out.println("Inscribed: 'LET YOUR ANGER OUT'");
        System.out.println("\nWhat do you do?");
        System.out.println("1. Strike the monument (Take 15 damage, charge Anger emotion)");
        System.out.println("2. Meditate before it (Heal 20 HP, reset 1 emotion cooldown)");
        System.out.println("3. Ignore it");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            player.takeDamage(15, null);
            System.out.println("\nYou unleash your frustration on the stone!");
            // Charge anger-type emotions
            for (EmotionCard e : emotionManager.getActiveEmotions()) {
                if (e.getChargeType().equals("damage_taken")) {
                    e.addCharge(3);
                }
            }
        } else if (choice == 2) {
            player.heal(20);
            emotionManager.resetAllCooldowns();
            System.out.println("\nYou find peace in the face of rage.");
        } else {
            System.out.println("\nYou walk past the monument.");
        }
    }
    
    private void encounterBurntSoldier(Player player, Scanner scanner) {
        System.out.println("\nA scarred warrior kneels in the ash, armor melted to their skin.");
        System.out.println("'Fight me...' they rasp. 'I need... to feel... something...'");
        System.out.println("\nWhat do you do?");
        System.out.println("1. Fight them (Lose 20 HP, gain +4 attack permanently)");
        System.out.println("2. Refuse and show mercy (Gain +2 defense permanently)");
        System.out.println("3. Walk away");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            player.takeDamage(20, null);
            player.attack += 4;
            System.out.println("\nYou clash with the soldier. They smile as they fall.");
            System.out.println("'Thank you... for letting me feel alive again...'");
        } else if (choice == 2) {
            player.defense += 2;
            System.out.println("\n'Perhaps... mercy is stronger than anger...' they whisper.");
            System.out.println("Your resolve strengthens.");
        } else {
            System.out.println("\nYou leave the soldier to their fate.");
        }
    }
    
    // ===== FROZEN HOLLOW ENCOUNTERS =====
    
    private void encounterFrozenStatue(Player player, EmotionManager emotionManager, Scanner scanner) {
        System.out.println("\nA person frozen in ice, their face showing neither pain nor peace.");
        System.out.println("Just... nothing.");
        System.out.println("\nWhat do you do?");
        System.out.println("1. Try to free them (Take 10 damage, unlock or charge Sadness emotion)");
        System.out.println("2. Study the ice (Gain resistance: +3 defense for 2 battles)");
        System.out.println("3. Leave them frozen");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            player.takeDamage(10, null);
            System.out.println("\nYou chip away at the ice, but they don't wake.");
            System.out.println("Seeing them trapped stirs something inside you...");
            // Charge sadness-type emotions
            for (EmotionCard e : emotionManager.getActiveEmotions()) {
                if (e.getName().equals("Sadness") || e.getName().equals("Fear")) {
                    e.addCharge(2);
                }
            }
        } else if (choice == 2) {
            player.applyBuff("defense", 3, 2);
            System.out.println("\nYou learn from the ice's resilience.");
        } else {
            System.out.println("\nYou can't save everyone.");
        }
    }
    
    private void encounterIceCave(Player player, Scanner scanner) {
        System.out.println("\nA cave entrance beckons. It's dark and cold inside.");
        System.out.println("Do you dare enter?");
        System.out.println("1. Enter the cave (High risk, high reward)");
        System.out.println("2. Stay outside (Safe)");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            Random rand = new Random();
            int outcome = rand.nextInt(3);
            
            if (outcome == 0) {
                player.heal(player.maxHealth / 2);
                player.attack += 3;
                System.out.println("\nYou find ancient treasures! +3 attack and massive healing!");
            } else if (outcome == 1) {
                player.takeDamage(30, null);
                System.out.println("\nThe cave collapses on you! You barely escape.");
            } else {
                player.setStatus("frozen", 2);
                System.out.println("\nA freezing wind blasts you! You're frozen!");
            }
        } else {
            System.out.println("\nYou decide not to risk it.");
        }
    }
    
    private void encounterNumbWanderer(Player player, EmotionManager emotionManager, Scanner scanner) {
        System.out.println("\nA figure wanders aimlessly through the snow.");
        System.out.println("They don't notice you at all.");
        System.out.println("\nWhat do you do?");
        System.out.println("1. Call out to them (Charge Pride or Joy emotion)");
        System.out.println("2. Give them your cloak (Lose 15 HP, gain +5 defense permanently)");
        System.out.println("3. Keep walking");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            System.out.println("\nThey stop. Turn. Look at you.");
            System.out.println("'Someone... remembered me...'");
            System.out.println("A small spark returns to their eyes.");
            for (EmotionCard e : emotionManager.getActiveEmotions()) {
                if (e.getName().equals("Pride") || e.getName().equals("Joy")) {
                    e.addCharge(2);
                }
            }
        } else if (choice == 2) {
            player.takeDamage(15, null);
            player.defense += 5;
            System.out.println("\nThey wrap the cloak around themselves.");
            System.out.println("'Warmth... I remember warmth...'");
            System.out.println("Your sacrifice makes you stronger.");
        } else {
            System.out.println("\nYou pass by the wanderer.");
        }
    }
    
    // ===== TWILIGHT DEPTHS ENCOUNTERS =====
    
    private void encounterWhisperingShadows(Player player, EmotionManager emotionManager, Scanner scanner) {
        System.out.println("\nShadows on the wall seem to whisper your name.");
        System.out.println("They speak of your fears, your doubts.");
        System.out.println("\nWhat do you do?");
        System.out.println("1. Face your fears (Take 20 damage, charge Fear/Hope emotion heavily)");
        System.out.println("2. Run from the shadows (Safe, but lose 1 emotion charge)");
        System.out.println("3. Ignore them");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            player.takeDamage(20, null);
            System.out.println("\nYou confront the shadows. They whisper truths you didn't want to hear.");
            System.out.println("But facing them makes you stronger.");
            for (EmotionCard e : emotionManager.getActiveEmotions()) {
                if (e.getName().equals("Fear") || e.getName().equals("Hope")) {
                    e.addCharge(3);
                }
            }
        } else if (choice == 2) {
            List<EmotionCard> active = emotionManager.getActiveEmotions();
            if (!active.isEmpty()) {
                active.get(0).resetCharge();
            }
            System.out.println("\nYou flee from the whispers. Some things are too painful to face.");
        } else {
            System.out.println("\nYou walk forward, ignoring the shadows.");
        }
    }
    
    private void encounterAncientShrine(Player player, EmotionManager emotionManager, Scanner scanner) {
        System.out.println("\nAn altar glows with strange energy. It pulses with emotional power.");
        System.out.println("You feel drawn to it.");
        System.out.println("\nWhat do you offer?");
        System.out.println("1. Offer your health (Lose 25 HP, reduce ALL emotion charge requirements by 1)");
        System.out.println("2. Offer your strength (Lose 3 attack permanently, reset all emotion cooldowns)");
        System.out.println("3. Take from the shrine (Gain 15 HP and +2 attack, but anger the shrine)");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            player.takeDamage(25, null);
            for (EmotionCard e : emotionManager.getAllEmotions()) {
                e.setMaxChargeTicks(Math.max(1, e.getMaxChargeTicks() - 1));
            }
            System.out.println("\nThe shrine accepts your sacrifice. All emotions charge faster now!");
        } else if (choice == 2) {
            player.attack -= 3;
            emotionManager.resetAllCooldowns();
            System.out.println("\nYour power flows into the shrine. Your emotions are refreshed!");
        } else {
            player.heal(15);
            player.attack += 2;
            player.setStatus("cursed", 5); // Custom status - take extra damage
            System.out.println("\nYou take from the shrine. Power courses through you...");
            System.out.println("But you feel a curse taking hold.");
        }
    }
    
    private void encounterLostSoul(Player player, Scanner scanner) {
        System.out.println("\nA translucent figure reaches out to you.");
        System.out.println("'Help... I can't remember... who I was...'");
        System.out.println("\nWhat do you do?");
        System.out.println("1. Share your memories (Heal 25 HP, +3 defense for 2 battles)");
        System.out.println("2. Absorb their energy (Lose 10 HP, +4 attack permanently)");
        System.out.println("3. Leave them");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            player.heal(25);
            player.applyBuff("defense", 3, 2);
            System.out.println("\nYou share stories of who you are. The soul smiles.");
            System.out.println("'Thank you... I remember now...'");
            System.out.println("They fade away peacefully.");
        } else if (choice == 2) {
            player.takeDamage(10, null);
            player.attack += 4;
            System.out.println("\nYou consume the lost soul's remaining essence.");
            System.out.println("You feel stronger, but hollow.");
        } else {
            System.out.println("\nYou walk away from the pleading soul.");
        }
    }
    
    // ===== UNIVERSAL ENCOUNTERS =====
    
    private void encounterEmotionalCrossroads(Player player, EmotionManager emotionManager, Scanner scanner) {
        System.out.println("\nThree paths diverge. Each radiates a different feeling.");
        System.out.println("Which emotion will you embrace?");
        System.out.println("1. Path of Anger (Gain +5 attack for 3 battles, take 15 damage)");
        System.out.println("2. Path of Sadness (Heal 30 HP, but lose +3 attack for 1 battle)");
        System.out.println("3. Path of Joy (Heal 20 HP, gain +3 attack for 2 battles)");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            player.takeDamage(15, null);
            player.applyBuff("attack", 5, 3);
            System.out.println("\nYou embrace your rage. It fuels you.");
        } else if (choice == 2) {
            player.heal(30);
            player.applyDeBuff("attack", 3, 1);
            System.out.println("\nYou embrace your sorrow. It's okay to feel sad.");
        } else {
            player.heal(20);
            player.applyBuff("attack", 3, 2);
            System.out.println("\nYou embrace happiness. It lifts your spirits!");
        }
    }
    
    private void encounterMemoryFragment(Player player) {
        Random rand = new Random();
        String[] memories = {
            "You remember a time when the world was full of color and laughter...",
            "A vision flashes: people dancing, celebrating, FEELING...",
            "You see a memory of someone you loved. They're smiling at you.",
            "The world wasn't always this empty. Once, emotions flowed freely.",
            "You remember: something TOOK the emotions. But what? And why?"
        };
        
        System.out.println("\n" + memories[rand.nextInt(memories.length)]);
        System.out.println("\nThe memory fades, but the feeling remains.");
        player.heal(10);
    }
    
    private void encounterStrangeRift(Player player, Scanner scanner) {
        System.out.println("\nA crack in reality shimmers before you.");
        System.out.println("Peering inside, you see... yourself? But different.");
        System.out.println("\nDo you enter?");
        System.out.println("1. Enter the rift (Completely random outcome)");
        System.out.println("2. Back away");
        
        int choice = scanner.nextInt();
        if (choice == 1) {
            Random rand = new Random();
            int outcome = rand.nextInt(6);
            
            switch(outcome) {
                case 0:
                    player.attack += 5;
                    System.out.println("\nYou emerge stronger! +5 attack!");
                    break;
                case 1:
                    player.defense += 5;
                    System.out.println("\nYou emerge tougher! +5 defense!");
                    break;
                case 2:
                    player.heal(player.maxHealth);
                    System.out.println("\nYou emerge refreshed! Fully healed!");
                    break;
                case 3:
                    player.takeDamage(40, null);
                    System.out.println("\nThe rift rejects you! You take heavy damage!");
                    break;
                case 4:
                    player.maxHealth += 20;
                    player.health += 20;
                    System.out.println("\nYou emerge changed! +20 max HP!");
                    break;
                case 5:
                    player.applyRandomBuffs(3);
                    System.out.println("\nThe rift blesses you with random powers!");
                    break;
            }
        } else {
            System.out.println("\nSome mysteries are better left alone.");
        }
    }
    
    // Create all mystery encounters by biome
    public static List<MysteryEncounter> createAllEncounters() {
        List<MysteryEncounter> encounters = new ArrayList<>();
        
        // Abandoned Village
        encounters.add(new MysteryEncounter("Hollow Merchant", 
            "A merchant with empty eyes...", "Abandoned Village"));
        encounters.add(new MysteryEncounter("Fading Memories",
            "A journal lies forgotten...", "Abandoned Village"));
        encounters.add(new MysteryEncounter("Abandoned Home",
            "A house full of memories...", "Abandoned Village"));
        
        // Scorched Wasteland
        encounters.add(new MysteryEncounter("Smoldering Campfire",
            "Flames that never die...", "Scorched Wasteland"));
        encounters.add(new MysteryEncounter("Rage Monument",
            "A monument to fury...", "Scorched Wasteland"));
        encounters.add(new MysteryEncounter("Burnt Soldier",
            "A warrior consumed by anger...", "Scorched Wasteland"));
        
        // Frozen Hollow
        encounters.add(new MysteryEncounter("Frozen Statue",
            "A person trapped in ice...", "Frozen Hollow"));
        encounters.add(new MysteryEncounter("Ice Cave",
            "A mysterious cavern...", "Frozen Hollow"));
        encounters.add(new MysteryEncounter("Numb Wanderer",
            "A soul lost to apathy...", "Frozen Hollow"));
        
        // Twilight Depths
        encounters.add(new MysteryEncounter("Whispering Shadows",
            "Voices in the dark...", "Twilight Depths"));
        encounters.add(new MysteryEncounter("Ancient Shrine",
            "A shrine of emotional power...", "Twilight Depths"));
        encounters.add(new MysteryEncounter("Lost Soul",
            "A spirit without identity...", "Twilight Depths"));
        
        // Universal (can appear anywhere)
        encounters.add(new MysteryEncounter("Emotional Crossroads",
            "Three paths, three feelings...", "Universal"));
        encounters.add(new MysteryEncounter("Memory Fragment",
            "A vision of the past...", "Universal"));
        encounters.add(new MysteryEncounter("Strange Rift",
            "A tear in reality...", "Universal"));
        
        return encounters;
    }
    
    // Get random encounter for a specific biome
    public static MysteryEncounter getRandomForBiome(String biomeName, Random random) {
        List<MysteryEncounter> all = createAllEncounters();
        List<MysteryEncounter> valid = new ArrayList<>();
        
        for (MysteryEncounter e : all) {
            if (e.getBiomeType().equals(biomeName) || e.getBiomeType().equals("Universal")) {
                valid.add(e);
            }
        }
        
        if (valid.isEmpty()) return null;
        return valid.get(random.nextInt(valid.size()));
    }
}
