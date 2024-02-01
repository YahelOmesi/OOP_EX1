public class Pawn extends ConcretePiece{

    int numOfEat= 0;
    public Pawn( Player owner, String id, Position startPosition) {
        super(owner, id, startPosition);
    }

    @Override
    public String getType() {
        if(getOwner().isPlayerOne()){
            return "♙";
        }
        return "♟";
    }

    public void addEat(){
        numOfEat++;
    }
    public int getNumOfEat() {
        return numOfEat;
    }



}
