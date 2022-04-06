package chess.controller.state;

import chess.domain.Color;
import chess.domain.board.Board;
import chess.domain.board.MoveResult;
import chess.dto.ScoreDto;

public abstract class Playing implements ChessGameState {
    protected final Board board;

    Playing(Board board) {
        this.board = board;
    }

    @Override
    public ChessGameState start() {
        System.out.println("이미 실행중입니다");
        return this;
    }

    @Override
    public ScoreDto status() {
        return ScoreDto.from(board.getScore());
    }

    MoveResult movePiece(String from, String to, Color color) {
        return board.move(from, to, color);
    }

    abstract ChessGameState checkMoveResult(MoveResult result);

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public Color getWinner() {
        throw new IllegalStateException("게임 실행 중에는 승자를 가져올 수 없습니다.");
    }

    @Override
    public boolean isEnded() {
        return false;
    }
}
