import java.math.BigInteger;

public class CSGOCrosshair {
    public String code;
    public int style; // LODWORD [14] >> 1
    public boolean hasCenterDot; // HIDWORD [14] & 1

    public float length; // [15] / 10
    public float thickness; // [13] / 10
    public float gap; // unchecked((sbyte) [3]) / 10

    public boolean hasOutline; // [11] & 8
    public float outline; // [4] / 2

    public int red; // [5]
    public int green; // [6]
    public int blue; // [7]

    public boolean hasAlpha; // HIDWORD [14] & 4
    public int alpha; // [8]

    public int splitDistance; // [9]
    public float innerSplitAlpha; // HIDWORD [11] / 10
    public float outerSplitAlpha; // LODWORD [12] / 10
    public float splitSizeRatio; // HIDWORD [12] / 10

    public boolean isTStyle; // HIDWORD [14] & 8

    public float customScale = 1f; // Override size

    public CSGOCrosshair(String code) {
        this.code = code;
        int[] bytes = decode(code);

        outline = (float) (bytes[4] / 2.0);
        red = bytes[5];
        green = bytes[6];
        blue = bytes[7];
        alpha = bytes[8];
        splitDistance = bytes[9];

        innerSplitAlpha = (bytes[11] >> 4) / 10f;
        hasOutline = (bytes[11] & 8) != 0;
        outerSplitAlpha = (bytes[12] & 0xF) / 10f;
        splitSizeRatio = (bytes[12] >> 4) / 10f;

        thickness = bytes[13] / 10f;
        length = bytes[15] / 10f;
        gap = ((byte) bytes[3]) / 10f;

        hasCenterDot = ((bytes[14] >> 4) & 1) != 0;
        hasAlpha = ((bytes[14] >> 4) & 4) != 0;
        isTStyle = ((bytes[14] >> 4) & 8) != 0;

        style = ((bytes[14] & 0xF) >> 1);
    }

    public int[] decode(String code) {
        final String dictionary = "ABCDEFGHJKLMNOPQRSTUVWXYZabcdefhijkmnopqrstuvwxyz23456789";

        code = code.substring(4).replace("-", "");

        StringBuilder builder = new StringBuilder();
        builder.append(code).reverse();

        BigInteger big = BigInteger.ZERO;
        for (char c : builder.toString().toCharArray()) {
            big = big.multiply(BigInteger.valueOf(dictionary.length())).add(BigInteger.valueOf(dictionary.indexOf(c)));
        }

        byte[] temp = big.toByteArray();
        int[] decoded = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            byte b = temp[i];

            decoded[i] = Byte.toUnsignedInt(b);
        }

        // set first char to 0
        if (decoded[0] != 0) {
            int[] copy = decoded;
            decoded = new int[copy.length + 1];
            System.arraycopy(copy, 0, decoded, 1, temp.length - 1);
            decoded[0] = 0;
        }

        return decoded;
    }
}
