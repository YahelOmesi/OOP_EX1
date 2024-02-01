public class ConcretePlayer implements Player {

    private boolean isPlayerOne;
    private int Wins;

    public ConcretePlayer(boolean isOne) { // isOne can be true\false
        this.isPlayerOne = isOne;
        this.Wins = 0;
    }


    @Override
    public boolean isPlayerOne() {
        return this.isPlayerOne;
    }

    @Override
    public int getWins() {
        return this.Wins;
    }

    public void addWins() {
        this.Wins++;
    }

    public boolean heIsPlayerOne() {
        return isPlayerOne();
    }

}