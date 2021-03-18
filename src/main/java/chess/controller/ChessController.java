package chess.controller;

import chess.domain.board.Board;
import chess.domain.command.Commands;
import chess.domain.game.ChessGame;
import chess.view.InputView;

public class ChessController {

    private final Board board;
    private final ChessGame game;
    private final Commands commands;

    public ChessController(final Board board, final ChessGame game, final Commands commands) {
        this.board = board;
        this.game = game;
        this.commands = commands;
    }

    public void run() {
        while (game.isFinished()) {
            commands.executeIf(InputView.inputCommandFromUser());
        }
    }
}
