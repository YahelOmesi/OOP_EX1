import org.junit.validator.PublicClassValidator;

import java.util.*;

public class GameLogic implements PlayableLogic {
    private final ConcretePiece[][] board;
    private final ConcretePlayer playerOne; //blue = white = player 1 who play second
    private final ConcretePlayer playerTwo; //red = black = player 2 who play first
    private final ConcretePiece[] pieces;
    private boolean isSecondPlayerTurn;
    private Position kingPosition;
    private ConcretePlayer winner;
    private final Position[][] allPositions;


    public GameLogic() {

        this.board = new ConcretePiece[11][11];
        this.playerTwo = new ConcretePlayer(false);
        this.playerOne = new ConcretePlayer(true);
        this.pieces = new ConcretePiece[37];
        this.allPositions = new Position[11][11];

        reset();
        setUpPosition();
    }

    @Override
    public boolean move(Position a, Position b) {
        ConcretePiece currentPiece = board[a.getY()][a.getX()];
        ConcretePlayer currentPlayer;


        if (isSecondPlayerTurn) {
            currentPlayer = playerTwo;
        } else {
            currentPlayer = playerOne;
        }
        //Conditions in which the move will not be possible

        // Condition 1 : When the user double-clicks on the same location
        if (a.equals(b)) {
            return false;
        }

        // Condition 2 : Diagonally across
        if (!(a.getX() == b.getX() || a.getY() == b.getY())) {
            return false;
        }

        // Condition 3 : There is no piece in the initial position
        if (currentPiece == null) {
            return false;
        }

        // Condition 4 : The position you want to move to is not available
        if (board[b.getY()][b.getX()] != null) {
            return false;
        }

        // Condition 5 : Pawns cannot be in the corners of the board
        if (currentPiece instanceof Pawn &&
                ((b.getY() == 0 && b.getX() == 0) ||
                        (b.getY() == 0 && b.getX() == 10) ||
                        (b.getY() == 10 && b.getX() == 0) ||
                        (b.getY() == 10 && b.getX() == 10))) {
            return false;
        }

        // Condition 6 : Only the person to whom the turn belongs can play
        if (currentPiece.getOwner() != currentPlayer) {
            return false;
        }

        // Condition 7 :
        //"move" will be possible if there is no other player in the way.
        // We will check 4 situations
        for (int i = a.getX() + 1; i < b.getX(); i++) {
            if (board[a.getY()][i] != null) {
                return false;
            }
        }

        for (int i = a.getX() - 1; i > b.getX(); i--) {
            if (board[a.getY()][i] != null) {
                return false;
            }
        }

        for (int i = a.getY() + 1; i < b.getY(); i++) {
            if (board[i][a.getX()] != null) {
                return false;
            }
        }

        for (int i = a.getY() - 1; i > b.getY(); i--) {
            if (board[i][a.getX()] != null) {
                return false;
            }
        }

        ConcretePiece current = board[a.getY()][a.getX()];

        //Updating positions after moving
        board[a.getY()][a.getX()] = null;
        board[b.getY()][b.getX()] = currentPiece;

        current.addPosition(b);

        String currentID = board[b.getY()][b.getX()].getId();
        allPositions[b.getY()][b.getX()].visitID.add(currentID);


        if (currentPiece instanceof King) { //follows king position
            kingPosition = b;
        }

        // Update turns
        isSecondPlayerTurn = !isSecondPlayerTurn;

        isEdible(b);
        currentPiece.addDistance(countDistance(a, b));

        if (isGameFinished()) {
            printResult();
        }


        return true;
    }


    private void isEdible(Position b) {
        ConcretePiece currentPiece = board[b.getY()][b.getX()];
        if (!(currentPiece instanceof Pawn))
            return;

        // We would like to temporarily add the soldiers to "cover" an edge case where the corner is used as a wall
        board[0][0] = new Pawn(currentPiece.getOwner(), "", null);
        board[10][10] = new Pawn(currentPiece.getOwner(), "", null);
        board[10][0] = new Pawn(currentPiece.getOwner(), "", null);
        board[0][10] = new Pawn(currentPiece.getOwner(), "", null);


        // Check if there is someone above me
        if (b.getY() - 1 >= 0 &&
                board[b.getY() - 1][b.getX()] != null &&
                board[b.getY() - 1][b.getX()] instanceof Pawn &&
                board[b.getY() - 1][b.getX()].getOwner() != currentPiece.getOwner()) {

            //There is opposite player above me, that means there are two options left
            // Option 1 : Close the piece with the board
            if (b.getY() == 1) {
                ((Pawn) board[b.getY()][b.getX()]).addEat();// updating eating
                board[b.getY() - 1][b.getX()] = null;
            } else if // Option 2 : Close with another player of my type from above
            (board[b.getY() - 2][b.getX()] != null &&
                            board[b.getY() - 2][b.getX()] instanceof Pawn &&
                            board[b.getY() - 2][b.getX()].getOwner() == currentPiece.getOwner()) {
                ((Pawn) board[b.getY()][b.getX()]).addEat();// updating eating
                board[b.getY() - 1][b.getX()] = null;
            }
        }

        // Check if there is someone below me
        if (b.getY() + 1 <= 10 &&
                board[b.getY() + 1][b.getX()] != null &&
                board[b.getY() + 1][b.getX()] instanceof Pawn &&
                board[b.getY() + 1][b.getX()].getOwner() != currentPiece.getOwner()) {

            //There is opposite player below me, that means there are two options left
            // Option 1 : Close the piece with the board
            if (b.getY() == 9) {
                ((Pawn) board[b.getY()][b.getX()]).addEat();// updating eating
                board[b.getY() + 1][b.getX()] = null;
            } else if // Option 2 : Close with another player of my type from below
            (board[b.getY() + 2][b.getX()] != null &&
                            board[b.getY() + 2][b.getX()] instanceof Pawn &&
                            board[b.getY() + 2][b.getX()].getOwner() == currentPiece.getOwner()) {
                ((Pawn) board[b.getY()][b.getX()]).addEat();// updating eating
                board[b.getY() + 1][b.getX()] = null;
            }
        }

        // Check if there is someone to my right
        if (b.getX() + 1 <= 10 &&
                board[b.getY()][b.getX() + 1] != null &&
                board[b.getY()][b.getX() + 1] instanceof Pawn &&
                board[b.getY()][b.getX() + 1].getOwner() != currentPiece.getOwner()) {
            //There is opposite player to my right, that means there are two options left
            // Option 1 : Close the piece with the board
            if (b.getX() == 9) {
                ((Pawn) board[b.getY()][b.getX()]).addEat();// updating eating
                board[b.getY()][b.getX() + 1] = null;
            } else if // Option 2 : Close with another player of my type from right
            (board[b.getY()][b.getX() + 2] != null &&
                            board[b.getY()][b.getX() + 2] instanceof Pawn &&
                            board[b.getY()][b.getX() + 2].getOwner() == currentPiece.getOwner()) {
                ((Pawn) board[b.getY()][b.getX()]).addEat();// updating eating
                board[b.getY()][b.getX() + 1] = null;
            }
        }

        // Check if there is someone to my left
        if (b.getX() - 1 >= 0 &&
                board[b.getY()][b.getX() - 1] != null &&
                board[b.getY()][b.getX() - 1] instanceof Pawn &&
                board[b.getY()][b.getX() - 1].getOwner() != currentPiece.getOwner()) {
            //There is opposite player to my left, that means there are two options left
            // Option 1 : Close the piece with the board
            if (b.getX() == 1) {
                ((Pawn) board[b.getY()][b.getX()]).addEat();// updating eating
                board[b.getY()][b.getX() - 1] = null;
            } else if // Option 2 : Close with another player of my type from left
            (board[b.getY()][b.getX() - 2] != null &&
                            board[b.getY()][b.getX() - 2] instanceof Pawn &&
                            board[b.getY()][b.getX() - 2].getOwner() == currentPiece.getOwner()) {
                ((Pawn) board[b.getY()][b.getX()]).addEat();// updating eating
                board[b.getY()][b.getX() - 1] = null;
            }
        }

        //Deleting temporary players
        board[0][0] = null;
        board[10][10] = null;
        board[10][0] = null;
        board[0][10] = null;
    }

    @Override
    public Piece getPieceAtPosition(Position position) {
        return board[position.getY()][position.getX()];
    }

    @Override
    public Player getFirstPlayer() {
        return this.playerOne;
    }

    @Override
    public Player getSecondPlayer() {
        return this.playerTwo;
    }

    @Override
    public boolean isGameFinished() {
        // Blue wins - The king is in one of the corners
        if (board[0][0] instanceof King || board[10][10] instanceof King || board[0][10] instanceof King || board[10][0] instanceof King) {
            playerOne.addWins();
            winner = playerOne;
            return true;
        }

        // Red wins - The blue king is surrounded by 4 red pawns / 3 red pawns + a wall.

        //Case 1: The king is on the bottom wall
        if (kingPosition.getY() == 10) {
            if (board[kingPosition.getY() - 1][kingPosition.getX()] != null &&
                    !board[kingPosition.getY() - 1][kingPosition.getX()].getOwner().isPlayerOne() &&
                    board[kingPosition.getY()][kingPosition.getX() + 1] != null &&
                    !board[kingPosition.getY()][kingPosition.getX() + 1].getOwner().isPlayerOne() &&
                    board[kingPosition.getY()][kingPosition.getX() - 1] != null &&
                    !board[kingPosition.getY()][kingPosition.getX() - 1].getOwner().isPlayerOne()) {
                playerTwo.addWins();
                winner = playerTwo;
                return true;
            }
        }

        else //Case 2: The king is on the left wall
            if (kingPosition.getX() == 0) {
                if (board[kingPosition.getY() + 1][kingPosition.getX()] != null &&
                        !board[kingPosition.getY() + 1][kingPosition.getX()].getOwner().isPlayerOne() &&
                        board[kingPosition.getY() - 1][kingPosition.getX()] != null &&
                        !board[kingPosition.getY() - 1][kingPosition.getX()].getOwner().isPlayerOne() &&
                        board[kingPosition.getY()][kingPosition.getX() + 1] != null &&
                        !board[kingPosition.getY()][kingPosition.getX() + 1].getOwner().isPlayerOne()) {
                    playerTwo.addWins();
                    return true;
                }
            } else //Case 3: The king is on the top wall
                if (kingPosition.getY() == 0) {
                    if (board[kingPosition.getY() + 1][kingPosition.getX()] != null &&
                            !board[kingPosition.getY() + 1][kingPosition.getX()].getOwner().isPlayerOne() &&
                            board[kingPosition.getY()][kingPosition.getX() - 1] != null &&
                            !board[kingPosition.getY()][kingPosition.getX() - 1].getOwner().isPlayerOne() &&
                            board[kingPosition.getY()][kingPosition.getX() + 1] != null &&
                            !board[kingPosition.getY()][kingPosition.getX() + 1].getOwner().isPlayerOne()) {
                        playerTwo.addWins();
                        winner = playerTwo;
                        return true;
                    }
                } else //Case 4: The king is on the right wall
                    if (kingPosition.getX() == 10) {
                        if (board[kingPosition.getY() + 1][kingPosition.getX()] != null &&
                                !board[kingPosition.getY() + 1][kingPosition.getX()].getOwner().isPlayerOne() &&
                                board[kingPosition.getY() - 1][kingPosition.getX()] != null &&
                                !board[kingPosition.getY() - 1][kingPosition.getX()].getOwner().isPlayerOne() &&
                                board[kingPosition.getY()][kingPosition.getX() - 1] != null &&
                                !board[kingPosition.getY()][kingPosition.getX() - 1].getOwner().isPlayerOne()) {
                            playerTwo.addWins();
                            winner = playerTwo;
                            return true;
                        }
                    } else
                        //Case 5: - The blue king is surrounded by 4 red pawns
                        if (board[kingPosition.getY() + 1][kingPosition.getX()] != null &&
                                !board[kingPosition.getY() + 1][kingPosition.getX()].getOwner().isPlayerOne() &&
                                board[kingPosition.getY() - 1][kingPosition.getX()] != null &&
                                !board[kingPosition.getY() - 1][kingPosition.getX()].getOwner().isPlayerOne() &&
                                board[kingPosition.getY()][kingPosition.getX() + 1] != null &&
                                !board[kingPosition.getY()][kingPosition.getX() + 1].getOwner().isPlayerOne() &&
                                board[kingPosition.getY()][kingPosition.getX() - 1] != null &&
                                !board[kingPosition.getY()][kingPosition.getX() - 1].getOwner().isPlayerOne()) {
                            playerTwo.addWins();
                            winner = playerTwo;
                            return true;
                        }
        return false;
    }

    public void compareLength() {
        ArrayList<ConcretePiece> blueList = new ArrayList<>();
        ArrayList<ConcretePiece> redList = new ArrayList<>();

        //Add only blue pieces that have moved from their starting position
        for (int i = 0; i < 13; i++) {
            if (pieces[i].getHistory().size() >= 2) {
                blueList.add(pieces[i]);
            }
        }
        blueList.sort(new ComparatorLength());

        //Add only red pieces that have moved from their starting position
        for (int i = 13; i < 37; i++) {
            if (pieces[i].getHistory().size() >= 2) {
                redList.add(pieces[i]);
            }
        }
        redList.sort(new ComparatorLength());

        if (winner.isPlayerOne()) { // first blue then red
            for (int i = 0; i < blueList.size(); i++) { //Printing of the blues
                ConcretePiece currentPiece = blueList.get(i);
                System.out.println(currentPiece.getId() + ": " + Arrays.toString(currentPiece.compareLengthArr()));
            }

            for (int i = 0; i < redList.size(); i++) {//Printing of the reds
                ConcretePiece currentPiece = redList.get(i);
                System.out.println(currentPiece.getId() + ": " + Arrays.toString(currentPiece.compareLengthArr()));
            }
        }

        if (!winner.isPlayerOne()) { // first red then blue
            for (int i = 0; i < redList.size(); i++) { //Printing of the reds
                ConcretePiece currentPiece = redList.get(i);
                System.out.println(currentPiece.getId() + ": " + Arrays.toString(currentPiece.compareLengthArr()));
            }

            for (int i = 0; i < blueList.size(); i++) { //Printing of the blues
                ConcretePiece currentPiece = blueList.get(i);
                System.out.println(currentPiece.getId() + ": " + Arrays.toString(currentPiece.compareLengthArr()));
            }
        }
    }

    public void compareEat() {
        Pawn[] allPawns = new Pawn[36];

        for (int i = 0; i < 36; i++) {
            allPawns[i] = (Pawn) pieces[i + 1];// The king is not included
        }

        ComparatorEat comparatorEat = new ComparatorEat(winner);
        Arrays.sort(allPawns, comparatorEat);

        for (int i = 0; i < allPawns.length; i++) {
            Pawn current = allPawns[i];
            if (current.getNumOfEat() > 0)
                System.out.println(current.getId() + ": " + current.getNumOfEat() + " kills");
        }
    }

    public int countDistance(Position a, Position b) {
        int countDistance = 0;
        if (a.getY() == b.getY()) {
            countDistance = Math.abs(a.getX() - b.getX());
        }
        if (a.getX() == b.getX()) {
            countDistance = Math.abs(a.getY() - b.getY());
        }
        return countDistance;
    }

    public void compareDistance() {
        ComparatorDistance comparatorDistance = new ComparatorDistance(winner);
        Arrays.sort(pieces, comparatorDistance);

        for (int i = 0; i < 37; i++) {
            ConcretePiece current = pieces[i];
            if (current.getDistance() >= 1) {
                System.out.println(current.getId() + ": " + current.getDistance() + " squares");
            }
        }
    }


    public void setUpPosition() {
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                allPositions[j][i] = new Position(i, j);
                if (board[j][i] != null) {
                    String str = board[j][i].getId();
                    allPositions[j][i].addVisit(str);
                }
            }
        }
    }

    public void compareVisit(Position[][] allPositions) {
        ArrayList<Position> list = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (allPositions[j][i].visitID.size() >= 2) {
                    list.add(allPositions[j][i]);
                }
            }
        }
        ComparatorVisit comparatorVisit = new ComparatorVisit();
        list.sort(comparatorVisit);
        for (int i = 0; i < list.size(); i++) {
            Position current = list.get(i);
            System.out.println(current.toString() + current.visitID.size() + " pieces");

        }
    }

    public void printResult() {
        compareLength();
        System.out.println("***************************************************************************");
        compareEat();
        System.out.println("***************************************************************************");
        compareDistance();
        System.out.println("***************************************************************************");
        compareVisit(allPositions);
        System.out.println("***************************************************************************");
    }

    @Override
    public boolean isSecondPlayerTurn() {
        return isSecondPlayerTurn;
    }

    @Override
    public void reset() {

        this.isSecondPlayerTurn = true;
        this.winner = null;

        // We will reset the board from past players, and after that we will place new ones
        for (int i = 0; i <= 10; i++) {
            for (int j = 0; j <= 10; j++) {
                board[i][j] = null;
            }
        }

        kingPosition = new Position(5, 5);

//        //Reset wins
//        if (!isGameFinished()) {
//            playerOne.setWins(0);
//            playerTwo.setWins(0);
//        }
//
        //king position
        board[5][5] = new King(playerOne, "K7", new Position(5, 5));

        //player 1 pawns position
        board[3][5] = new Pawn(playerOne, "D1", new Position(5, 3));
        board[4][4] = new Pawn(playerOne, "D2", new Position(4, 4));
        board[4][5] = new Pawn(playerOne, "D3", new Position(5, 4));
        board[4][6] = new Pawn(playerOne, "D4", new Position(6, 4));
        board[5][3] = new Pawn(playerOne, "D5", new Position(3, 5));
        board[5][4] = new Pawn(playerOne, "D6", new Position(4, 5));
        board[5][6] = new Pawn(playerOne, "D8", new Position(6, 5));
        board[5][7] = new Pawn(playerOne, "D9", new Position(7, 5));
        board[6][4] = new Pawn(playerOne, "D10", new Position(4, 6));
        board[6][5] = new Pawn(playerOne, "D11", new Position(5, 6));
        board[6][6] = new Pawn(playerOne, "D12", new Position(6, 6));
        board[7][5] = new Pawn(playerOne, "D13", new Position(5, 7));

        //player 2 pawns position
        //up
        board[0][3] = new Pawn(playerTwo, "A1", new Position(3, 0));
        board[0][4] = new Pawn(playerTwo, "A2", new Position(4, 0));
        board[0][5] = new Pawn(playerTwo, "A3", new Position(5, 0));
        board[0][6] = new Pawn(playerTwo, "A4", new Position(6, 0));
        board[0][7] = new Pawn(playerTwo, "A5", new Position(7, 0));
        board[1][5] = new Pawn(playerTwo, "A6", new Position(5, 1));
        //left
        board[3][0] = new Pawn(playerTwo, "A7", new Position(0, 3));
        board[4][0] = new Pawn(playerTwo, "A9", new Position(0, 4));
        board[5][0] = new Pawn(playerTwo, "A11", new Position(0, 5));
        board[6][0] = new Pawn(playerTwo, "A15", new Position(0, 6));
        board[7][0] = new Pawn(playerTwo, "A17", new Position(0, 7));
        board[5][1] = new Pawn(playerTwo, "A12", new Position(1, 5));
        //right
        board[3][10] = new Pawn(playerTwo, "A8", new Position(10, 3));
        board[4][10] = new Pawn(playerTwo, "A10", new Position(10, 4));
        board[5][10] = new Pawn(playerTwo, "A14", new Position(10, 5));
        board[6][10] = new Pawn(playerTwo, "A16", new Position(10, 6));
        board[7][10] = new Pawn(playerTwo, "A18", new Position(10, 7));
        board[5][9] = new Pawn(playerTwo, "A13", new Position(9, 5));
        //down
        board[10][3] = new Pawn(playerTwo, "A20", new Position(3, 10));
        board[10][4] = new Pawn(playerTwo, "A21", new Position(4, 10));
        board[10][5] = new Pawn(playerTwo, "A22", new Position(5, 10));
        board[10][6] = new Pawn(playerTwo, "A23", new Position(6, 10));
        board[10][7] = new Pawn(playerTwo, "A24", new Position(7, 10));
        board[9][5] = new Pawn(playerTwo, "A19", new Position(5, 9));

        //king
        pieces[0] = board[5][5];

        //defence
        pieces[1] = board[3][5];
        pieces[2] = board[4][4];
        pieces[3] = board[4][5];
        pieces[4] = board[4][6];
        pieces[5] = board[5][3];
        pieces[6] = board[5][4];
        pieces[7] = board[5][6];
        pieces[8] = board[5][7];
        pieces[9] = board[6][4];
        pieces[10] = board[6][5];
        pieces[11] = board[6][6];
        pieces[12] = board[7][5];

        //attack-up
        pieces[13] = board[0][3];
        pieces[14] = board[0][4];
        pieces[15] = board[0][5];
        pieces[16] = board[0][6];
        pieces[17] = board[0][7];
        pieces[18] = board[1][5];
        //attack-left
        pieces[19] = board[3][0];
        pieces[20] = board[4][0];
        pieces[21] = board[5][0];
        pieces[22] = board[6][0];
        pieces[23] = board[7][0];
        pieces[24] = board[5][1];
        //attack-right
        pieces[25] = board[3][10];
        pieces[26] = board[4][10];
        pieces[27] = board[5][10];
        pieces[28] = board[6][10];
        pieces[29] = board[7][10];
        pieces[30] = board[5][9];
        //attack-down
        pieces[31] = board[10][3];
        pieces[32] = board[10][4];
        pieces[33] = board[10][5];
        pieces[34] = board[10][6];
        pieces[35] = board[10][7];
        pieces[36] = board[9][5];

    }


    @Override
    public void undoLastMove() {
    }

    @Override
    public int getBoardSize() {
        return 11;
    }
}
