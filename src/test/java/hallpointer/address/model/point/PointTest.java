package hallpointer.address.model.point;

import static hallpointer.address.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PointTest {

    @Test
    public void constructor_invalidPoints_throwsIllegalArgumentException() {
        // Testing invalid points values
        assertThrows(IllegalArgumentException.class, () -> new Point("-1")); // Negative points
    }

    @Test
    public void isValidPoints() {
        // valid points
        assertTrue(Point.isValidPoints("0")); // Minimum valid value
        assertTrue(Point.isValidPoints("5")); // Positive integer
        assertTrue(Point.isValidPoints("10")); // Positive integer, trailing zero
        assertTrue(Point.isValidPoints("0005")); // Positive integer, leading zero


        // invalid points
        assertFalse(Point.isValidPoints("-1")); // Negative value
        assertFalse(Point.isValidPoints("1.0")); // Decimal value
        assertFalse(Point.isValidPoints("1 1")); // Not one integer
        assertFalse(Point.isValidPoints("" + Integer.MAX_VALUE + 1)); // Overflow
    }

    @Test
    public void equals() {
        Point point = new Point("10");

        // same values -> returns true
        assertTrue(point.equals(new Point("10")));

        // same object -> returns true
        assertTrue(point.equals(point));

        // null -> returns false
        assertFalse(point.equals(null));

        // different types -> returns false
        assertFalse(point.equals(5.0f));

        // different points -> returns false
        assertFalse(point.equals(new Point("5")));
    }

    @Test
    public void hashCodeConsistency() {
        Point point = new Point("10");

        // Ensure that hash code remains consistent for the same object
        int initialHashCode = point.hashCode();
        assertTrue(initialHashCode == point.hashCode());
    }

    @Test
    public void toStringTest() {
        Point point = new Point("10");

        // Check if the toString returns the expected value
        assertTrue(point.toString().equals("10 points")); // Ensure toString returns correct value
    }

    @Test
    public void getValueTest() {
        Point point = new Point("10");

        // Ensure that getValue returns the correct points value
        assertTrue(point.getValue() == 10); // Verify the getValue method
    }
}
