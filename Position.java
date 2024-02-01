import javax.print.attribute.standard.Copies;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Position {

    //the position will be represented by x and y coordinates
    private int x;
    private int y;

    Set<String> visitID = new HashSet<>();

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    public void addVisit(String str){
        this.visitID.add(str);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + this.getX() + ", " + this.getY() + ")";
    }



}
