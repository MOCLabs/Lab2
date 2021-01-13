public class Runner {
  public static void main(String[] args) {
    DataContainer data = new DataContainer();
    String[] tenCharText = data.prepareTexts(1000, 10000);
    for (int i = 0; i < tenCharText.length; i++) {
      int alpha = CreterionContainer.crt0x0(2, tenCharText[i], DataContainer.frequencySymbols,
          DataContainer.bigramsFrequency);
      if (i < 10000 && alpha == 1) {
        CreterionContainer.falseNegative++;
      }
      if (i >= 10000 && alpha == 0) {
        CreterionContainer.falsePositive++;
      }
    }
    System.out.println("alpha = " + CreterionContainer.falseNegative);
    System.out.println("beta = " + CreterionContainer.falsePositive);
  }
}
