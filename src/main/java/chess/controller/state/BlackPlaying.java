package chess.controller.state;

import chess.domain.Color;
import chess.domain.board.Board;
import chess.domain.board.MoveResult;

public class BlackPlaying extends Playing {
    BlackPlaying(Board board) {
        super(board);
    }

    @Override
    public ChessGameState move(String from, String to) {
        MoveResult result = movePiece(from, to, Color.BLACK);
        return getMoveResult(result, MoveResult.FAIL, Color.BLACK);
    }

    @Override
    public ChessGameState end() {
        return new Finished(Color.EMPTY);
    }
}
