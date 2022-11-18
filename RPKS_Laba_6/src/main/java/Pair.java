import java.util.Objects;

public class Pair<T1, T2> {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    public T1 getLeft() {
        return left;
    }

    public void setLeft(T1 left) {
        this.left = left;
    }

    public T2 getRight() {
        return right;
    }

    public void setRight(T2 right) {
        this.right = right;
    }

    private T1 left;
    private T2 right;
    public Pair(T1 left, T2 right) {
        this.left = left;
        this.right = right;
    }
    public Pair() {
        this.left = null;
        this.right = null;
    }
}
