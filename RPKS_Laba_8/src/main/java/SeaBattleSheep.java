import java.util.Objects;

public class SeaBattleSheep {
    private Integer x;
    private Integer y;
    private Integer dir;
    private boolean[] aliveParts;
    private Integer hits;

    public SeaBattleSheep(Integer x, Integer y, Integer dir, Integer length) {

        this.x = x;
        this.y = y;
        this.dir = dir;
        this.aliveParts = new boolean[length];
        this.hits = 0;
    }

    public Integer tryShot(Integer x, Integer y) {
        int res = 0;
        Integer dx = 0;
        Integer dy = 0;
        for (int i = 0; i < this.aliveParts.length; i++) {
            if (Objects.equals(x, this.x + dx) && Objects.equals(y, this.y + dy)) {
                this.aliveParts[i] = false;
                hits++;
                res = 1;
                break;
            } else {
                if (dir == 0) {
                    dy++;
                } else {
                    dx++;
                }
            }
        }
        if (hits == aliveParts.length) {
            res = 2;
        }
        return res;
    }

}
