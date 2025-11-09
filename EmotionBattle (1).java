import java.util.*;

public class EmotionBattle {
	private Player player;
	private Enemy enemy;
	private Scanner scanner;
	private EmotionManager emotionManager;
	private int turn = 0;

	// Tracking for emotion charges
	private int playerDamageDealtThisTurn = 0;
	private int enemyDamageDealtThisTurn = 0;
	private int playerHealthAtTurnStart = 0;
	private int turnsSincePlayerDamage = 0;

	public EmotionBattle(Player player, Enemy enemy, EmotionManager emotionManager) {
		this.player = player;
		this.enemy = enemy;
		this.emotionManager = emotionManager;
		this.scanner = new Scanner(System.in);
	}

	public boolean start() {
		System.out.println("\n--- A wild " + enemy.getName() + " appears! ---");

		player.resetAllCooldowns();
		enemy.resetAllCooldowns();

		while (player.isAlive() && enemy.isAlive()) {
			// Start of turn setup
			turn++;
			playerHealthAtTurnStart = player.health;
			playerDamageDealtThisTurn = 0;
			enemyDamageDealtThisTurn = 0;

			// Update buffs and effects
			player.updateBuffs();
			player.reduceCooldowns();
			if (!player.isAlive()) break;

			player.applyRegen();
			player.applyPassiveStart(enemy);

			enemy.updateBuffs();
			enemy.reduceCooldowns();
			if (!enemy.isAlive()) break;

			// Update emotions
			emotionManager.updateEmotions();

			System.out.println("\n" + "=".repeat(50));
			System.out.println("Turn " + turn);
			System.out.println("=".repeat(50));

			// Display stats
			player.displayStats();
			emotionManager.displayEmotionStatus();
			System.out.println();
			enemy.displayStats();

			// Player's turn
			player.applyStatus();
			player.cleanupStatuses();

			if (player.isFrozen() || player.isStunned()) {
				handlePlayerSkipTurn();
			} else if (enemy.isInvisible()) {
				handleInvisibleEnemy();
			} else {
				handlePlayerAction();
			}

			if (player.hasExtraTurn()) {
				handlePlayerAction();
			}

			// Enemy's turn if still alive
			if (enemy.isAlive()) {
				enemy.applyStatus();
				enemy.cleanupStatuses();

				if (enemy.isFrozen() || enemy.isStunned()) {
					handleEnemySkipTurn();
				} else if (player.isInvisible()) {
					handleInvisiblePlayer();
				} else {
					int enemyHealthBefore = enemy.health;
					enemy.useSkill(player);
					enemyDamageDealtThisTurn = enemyHealthBefore - enemy.health;
				}
			}

			// End of turn emotion charge tracking
			trackEmotionCharges();

			// Check and auto-activate charged emotions
			emotionManager.checkAndActivateEmotions(enemy);
		}

		// End of battle
		if (player.isAlive()) {
			System.out.println("\n*** VICTORY! ***");
			System.out.println("You defeated the " + enemy.getName() + "!");

			// Don't give XP here - return the reward amount instead
			return true;
		} else {
			System.out.println("\n*** DEFEAT ***");
			System.out.println("You were defeated by the " + enemy.getName() + "...");
			return false;
		}
	}

	// Add this new method to get XP reward without giving it
	public int getXPReward() {
		return enemy.getXpReward();
	}

	private void handlePlayerSkipTurn() {
		if (player.isFrozen()) {
			System.out.println(player.name + " is frozen, their turn is skipped");
			player.takeDamage(8, null);
		} else if (player.isStunned()) {
			System.out.println(player.name + " is stunned, their turn is skipped");
		}
		turnsSincePlayerDamage++;
		emotionManager.onNoDamageDealt();
	}

	private void handleInvisibleEnemy() {
		System.out.println(enemy.name + " is invisible. " + player.name + " misses!");
		emotionManager.onMissedAttack();
		turnsSincePlayerDamage++;
		emotionManager.onNoDamageDealt();
	}

	private void handlePlayerAction() {
		System.out.println("\nChoose an action:");
		System.out.println("1. Normal Attack");
		System.out.println("2. Use Skill");

		int choice = scanner.nextInt();
		System.out.println();

		if (choice == 1) {
			int enemyHealthBefore = enemy.health;
			int dmg = player.attack;

			if (player.tempCritBoost > 0.0) {
				System.out.println("CRITICAL HIT");
				dmg *= 1.5;
			}

			enemy.takeDamage(dmg, player);
			playerDamageDealtThisTurn = enemyHealthBefore - enemy.health;

			if (playerDamageDealtThisTurn > 0) {
				turnsSincePlayerDamage = 0;
			} else {
				turnsSincePlayerDamage++;
			}

		} else if (choice == 2) {
			List<Skills> skills = player.getSkills();
			for (int i = 0; i < skills.size(); i++) {
				Skills skill = skills.get(i);
				if (!skill.isReady()) {
					System.out.println((i + 1) + ". " + skill.getName() + " (Cooldown: " + skill.currentCooldown() + ")");
				} else {
					System.out.println((i + 1) + ". " + skill.getName());
				}
			}

			int skillChoice = scanner.nextInt() - 1;

			if (skillChoice >= 0 && skillChoice < skills.size()) {
				Skills selected = skills.get(skillChoice);
				if (!selected.isReady()) {
					System.out.println("That skill is still on cooldown!");
				} else {
					int enemyHealthBefore = enemy.health;
					player.useSkill(skillChoice, enemy);
					playerDamageDealtThisTurn = enemyHealthBefore - enemy.health;

					if (playerDamageDealtThisTurn > 0) {
						turnsSincePlayerDamage = 0;
					} else {
						turnsSincePlayerDamage++;
					}

					// Track RNG actions for Goofy emotion
					if (selected.getType().contains("Random")) {
						emotionManager.onRNGAction();
					}
				}
			}
		}
	}

	private void handleEnemySkipTurn() {
		if (enemy.isFrozen()) {
			System.out.println(enemy.name + " is frozen, their turn is skipped");
			enemy.takeDamage(8, null);
		} else if (enemy.isStunned()) {
			System.out.println(enemy.name + " is stunned, their turn is skipped");
		}
	}

	private void handleInvisiblePlayer() {
		System.out.println(player.name + " is invisible. " + enemy.name + " misses!");
	}

	private void trackEmotionCharges() {
		// Check if player took damage
		if (player.health < playerHealthAtTurnStart) {
			int damageTaken = playerHealthAtTurnStart - player.health;
			emotionManager.onDamageTaken(damageTaken);
		} else {
			emotionManager.onNoDamageTaken();
		}

		// Check if no damage dealt this turn
		if (playerDamageDealtThisTurn == 0 && turnsSincePlayerDamage >= 2) {
			emotionManager.onNoDamageDealt();
		}

		// Check if no combat damage on both sides
		if (playerDamageDealtThisTurn == 0 && enemyDamageDealtThisTurn == 0) {
			emotionManager.onTurnNoCombatDamage();
		} else {
			emotionManager.resetTurnTracking();
		}

		// Check if losing badly
		if (player.isAlive() && enemy.isAlive()) {
			emotionManager.checkLosingBadly(player.health, player.maxHealth, enemy.health, enemy.maxHealth);
		}

		// Check for debuffs
		if (player.attDeBuff > 0 || player.defDeBuff > 0) {
			emotionManager.onDebuffApplied();
		}
	}
}