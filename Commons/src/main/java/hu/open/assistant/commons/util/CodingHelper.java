package hu.open.assistant.commons.util;

/**
 * Helper class for character coding tasks.
 */
public class CodingHelper {

    /**
     * Convert (encode) text to a Cesar coded ASCI code.
     *
     * @param base  text to encrypt
     * @param shift value for Cesar coding
     * @return encrypted text
     */
    public static String stringToAsciCesarCode(String base, int shift) {
        char[] baseCharacters = shiftLetters(base.toCharArray(), shift);
        String line = "";
        for (char character : baseCharacters) {
            if ((int) character < 100) {
                line = line.concat("0");
            }
            line = line.concat(String.valueOf((int) character));
        }
        return line;
    }

    /**
     * Decode a Cesar coded ASCI code back to text.
     *
     * @param base Cesar coded ASCI code to decode
     * @param shift value used for Cesar coding at encryption
     * @return decoded text
     */
    public static String asciCesarCodeToString(String base, int shift) {
        if (ValidationHelper.hasOnlyNumbers(base)) {
            char[] baseCharacters = base.toCharArray();
            char[] codedCharacters = new char[baseCharacters.length / 3];
            String buffer = "";
            int pointer = -1;
            for (char character : baseCharacters) {
                buffer = buffer.concat(String.valueOf(character));
                if (buffer.length() == 3) {
                    pointer++;
                    codedCharacters[pointer] = (char) (Integer.parseInt(buffer));
                    buffer = "";
                }
            }
            return String.valueOf(shiftLetters(codedCharacters, shift * -1));
        }
        return "";
    }

    private static char[] shiftLetters(char[] characters, int shift) {
        for (int i = 0; i < characters.length; i++) {
            characters[i] = (char) (characters[i] + shift);
        }
        return characters;
    }
}
