import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SeaBattleField {
    private String[][] field = new String[10][10];
    private List<Integer[]> shots = new ArrayList<>();
    private List<Integer[]> quads = new ArrayList<>(1);
    private List<Integer[]> triples = new ArrayList<>(2);
    private List<Integer> doubles = new ArrayList<>(3);
    private List<Integer> single = new ArrayList<>(4);
    static String squareSym = "";
    static String hitShotSym = "X";
    static String missShotSym = "*";
    static String sheepSym = "#";
    public SeaBattleField() {

    }

    public boolean tryAddMyShot(Integer x, Integer y) {
        if (x > 0 && x < 11 && y > 0 && y < 11) {
            shots.add(new Integer[]{x, y});
            return true;
        }
        return false;
    }

    public Integer addEnemyShot(Integer x, Integer y) {
        if (Objects.equals(field[x][y], "#")) {
            if ()
        }
    }

    public String getStringField() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0 ; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                stringBuilder.append(field[i][j]);
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }



}
