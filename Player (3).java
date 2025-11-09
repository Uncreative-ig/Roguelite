import java.util.*;

public class Player extends Character {

	// ===== Attributes =====
	private int xp;
	private String classType;
	private List<Skills> skills;
	private Random power = new Random();
	private Scanner choice;
	private String specialization = null;
	private List<Skills> specializationSkills = new ArrayList<>();
	private int chargeTurn = 0;

	// ===== Constructor =====
	public Player(String name, String classType) {
		super(name, 1, 100, 15, 5);
		this.xp = 0;
		this.classType = classType.toLowerCase();
		this.skills = SkillManager.getSkillsFor(this.classType, this.level);
		setStats(this.classType);
	}

	// ===== Initial Stat Setup by Class =====
	private void setStats(String classType) {
		if (classType.equals("warrior")) {
			this.attack = 13;
			this.defense = 5;
			this.health = 110;
			this.maxHealth = 110;
		} else if (classType.equals("wizard")) {
			this.attack = 15;
			this.defense = 2;
			this.health = 85;
			this.maxHealth = 85;
		} else if (classType.equals("bandit")) {
			this.attack = 11;
			this.defense = 4;
			this.health = 90;
			this.maxHealth = 90;
		} else if (classType.equals("chronomancer")) {
			this.attack = 12;
			this.defense = 5;
			this.health = 95;
			this.maxHealth = 95;
		} else if (classType.equals("alchemist")) {
			this.attack = 10;
			this.defense = 4;
			this.health = 95;
			this.maxHealth = 95;
		} else if (classType.equals("monk")) {
			this.attack = 12;
			this.defense = 4;
			this.health = 100;
			this.maxHealth = 100;
		}
	}

	// ===== Skill Handling =====
	public List<Skills> getSkills() {
		return skills;
	}

	public void reduceCooldowns() {
		for (Skills s : skills) s.reduceCooldown();
	}

	public void resetAllCooldowns() {
		for (Skills s : skills) s.setCurrentCooldown(0);
	}

	public void increaseSkillPower()
	{
		for (Skills s : skills) {
			int amt = s.getBasePower();

			if(s.getType().equals("IncreaseCD") || amt <= 1 || s.getType().indexOf("Buff") >= 1)
			{
				continue;
			}
			// Increase skill power by 10%
			double multiplier = 1.0 + (0.1 * (this.level - 1));
			int newPower = (int)(amt * multiplier);
			s.setBasePower(newPower);
		}
	}

	public void useSkill(int index, Character target) {
		if (index < 0 || index >= skills.size()) return;
		Skills skill = skills.get(index);
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
				System.out.println(name + " is charging a powerful attack!");
			} else {
				target.takeDamage(getRanDmg(base, this), this);
				System.out.println(name + " unleashes the charged strike!");
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
			target.takeDamage(getRanDmg(base, this) + 10, this);
			applyBuff("attack", base, skill.getDuration());

		} else if (type.equals("DmgDefBuff")) {
			target.takeDamage(getRanDmg(base, this) + 10, this);
			applyBuff("defense", base, skill.getDuration());

		} else if (type.equals("Scramble")) {
			System.out.println("You scrambled your enemy's cooldowns!");
			for (Skills s : ((Enemy)target).getSkills()) {
				s.setCurrentCooldown(power.nextInt(s.getCooldown() + 1) + 1);
			}

		} else if (type.equals("Mark")) {
			System.out.println("The enemy is marked, your crit chance increased!");
			setCritChance(0.9, skill.getDuration());

		} else if (type.equals("MultiHit")) {
			for (int i = 0; i < skill.getDuration(); i++) {
				target.takeDamage(getRanDmg(base, this), this);
			}

		} else if (type.equals("invis")) {
			setStatus("invisible", skill.getDuration());
			System.out.println(name + " has vanished!");

		} else if (type.equals("Health Steal")) {
			int damage = getRanDmg(base, this);
			target.takeDamage(damage, this);
			heal((int)(damage * 0.75));
			System.out.println(name + " drains life like a vampire!");

		} else if (type.equals("Status")) {
			if (classType.equals("alchemist") || classType.equals("plantmancer") || classType.equals("wizard")) {
				target.setStatus("poisoned", skill.getDuration());
			}

		} else if (type.equals("Both")) {
			System.out.println("You both take damage!");
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
			heal(base + 15);
			applyBuff("attack", base, skill.getDuration() + 1);
			applyBuff("defense", base, skill.getDuration());

		} else if (type.equals("reflect")) {
			setStatus("reflect", skill.getDuration());

		} else if (type.equals("ReduceCD")) {
			System.out.println(name + " manipulates time to refresh their skills!");
			reduceAllSkillCooldowns(base);

		} else if (type.equals("IncreaseCD")) {
			System.out.println(name + " manipulates time to delay enemy abilities!");
			((Enemy)target).increaseAllSkillCooldowns(base);

		} else if (type.equals("regen")) {
			setRegen(base, skill.getDuration());
			System.out.println(name + " regenerates for " + skill.getDuration() + " turns!");

		} else if (type.equals("healRegen")) {
			heal(base + 5);
			setRegen(base, skill.getDuration());
			System.out.println(name + " heals and regenerates!");

		} else if (type.equals("Random Debuff")) {
			int amount = power.nextInt(6);
			if (amount == 0) target.setStatus("frozen", skill.getDuration());
			else if (amount == 1) {
				System.out.println("The enemy has been marked");
				setCritChance(0.9, skill.getDuration());
			}
			else if (amount == 2) {
				System.out.println("The enemies defense and health is now at 50%");
				target.health /= 0.5;
				target.defense /= 0.5;
			}
			else if (amount == 3) ((Enemy)target).increaseAllSkillCooldowns(2);
			else if (amount == 4) {
				System.out.println("A flash fire burned the enemy");
				setStatus("burned", 3);
			}
			else if (amount == 5) {
				System.out.println("You halved the enemies defense");
				target.defense /= 2;
			}

		} else if (type.equals("Random Buff")) {
			int amount = power.nextInt(6);
			if (amount == 0) setExtraTurn(true);
			else if (amount == 1) {
				setStatus("refelct", 3);
			}
			else if (amount == 2) heal(maxHealth / 2);
			else if (amount == 3) setCritChance(0.25, 3);
			else if (amount == 4) reduceAllSkillCooldowns(1);
			else if (amount == 5) setStatus("invisible", 2);

		} else if (type.equals("Trade off")) {
			if (classType.equals("warrior")) {
				target.takeDamage(30, null);
				applyDeBuff("attack", 4, 3);
			}
			else if (classType.equals("monk")) {
				heal(maxHealth);
				setStatus("reflect", 4);
				applyDeBuff("attack", 4, 3);
				applyDeBuff("defense", 2, 3);
			}
		}

		// End turn housekeeping
		setExtraTurn(false);
		skill.setCurrentCooldown(skill.getCooldown());
	}

	// Specialization
	public void chooseSpecialization(Scanner scanner) {  // b Added Scanner parameter
		if (specialization != null) {
			System.out.println("You already chose specialization: " + specialization);
			return;
		}

		String[] specs = SpecializationManager.getSpecializationsForClass(classType);
		if (specs.length == 0) {
			System.out.println("No specializations available.");
			return;
		}

		System.out.println("\n=== CHOOSE YOUR SPECIALIZATION ===");
		for (int i = 0; i < specs.length; i++) {
			String name = specs[i];
			String desc = specDesc(classType, name);
			String stat = specStatChanges(classType, name);

			System.out.println((i+1) + ". " + name + desc);
			System.out.println("   " + stat);
			System.out.println();
		}

		System.out.print("Your choice: ");
		int choice = scanner.nextInt() - 1;  // b Use passed scanner

		if (choice < 0 || choice >= specs.length) {
			System.out.println("Invalid choice!");
			return;
		}

		specialization = specs[choice];
		System.out.println("\nYou chose " + specialization + "!");
		statChange(classType, specialization);

		unlockSpecSkill(0);
	}

	private void unlockSpecSkill(int index) {
		List<Skills> allSpecSkills = SpecializationManager.getSkillsForSpecialization(classType, specialization);
		if (index < allSpecSkills.size()) {
			Skills skill = allSpecSkills.get(index);
			specializationSkills.add(skill);
			skills.add(skill);    // <<< Add skill to main skills list here
			System.out.println("Unlocked specialization skill: " + skill.getName());
		}
	}

	private String specDesc(String classType, String specName) {
		if (classType.equals("warrior")) {
			if (specName.equals("Juggernaut")) {
				return ": A slow moving wall of defense. Basically a brick wall...";
			}
			else if (specName.equals("Warlord")) {
				return ": Deals heavy damage but low sustainability. Its a big attacker...";
			}
			else if (specName.equals("Blademaster")) {
				return ": An offensive special who sustains itself with self-heals and buffs...";
			}
		}
		else if (classType.equals("wizard")) {
			if (specName.equals("Reg Wizard")) {
				return ": An upgrader with buffs and status attacks";
			}
			else if (specName.equals("Ice Wizard")) {
				return ": A controller with freeze, cooldown manipulation, and debuffs";
			}
			else if (specName.equals("E Wiz")) {
				return ": An offensive controller with cooldown manipulation and stuns. ";
			}
		}
		else if (classType.equals("bandit")) {
			if (specName.equals("Phantom Striker")) {
				return ": A buff controller. Self buffs, debuffs, and freeze...";
			}
			else if (specName.equals("Trapist")) {
				return ": Can confuse the enmies with multiple tools like cooldown manipulation and status...";
			}
			else if (specName.equals("Bruiser")) {
				return ": A straight up offensive attacker. Buffs and..yeah attacks...";
			}
		}
		else if (classType.equals("chronomancer")) {
			if (specName.equals("Time Stitcher")) {
				return ": A high sustainable support with heals and a reflector...";
			}
			else if (specName.equals("Clock Piercer")) {
				return ": An offensive special with buffs and debuffs...";
			}
			else if (specName.equals("Loopweaver")) {
				return ": An evasive special with stuns and cooldown manipulation...";
			}
		}
		else if (classType.equals("alchemist")) {
			if (specName.equals("Toxicologist")) {
				return ": A 'dps' menace with stuns, debuffs, and statuses...";
			}
			else if (specName.equals("Biologist")) {
				return ": A healer with health steal and buffs...";
			}
			else if (specName.equals("Combustinoneer")) {
				return ": An offensive special mainly with attacks and multihits...";
			}
		}
		else if (classType.equals("monk")) {
			if (specName.equals("Hermit")) {
				return ": A bulky, self sufficient special.Filled with defense buffs and heals...";
			}
			else if (specName.equals("Sarabaite")) {
				return ": A offensive special that uses buffs to boosts its damage...";
			}
			else if (specName.equals("Gyrovagi")) {
				return ": A special that has huge power with some risks included...";
			}
		}
		return "No desc";
	}

	private String specStatChanges(String classType, String specName) {
		if (classType.equals("warrior")) {
			// Range: +-[2-6]ATK, +-[1-3]DEF, +-[10-30]HP
			if (specName.equals("Juggernaut")) {
				return "[-4 ATK, +2 DEF, +16 HP]";
			}
			else if (specName.equals("Warlord")) {
				return "[+6 Atk, -2 DEF, -12 HP]";
			}
			else if (specName.equals("Blademaster")) {
				return "[+2 Atk, +1 DEF, +12 HP]";
			}
		}
		else if (classType.equals("wizard")) {
			if (specName.equals("Reg Wizard")) {
				return "[+2 Atk, -2 DEF, +18 HP]";
			}
			else if (specName.equals("Ice Wizard")) {
				return "[-6 Atk, +3 DEF, +21 HP]";
			}
			else if (specName.equals("E Wiz")) {
				return "[+4 Atk, +2 DEF, -13 HP]";
			}
		}
		else if (classType.equals("bandit")) {
			if (specName.equals("Phantom Striker")) {
				return "[+4 Atk, +3 DEF, -15 HP]";
			}
			else if (specName.equals("Trapist")) {
				return "[-2 Atk, +2 DEF, +22 HP]";
			}
			else if (specName.equals("Bruiser")) {
				return "[+6 Atk, -3 DEF, +18 HP]";
			}
		}
		else if (classType.equals("chronomancer")) {
			if (specName.equals("Time Stitcher")) {
				return "[+2 Atk, +3 DEF, -11 HP]";
			}
			else if (specName.equals("Clock Piercer")) {
				return "[+6 Atk, -2 DEF, +14 HP]";
			}
			else if (specName.equals("Loopweaver")) {
				return "[+2 Atk, +1 DEF, +16 HP]";
			}
		}
		else if (classType.equals("alchemist")) {
			if (specName.equals("Toxicologist")) {
				return "[-4 Atk, +1 DEF, +16 HP]";
			}
			else if (specName.equals("Biologist")) {
				return "[-2 Atk, +3 DEF, +16 HP]";
			}
			else if (specName.equals("Combustinoneer")) {
				return "[+6 Atk, +2 DEF, -17 HP]";
			}
		}
		else if (classType.equals("monk")) {
			if (specName.equals("Hermit")) {
				return "[-4 Atk, +2 DEF, +25 HP]";
			}
			else if (specName.equals("Sarabaite")) {
				return "[+3 Atk, -1 DEF, +18 HP]";
			}
			else if (specName.equals("Gyrovagi")) {
				return "[+2 Atk, +1 DEF, +10 HP]";
			}
		}
		return "No stat changes";
	}

	public void statChange(String classType, String specName) {
		if (classType.equals("warrior")) {
			if (specName.equals("Juggernaut")) {
				this.attack -= 4;
				this.defense += 2;
				this.maxHealth += 16;
			}
			else if (specName.equals("Warlord")) {
				this.attack += 6;
				this.defense -= 2;
				this.maxHealth -= 12;
			}
			else if (specName.equals("Blademaster")) {
				this.attack += 2;
				this.defense += 1;
				this.maxHealth += 12;
			}
		}
		else if (classType.equals("wizard")) {
			if (specName.equals("Reg Wizard")) {
				this.attack += 2;
				this.defense -= 2;
				this.maxHealth += 18;
			}
			else if (specName.equals("Ice Wizard")) {
				this.attack -= 6;
				this.defense += 3;
				this.maxHealth += 21;
			}
			else if (specName.equals("E Wiz")) {
				this.attack += 4;
				this.defense += 2;
				this.maxHealth -= 13;
			}
		}
		else if (classType.equals("bandit")) {
			if (specName.equals("Phantom Striker")) {
				this.attack += 4;
				this.defense += 3;
				this.maxHealth -= 15;
			}
			else if (specName.equals("Trapist")) {
				this.attack -= 2;
				this.defense += 2;
				this.maxHealth += 22;
			}
			else if (specName.equals("Bruiser")) {
				this.attack += 6;
				this.defense -= 3;
				this.maxHealth += 18;
			}
		}
		else if (classType.equals("chronomancer")) {
			if (specName.equals("Time Stitcher")) {
				this.attack += 2;
				this.defense += 3;
				this.maxHealth -= 11;
			}
			else if (specName.equals("Clock Piercer")) {
				this.attack += 6;
				this.defense -= 2;
				this.maxHealth += 14;
			}
			else if (specName.equals("Loopweaver")) {
				this.attack += 2;
				this.defense += 1;
				this.maxHealth += 16;
			}
		}
		else if (classType.equals("alchemist")) {
			if (specName.equals("Toxicologist")) {
				this.attack -= 4;
				this.defense += 1;
				this.maxHealth += 16;
			}
			else if (specName.equals("Biologist")) {
				this.attack -= 2;
				this.defense += 3;
				this.maxHealth += 16;
			}
			else if (specName.equals("Combustinoneer")) {
				this.attack += 6;
				this.defense += 2;
				this.maxHealth -= 17;
			}
		}
		else if (classType.equals("monk")) {
			if (specName.equals("Hermit")) {
				this.attack -= 4;
				this.defense += 2;
				this.maxHealth += 25;
			}
			else if (specName.equals("Sarabaite")) {
				this.attack += 3;
				this.defense -= 1;
				this.maxHealth += 18;
			}
			else if (specName.equals("Gyrovagi")) {
				this.attack += 2;
				this.defense += 1;
				this.maxHealth -= 10;
			}
		}
	}

	// ===== Cooldown manipulation =====
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

	// ===== Leveling System =====
	public void gainXP(int amount, Scanner scanner) {  // b Added Scanner parameter
		xp += amount;
        System.out.println("DEBUG: Total XP = " + xp + " / " + (level * 75) + " needed");

		if (xp >= level * 75) {
			level++;
			updateSkills();
			increaseSkillPower();
			xp = 0;
			System.out.println(name + " leveled up to " + level + "!");

			// Permanent buff choice
			System.out.println("\nChoose a permanent buff:");
			System.out.println("1. +10 HP");
			System.out.println("2. +2 attack");
			System.out.println("3. +1 defense");
			System.out.print("Your choice: ");

			int c = scanner.nextInt();  // b Use passed scanner

			if (c == 1) {
				maxHealth += 10;
				System.out.println("Max HP increased by 10!");
			} else if (c == 2) {
				attack += 2;
				System.out.println("Attack increased by 2!");
			} else {
				defense += 1;
				System.out.println("Defense increased by 1!");
			}
			health = maxHealth;

			if(level == 2) {
				chooseSpecialization(scanner);  // b Pass scanner
			}

			if (specialization != null) {
				if (level == 3) {
					unlockSpecSkill(1);
				}
				if (level == 4) {
					unlockSpecSkill(2);
				}
			}
		}
	}


	public void updateSkills() {
		List<Skills> baseSkills = SkillManager.getSkillsFor(this.classType, this.level);
		baseSkills.addAll(specializationSkills);
		this.skills = baseSkills;
	}




	// ===== Passive Abilities =====
	public void applyPassiveStart(Enemy enemy) {
		if (this.getLevel() >= 3) {
			if (classType.equals("warrior")) {
				System.out.println(name + "'s passive ability activated!");
				applyBuff("attack", 1, 1);
			} else if (classType.equals("bandit")) {
				if (power.nextInt(5) == 0) {
					System.out.println(name + "'s passive ability activated!");
					setStatus("invisible", 3);
				}
			} else if (classType.equals("chronomancer")) {
				if (power.nextInt(10) == 0) {
					System.out.println(name + "'s passive ability activated!");
					reduceAllSkillCooldowns(1);
				}
			} else if (classType.equals("witch")) {
				System.out.println(name + "'s passive ability activated!");
				System.out.println("Larry comes out for assistant");
				enemy.takeDamage(15, this);
			}
		}
	}

	public void applyPassiveEnd(Enemy enemy) {
		if (this.getLevel() >= 3) {
			if (classType.equals("wizard")) {
				if (power.nextInt(10) < 3)
				{
					System.out.println(name + "'s passive ability activated!");
					enemy.setStatus("burned", 2);
				}
			} else if (classType.equals("alchemist")) {
				if (power.nextInt(10) < 3)
				{
					System.out.println(name + "'s passive ability activated!");
					enemy.setStatus("poisoned", 2);
				}
			} else if (classType.equals("monk")) {
				System.out.println(name + "'s passive ability activated!");
				heal(8);
			} else if (classType.equals("illusionist")) {
				System.out.println(name + "'s passive ability activated!");
				if (power.nextInt(10) == 0) {
					setStatus("invisible", 2);
					System.out.println(name + " has vanished!");
				}
			} else if (classType.equals("plantmancer")) {
				if (this.health >= (this.maxHealth / 2)) {
					System.out.println(name + "'s passive ability activated!");
					applyBuff("attack", 2, 2);
				}
			}
		}
	}

	public void halveAllCooldowns() {
		System.out.println(name + " halves all cooldowns!");
		for (Skills s : skills) {
			int current = s.currentCooldown();
			s.setCurrentCooldown(current / 2);
		}
	}

	public void convertDebuffsToBuffs() {
		System.out.println(name + " converts all debuffs to buffs!");

		if (attDeBuff > 0) {
			attBuff += attDeBuff;
			attBuffTurns = Math.max(attBuffTurns, deAttBuffTurns);
			attDeBuff = 0;
			deAttBuffTurns = 0;
			System.out.println("  Attack debuff became a buff!");
		}

		if (defDeBuff > 0) {
			defBuff += defDeBuff;
			defBuffTurns = Math.max(defBuffTurns, deDefBuffTurns);
			defDeBuff = 0;
			deDefBuffTurns = 0;
			System.out.println("  Defense debuff became a buff!");
		}

		// Clear status debuffs too
		if (frozen || stunned) {
			clearStatus("all");
			System.out.println("  Status effects cleared!");
		}
	}

	public void applyRandomBuffs(int count) {
		System.out.println(name + " receives " + count + " random buff(s)!");
		Random rand = new Random();

		for (int i = 0; i < count; i++) {
			int buff = rand.nextInt(6);

			switch(buff) {
			case 0:
				applyBuff("attack", 5, 3);
				System.out.println("  + Attack boost!");
				break;

			case 1:
				applyBuff("defense", 5, 3);
				System.out.println("  + Defense boost!");
				break;

			case 2:
				setCritChance(0.30, 3);
				System.out.println("  + Critical chance boost!");
				break;

			case 3:
				setRegen(8, 3);
				System.out.println("  + Regeneration!");
				break;

			case 4:
				reduceAllSkillCooldowns(1);
				System.out.println("  + Cooldown reduction!");
				break;

			case 5:
				setStatus("invisible", 2);
				System.out.println("  + Invisibility!");
				break;
			}
		}
	}

	public String getName() {
		return name;
	}

	public int getCurrentHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}
	public boolean dealtDamageThisTurn(int enemyHealthBefore, int enemyHealthAfter) {
		return enemyHealthAfter < enemyHealthBefore;
	}

}