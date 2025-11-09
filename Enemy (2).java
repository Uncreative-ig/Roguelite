import java.util.*;

public class Enemy extends Character {
	private int xpReward;
	private List<Skills> skills;
	private Random random = new Random();
	private int chargeTurn = 0;

	public Enemy(String name, int level, int health, int attack, int defense, int xpReward, List<Skills> skills) {
		super(name, level, health, attack, defense);
		this.xpReward = xpReward;
		this.skills = skills;
	}

	public void useSkill(Character target) {
		// pick a random available skill
		List<Skills> availableSkills = new ArrayList<>();
		for (Skills s : skills) {
			if (s.isReady()) availableSkills.add(s);
		}

		if (availableSkills.isEmpty()) {
			System.out.println(name + " uses a basic attack!");
			target.takeDamage(attack, this);
			return;
		}

		Skills skill = availableSkills.get(random.nextInt(availableSkills.size()));
		int base = skill.getBasePower();
		System.out.println(name + " uses " + skill.getName() + "!");

		String type = skill.getType();

		if (type.equals("attack")) {
			target.takeDamage(getRanDmg(base, this), this);

		} else if (type.equals("heal")) {
			heal(base);

		} else if (type.equals("Charged")) {
			if (chargeTurn == 0) {
				chargeTurn = 1;
				System.out.println(this.name + " is charging a powerful attack!");
			} else {
				target.takeDamage(getRanDmg(base, this), this);
				System.out.println(this.name + " unleashes the charged strike!");
				chargeTurn = 0;
			}

		} else if (type.equals("DefBuff")) {
			applyBuff("defense", base, skill.getDuration());

		} else if (type.equals("attBuff")) {
			applyBuff("attack", base, skill.getDuration());

		} else if (type.equals("DeDefBuff")) {
			target.applyDeBuff("defense", base, skill.getDuration());

		} else if (type.equals("DeAttBuff")) {
			target.applyDeBuff("attack", base, skill.getDuration());

		} else if (type.equals("Buffs")) {
			applyBuff("both", base, skill.getDuration());

		} else if (type.equals("DmgAttBuff")) {
			target.takeDamage(getRanDmg(base, this) + 15, this);
			applyBuff("attack", base, skill.getDuration());

		} else if (type.equals("DmgDefBuff")) {
			target.takeDamage(getRanDmg(base, this) + 15, this);
			applyBuff("defense", base, skill.getDuration());

		} else if (type.equals("Mark")) {
			System.out.println(target.getName() + " is marked, player crit chance increased!");
			setCritChance(0.9, skill.getDuration());

		} else if (type.equals("MultiHit")) {
			for (int i = 0; i < skill.getDuration(); i++) {
				target.takeDamage(getRanDmg(base, this), this);
			}

		} else if (type.equals("invis")) {
			setStatus("invisible", skill.getDuration());
			System.out.println(this.name + " has vanished!");

		} else if (type.equals("Health Steal")) {
			int damage = getRanDmg(base, this);
			target.takeDamage(damage, this);
			heal((int)(damage * 0.75));
			System.out.println(name + " drains life like a vampire!");

		} else if (type.equals("Status")) {
			target.setStatus("poisoned", skill.getDuration());
		} else if (type.equals("Both")) {
			System.out.println("Both sides take damage!");
			target.takeDamage(getRanDmg(base, this) + 15, this);
			takeDamage(base, this);

		} else if (type.equals("hitStun")) {
			target.setStatus("stunned", skill.getDuration());
			target.takeDamage(getRanDmg(base, this), this);

		} else if (type.equals("Stun")) {
			target.setStatus("stunned", skill.getDuration());

		} else if (type.equals("Freeze")) {
			target.setStatus("frozen", skill.getDuration());
		} else if (type.equals("healBuff")) {
			heal(base + 10);
			applyBuff("attack", base, skill.getDuration() + 1);
			applyBuff("defense", base, skill.getDuration());

		} else if (type.equals("reflect")) {
			setStatus("reflect", skill.getDuration());

		} else if (type.equals("ReduceCD")) {
			System.out.println(this.name + " manipulates time to refresh their skills!");
			reduceAllSkillCooldowns(base);

		} else if (type.equals("IncreaseCD")) {
			System.out.println(this.name + " manipulates time to delay enemy abilities!");
			((Enemy)target).increaseAllSkillCooldowns(base);

		} else if (type.equals("regen")) {
			setRegen(base, skill.getDuration());
			System.out.println(this.name + " regenerates for " + skill.getDuration() + " turns!");

		} else if (type.equals("healRegen")) {
			heal(base + 5);
			setRegen(base, skill.getDuration());
			System.out.println(name + " heals and regenerates!");

		} else if (type.equals("Random Debuff")) {
			int amount = random.nextInt(6);
			if (amount == 0) target.setStatus("frozen", skill.getDuration());
			else if (amount == 1) {
				System.out.println("The player has been marked");
				setCritChance(0.9, skill.getDuration());
			}
			else if (amount == 2) {
				System.out.println("The playerbs defense is now halved!");
				target.defense /= 2;
			}
			else if (amount == 3) ((Enemy)target).increaseAllSkillCooldowns(2);
			else if (amount == 4) {
				System.out.println("A flash fire burned the player!");
				setStatus("burned", 3);
			}
			else if (amount == 5) {
				System.out.println("You halved the playerbs defense!");
				target.defense /= 2;
			}

		} else if (type.equals("Random Buff")) {
			int amount = random.nextInt(6);
			if (amount == 0) applyBuff("attack", 6, 3);
			else if (amount == 1) {
				this.attack = 10;
				this.defense = 10;
			}
			else if (amount == 2) heal(maxHealth / 2);
			else if (amount == 3) setCritChance(0.25, 3);
			else if (amount == 4) reduceAllSkillCooldowns(1);
			else if (amount == 5) setStatus("invisible", 2);
		}

		// End of turn
		skill.setCurrentCooldown(skill.getCooldown());
	}

	public void reduceCooldowns() {
		for (Skills s : skills) s.reduceCooldown();
	}

	public void resetAllCooldowns() {
		for (Skills s : skills) {
			s.setCurrentCooldown(0);
		}
	}


	public void reduceAllSkillCooldowns(int amount) {
		for (Skills s : skills) {
			s.setCurrentCooldown(Math.max(0, s.currentCooldown() - amount));
		}
	}

	public void increaseAllSkillCooldowns(int amount) {
		for (Skills s : skills) {
			s.setCurrentCooldown(s.currentCooldown() + amount);
		}
	}

	public List<Skills> getSkills() {
		return skills;
	}

	public int getXpReward() {
		return xpReward;
	}

	// --- Static enemy sets for spawning ---
	public static Enemy[] getDefaultEnemies() {
		return new Enemy[] {
		           // Health (1-59), Attack (8-11), Defense (1-3)
		           new Enemy("Goblin", 1, 50, 11, 2, 35, SkillManager.getEnemySkillsFor("Goblin")),
		           new Enemy("Berserker", 1, 55, 9, 3, 35, SkillManager.getEnemySkillsFor("Berserker")),
		           new Enemy("Skeleton", 1, 45, 10, 1, 35, SkillManager.getEnemySkillsFor("Skeleton"))
		       };
	}

	public static Enemy[] getLvl2Enemies() {
		return new Enemy[] {
		           // Health (56-79), Attack (9-12), Defense (2-4)
		           new Enemy("Bats", 2, 65, 10, 2, 45, SkillManager.getEnemySkillsFor("Bats")),
		           new Enemy("Firecraker", 2, 70, 11, 3, 45, SkillManager.getEnemySkillsFor("Firecraker")),
		           new Enemy("Bomber", 2, 75, 9, 4, 45, SkillManager.getEnemySkillsFor("Bomber"))
		       };
	}

	public static Enemy[] getModerateEnemies() {
		return new Enemy[] {
		           // Health (80-99), Attack (13-16), Defense (3-6)
		           new Enemy("Archers", 3, 90, 13, 4, 65, SkillManager.getEnemySkillsFor("Archers")),
		           new Enemy("Mini Pekka", 3, 95, 16, 6, 65, SkillManager.getEnemySkillsFor("Mini Pekka")),
		           new Enemy("Dart Goblin", 3, 80, 14, 3, 65, SkillManager.getEnemySkillsFor("Dart Goblin"))
		       };
	}

	public static Enemy[] getLvl4Enemies() {
		return new Enemy[] {
		           // Health (100-110), Attack (16-20), Defense (4-7)
		           new Enemy("Goblin Machine", 4, 110, 20, 6, 90, SkillManager.getEnemySkillsFor("Goblin Machine")),
		           new Enemy("Archer Queen", 4, 100, 18, 5, 90, SkillManager.getEnemySkillsFor("Archer Queen")),
		           new Enemy("Dark Prince", 4, 105, 18, 7, 95, SkillManager.getEnemySkillsFor("Dark Prince"))
		       };
	}

	public static Enemy[] getMBossEnemies() {
		return new Enemy[] {
		           // Health (150+), Attack (20-25), Defense (8+)
		           new Enemy("Mega Knight", 5, 180, 25, 8, 150, SkillManager.getEnemySkillsFor("Megaknight")),
		           new Enemy("Boss Bandit", 5, 200, 23, 9, 175, SkillManager.getEnemySkillsFor("Boss Bandit")),
		           new Enemy("Golem", 5, 250, 20, 12, 200, SkillManager.getEnemySkillsFor("Golem"))
		       };
	}
}
