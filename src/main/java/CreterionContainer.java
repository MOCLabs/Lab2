import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

public class CreterionContainer {
  public static int falsePositive;
  public static int falseNegative;

  public static int crt1x0(int gramLength, String text) {
    switch (gramLength) {
      case 1:
        for (char letter : text.toCharArray()) {
          if (DataContainer.unusedMonogram.contains(letter)) {
            return 1;
          }
        }
        break;
      case 2:
        for (int i = 0; i < text.length() - 1; i++) {
          if (DataContainer.unusedBigram
              .contains(Character.toString(text.charAt(i)) + text.charAt(i + 1))) {
            return 1;
          }
        }
        break;
    }
    return 0;
  }

  public static int crt1x1(int gramLength, String text) {
    switch (gramLength) {
      case 1:
        List<Character> unusedCharInText = new ArrayList<>();
        for (char letter : text.toCharArray()) {
          if (DataContainer.unusedMonogram.contains(letter)) {
            unusedCharInText.add(letter);
          }
        }
        if (unusedCharInText
            .stream()
            .filter(let -> DataContainer.unusedMonogram.contains(let))
            .count() > 3) {
          return 1;
        }
        break;
      case 2:
        List<String> unusedBigramInText = new ArrayList<>();
        for (int i = 0; i < text.length() - 1; i++) {
          String bigram = Character.toString(text.charAt(i)) + text.charAt(i + 1);
          if (DataContainer.unusedBigram.contains(bigram)) {
            unusedBigramInText.add(bigram);
          }
        }
        if (unusedBigramInText
            .stream()
            .filter(let -> DataContainer.unusedBigram.contains(let))
            .count() > 3) {
          return 1;
        }
        break;
    }
    return 0;
  }

  public static int crt1x2(int gramLength, String text) {
    switch (gramLength) {
      case 1:
        HashMap<Character, Double> frq = DataContainer.resetKey(
            DataContainer.calculateFrequencyInCrit(text, gramLength)
        );
        if (DataContainer.unusedMonogram
            .stream()
            .filter(frq::containsKey)
            .anyMatch(let -> frq.get(let) > DataContainer.frequencySymbols.get(let))) {
          return 1;
        }
        break;
      case 2:
        HashMap<String, Double> bfrg = DataContainer.calculateFrequencyInCrit(text, gramLength);
        if (DataContainer.bigramsFrequency
            .entrySet()
            .stream()
            .filter(e -> bfrg.containsKey(e.getKey()))
            .anyMatch(
                big -> bfrg.get(big.getKey()) > DataContainer.bigramsFrequency.get(big.getKey()))) {
          return 1;
        }
        break;
    }
    return 0;
  }

  public static int crt1x3(int gramLength, String text) {
    switch (gramLength) {
      case 1:
        HashMap<Character, Double> frq = DataContainer.resetKey(
            DataContainer.calculateFrequencyInCrit(text, gramLength)
        );
        double fP = 0.0;
        double kP = 0.0;
        for (Map.Entry<Character, Double> let : frq.entrySet()) {
          if (DataContainer.unusedMonogram.contains(let.getKey())) {
            fP += let.getValue();
            kP += DataContainer.frequencySymbols.get(let.getKey());
          }
        }
        return fP > kP ? 1 : 0;
      case 2:
        HashMap<String, Double> bfrq = DataContainer.calculateFrequencyInCrit(text, gramLength);
        double bfP = 0.0;
        double bkP = 0.0;
        for (Map.Entry<String, Double> let : bfrq.entrySet()) {
          if (DataContainer.unusedBigram.contains(let.getKey())) {
            bfP += let.getValue();
            bkP += DataContainer.bigramsFrequency.get(let.getKey());
          }
        }
        return bfP > bkP ? 1 : 0;
    }
    return 0;
  }

  public static int crt3x0(int gramLength, String text) {
    switch (gramLength) {
      case 1:
        HashMap<Character, Double> frq = DataContainer.resetKey(
            DataContainer.calculateFrequencyInCrit(text, 1)
        );
        double lgramEntropy = DataContainer.calculateEntropyForMonoLetters(1, false, frq);
        return Math.abs(DataContainer.entropy1 - lgramEntropy) > 1.5 ? 1 : 0;
      case 2:
        HashMap<String, Double> bfrq = DataContainer.calculateFrequencyInCrit(text, 2);
        double lgramBEntropy = DataContainer.calculateEntropyForBigrams(1, false, bfrq);
        return Math.abs(DataContainer.entropy2 - lgramBEntropy) > 1.5 ? 1 : 0;
    }
    return 0;
  }

  public static int crt5x1(int gramLength, int count, String text) {
    switch (gramLength) {
      case 1:
        HashMap<Character, Double> frq = DataContainer.resetKey(
            DataContainer.calculateFrequencyInCrit(text, 1)
        );
        Map<Character, Double> sortedMap = DataContainer.frequencySymbols
            .entrySet()
            .stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .limit(count)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        HashMap<Character, Double> checkMap = new HashMap<>();
        for (Map.Entry<Character, Double> l : sortedMap.entrySet()) {
          checkMap.put(l.getKey(), 0.0);
        }
        for (Map.Entry<Character, Double> l : checkMap.entrySet()) {
          if (frq.get(l.getKey()) != 0) {
            checkMap.computeIfPresent(l.getKey(), (k, v) -> v += 1);
          }
        }
        int emptyBoxes = 0;
        for (Map.Entry<Character, Double> l : checkMap.entrySet()) {
          if (l.getValue() == 0) {
            emptyBoxes++;
          }
        }
        return emptyBoxes >= (count / 2 + count / 4) ? 1 : 0;
      case 2:
        HashMap<String, Double> bfrq = DataContainer.calculateFrequencyInCrit(text, 2);
        Map<String, Double> bsortedMap = DataContainer.bigramsFrequency
            .entrySet()
            .stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .limit(count)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        HashMap<String, Double> bcheckMap = new HashMap<>();
        for (Map.Entry<String, Double> l : bsortedMap.entrySet()) {
          bcheckMap.put(l.getKey(), 0.0);
        }
        for (Map.Entry<String, Double> l : bcheckMap.entrySet()) {
          if (bfrq.get(l.getKey()) != 0) {
            bcheckMap.computeIfPresent(l.getKey(), (k, v) -> v += 1);
          }
        }
        int emptyBBoxes = 0;
        for (Map.Entry<String, Double> l : bcheckMap.entrySet()) {
          if (l.getValue() == 0) {
            emptyBBoxes++;
          }
        }
        return emptyBBoxes >= (count / 2 + count / 4) ? 1 : 0;
    }
    return 0;
  }

  public static int structCrt(String text, double limit) throws IOException {
    ByteArrayOutputStream out = compress(text);
    int byteCount = out.toByteArray().length;
    double ratio = new BigDecimal(byteCount)
        .divide(new BigDecimal(text.getBytes(StandardCharsets.UTF_8).length), 3,
            BigDecimal.ROUND_HALF_DOWN).doubleValue();
    return ratio > limit ? 0 : 1;
  }

  public static ByteArrayOutputStream compress(String str) throws IOException {
    if (str == null || str.length() == 0) {
      return null;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    GZIPOutputStream gzip = new GZIPOutputStream(out);
    gzip.write(str.getBytes());
    gzip.close();
    return out;
  }
}
