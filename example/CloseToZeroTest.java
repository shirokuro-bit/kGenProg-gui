import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CloseToZeroTest {
    @Test
    public void close_to_zero00() {
        assertEquals(9, new CloseToZero().close_to_zero(10));
    }

    @Test
    public void close_to_zero01() {
        assertEquals(99, new CloseToZero().close_to_zero(100));
    }

    @Test
    public void close_to_zero02() {
        assertEquals(0, new CloseToZero().close_to_zero(0));
    }

    @Test
    public void close_to_zero03() {
        assertEquals(-9, new CloseToZero().close_to_zero(-10));
    }

}
