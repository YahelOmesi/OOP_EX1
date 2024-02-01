import java.util.Comparator;

public class ComparatorEat implements Comparator<Pawn> {

    private final ConcretePlayer winner;

    public ComparatorEat(ConcretePlayer winner) {
        this.winner = winner;
    }

    @Override
    public int compare(Pawn o1, Pawn o2) {
        if (o1 == o2) return 0;

        if (o1.getNumOfEat() != o2.getNumOfEat()) {
            return (o2.getNumOfEat() - o1.getNumOfEat());
        }

        int id1 = Integer.parseInt(o1.getId().substring(1));
        int id2 = Integer.parseInt(o1.getId().substring(1));
        if (id1 != id2) {
            return id1 - id2;
        }

        if (o1.getOwner() == winner)
            return -1;
        else
            return 1;
        //איך אני מתחשבת במצב שבו מספר האכילות זהה וצריך למיין לפי הקבוצה המנצחת
    }
}
