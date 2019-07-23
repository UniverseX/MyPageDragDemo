package com.autoai.pagedragframe;

public class Util {
    public static long strToLong(String str){
        if(str.length() > 17) {
            str = str.substring(0, 17);
        }
        long result = 0;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            result += charToInteger(chars[i]) * (10 ^ i);
        }
        return result;
    }

    private static final String ALL_WORDS = "0123456789qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM_.";
    private static final char[] ALL_CHARS = ALL_WORDS.toCharArray();
    private static int charToInteger(char c){
        for (int i = 0; i < ALL_CHARS.length; i++) {
            if(c == ALL_CHARS[i]){
                return i;
            }
        }
        return ALL_CHARS.length;
    }
}
