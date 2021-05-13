package app.mirru;

import org.junit.Test;

import mikera.vectorz.Vector3;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CalculatePalmNormal {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void sideAisCorrect() {
        Vector3 palm17 = Vector3.of(1,2,-2);
        Vector3 palm0 = Vector3.of(2,0,-2);
        palm17.sub(palm0);
        Vector3 sideA = palm17;
        assertEquals(Vector3.of(-1,2,0), sideA);
    }

    @Test
    public void sideBisCorrect() {

    }
}