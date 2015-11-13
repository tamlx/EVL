package com.thanhle.englishvocabulary.utils;

import android.content.Context;
import android.util.Log;

import com.thanhle.englishvocabulary.R;
import com.thanhle.englishvocabulary.database.DatabaseHandler;
import com.thanhle.englishvocabulary.database.tables.CardTable;
import com.thanhle.englishvocabulary.database.tables.LibraryTable;
import com.thanhle.englishvocabulary.database.tables.WordTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Random;

public class DataUtils {
    private static final String TAG = DataUtils.class.getSimpleName();
    private static Random rnd = new Random();

    public static int getRawResIdByName(Context context, String name) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(name, "raw",
                packageName);
        return resId;
    }

    public static ArrayList<LibraryTable> getListLibrary(Context context) {
        ArrayList<LibraryTable> r = new ArrayList<LibraryTable>();
        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {
            String name = fields[count].getName();
            if (name.startsWith("data_")) {
                String code = name.substring(5);
                name = getLibraryName(context, code);
                r.add(new LibraryTable(code, name));
            }
        }
        return r;
    }

    /**
     * Read card data from raw resource
     *
     * @param context context
     * @param library library name
     * @return true if success
     */
    public static boolean readData(Context context, String library) {
        int dataResId = getRawResIdByName(context, "data_" + library);
        if (dataResId == 0) {
            return false;
        }
        DatabaseHandler database = new DatabaseHandler(context);
        InputStream in = context.getResources().openRawResource(dataResId);
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        String line;
        int lineCount = 1;
        ArrayList<CardTable> cards = new ArrayList<CardTable>();
        try {
            // skip first line (name of words)
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                String[] values = line.split("\t");
                Log.d("abc",values.toString());
                CardTable card = new CardTable(values, library);
                if (card.word != null) {
                    card.word = card.word.toLowerCase(Locale.US).trim();
                    cards.add(card);
                } else {
                    Log.w(TAG, "data line error: " + lineCount);
                }
                lineCount++;
            }
            String libraryName = getLibraryName(context, library);
            database.insertLibrary(new LibraryTable(library, libraryName));
            database.insertCard(cards);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getLibraryName(Context context, String library) {
        int dataResId = getRawResIdByName(context, "data_" + library);
        if (dataResId == 0) {
            return null;
        }
        InputStream in = context.getResources().openRawResource(dataResId);
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        String line = null;
        try {
            // read first line
            line = bf.readLine();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    /**
     * get random number from 1 to max, and not have valueNotExpect
     *
     * @param max
     * @param valueNotExpect
     * @return
     */
    public static int getRandomNumber(int max, int valueNotExpect) {
        int value = 0;
        while ((value = rnd.nextInt(max) + 1) == valueNotExpect)
            ;
        return value;
    }

    /**
     * Returns a pseudo-random uniformly distributed int in the half-open range
     * [0, n).
     *
     * @param n
     * @return
     */
    public static int getRandomNumber(int n) {
        return rnd.nextInt(n);
    }

    /**
     * get random boolean
     *
     * @return
     */
    public static boolean getRandomBoolean() {
        return rnd.nextBoolean();
    }

    /**
     * get random card, and not have wordNotExpect
     *
     * @param cardNotExpect
     * @return
     */
    public static CardTable[] get3RandomCard(DatabaseHandler database,
                                             String library, ArrayList<WordTable> testingWord,
                                             CardTable cardNotExpect) {
        String wordNotExpect = cardNotExpect.word.trim();
        String meanNotExpect = cardNotExpect.mean.trim();
        CardTable[] result = new CardTable[3];
        ArrayList<String> wordResult = new ArrayList<String>();
        ArrayList<String> meanResult = new ArrayList<String>();
        CardTable card = null;
        int idx = 0, i;
        String word = "", mean = "";
        do {
            if (idx < testingWord.size() - 1) {
                i = rnd.nextInt(testingWord.size());
                word = testingWord.get(i).word.trim();
                card = database.getCard(library, word);
            } else {
                card = database.getRandomCard(Consts.DEFAULT_LIBRARY, 1);
                word = card.word;
            }

            if (card != null) {
                mean = card.mean.trim();
                if (!mean.equals(meanNotExpect) && !word.equals(wordNotExpect)
                        && !wordResult.contains(word)
                        && !meanResult.contains(mean)) {
                    result[idx] = card;
                    wordResult.add(word);
                    meanResult.add(mean);
                    idx++;
                }
            }
        } while (idx < 3);
        return result;
    }

    /**
     * array shuffle
     *
     * @param ar
     */
    public static void shuffleArray(Object[] ar) {
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Object a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public static void checkAndGenerateNewTestingWord(DatabaseHandler database,
                                                      String library, ArrayList<WordTable> output) {
        // check having testing word
        int testingWordCount = database.countTestingWord(library);
        ArrayList<WordTable> testingWords;
        // number of testing word is 0, generate word in new wordlist
        if (testingWordCount == 0) {
            // count new word
            testingWordCount = database.countNewWord(library);
            if (testingWordCount > Consts.MAX_NEW_WORD_PER_TEST) {
                testingWordCount = Consts.MAX_NEW_WORD_PER_TEST;
            }

            // get list new word
            testingWords = database.getListNewWords(library, testingWordCount);

            // change new word list after getting to testing state
            database.changeWordTesting(library, testingWords);
        } else {
            // get testing wordlist
            testingWords = database.getTestingWord(library);
        }
        output.addAll(testingWords);
    }

    /**
     * convert list of card to list of Wear data string (only contain word, phonetically, type, mean)
     *
     * @param cards list of card need convert
     * @return list of string
     */
    public static ArrayList<String> convertListCardToWearDataString(
            ArrayList<CardTable> cards) {
        ArrayList<String> r = new ArrayList<String>();
        for (int i = 0; i < cards.size(); i++) {
            r.add(cards.get(i).toWearString());
        }
        return r;
    }

    /**
     * return list of card studying
     *
     * @param database database helper
     * @return list of card
     */
    public static ArrayList<CardTable> getStudyingCardList(DatabaseHandler database) {
        ArrayList<CardTable> result = new ArrayList<CardTable>();
        // load all new card in testing state
        String currentLibrary = SharePrefs.getInstance().getCurrentLibrary();
        ArrayList<WordTable> words = database.getListWordByForget(
                currentLibrary, true);

        if (words.size() == 0) {
            // not have new word, show all word
            words = database.getListWords(currentLibrary, 0);
        }
        // change word list to card list
        if (words.size() > 0) {
            LinkedHashMap<String, CardTable> cards = database
                    .getListHashMapCards(currentLibrary);
            for (WordTable word : words) {
                result.add(cards.get(word.word));
            }
        }
        return result;
    }
}
