import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     EMOTION ROGUELITE RPG v1.0       ║");
        System.out.println("╔══════════════════════════════════════╗");
        
        System.out.print("\nEnter your character name: ");
        String name = scanner.nextLine();
        
        System.out.println("\nChoose your class:");
        System.out.println("1. Warrior\n2. Wizard\n3. Bandit\n4. Chronomancer\n5. Alchemist\n6. Monk");
        
        int classChoice = scanner.nextInt();
        scanner.nextLine();
        
        String classType = "Warrior";
        if (classChoice == 2) classType = "Wizard";
        else if (classChoice == 3) classType = "Bandit";
        else if (classChoice == 4) classType = "Chronomancer";
        else if (classChoice == 5) classType = "Alchemist";
        else if (classChoice == 6) classType = "Monk";
        
        Player player = new Player(name, classType);
        
        System.out.println("\nYou start with ANGER - it charges when you take damage.");
        System.out.println("Unlock more emotions by surviving battles!");
        
        // Use RogueliteLoop instead of Exploration
        RogueliteLoop roguelite = new RogueliteLoop(player);
        roguelite.startRun();
        
        System.out.println("\n=== Game Over ===");
        scanner.close();
    }
}