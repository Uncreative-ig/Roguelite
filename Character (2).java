import java.util.*;

public class Character {
	protected String name;
	protected static int level;
	protected int health;
	protected int maxHealth;
	protected int attack;
	protected int defense;
	private Random ran = new Random();
	protected boolean neverMiss = false;
	protected int neverMissTurns = 0;
	protected boolean hopeMode = false;
	protected int hopeModeTurns = 0;

	// ====== Buffs & Debuffs ======
	protected int attBuff = 0;
	protected int defBuff = 0;
	protected int attDeBuff = 0;
	protected int defDeBuff = 0;
	protected int attBuffTurns = 0;
	protected int defBuffTurns = 0;
	protected int deAttBuffTurns = 0;
	protected int deDefBuffTurns = 0;
	protected boolean extraTurn = false;
	// ====== Status Effects ======
	private List<String> statusEffects = new ArrayList<>();
	private List<Integer> statusDurations = new ArrayList<>();
	protected boolean stunned = false;
	protected boolean frozen = false;
	protected boolean reflector = false;
	protected boolean isInvisible = false;
	protected int burnAttackPenalty = 0;

	// ====== Regen & Healing ======
	protected int regenAmount = 0;
	protected int regenDuration = 0;

	// ====== Critical Chance ======
	protected double critChance = 0.1;
	protected double tempCritBoost = 0.0;
	protected int critBoostTurns = 0;
	protected double critMultiplier = 1.5;

	// ====== Constructor ======
	public Character(String name, int level, int health, int attack, int defense) {
		this.name = name;
		this.level = level;
		this.health = this.maxHealth = health;
		this.attack = attack;
		this.defense = defense;
	}


	// ====== Combat Status ======
	public boolean isAlive() {
		return health > 0;
	}

	public void resetHealth() {
		health = maxHealth;
	}

	public boolean hasExtraTurn() {
		return extraTurn;
	}

	public void setExtraTurn (boolean t) {
		extraTurn = t;
	}

	public void takeDamage(double damage, Character attacker) {
		// Hope Mode protection - prevent death
		if (hopeMode && health - damage < 1) {
			health = 1;
			System.out.println(name + " refuses to fall! (Hope Mode)");
			return;
		}

		// Miss chance (unless never miss is active)
		if (attacker != null) {
			if (attacker.neverMiss) {
				// Never miss when Sadness emotion is active
			} else if (ran.nextInt(18) == 0) {
				System.out.println(attacker.name + " missed");
				return;
			}
		}

		int totalDefense = defense + defBuff - defDeBuff;
		double bouncedDamage = 0;

		if (hasReflector()) {
			System.out.println(name + " has a reflector! 50% of damage is bounced back!");
			bouncedDamage = damage * 0.50;
			damage *= 0.50;
		}

		int reduced = (int)Math.max(1, damage - totalDefense);
		health -= reduced;
		if (health < 0) health = 0;
		System.out.println(name + " takes " + reduced + " damage. (HP: " + health + ")");

		if (bouncedDamage > 0 && attacker != null) {
			attacker.health -= bouncedDamage;
			if (attacker.health < 0) attacker.health = 0;
			System.out.println(attacker.name + " takes " + (int)bouncedDamage + " bounced damage. (HP: " + attacker.health + ")");
		}
	}

	public int getRanDmg(int base, Character attacker) {
		int damage = base + attack + ran.nextInt(5) + attacker.attBuff - attacker.attDeBuff;

		if(this.critBoostTurns == 0)
		{
			this.critChance = 0.1;
		}
		this.critChance += this.tempCritBoost;
		if (ran.nextDouble() < critChance) {
			System.out.println(name + " lands a CRITICAL HIT!");
			damage *= critMultiplier;
		}

		return damage;
	}

	public void heal(int amount) {
		health = Math.min(maxHealth, health + amount);
		System.out.println(name + " heals for " + amount + " (HP: " + health + ")");
	}

	// ====== Buffs ======
	public void applyBuff(String type, int amount, int turns) {
		if (type.equals("attack")) {
			this.attBuff += amount;
			this.attBuffTurns = turns;
			System.out.println(name + " gains a attack buff for " + turns + " turns");
		} else if (type.equals("defense")) {
			this.defBuff += amount;
			this.defBuffTurns = turns;
			System.out.println(name + " gains a defense buff for " + turns + " turns");
		} else if (type.equals("both")) {
			this.attBuff += amount;
			this.defBuff += amount;
			this.attBuffTurns = turns;
			this.defBuffTurns = turns;
			System.out.println(name + " gains a defense and attack buff for " + turns + " turns");
		}
	}


	public void applyDeBuff(String type, int amount, int turns) {
		if (type.equals("attack")) {
			this.attDeBuff += amount;
			this.deAttBuffTurns = turns;
			System.out.println(name + " loses attack for " + turns + " turns");
		} else if (type.equals("defense")) {
			this.defDeBuff += amount;
			this.deDefBuffTurns = turns;
			System.out.println(name + " loses defense for " + turns + " turns");
		} else if (type.equals("both")) {
			this.attDeBuff += amount;
			this.defDeBuff += amount;
			this.deAttBuffTurns = turns;
			this.deDefBuffTurns = turns;
			System.out.println(name + " loses attack and defense for " + turns + " turns");
		}
	}

	public void updateBuffs() {
		if (attBuffTurns > 0 && --attBuffTurns == 0) {
			attBuff = 0;
			System.out.println(name + "'s attack buff wore off!");
		}

		if (defBuffTurns > 0 && --defBuffTurns == 0) {
			defBuff = 0;
			System.out.println(name + "'s defense buff wore off!");
		}

		if (deAttBuffTurns > 0 && --deAttBuffTurns == 0) {
			attDeBuff = 0;
			System.out.println(name + "'s attack debuff wore off!");
		}

		if (deDefBuffTurns > 0 && --deDefBuffTurns == 0) {
			defDeBuff = 0;
			System.out.println(name + "'s defense debuff wore off!");
		}

		if (critBoostTurns > 0 && --critBoostTurns == 0) {
			tempCritBoost = 0.0;
			System.out.println(name + "'s critical buff has worn off");
		}

		// NEW: Handle emotion-related buffs
		if (neverMissTurns > 0 && --neverMissTurns == 0) {
			neverMiss = false;
			System.out.println(name + " can miss again");
		}

		if (hopeModeTurns > 0 && --hopeModeTurns == 0) {
			hopeMode = false;
			System.out.println(name + "'s Hope Mode has ended!");
		}
	}


	// Status System
	public void setStatus(String status, int duration) {
		status = status.toLowerCase();

		int index = statusEffects.indexOf(status);
		if(index != -1) {
			statusDurations.set(index, duration);
		} else {
			statusEffects.add(status);
			statusDurations.add(duration);
		}
		System.out.println(name + " is now " + status);
	}

	public void applyStatus() {
		for(int i = 0; i < statusEffects.size(); i++) {
			String effect = statusEffects.get(i);
			int duration = statusDurations.get(i);

			if (duration <= 0) continue;

			if (effect.equals("frozen")) {
				frozen = true;
			} else if (effect.equals("stunned")) {
				stunned = true;
			} else if (effect.equals("burned")) {
				takeDamage(5, null);
				attack -= 2;
				burnAttackPenalty += 2;
			} else if (effect.equals("poisoned")) {
				takeDamage(12, null);
			} else if (effect.equals("invisible")) {
				isInvisible = true;
			} else if (effect.equals("reflect")) {
				reflector = true;
			}
			statusDurations.set(i, duration - 1);
		}
	}

	public void cleanupStatuses() {
		for (int i = 0; i < statusEffects.size(); i++) {
			String effect = statusEffects.get(i);
			int duration = statusDurations.get(i);

			if (duration <= 0) {
				clearStatus(effect);
				statusEffects.remove(i);
				statusDurations.remove(i);
				i--;
			}
		}
	}

	public void clearStatus(String effect) {
		if (effect.equals("frozen")) {
			frozen = false;
		} else if (effect.equals("stunned")) {
			stunned = false;
		} else if (effect.equals("burned")) {
			attack += burnAttackPenalty;
			burnAttackPenalty = 0;
		} else if (effect.equals("invisible")) {
			isInvisible = false;
		} else if (effect.equals("reflect")) {
			reflector = false;
		} else if (effect.equals("all")) {
			frozen = false;
			stunned = false;
			attack += burnAttackPenalty;
			burnAttackPenalty = 0;
			isInvisible = false;
			reflector = false;
		}
		System.out.println(name + "'s status effect has worn off");
	}

	public boolean hasReflector() {
		return reflector;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public boolean isStunned() {
		return stunned;
	}

	public boolean isInvisible() {
		return isInvisible;
	}

	// ====== Regen ======
	public void setRegen(int amount, int duration) {
		this.regenAmount = amount;
		this.regenDuration = duration;
	}

	public void applyRegen() {
		if (regenAmount > 0 && regenDuration > 0) {
			heal(regenAmount);
			regenDuration--;
			if (regenDuration == 0) {
				regenAmount = 0;
			}
		}
	}

	// ====== Crit Chance ======
	public void setCritChance(double amt, int duration) {
		this.tempCritBoost = amt;
		this.critBoostTurns = duration;
	}

	// ====== Display ======
	public void displayStats() {
		System.out.print(name + " (Level " + level + ") HP: " + health + "/" + maxHealth);
		if (!statusEffects.isEmpty()) {
			System.out.print(" | Status: ");
			for (int i = 0; i < statusEffects.size(); i++) {
				System.out.print(statusEffects.get(i) + "(" + statusDurations.get(i) + ")");
				if (i < statusEffects.size() - 1) {
					System.out.print(", ");
				}
			}
		}
		System.out.println("");
	}

	// ====== Emotion-Related ======
	public void setNeverMiss(boolean value, int turns) {
		this.neverMiss = value;
		this.neverMissTurns = turns;
		if (value) {
			System.out.println(name + " will never miss!");
		}
	}

	public void setHopeMode(boolean value, int turns) {
		this.hopeMode = value;
		this.hopeModeTurns = turns;
		if (value) {
			System.out.println(name + " enters Hope Mode - refuses to die!");
		}
	}

	public boolean isInHopeMode() {
		return hopeMode;
	}

	public boolean hasNeverMiss() {
		return neverMiss;
	}

	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	public int getAttack() {
		return attack;
	}
}
