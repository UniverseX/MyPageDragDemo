package com.autoai.pagedragframe;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String ALL_WORDS = "0123456789qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM_.";
    @Test
    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);

        System.out.println(Long.MAX_VALUE);
        System.out.println(String.valueOf(Long.MAX_VALUE).length());
        System.out.println(ALL_WORDS.length());

        String mpkg = "com.autoai.mypagedragdemo";

        System.out.println("---------");
        System.out.println(Util.strToLong(mpkg));
    }
}