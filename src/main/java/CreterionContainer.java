import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreterionContainer {
  public static int falsePositive;
  public static int falseNegative;

  public static int crt0x0(int length, String text,
                           HashMap<Character, Double> lettersFrequency,
                           HashMap<String, Double> bigramsFrequency) {
    int count = 0;
    switch (length) {
      case 1:
        List<Character> unusedSymbols = new ArrayList<>();
        for (Map.Entry<Character, Double> item : lettersFrequency.entrySet()) {
          if (item.getValue() < 0.04) {
            unusedSymbols.add(item.getKey());
          }
        }
        for (char letter : text.toCharArray()) {
          if (unusedSymbols.contains(letter)) {
            count++;
            break;
          }
        }
        break;
      case 2:
        List<String> unusedBigram = new ArrayList<>();
        for (Map.Entry<String, Double> bigram : bigramsFrequency.entrySet()) {
          if (bigram.getValue() < 1.6844185532526908E-5) {
            unusedBigram.add(bigram.getKey());
          }
        }
        for (int i = 0; i < text.length() - 1; i++) {
          if (unusedBigram.contains(Character.toString(text.charAt(i)) + text.charAt(i + 1))) {
            count++;
            break;
          }
        }
        break;
    }
    System.out.println(String.format("was accepted %d hypot. by 1.0", count));
    return count;
  }
}
