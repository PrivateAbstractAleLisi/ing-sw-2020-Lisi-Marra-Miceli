package auxiliary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RangeTest {

    private Range r, rIsla;

    @Before
    public void setUp() throws Exception {
        r = new Range(1, 100);
        rIsla = new Range(0, 4);
    }

    @After
    public void tearDown() throws Exception {
        r = null;
        rIsla = null;
    }

    @Test
    public void contains() {
        for (int i = 1; i < 100; i++) {
            assertTrue(r.contains(i));
        }
        for (int i = -100; i < 1; i++) {
            assertFalse(r.contains(i));
        }
        for (int i = 101; i < 1000; i++) {
            assertFalse(r.contains(i));
        }

    }

    @Test
    public void isIndexOfCellInRange() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                assertTrue(rIsla.isIndexOfCellInRange(i,j));
            }
        }
    }
}