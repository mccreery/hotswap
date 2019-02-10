package jobicade.hotswap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

public class TestNthList {
    private List<Integer> ints = new ArrayList<>();

    @Before
    public void before() {
        ints.clear();
        IntStream.range(0, 100).boxed().collect(Collectors.toCollection(() -> ints));
    }

    @Test
    public void testAccess() {
        for(int n = 0; n <= ints.size(); n++) {
            for(int step = 1; step < n; step++) {
                testAccess(ints.subList(0, n), 0, step);
                testAccess(ints.subList(0, n), n-1, -step);
            }
        }
    }

    private void testAccess(List<?> list, int offset, int step) {
        NthSubList<?> steppedList = new NthSubList<>(list, offset, step);

        for(int i = offset, j = 0; i < list.size(); i += step, j++) {
            String message = String.format("size %d, step %d, access %d", list.size(), step, j);
            Assert.assertEquals(message, list.get(i), steppedList.get(j));
        }
    }

    @Test
    public void testSize() {
        for(int i = 0; i <= ints.size(); i++) {
            int n = 0;
            for(int j = 0; j < i; j += 5) n++;

            Assert.assertEquals(n, new NthSubList<>(ints.subList(0, i), 0, 5).size());
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNegativeOffset() {
        new NthSubList<>(ints, -1, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testZeroStep() {
        new NthSubList<>(ints, 0, 0);
    }
}
