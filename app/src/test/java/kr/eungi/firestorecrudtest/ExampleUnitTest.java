package kr.eungi.firestorecrudtest;

import org.junit.Test;

import kr.eungi.firestorecrudtest.util.NameGenerator;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {


        for (int i = 0; i < 50; i++) {
            String name = NameGenerator.generateName();
            System.out.println(name);
        }
        assertEquals(4, 2 + 2);
    }
}