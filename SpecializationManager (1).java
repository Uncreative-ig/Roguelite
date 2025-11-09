import java.util.*;

public class SpecializationManager {

	// For each class, three specializations, each has 3 skills (hardcoded)

	public static String[] getSpecializationsForClass(String classType) {
		if (classType.equalsIgnoreCase("warrior")) {
			return new String[] {"Juggernaut", "Warlord", "Blademaster"};
		} else if (classType.equalsIgnoreCase("wizard")) {
			return new String[] {"Reg Wizard", "Ice Wizard", "E Wiz"};
		} else if (classType.equalsIgnoreCase("bandit")) {
			return new String[] {"Phantom Striker", "Trapist", "Bruiser"};
		} else if (classType.equalsIgnoreCase("chronomancer")) {
			return new String[] {"Time Stitcher", "Clock Piercer", "Loopweaver"};
		} else if (classType.equalsIgnoreCase("alchemist")) {
			return new String[] {"Toxicologist", "Biologist", "Combustinoneer"};
		} else if (classType.equalsIgnoreCase("monk")) {
			return new String[] {"Hermit", "Sarabaite", "Gyrovagi"};
		}
		return new String[0];
	}

	public static List<Skills> getSkillsForSpecialization(String classType, String specialization) {
		List<Skills> skills = new ArrayList<>();

		if (classType.equalsIgnoreCase("warrior")) {
			if (specialization.equalsIgnoreCase("Juggernaut")) {
				skills.add(new Skills("Brick Wall", 5, "DefBuff", 5, 2));
				skills.add(new Skills("Punishing Blow", 10, "Both", 4, 0));
				skills.add(new Skills("'Final' Blow", 0, "Trade off", 5, 0));
			} else if (specialization.equalsIgnoreCase("Warlord")) {
				skills.add(new Skills("Battle Cry", 5, "attBuff", 4, 2));
				skills.add(new Skills("War March", 1, "ReduceCD", 5, 0));
				skills.add(new Skills("Earth Strike", 4, "DmgAttBuff", 5, 3));
			} else if (specialization.equalsIgnoreCase("Blademaster")) {
				skills.add(new Skills("Inner Focus", 2, "healBuff", 5, 2));
				skills.add(new Skills("Marked Target", 0, "Mark", 5, 3));
				skills.add(new Skills("Riposte", 6, "MultiHit", 5, 3));
			}
		}
		else if (classType.equalsIgnoreCase("wizard")) {
			if (specialization.equalsIgnoreCase("Reg Wizard")) {
				skills.add(new Skills("Poison Spell", 8, "Status", 4, 3));
				skills.add(new Skills("Fire Shield", 5, "DefBuff", 5, 3));
				skills.add(new Skills("Rage Spell", 5, "attBuff", 4, 2));
			} else if (specialization.equalsIgnoreCase("Ice Wizard")) {
				skills.add(new Skills("Slow Down", 2, "IncreaseCD", 5, 0));
				skills.add(new Skills("Freeze Spell", 0, "Freeze", 5, 3));
				skills.add(new Skills("Sluggish", -5, "DeAttBuff", 6, 4));
			} else if (specialization.equalsIgnoreCase("E Wiz")) {
				skills.add(new Skills("Zap", 0, "Stun", 4, 3));
				skills.add(new Skills("Spawn Shock", 20, "attack", 3, 0));
				skills.add(new Skills("Brain Reset", 0, "Scarmble", 5, 0));
			}
		}
		else if (classType.equalsIgnoreCase("bandit")) {
			if (specialization.equalsIgnoreCase("Phantom Striker")) {
				skills.add(new Skills("Soul Suck", 15, "Health Steal", 3, 0));
				skills.add(new Skills("Ghost Step", 5, "Buffs", 5, 2));
				skills.add(new Skills("Jumpscare", 0, "Freeze", 5, 3));
			} else if (specialization.equalsIgnoreCase("Trapist")) {
				skills.add(new Skills("False Trail", 0, "Sramble", 6, 0));
				skills.add(new Skills("Bear Trap", 0, "Mark", 6, 4));
				skills.add(new Skills("Poison Trap", 8, "Status", 6, 4));
			} else if (specialization.equalsIgnoreCase("Bruiser")) {
				skills.add(new Skills("Blood Rush", 10, "attBuff", 5, 2));
				skills.add(new Skills("Reckless Charge", 10, "Both", 4, 0));
				skills.add(new Skills("Heavy Punch", 20, "attack", 3, 0));
			}
		}
		else if (classType.equalsIgnoreCase("chronomancer")) {
			if (specialization.equalsIgnoreCase("Time Stitcher")) {
				skills.add(new Skills("Time Lash", 5, "DmgDefBuff", 5, 3));
				skills.add(new Skills("Temporal Stitch", 5, "healRegen", 6, 4));
				skills.add(new Skills("Reverse", 0, "reflect", 5, 0));
			} else if (specialization.equalsIgnoreCase("Clock Piercer")) {
				skills.add(new Skills("Time Break", 16, "attack", 3, 0));
				skills.add(new Skills("Temporal Spike", -5, "DeDefBuff", 5, 3));
				skills.add(new Skills("Overclock", 8, "attBuff", 6, 3));
			} else if (specialization.equalsIgnoreCase("Loopweaver")) {
				skills.add(new Skills("Destabilize", -4, "DeAttBuff", 4, 3));
				skills.add(new Skills("Flux Bomb", 14, "hitStun", 5, 0));
				skills.add(new Skills("Cooltwist", 2, "IncreaseCD", 5, 0));

			}
		}
		else if (classType.equalsIgnoreCase("alchemist")) {
			if (specialization.equalsIgnoreCase("Toxicologist")) {
				skills.add(new Skills("Poison Bomb", 10, "Status", 5, 3));
				skills.add(new Skills("Toxic Mist", 0, "Stun", 4, 0));
				skills.add(new Skills("Fungi Concoction", 0, "Random Debuff", 5, 0));
			} else if (specialization.equalsIgnoreCase("Biologist")) {
				skills.add(new Skills("White Cell Overdrive", 5, "healBuff", 6, 2));
				skills.add(new Skills("Genome Shuffle", 0, "Random Buff", 5, 0));
				skills.add(new Skills("Parasitic Leeching", 16, "Health Steal", 5, 0));
			} else if (specialization.equalsIgnoreCase("Combustinoneer")) {
				skills.add(new Skills("Ignit Catalyst", 6, "DmgAttBuff", 5, 3));
				skills.add(new Skills("Combustion Vial", 24, "Charged", 3, 0));
				skills.add(new Skills("Chain Reaction", 5, "MultiHit", 5, 3));

			}
		}
		else if (classType.equalsIgnoreCase("monk")) {
			if (specialization.equalsIgnoreCase("Hermit")) {
				skills.add(new Skills("Balanced Stance", 2, "DefBuff", 5, 3));
				skills.add(new Skills("Aura Pulse", 20, "Charged", 3, 0));
				skills.add(new Skills("Meditate", 2, "healBuff", 5, 2));
			} else if (specialization.equalsIgnoreCase("Sarabaite")) {
				skills.add(new Skills("Self Medicate", 0, "Random Buff", 4, 0));
				skills.add(new Skills("Eerie Tension", 4, "DmgAttBuff", 3, 4));
				skills.add(new Skills("Sinned Belief", 15, "Health Steal", 4, 0));
			} else if (specialization.equalsIgnoreCase("Gyrovagi")) {
				skills.add(new Skills("Disapline", 12, "Both", 3, 0));
				skills.add(new Skills("Will Power", 10, "regen", 7, 5));
				skills.add(new Skills("Honored Belief", 5, "Trade off", 5, 0));

			}
		}

		return skills;
	}
}
