import java.util.Comparator;

public class ComparatorLength implements Comparator<ConcretePiece> {

    @Override
    public int compare(ConcretePiece o1, ConcretePiece o2) {
        if(o1.getHistory().size() != o2.getHistory().size()){
            return o1.getHistory().size() - o2.getHistory().size();
        }

        int id1 = Integer.parseInt(o1.getId().substring(1));
        int id2 = Integer.parseInt(o2.getId().substring(1));

        if(id1 > id2){
            return 1;
        }else if(id1 < id2){
            return -1;
        }
        return 0;
    }
}
