package com.example.zero2dev.storage;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CongratulationsGenerator {
    private static final List<String> CONGRATULATIONS_TEMPLATES = Arrays.asList(
            "Congratulations on your outstanding performance! You've achieved a remarkable {ranking} ranking in the {contestName}, completing {completedTasks} tasks.",
            "What an incredible achievement! Securing the {ranking} position in {contestName} and solving {completedTasks} problems is truly impressive.",
            "Your hard work has paid off! With a {ranking} place finish in {contestName} and {completedTasks} tasks completed, you've shown exceptional skill.",
            "Bravo! Your {ranking} ranking in {contestName} and successful completion of {completedTasks} challenges showcase your dedication and talent.",
            "Exceptional work! You've earned the {ranking} spot in {contestName} by tackling {completedTasks} problems with great success.",
            "Kudos on your fantastic performance! Achieving {ranking} place in {contestName} and conquering {completedTasks} tasks is a remarkable feat.",
            "Your brilliance shines through! With a {ranking} ranking in {contestName} and {completedTasks} problems solved, you've set a high bar.",
            "Celebrate your success! Your {ranking} position in {contestName} and mastery of {completedTasks} challenges reflect your outstanding abilities.",
            "Impressive results! Securing the {ranking} rank in {contestName} and overcoming {completedTasks} tasks demonstrates your exceptional skills.",
            "A round of applause for your achievement! Your {ranking} place in {contestName} and completion of {completedTasks} problems is truly commendable.",
            "Phenomenal job! Your {ranking} ranking in {contestName} and mastery of {completedTasks} tasks showcase your exceptional talent.",
            "Stellar performance! Achieving the {ranking} spot in {contestName} while conquering {completedTasks} challenges is truly remarkable.",
            "You've outdone yourself! Securing {ranking} place in {contestName} and solving {completedTasks} problems demonstrates your outstanding capabilities.",
            "Incredible accomplishment! Your {ranking} ranking in {contestName} and completion of {completedTasks} tasks is a testament to your dedication.",
            "Bravo on your exceptional achievement! Reaching {ranking} place in {contestName} and mastering {completedTasks} challenges shows your remarkable skill.",
            "Hats off to your success! Your {ranking} position in {contestName} and triumph over {completedTasks} problems is truly inspiring.",
            "Remarkable feat! Attaining the {ranking} rank in {contestName} while solving {completedTasks} tasks demonstrates your extraordinary abilities.",
            "You've raised the bar! Your {ranking} place in {contestName} and conquest of {completedTasks} challenges is an outstanding achievement.",
            "Exceptional performance! Securing {ranking} ranking in {contestName} and overcoming {completedTasks} problems showcases your remarkable talent.",
            "Kudos on your brilliant success! Your {ranking} position in {contestName} and mastery of {completedTasks} tasks is truly commendable.",
            "Outstanding work! Achieving {ranking} place in {contestName} while completing {completedTasks} challenges demonstrates your exceptional skills.",
            "A stellar accomplishment! Your {ranking} ranking in {contestName} and triumph over {completedTasks} problems is worthy of celebration.",
            "Magnificent performance! Securing the {ranking} spot in {contestName} and conquering {completedTasks} tasks shows your remarkable dedication.",
            "Bravo on your extraordinary achievement! Your {ranking} place in {contestName} and mastery of {completedTasks} challenges is truly impressive.",
            "You've excelled beyond measure! Attaining {ranking} rank in {contestName} while solving {completedTasks} problems showcases your exceptional abilities.",
            "Remarkable success! Your {ranking} position in {contestName} and completion of {completedTasks} tasks is a testament to your outstanding skills.",
            "Exceptional accomplishment! Achieving {ranking} place in {contestName} and overcoming {completedTasks} challenges demonstrates your remarkable talent.",
            "Phenomenal achievement! Your {ranking} ranking in {contestName} and mastery of {completedTasks} problems is truly worthy of recognition.",
            "Outstanding performance! Securing the {ranking} spot in {contestName} while conquering {completedTasks} tasks showcases your exceptional abilities.",
            "Bravo on your remarkable success! Your {ranking} place in {contestName} and triumph over {completedTasks} challenges is truly commendable."
    );

    public static String generateCongratulations(long ranking, String contestName, long completedTasks) {
        Random random = new Random();
        String template = CONGRATULATIONS_TEMPLATES.get(random.nextInt(CONGRATULATIONS_TEMPLATES.size()));

        return template
                .replace("{ranking}", getOrdinal(ranking))
                .replace("{contestName}", contestName)
                .replace("{completedTasks}", String.valueOf(completedTasks));
    }

    private static String getOrdinal(long number) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch ((int) (number % 100)) {
            case 11:
            case 12:
            case 13:
                return number + "th";
            default:
                return number + suffixes[(int) (number % 10)];
        }
    }
}
