package jobicade.invrotate;

import java.util.AbstractList;
import java.util.List;

/**
 * A wrapper list whose first element is the nth element of its source list,
 * and any subsequent elements are at n+step, n+2*step and so on.
 */
public class NthSubList<T> extends AbstractList<T> {
    private final List<T> source;
    private final int offset, step;

    /**
     * Constructor for stepped lists.
     *
     * @param source The source list.
     * @param offset The offset of the first item.
     * @param step The step between items.
     * @throws IllegalArgumentException If {@code offset} is negative
     */
    public NthSubList(List<T> source, int offset, int step) {
        if(offset < 0) {
            throw new IllegalArgumentException("Offset must not be negative");
        }
        this.source = source;
        this.offset = offset;
        this.step = step;
    }

    @Override
    public T get(int index) {
        return source.get(offset + index * step);
    }

    @Override
    public T set(int index, T element) {
        return source.set(offset + index * step, element);
    }

    @Override
    public int size() {
        return Math.max(0, (source.size() - offset + step - 1) / step);
    }
}
