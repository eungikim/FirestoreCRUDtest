package kr.eungi.firestorecrudtest;

import com.github.javafaker.Faker;

import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        Faker faker = new Faker();

        for (int i = 0; i < 1000; i++) {
            System.out.println(faker.beer().name());
        }
        assertEquals(4, 2 + 2);
    }
}