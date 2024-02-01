import java.util.Comparator;

public class ComparatorVisit implements Comparator<Position> {


    @Override
    public int compare(Position o1, Position o2) {
        if (o1.visitID.size() != o2.visitID.size()) {
            return o2.visitID.size() - o1.visitID.size();
        }

        if (o1.getX() != o2.getX()) {
            return o1.getX() - o2.getX();
        }

        if (o1.getY() != o2.getY()) {
            return o1.getY() - o2.getY();
        }
        return 0;
    }
}
