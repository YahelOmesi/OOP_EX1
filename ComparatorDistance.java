import java.util.Comparator;

public class ComparatorDistance implements Comparator<ConcretePiece> {


    private final ConcretePlayer winner;

    public ComparatorDistance(ConcretePlayer winner) {
        this.winner = winner;
    }

    @Override
    public int compare(ConcretePiece o1, ConcretePiece o2) {

        if (o1.getDistance() != o2.getDistance()){
            return o2.getDistance() - o1.getDistance();
        }

        int id1 = Integer.parseInt(o1.getId().substring(1));
        int id2 = Integer.parseInt(o1.getId().substring(1));

        if (id1 != id2) {
            return id1 - id2;
        }

        if (o2.getOwner() == winner)
            return -1;
        else
            return 1;
        //איך אני מתחשבת במצב שבו מספר האכילות זהה וצריך למיין לפי הקבוצה המנצחת
    }
}
