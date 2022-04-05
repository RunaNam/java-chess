package chess.controller;

import static spark.Spark.get;
import static spark.Spark.post;

import chess.dao.ChessGameDao;
import chess.dao.PieceDao;
import chess.dto.BoardDto;
import chess.dto.MoveDto;
import chess.dto.ScoreDto;
import chess.dto.TurnDto;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class ChessWebController {
    private final ChessGame chessGame;
    private final ChessGameDao chessGameDao;
    private final PieceDao pieceDao;

    public ChessWebController() {
        this.chessGame = new ChessGame();
        this.chessGameDao = new ChessGameDao();
        this.pieceDao = new PieceDao();
    }

    public void run() {
        Gson gson = new Gson();

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return render(model, "index.html");
        });

        get("/start", (req, res) -> {
            chessGame.start();
            chessGameDao.save("chess", "start");
            pieceDao.save(chessGame.getBoard().getValue());
            BoardDto boardDto = BoardDto.from(chessGame.getBoard());
            return gson.toJson(boardDto.getBoard());
        });

        post("/move", (req, res) -> {
            MoveDto moveDto = gson.fromJson(req.body(), MoveDto.class);
            chessGame.move(moveDto);
            if(chessGame.isEnded()){
                res.redirect("/end");
            }
            pieceDao.movePiece(chessGame.getBoard().getValue(), "chess");
            chessGameDao.updateTurn(chessGame.getTurn().getValue(), "chess");
            BoardDto boardDto = BoardDto.from(chessGame.getBoard());
            return gson.toJson(boardDto.getBoard());
        });

        get("/turn", (req, res) -> {
            TurnDto turnDto = TurnDto.from(chessGame.getTurn());
            return gson.toJson(turnDto.getTurn());
        });

        get("/status", (req, res) -> {
            ScoreDto scoreDto = chessGame.status();
            return gson.toJson(scoreDto.getScore());
        });

        get("/end", (req, res) -> {
            chessGame.end();
            chessGameDao.deleteByName("chess");
            pieceDao.deleteByGameName("chess");
            return gson.toJson(chessGame.getWinner());
        });
    }


    private static String render(Map<String, Object> model, String templatePath) {
        return new HandlebarsTemplateEngine().render(new ModelAndView(model, templatePath));
    }
}
