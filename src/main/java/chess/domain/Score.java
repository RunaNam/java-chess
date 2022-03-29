package chess.domain;

import chess.domain.piece.Piece;
import chess.domain.piece.PieceClassChecker;
import java.util.Arrays;
import java.util.function.Predicate;

public enum Score {
    KING((piece) -> isRightPiece(piece, PieceClassChecker.KING), 0),
    QUEEN((piece) -> isRightPiece(piece, PieceClassChecker.QUEEN), 9),
    BISHOP((piece) -> isRightPiece(piece, PieceClassChecker.BISHOP), 3),
    KNIGHT((piece) -> isRightPiece(piece, PieceClassChecker.KNIGHT), 2.5),
    ROOK((piece) -> isRightPiece(piece, PieceClassChecker.ROOK),5),
    PAWN((piece) -> isRightPiece(piece, PieceClassChecker.PAWN),1),
    ;

    private static boolean isRightPiece(Piece piece, PieceClassChecker checker) {
        return PieceClassChecker.from(piece) == checker;
    }

    private final Predicate<Piece> piecePredicate;
    private final double score;

    Score(Predicate<Piece> piecePredicate, double score) {
        this.piecePredicate = piecePredicate;
        this.score = score;
    }

    public static double from(Piece piece) {
        return Arrays.stream(values())
                .filter(score -> score.piecePredicate.test(piece))
                .mapToDouble(score -> score.score)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 기물입니다."));
    }
}
