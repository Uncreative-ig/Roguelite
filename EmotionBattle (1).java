import java.util.*;

public class EmotionBattle {
	private Player player;
	private Enemy enemy;
	private Scanner scanner;
	private EmotionManager emotionManager;
	private int turn = 0;

	// FIXED: Simplified tracking for emotion charges
	private int playerDamageDealtThisTurn = 0;
	private int playerHealthAtTurnStart = 0;
	private int enemyHealthAtTurnStart = 0;

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
			enemyHealthAtTurnStart = enemy.health;
			playerDamageDealtThisTurn = 0;

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

			// FIXED: Check if enemy died from player action
			if (!enemy.isAlive()) {
				playerDamageDealtThisTurn = enemyHealthAtTurnStart - enemy.health;
			}

			// Extra turn
			if (player.hasExtraTurn() && enemy.isAlive()) {
				System.out.println("\n" + player.getName() + " gets an extra turn!");
				int enemyHealthBefore = enemy.health;
				handlePlayerAction();
				playerDamageDealtThisTurn += Math.max(0, enemyHealthBefore - enemy.health);
				player.setExtraTurn(false);  // FIXED: Reset extra turn flag
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
					enemy.useSkill(player);
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
			return true;
		} else {
			System.out.println("\n*** DEFEAT ***");
			System.out.println("You were defeated by the " + enemy.getName() + "...");
			return false;
		}
	}

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
	}

	private void handleInvisibleEnemy() {
		System.out.println(enemy.name + " is invisible. " + player.name + " misses!");
		emotionManager.onMissedAttack();
	}

	private void handlePlayerAction() {
		System.out.println("\nChoose an action:");
		System.out.println("1. Normal Attack");
		System.out.println("2. Use Skill");

		int choice = scanner.nextInt();
		System.out.println();

		int enemyHealthBefore = enemy.health;

		if (choice == 1) {
			// FIXED: Use proper damage calculation
			int damage = player.getRanDmg(0, player);  // Base 0, adds attack in getRanDmg
			enemy.takeDamage(damage, player);

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
					// FIXED: Don't consume the turn if skill is on cooldown
					return;
				} else {
					player.useSkill(skillChoice, enemy);

					// Track RNG actions for Goofy emotion
					if (selected.getType().contains("Random")) {
						emotionManager.onRNGAction();
					}
				}
			}
		}

		// FIXED: Calculate damage dealt properly
		playerDamageDealtThisTurn = Math.max(0, enemyHealthBefore - enemy.health);
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

	// FIXED: Better emotion charge tracking logic
	private void trackEmotionCharges() {
		// Check if player took damage
		int actualDamageTaken = playerHealthAtTurnStart - player.health;
		if (actualDamageTaken > 0) {
			emotionManager.onDamageTaken(actualDamageTaken);
		}

		// Check if player dealt damage (for Pride)
		if (playerDamageDealtThisTurn > 0) {
			emotionManager.onDamageDealt();
		}

		// Check if fighting at low health (for Ashamed)
		emotionManager.checkLowHealthCombat();
		
		// Check if at full health (for Bored)
		emotionManager.checkFullHealth();

		// Check if losing badly (for Hope)
		if (player.isAlive() && enemy.isAlive()) {
			emotionManager.checkLosingBadly(player.health, player.maxHealth, enemy.health, enemy.maxHealth);
		}

		// Check for debuffs (for Confusion)
		if (player.attDeBuff > 0 || player.defDeBuff > 0) {
			emotionManager.onDebuffApplied();
		}
	}
}
