import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Writer {

  static BufferedWriter writer;

  static {
    try {
      writer = Files.newBufferedWriter(Paths.get("out_sc.csv"));
      writer
          .append("L").append(",")
          .append("N").append(",")
          .append("LgramLength").append(",")
          .append("Algorithm name").append(",")
          .append("Criteria name").append(",")
          .append("False Negative");
      writer.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void write(DataContainer data, int gramLength, int L, int N, String algName,
                           String crtName)
      throws IOException {
    String[] tenCharText = data.prepareTexts(L, N, algName);
    for (String s : tenCharText) {
      int alpha = calculateFNValue(crtName, s, gramLength, L);
      if (alpha == 1) {
        CreterionContainer.falseNegative++;
      }
    }
    writer
        .append(String.valueOf(L)).append(",")
        .append(String.valueOf(N)).append(",")
        .append(String.valueOf(gramLength)).append(",")
        .append(algName).append(",")
        .append(crtName).append(",")
        .append(new BigDecimal(CreterionContainer.falseNegative).divide(
            new BigDecimal(tenCharText.length), 3, BigDecimal.ROUND_HALF_DOWN).toString());
    writer.newLine();
    CreterionContainer.falseNegative = 0;
  }

  private static int calculateFNValue(String crtName, String s, int gramLength, int L)
      throws IOException {
    switch (crtName) {
      case "Criteria 1.0":
        return CreterionContainer.crt1x0(gramLength, s);
      case "Criteria 1.1":
        return CreterionContainer.crt1x1(gramLength, s);
      case "Criteria 1.2":
        return CreterionContainer.crt1x2(gramLength, s);
      case "Criteria 1.3":
        return CreterionContainer.crt1x3(gramLength, s);
      case "Criteria 3.0":
        return CreterionContainer.crt3x0(gramLength, s);
      case "Criteria 5.1":
        return CreterionContainer.crt5x1(gramLength, 15, s);
      case "Structural criterion":
        switch (L) {
          case 10:
            return CreterionContainer.structCrt(s, 2);
          case 100:
          case 1000:
            return CreterionContainer.structCrt(s, 4.7);
          case 10000:
            return CreterionContainer.structCrt(s, 0.39);
        }
    }
    return 0;
  }

  public static void newLine() throws IOException {
    writer.newLine();
  }

  public static void closeWriter() throws IOException {
    writer.close();
  }
}
