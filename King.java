public class King extends ConcretePiece{


    public King( Player owner, String id, Position startPosition) {
        super(owner, id, startPosition);
    }

    @Override
    public String getType() {
        return "♔";
    }
}
