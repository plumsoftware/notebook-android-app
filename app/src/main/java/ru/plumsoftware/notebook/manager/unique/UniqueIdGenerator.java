package ru.plumsoftware.notebook.manager.unique;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class UniqueIdGenerator {

    private static final String UPPER_CASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE_ALPHABET = UPPER_CASE_ALPHABET.toLowerCase();
    private static final String DIGITS = "0123456789";
    private static final int ID_LENGTH = 16;


    public static String generateUniqueId() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            sb.append(getRandomCharacter(UPPER_CASE_ALPHABET, random));
            sb.append(getRandomCharacter(LOWER_CASE_ALPHABET, random));
            sb.append(getRandomCharacter(DIGITS, random));
            sb.append(getRandomCharacter(UPPER_CASE_ALPHABET + LOWER_CASE_ALPHABET + DIGITS, random));

            if (i != 3) {
                sb.append("-");
            }
        }

        return sb.toString();
    }

    @NonNull
    public static String generateUniqueId_2() {
        Set<String> generatedIds = new HashSet<>();
        Random random = new Random();
        StringBuilder idBuilder = new StringBuilder(ID_LENGTH);

        while (true) {
            idBuilder.setLength(0);

            for (int i = 0; i < ID_LENGTH; i++) {
                char randomChar;
                if (random.nextBoolean()) {
                    randomChar = (char) (random.nextInt(26) + 'A');
                } else {
                    randomChar = (char) (random.nextInt(26) + 'a');
                }
                idBuilder.append(randomChar);
            }

            String newId = idBuilder.toString();

            generatedIds.add(newId);
            return newId;
        }
    }

    private static char getRandomCharacter(@NonNull String characters, @NonNull Random random) {
        int index = random.nextInt(characters.length());
        return characters.charAt(index);
    }
}
