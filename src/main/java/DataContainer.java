import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class DataContainer {
  public static String data;
  public static List<String> bigrams;
  public static HashMap<String, Double> bigramsFrequency;
  public static HashMap<Character, Double> frequencySymbols;
  public static String alphabet = "абвгдеєжзиіїйклмнопрстуфхцчшщьюя";
  public static double entropy1;
  public static double entropy2;

  public DataContainer() {
    init();
  }

  private void init() {
    readTextFromFile();
    BigramsInitialization();
    bigramsFrequency = calculateFrequency(data, 2);
    frequencySymbols = resetKey(calculateFrequency(data, 1));
    entropy1 = calculateEntropyForMonoLetters(1, true, frequencySymbols);
    entropy2 = calculateEntropyForBigrams(2, true, bigramsFrequency);
  }

  // calculate bingam and monogram frequency
  public static HashMap<String, Double> calculateFrequency(String text, int length) {
    HashMap<String, Double> frequency = new HashMap<>();
    switch (length) {
      case 1:
        for (char letter : alphabet.toCharArray()) {
          frequency.put(Character.toString(letter), 0.0);
        }
        for (char symbol : text.toCharArray()) {
          frequency.computeIfPresent(Character.toString(symbol), (k, v) -> v = v + 1);
        }
        break;
      case 2:
        for (String bigram : bigrams) {
          frequency.put(bigram, 0.0);
        }
        for (int i = 0; i < text.length() - 1; i++) {
          frequency.computeIfPresent(Character.toString(text.charAt(i)) + text.charAt(i + 1),
              (k, v) -> v = v + 1);
        }
        break;
      default:
        break;
    }

    return length == 1 ? toSymbolsStats(frequency, data.length()) :
        toSymbolsStats(frequency, data.length() - 1);
  }

  public static HashMap<Character, Double> resetKey(HashMap<String, Double> calculateFrequency) {
    HashMap<Character, Double> result = new HashMap<>();
    calculateFrequency.keySet().forEach(
        k -> result.put(k.charAt(0), calculateFrequency.get(k))
    );
    return result;
  }

  static HashMap<String, Double> toSymbolsStats(HashMap<String, Double> rowData, int divisor) {
    final HashMap<String, Double> result = new HashMap<>();
    for (String key : rowData.keySet()) {
      result.put(key, rowData.get(key) / divisor);
    }
    return result;
  }
  // finished calculate section

  // calculating entropy for monograms and bigrams
  public static double calculateEntropyForMonoLetters(int length, boolean isSource,
                                                      HashMap<Character, Double> frequencySymbols) {
    double entropy = 0;

    for (Map.Entry<Character, Double> item : frequencySymbols.entrySet()) {
      if (item.getValue() != 0) {
        entropy += (-1) * item.getValue() * (Math.log(item.getValue()) / Math.log(2));
      }
    }

    return entropy;
  }

  public static double calculateEntropyForBigrams(int length, boolean isSource,
                                                  HashMap<String, Double> frequencySymbols) {
    double entropy = 0;

    for (Map.Entry<String, Double> item : frequencySymbols.entrySet()) {
      if (item.getValue() != 0) {
        entropy += (-1) * item.getValue() * (Math.log(item.getValue()) / Math.log(2));
      }
    }

    return entropy / length;
  }
  // finished calculating entropy

  private void BigramsInitialization() {
    bigrams = new ArrayList<>();
    for (int i = 0; i < alphabet.length(); i++) {
      for (int j = 0; j < alphabet.length(); j++) {
        bigrams
            .add(Character.toString(alphabet.charAt(i)) + alphabet.charAt(j));
      }
    }
  }

  private void readTextFromFile() {
    try {
      final StringBuilder str = new StringBuilder();
      final BufferedReader in = new BufferedReader(
          new InputStreamReader(new FileInputStream("D:\\moc_2\\src\\main\\java\\text.txt"),
              StandardCharsets.UTF_8));
      String line;
      while ((line = in.readLine()) != null) {
        str.append(line.toLowerCase(Locale.ROOT).replace('ґ', 'г').replaceAll("[^а-яєії]", ""));
      }
      in.close();
      data = str.toString();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  // shuffle

  public static String vigenereMonoShuffle(String text) {
    Random generator = new Random();
    StringBuilder key = new StringBuilder();
    StringBuilder result = new StringBuilder();
    char[] textInCharArray = text.toCharArray();
    char[] keyInCharArray = null;
    char[] alphabetInCharArray = alphabet.toCharArray();

    for (int i = 0; i < 5; i++) {
      key.append(alphabet.charAt(generator.nextInt(alphabet.length())));
    }
    keyInCharArray = key.toString().toCharArray();
    for (int i = 0; i < text.length(); i++) {
      result
          .append(alphabet.charAt((getIndexInArray(alphabetInCharArray, textInCharArray[i]) +
              getIndexInArray(alphabetInCharArray, keyInCharArray[i % key.length()])) %
              alphabet.length()));
    }
    return result.toString();
  }

  public static String vigenereBigramShuffle(String text) {
    Random generator = new Random();
    StringBuilder key = new StringBuilder();
    StringBuilder result = new StringBuilder();
    String[] keyBigrams = new String[5];
    String[] languageBigrams = bigramsFrequency.keySet().toArray(new String[] {});

    for (int i = 0; i < 10; i++) {
      key.append(alphabet.charAt(generator.nextInt(alphabet.length())));
    }
    int counter = 0;
    for (int i = 0; i < 9; i += 2) {
      keyBigrams[counter] =
          (Character.toString(key.toString().charAt(i)) + key.toString().charAt(i + 1));
      counter++;
    }
    for (int i = 0; i < text.length() - 1; i += 2) {
      result.append(languageBigrams[(getIndexInArrayByStr(languageBigrams,
          (Character.toString(text.charAt(i)) + text.charAt(i + 1))) +
          getIndexInArrayByStr(languageBigrams, keyBigrams[i % (key.length() / 2)])) %
          (alphabet.length() * alphabet.length())]);
    }
    return result.toString();
  }

  public static String affineMonoShuffle(String text) {
    StringBuilder result = new StringBuilder();
    Random generator = new Random();
    char[] key = {
        alphabet.charAt(generator.nextInt(alphabet.length())),
        alphabet.charAt(generator.nextInt(alphabet.length())),
    };
    char[] alphabetInCharArray = alphabet.toCharArray();
    for (int i = 0; i < text.length(); i++) {
      result.append(alphabet.charAt(
          (getIndexInArray(alphabetInCharArray, key[0]) *
              getIndexInArray(alphabetInCharArray, text.charAt(i)) +
              getIndexInArray(alphabetInCharArray, key[1])) % alphabet.length()));
    }
    return result.toString();
  }

  public static String affineBigramShuffle(String text) {
    StringBuilder result = new StringBuilder();
    Random generator = new Random();
    String[] languageBigrams = bigramsFrequency.keySet().toArray(new String[] {});
    String[] key = {
        languageBigrams[generator.nextInt(languageBigrams.length)],
        languageBigrams[generator.nextInt(languageBigrams.length)]
    };
    for (int i = 0; i < text.length() - 1; i += 2) {
      result.append(languageBigrams[((getIndexInArrayByStr(languageBigrams, key[0]) *
          getIndexInArrayByStr(languageBigrams,
              (Character.toString(text.charAt(i)) + text.charAt(i + 1))) +
          getIndexInArrayByStr(languageBigrams, key[1])) % languageBigrams.length)]);
    }
    return result.toString();
  }

  public static String uniformMonoShuffle(String text) {
    StringBuilder result = new StringBuilder();
    Random generator = new Random();
    while (result.length() != text.length()) {
      result.append(alphabet.charAt(generator.nextInt(alphabet.length())));
    }
    return result.toString();
  }

  public static String uniformBigramShuffle(String text) {
    StringBuilder result = new StringBuilder();
    Random generator = new Random();
    String[] languageBigrams = bigrams.toArray(new String[] {});
    while (result.length() <= text.length()) {
      result.append(languageBigrams[generator.nextInt(languageBigrams.length)]);
    }
    return result.toString();
  }

  public static String recurrentMonoShuffle(String text) {
    StringBuilder result = new StringBuilder();
    Random generator = new Random();
    int s0 = generator.nextInt(alphabet.length());
    int s1 = generator.nextInt(alphabet.length());
    result.append(Character.toString(alphabet.charAt(s0)) + alphabet.charAt(s0));
    char[] alphabetInCharArray = alphabet.toCharArray();
    for (int i = 2; i < text.length(); i++) {
      result.append(alphabet.charAt((getIndexInArray(alphabetInCharArray, result.charAt(i - 1)) +
          getIndexInArray(alphabetInCharArray, result.charAt(i - 2))) % alphabet.length()));
    }
    return result.toString();
  }

  public static String recurrentBigramShuffle(String text) {
    StringBuilder result = new StringBuilder();
    Random generator = new Random();
    String[] languageBigrams = bigrams.toArray(new String[] {});
    int s0 = generator.nextInt(bigrams.size());
    int s1 = generator.nextInt(bigrams.size());
    result.append(languageBigrams[s0]).append(languageBigrams[s1]);
    for (int i = 4; i < text.length() - 1; i += 2) {
      result.append(languageBigrams[
          (getIndexInArrayByStr(languageBigrams,
              Character.toString(result.charAt(i - 2)) + result.charAt(i - 1)) +
              getIndexInArrayByStr(languageBigrams,
                  Character.toString(result.charAt(i - 4)) + result.charAt(i - 3)))
              % languageBigrams.length]);
    }
    return result.toString();
  }

  public static int getIndexInArrayByStr(String[] array, String symbol) {
    for (int i = 0; i < array.length; i++) {
      if (array[i].equals(symbol)) {
        return i;
      }
    }
    return 0;
  }

  public static int getIndexInArray(char[] array, char symbol) {
    for (int i = 0; i < array.length; i++) {
      if (array[i] == symbol) {
        return i;
      }
    }
    throw new UnsupportedOperationException();
  }
  // finished shuffle

  public String[] prepareTexts(int length, int quantity) {
    final StringBuilder str = new StringBuilder();
    try {
      final BufferedReader in = new BufferedReader(
          new InputStreamReader(
              new FileInputStream("D:\\moc_2\\src\\main\\java\\preparingText.txt"),
              StandardCharsets.UTF_8));
      String line;
      while ((line = in.readLine()) != null) {
        str.append(line.toLowerCase(Locale.ROOT).replace('ґ', 'г').replaceAll("[^а-яєії]", ""));
      }
      in.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    String[] texts = new String[quantity];
    int counter = 0;
    for (int i = 0; i < quantity; i++) {
      texts[i] = str.substring(counter, (counter + length));
      counter += 10;
    }
    for (int i = 0; i < quantity; i++) {
      texts[i] = vigenereBigramShuffle(texts[i]);
    }
    return texts;
  }
}
