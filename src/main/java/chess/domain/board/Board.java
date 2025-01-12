package chess.domain.board;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

import chess.domain.Color;
import chess.domain.Score;
import chess.domain.piece.InvalidPiece;
import chess.domain.piece.Piece;
import chess.domain.piece.PieceType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Board {

    private static final double PAWN_SUBTRACT_UNIT = 0.5;
    private static final double DEFAULT_SCORE = 0D;

    private final Map<Position, Piece> value;

    Board(Map<Position, Piece> board) {
        this.value = new LinkedHashMap<>(board);
    }

    public MoveResult move(String from, String to, Color color) {
        Position fromPosition = Position.of(from);
        Position toPosition = Position.of(to);

        Piece pieceAtFrom = value.get(fromPosition);
        Piece pieceAtTo = value.getOrDefault(toPosition, InvalidPiece.getInstance());

        if (!pieceAtFrom.matchColor(color) || isInValidMove(fromPosition, toPosition, pieceAtFrom, pieceAtTo)) {
            return MoveResult.FAIL;
        }
        movePiece(fromPosition, toPosition, pieceAtFrom);
        return getMoveResult(pieceAtTo);
    }

    private boolean isInValidMove(Position from, Position to, Piece pieceAtFrom, Piece pieceAtTo) {
        return isInvalidFrom(pieceAtFrom) || isNotMovable(from, to, pieceAtFrom, pieceAtTo)
                || canNotGoOnTheWay(from, to, pieceAtFrom);
    }

    private boolean isInvalidFrom(Piece pieceAtFrom) {
        return pieceAtFrom.matchType(PieceType.INVALID);
    }

    private boolean isNotMovable(Position from, Position to, Piece pieceAtFrom, Piece pieceAtTo) {
        return !pieceAtFrom.movable(from.calculateDistance(to), pieceAtTo);
    }

    private boolean canNotGoOnTheWay(Position from, Position to, Piece pieceAtFrom) {
        return !pieceAtFrom.matchType(PieceType.KNIGHT) && isPieceOnTheWay(from, to);
    }

    private boolean isPieceOnTheWay(Position fromPosition, Position toPosition) {
        List<Position> positionsOnTheWay = fromPosition.getPositionBetween(toPosition);

        return positionsOnTheWay.stream()
                .anyMatch(value::containsKey);
    }

    private void movePiece(Position from, Position to, Piece pieceAtFrom) {
        value.put(to, pieceAtFrom);
        value.remove(from);
    }

    private MoveResult getMoveResult(Piece pieceAtTo) {
        if (pieceAtTo.matchType(PieceType.KING)) {
            return MoveResult.ENDED;
        }
        return MoveResult.SUCCESS;
    }

    public Map<Color, Double> getScore() {
        List<Position> whitePawnPositions = getPawnPositionsByColor(piece -> piece.matchColor(Color.WHITE));
        List<Position> blackPawnPositions = getPawnPositionsByColor(piece -> piece.matchColor(Color.BLACK));

        double whitePawnScore = calculatePawnScore(whitePawnPositions);
        double blackPawnScore = calculatePawnScore(blackPawnPositions);

        return calculateChessScore(whitePawnScore, blackPawnScore);
    }

    private List<Position> getPawnPositionsByColor(Predicate<Piece> condition) {
        return value.entrySet()
                .stream()
                .filter(entry -> entry.getValue().matchType(PieceType.PAWN))
                .filter(entry -> condition.test(entry.getValue()))
                .map(Entry::getKey)
                .collect(Collectors.toList());
    }

    private double calculatePawnScore(List<Position> pawnPositions) {
        double pawnScore = pawnPositions.size();
        for (File file : File.values()) {
            long count = pawnPositions.stream()
                    .filter(position -> position.isSameFile(file))
                    .count();

            pawnScore = subtractPawnScore(pawnScore, count);
        }
        return pawnScore;
    }

    private double subtractPawnScore(double pawnScore, long count) {
        if (count > 1) {
            pawnScore -= count * PAWN_SUBTRACT_UNIT;
        }
        return pawnScore;
    }

    private Map<Color, Double> calculateChessScore(double whitePawnScore, double blackPawnScore) {
        Map<Color, Double> scoreWithoutPawn = getChessScoreWithoutPawn();
        Map<Color, Double> score = new HashMap<>();

        score.put(Color.WHITE, scoreWithoutPawn.get(Color.WHITE) + whitePawnScore);
        score.put(Color.BLACK, scoreWithoutPawn.get(Color.BLACK) + blackPawnScore);

        return score;
    }

    private Map<Color, Double> getChessScoreWithoutPawn() {
        Map<Boolean, Double> scoreKeyByBoolean = value.values()
                .stream()
                .filter(piece -> !piece.matchType(PieceType.PAWN))
                .collect(groupingBy(piece -> piece.matchColor(Color.WHITE), summingDouble(Score::from)));

        return convertScoreKeyByColor(scoreKeyByBoolean);
    }

    private Map<Color, Double> convertScoreKeyByColor(Map<Boolean, Double> scoreKeyByBoolean) {
        Map<Color, Double> score = new HashMap<>();
        score.put(Color.WHITE, scoreKeyByBoolean.getOrDefault(true, DEFAULT_SCORE));
        score.put(Color.BLACK, scoreKeyByBoolean.getOrDefault(false, DEFAULT_SCORE));
        return score;
    }

    public Map<Position, Piece> getValue() {
        return new LinkedHashMap<>(value);
    }
}
