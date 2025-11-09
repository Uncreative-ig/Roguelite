import java.util.*;
import java.io.*;
import java.util.concurrent.*;

public class QTE {
    private static List<String> easyWords = new ArrayList<>();
    private static List<String> hardWords = new ArrayList<>();
    private static List<String> extremeWords = new ArrayList<>();
    private static Random random = new Random();
    private static Scanner scanner = new Scanner(System.in);

    static {
        loadWords("qte_words.txt");
    }

    private static void loadWords(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty()) {
                    if (word.length() <= 5) {
                        easyWords.add(word);
                    } else if (word.length() >= 8) {
                        extremeWords.add(word);
                    } else {
                        hardWords.add(word);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading QTE words. Using default.");
            easyWords = Arrays.asList("tap", "hit", "run");
            hardWords = Arrays.asList("parry", "strike", "deflect");
        }
    }

    public static boolean triggerQTE(String difficulty) {
        String target;
        int timeLimit;

        if (difficulty.equalsIgnoreCase("easy")) {
            target = easyWords.get(random.nextInt(easyWords.size()));
            timeLimit = 3000;
        } else if (difficulty.equalsIgnoreCase("Extreme")) {
            target = extremeWords.get(random.nextInt(extremeWords.size()));
            timeLimit = 6000;
        } else {
            target = hardWords.get(random.nextInt(hardWords.size()));
            timeLimit = 4000;
        }

        System.out.println("==QUICK TIME EVENT==");
        System.out.println("Type: '" + target + "' within " + (timeLimit / 1000.0) + " seconds!");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> {
            System.out.print("> ");
            return scanner.nextLine().trim();
        });

        try {
            String input = future.get(timeLimit, TimeUnit.MILLISECONDS);
            if (input.equalsIgnoreCase(target)) {
                System.out.println("Success!");
                return true;
            } else {
                System.out.println("Wrong word!");
                return false;
            }
        } catch (TimeoutException e) {
            System.out.println("\nToo slow!");
            future.cancel(true);
            return false;
        } catch (Exception e) {
            System.out.println("Error during QTE.");
            return false;
        } finally {
            executor.shutdownNow();
        }
    }
}
