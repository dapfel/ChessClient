package ChessGUI;

import ChessGUI.PieceImageView.PieceType;
import ChessGameLogic.ChessBoard;
import ChessGameLogic.ChessGame;
import ChessGameLogic.ChessGame.PlayerColor;
import ChessGameLogic.ChessPiece;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 *
 * @author dapfel
 */
public class ChessBoardGUI {

    private final GridPane chessBoardPane;
    private final Square[][] squares;
    private final ChessGame chessGame;
    private final EventHandler<MouseEvent> handler = MouseEvent::consume; // event filter to consume mouse events during opponents turn
    
    public ChessBoardGUI(PlayerColor playerColor, ChessGame chessGame) {
        this.chessGame = chessGame;
        chessBoardPane = new GridPane();
        squares = new Square[8][8];
        
        // for white bottom left is a1. for black bottom left is h8
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int colorType = (x + y) % 2;
                squares[x][y] = new Square(colorType, y + 1, (char) ('a' + x));
                if (playerColor.equals(PlayerColor.WHITE))
                    chessBoardPane.add(squares[x][y], x, 7 - y);
                else
                    chessBoardPane.add(squares[x][y], 7 - x, y);
            }
        }
        Square.setChessBoardGUI(this);
        initializePieces(chessGame.getBoard());
    }
    
    private void initializePieces(ChessBoard board) {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (board.getPiece((char) ('a' + j), i + 1) != null)
                squares[j][i].initializePieceImage(board.getPiece((char) ('a' + j), i + 1));
    }
 
    public GridPane getChessBoardPane() {
        return chessBoardPane;
    }
    
    public void ProcessPlayersMove(Square newSquare, PieceImageView pieceImageView) {
        if (chessGame.makeMove(pieceImageView.getRank(), pieceImageView.getFile(), newSquare.getRank(), newSquare.getFile())) { // move legality check
            if (!newSquare.equals((Square) pieceImageView.getParent()))
                freezeBoard();
            newSquare.getChildren().clear(); 
            newSquare.getChildren().add(pieceImageView);  
            finishIfCastle(newSquare, pieceImageView);  
            finishIfPawnPromotion(newSquare, pieceImageView);
            resetPieceImage(newSquare, pieceImageView);
        }
        else { // if illegal move, put piece back in original square
            Square oldSquare = squares[pieceImageView.getFile() - 'a'][pieceImageView.getRank() - 1];
            oldSquare.getChildren().clear();
            oldSquare.getChildren().add(pieceImageView); 
            resetPieceImage(oldSquare, pieceImageView);
        }
    }
        
    private void finishIfPawnPromotion(Square newSquare, PieceImageView pieceImageView) {
        ChessPiece newPiece;
        if (pieceImageView.getType().equals(PieceImageView.PieceType.PAWN) && pieceImageView.getColor().equals(PlayerColor.WHITE) && newSquare.getRank() == 8 ||
            pieceImageView.getColor().equals(PlayerColor.BLACK) && newSquare.getRank() == 1) {
            newPiece = chessGame.getBoard().getPiece(newSquare.getFile(), newSquare.getRank());
            newSquare.getChildren().clear();
            newSquare.initializePieceImage(newPiece);
        }
    }
    
    private void finishIfCastle(Square newSquare, PieceImageView pieceImageView) {
        if (pieceImageView.getType().equals(PieceType.KING) && Math.abs(newSquare.getFile() - pieceImageView.getFile()) == 2) {
            PieceImageView rookPieceImageView = null;
            Square newRookSquare = null;
            if (newSquare.getRank() == 1 && newSquare.getFile() == 'c') {// white queen side castle
                rookPieceImageView = (PieceImageView) squares[0][0].getChildren().get(0);
                rookPieceImageView.setFile('d');
                squares[0][0].getChildren().clear();
                newRookSquare = squares[3][0];
            }
            if (newSquare.getRank() == 1 && newSquare.getFile() == 'g') {// white king side castle
                rookPieceImageView = (PieceImageView) squares[7][0].getChildren().get(0);
                rookPieceImageView.setFile('f');
                squares[7][0].getChildren().clear();
                newRookSquare = squares[5][0];
            }
            if (newSquare.getRank() == 8 && newSquare.getFile() == 'c') {// black queen side castle
                rookPieceImageView = (PieceImageView) squares[0][7].getChildren().get(0);
                rookPieceImageView.setFile('d');
                squares[0][7].getChildren().clear();
                newRookSquare = squares[3][7];
            }
            if (newSquare.getRank() == 8 && newSquare.getFile() == 'g') {// black king side castle
                rookPieceImageView = (PieceImageView) squares[7][7].getChildren().get(0);
                rookPieceImageView.setFile('f');
                squares[7][7].getChildren().clear();
                newRookSquare = squares[3][7];
            }          
            newRookSquare.getChildren().add(rookPieceImageView);
            resetPieceImage(newRookSquare, rookPieceImageView);
        }
    }

    private void resetPieceImage(Square newSquare, PieceImageView newPieceImageView) {
        newPieceImageView.setFile(newSquare.getFile());
        newPieceImageView.setRank(newSquare.getRank()); 
        newPieceImageView.setTranslateX(0);
        newPieceImageView.setTranslateY(0);
        newPieceImageView.setStartDragX(0);
        newPieceImageView.setStartDragY(0);
        newPieceImageView.setMouseTransparent(false);
    }

    public void processOpponentsMove(int fromRank, char fromFile, int toRank, char toFile) {
        PieceImageView pieceImageView = (PieceImageView) squares[fromFile - 'a'][fromRank - 1].getChildren().get(0);
        squares[toFile - 'a'][toRank - 1].getChildren().clear();
        squares[toFile - 'a'][toRank - 1].getChildren().add(pieceImageView);
        unfreezeBoard();
    }
    
    public void promoteOpponentPawn(int rank, char file, ChessPiece piece) {
        PieceImageView pieceImageView = new PieceImageView(piece);
        squares[file - 'a'][rank - 1].getChildren().clear();
        squares[file - 'a'][rank - 1].getChildren().add(pieceImageView);
    }
    
    /*
    Freeze the game board during opponents turn to not allow this player to move.
    */
    public void freezeBoard() {
        getChessBoardPane().addEventFilter(MouseEvent.ANY, handler);
    }
    
    /*
    Unfreeze the game board when its this players turn
    */
    private void unfreezeBoard() {
        getChessBoardPane().removeEventFilter(MouseEvent.ANY, handler);
    }

    public ChessGame getChessGame() {
        return chessGame;
    }

}
