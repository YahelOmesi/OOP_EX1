import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class ConcretePiece implements Piece{
    private String id;
    private Player owner;
    private List<Position> history;
    private int distance = 0;

    public ConcretePiece(Player owner, String id, Position startPosition){
        this.owner = owner;
        this.id = id;
        history = new ArrayList<>();
        history.add(startPosition);
    }

    public void addPosition(Position p){
        history.add(p);
    }

    @Override
    public Player getOwner() {
        return this.owner;
    }

    public List<Position> getHistory() {
        return history;
    }

    public String getId() {
        return id;
    }

    public void addDistance(int x) {
        distance = x + distance;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String[] compareLengthArr(){
        String[] str = new String[history.size()];
        for (int i = 0; i < history.size(); i++) {
            str[i] = history.get(i).toString();
        }
        return str;
    }


}
