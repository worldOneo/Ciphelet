package com.github.worldoneo.ciphelet.connector;

public final class FlakeHelper {
    private static final char[] chars = "ABCDEFGH=JKLMN!PQRSTUVWXYZabcdefghijk/mnopqrstuvwxyz1234567890-+".toCharArray();

    public static String humanIDFromFlake(long flake) {
        StringBuilder stringBuilder = new StringBuilder();
        while (flake > 0) {
            stringBuilder.append(chars[(int) flake & 0x3F]);
            flake >>= 6;
        }
        return stringBuilder.toString();
    }

    public static long flakFromHumanID(String humanID) {
        long flake = 0L;
        for (byte b : new StringBuilder(humanID).reverse().toString().getBytes()) {
            flake <<= 6;
            for (int i = 0; i < chars.length; i++)
                if (chars[i] == b)
                    flake += i;
        }
        return flake;
    }

    public static long timeStampOfFlake(long flake) {
        return flake >> 21;
    }
}
