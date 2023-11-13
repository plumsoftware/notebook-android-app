package ru.plumsoftware.notebook.utilities;

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

            if (i != 3) { // Добавляем "-" между каждым блоком
                sb.append("-");
            }
        }

        return sb.toString();
    }

    public static String generateUniqueId_2() {
        Set<String> generatedIds = new HashSet<>();
        Random random = new Random();
        StringBuilder idBuilder = new StringBuilder(ID_LENGTH);

        while (true) {
            idBuilder.setLength(0); // Очищаем StringBuilder перед генерацией нового идентификатора

            // Генерируем символы и числа в идентификатор
            for (int i = 0; i < ID_LENGTH; i++) {
                char randomChar;
                if (random.nextBoolean()) {
                    randomChar = (char) (random.nextInt(26) + 'A'); // Заглавная буква
                } else {
                    randomChar = (char) (random.nextInt(26) + 'a'); // Строчная буква
                }
                idBuilder.append(randomChar);
            }

            String newId = idBuilder.toString();

            generatedIds.add(newId);
            return newId;
        }
    }

    private static char getRandomCharacter(String characters, Random random) {
        int index = random.nextInt(characters.length());
        return characters.charAt(index);
    }
}
