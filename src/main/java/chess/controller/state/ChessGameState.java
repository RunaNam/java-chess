package chess.controller.state;

import chess.domain.Color;
import chess.domain.board.Board;
import chess.dto.ScoreDto;

public interface ChessGameState {
    ChessGameState start();

    ChessGameState move(String from, String to);

    ScoreDto status();

    ChessGameState end();

    Board getBoard();

    Color getWinner();

    boolean isEnded();
}
