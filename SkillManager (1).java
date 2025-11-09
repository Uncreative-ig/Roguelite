import java.util.*;

public class SkillManager {



	public static List<Skills> getSkillsFor(String classType, int level) {
		List<Skills> skillList = new ArrayList<>();

		if (classType.equalsIgnoreCase("warrior")) {
			skillList.add(new Skills("Charged Hit", 22, "Charged", 3, 0));
			skillList.add(new Skills("Battle Hardened", 2, "DmgDefBuff", 4, 3));
		} else if (classType.equalsIgnoreCase("wizard")) {
			skillList.add(new Skills("Fireball", 18, "attack", 2, 0));
			skillList.add(new Skills("Heal", 21, "heal", 3, 0));
		} else if (classType.equalsIgnoreCase("bandit")) {
			skillList.add(new Skills("Backstab", 10, "hitStun", 3, 2));
			skillList.add(new Skills("Smoke Dash", 0, "invis", 5, 3));
		} else if (classType.equalsIgnoreCase("chronomancer")) {
			skillList.add(new Skills("Rewind", 1, "ReduceCD", 4, 0));
			skillList.add(new Skills("Twisted Timeline", 0, "Scramble", 5, 0));
		} else if (classType.equalsIgnoreCase("alchemist")) {
			skillList.add(new Skills("Anicdote", 8, "regen", 6, 5));
			skillList.add(new Skills("Failed Experiment", 7, "Both", 3, 0));
		} else if (classType.equalsIgnoreCase("monk")) {
			skillList.add(new Skills("Triple Push", 3, "MultiHit", 4, 3));
			skillList.add(new Skills("Focused Eye", 0, "reflect", 6, 4));
		} 
		return skillList;
	}

	public static List<Skills> getEnemySkillsFor(String enemyType) {
		List<Skills> skillList = new ArrayList<>();

		if (enemyType.equalsIgnoreCase("Goblin")) {
			skillList.add(new Skills("Spear Throw", 10, "Charged", 2, 0));
		} else if (enemyType.equalsIgnoreCase("Berserker")) {
			skillList.add(new Skills("Frenzy", 4, "MultiHit", 4, 3));
		} else if (enemyType.equalsIgnoreCase("Skeleton")) {
			skillList.add(new Skills("Clones", 4, "MultiHit", 4, 4));
		} else if (enemyType.equalsIgnoreCase("Bats")) {
			skillList.add(new Skills("Health Steal", 14, "Health Steal", 4, 0));
			skillList.add(new Skills("Bites", 3, "MultiHit", 5, 5));
		} else if (enemyType.equalsIgnoreCase("Firecraker")) {
			skillList.add(new Skills("Firework Rocket", 14, "attack", 4, 0));
			skillList.add(new Skills("Sparks", 4, "MultiHit", 5, 4));
		} else if (enemyType.equalsIgnoreCase("Bomber")) {
			skillList.add(new Skills("Bomb Toss", 16, "attack", 4, 0));
			skillList.add(new Skills("Bomb Bounce", 6, "MultiHit", 3, 2));
		} else if (enemyType.equalsIgnoreCase("Archers")) {
			skillList.add(new Skills("Twin Arrows", 8, "MultiHit", 3, 2));
			skillList.add(new Skills("Power Shot", 15, "attack", 3, 0));
			skillList.add(new Skills("Charged Shot", 25, "Charged", 3, 0));
		} else if (enemyType.equalsIgnoreCase("Mini Pekka")) {
			skillList.add(new Skills("Slash", 23, "attack", 4, 0));
			skillList.add(new Skills("Pancakes", 20, "heal", 4, 0));
			skillList.add(new Skills("Toughen Up", 5, "DefBuff", 5, 3));
		} else if (enemyType.equalsIgnoreCase("Dart Goblin")) {
			skillList.add(new Skills("Poison Dart", 12, "Status", 5, 4));
			skillList.add(new Skills("Rapid Fire", 4, "MultiHit", 5, 5));
			skillList.add(new Skills("Tranqilizer", 7, "DeAttBuff", 4, 3));
		} else if (enemyType.equalsIgnoreCase("Goblin Machine")) {
			skillList.add(new Skills("Fist Smash", 18, "attack", 4, 0));
			skillList.add(new Skills("Rocket", 24, "attack", 5, 0));
			skillList.add(new Skills("Tatical Shot", 30, "Charged", 3, 0));
		} else if (enemyType.equalsIgnoreCase("Archer Queen")) {
			skillList.add(new Skills("Royal Shot", 20, "attack", 4, 0));
			skillList.add(new Skills("Sneaky", 0, "Invis", 6, 2));
			skillList.add(new Skills("Rapid Fire", 4, "MultiHit", 5, 4));
		} else if (enemyType.equalsIgnoreCase("Dark Prince")) {
			skillList.add(new Skills("Ram", 24, "attack", 4, 0));
			skillList.add(new Skills("Shield Up", 10, "DefBuff", 6, 2));
			skillList.add(new Skills("Dizzy Pathing", 0, "Stun", 5, 2));
		} else if (enemyType.equalsIgnoreCase("Mega Knight")) {
			skillList.add(new Skills("Mega Slam", 24, "attack", 5, 0));
			skillList.add(new Skills("Jump", 30, "Charged", 3, 0));
			skillList.add(new Skills("Lift Up", 0, "Stun", 5, 0));
			skillList.add(new Skills("Midladder Menace", 5, "DeAttBuff", 5, 3));
		} else if (enemyType.equalsIgnoreCase("Boss Bandit")) {
			skillList.add(new Skills("Club Hit", 23, "attack", 5, 0));
			skillList.add(new Skills("Invisible", 0, "Invis", 6, 3));
			skillList.add(new Skills("Bandit Dash", 30, "Charged", 3, 0));
			skillList.add(new Skills("No Skill", 16, "Health Steal", 4, 0));
		} else if (enemyType.equalsIgnoreCase("Golem")) {
			skillList.add(new Skills("Rock Punch", 25, "attack", 4, 0));
			skillList.add(new Skills("Golemite", 15, "MultiHit", 5, 2));
			skillList.add(new Skills("Split Damage", 15, "Both", 4, 0));
			skillList.add(new Skills("Smirk", 6, "DeDefBuff", 5, 3));
		} else if (enemyType.equalsIgnoreCase("Spirit King")) {
			skillList.add(new Skills("Slash", 35, "attack", 2, 0));
			skillList.add(new Skills("Soul Suck", 25, "Health Steal", 3, 0));
			skillList.add(new Skills("Bad Omen", 3, "DeDefBuff", 5, 3));
			skillList.add(new Skills("Flame Burst", 15, "Status", 4, 3));
			skillList.add(new Skills("Transparency", 3, "DeAttBuff", 5, 3));
		}

		return skillList;
	}
}
