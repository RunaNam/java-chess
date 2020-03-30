package chess.piece;

import chess.coordinate.Vector;

public class Queen extends Piece {

    private static final int SCORE = 9;

    public Queen(final Team team) {
        super(team, SCORE);
    }

    @Override
    protected boolean canReach(Vector vector, Piece targetPiece) {
        return vector.isDiagonal() || vector.isStraight();
    }
}