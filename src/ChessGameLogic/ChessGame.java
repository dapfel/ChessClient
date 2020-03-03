
package ChessGameLogic;

import ChessGUI.ChessBoardGUI;
import ChessGUI.Square;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author dapfel
 */
public class ChessGame {
    
    public enum PlayerColor {WHITE, BLACK};
    
    private final PlayerColor playerColor; // color of the client player
    private final PlayerColor opponentColor;
    private PlayerColor turn; // whoose turn it is 
    private String lastMove;
    private StringProperty turnProperty;
    private final ChessPiece[][] board;
    private final ChessBoardGUI boardGUI;
    private final EventHandler<MouseEvent> handler = MouseEvent::consume; // event filter to consume mouse events during opponents turn
    private StringProperty moveProperty; 
    
    public ChessGame(PlayerColor playerColor) {
        this.playerColor = playerColor;
        if (playerColor.equals(PlayerColor.WHITE))
            opponentColor = PlayerColor.BLACK;
        else
            opponentColor = PlayerColor.WHITE;
        
        board = new ChessPiece[8][8];        
        initializePieces();        
        boardGUI = new ChessBoardGUI(playerColor, this);
        Square.setChessBoardGUI(boardGUI);
        
        turn = PlayerColor.WHITE;
        if (!playerColor.equals(turn))
            freezeBoard();
    }

    private void initializePieces() {
        board[0][1] = new Pawn(PlayerColor.WHITE, 2, 'a');
        board[1][1] = new Pawn(PlayerColor.WHITE, 2, 'b');
        board[2][1] = new Pawn(PlayerColor.WHITE, 2, 'c');
        board[3][1] = new Pawn(PlayerColor.WHITE, 2, 'd');
        board[4][1] = new Pawn(PlayerColor.WHITE, 2, 'e');
        board[5][1] = new Pawn(PlayerColor.WHITE, 2, 'f');
        board[6][1] = new Pawn(PlayerColor.WHITE, 2, 'g');
        board[7][1] = new Pawn(PlayerColor.WHITE, 2, 'h');
        board[0][0] = new Rook(PlayerColor.WHITE, 1, 'a');
        board[7][0] = new Rook(PlayerColor.WHITE, 1, 'h');
        board[1][0] = new Knight(PlayerColor.WHITE, 1, 'b');
        board[6][0] = new Knight(PlayerColor.WHITE, 1, 'g');
        board[2][0] = new Bishop(PlayerColor.WHITE, 1, 'c');
        board[5][0] = new Bishop(PlayerColor.WHITE, 1, 'f');
        board[3][0] = new Queen(PlayerColor.WHITE, 1, 'd');
        board[4][0] = new King(PlayerColor.WHITE, 1, 'e');
        
        board[0][6] = new Pawn(PlayerColor.BLACK, 7, 'a');
        board[1][6] = new Pawn(PlayerColor.BLACK, 7, 'b');
        board[2][6] = new Pawn(PlayerColor.BLACK, 7, 'c');
        board[3][6] = new Pawn(PlayerColor.BLACK, 7, 'd');
        board[4][6] = new Pawn(PlayerColor.BLACK, 7, 'e');
        board[5][6] = new Pawn(PlayerColor.BLACK, 7, 'f');
        board[6][6] = new Pawn(PlayerColor.BLACK, 7, 'g');
        board[7][6] = new Pawn(PlayerColor.BLACK, 7, 'h');
        board[0][7] = new Rook(PlayerColor.BLACK, 8, 'a');
        board[7][7] = new Rook(PlayerColor.BLACK, 8, 'h');
        board[1][7] = new Knight(PlayerColor.BLACK, 8, 'b');
        board[6][7] = new Knight(PlayerColor.BLACK, 8, 'g');
        board[2][7] = new Bishop(PlayerColor.BLACK, 8, 'c');
        board[5][7] = new Bishop(PlayerColor.BLACK, 8, 'f');
        board[4][7] = new Queen(PlayerColor.BLACK, 8, 'e');
        board[3][7] = new King(PlayerColor.BLACK, 8, 'd');
    }
    
    public boolean movePiece(int fromRank, char fromFile, int toRank, char toFile) {
        //if same square - don't change turn. 
        if (fromRank == toRank && fromFile == toFile)
             return true;
        
        ChessPiece playersPiece = board[fromFile][fromRank];
        ChessPiece opponentPiece = board[toFile][toRank];
        
        // if taking piece of own color - return false.
        if (opponentPiece != null && opponentPiece.getColor().equals(playerColor))
            return false;
        
        // check if legal move for specific piece
        if (!playersPiece.move(toRank, toFile, board))
            return false;
        
        //check if moves puts or keeps own king in check   
        board[fromFile][fromRank] = null;
        board[toFile][toRank] = playersPiece;
        if (inCheck(playerColor)) { //if in check - reverse the move
            board[fromFile][fromRank] = playersPiece;
            board[toFile][toRank] = null;
            return false;
        }
 
        changeTurn();               
        //send move to server for opponent
        lastMove = "" + fromRank + fromFile + toRank + toFile;
        moveProperty.set(lastMove);        
        if (checkmateCheck(opponentColor)) {
            endGame(playerColor);
        }
        return true;
    }
    
    public void processOponentsMove(String move) {
        int fromRank = Integer.parseInt(move.substring(0, 1));
        char fromFile = move.charAt(1);
        int toRank = Integer.parseInt(move.substring(2, 3));
        char toFile = move.charAt(3);
        ChessPiece piece = board[fromFile][fromRank];
        board[fromFile][fromRank] = null;
        board[toFile][toRank] = piece;
        boardGUI.processOponentsMove(fromRank, fromFile, toRank, toFile);
        changeTurn();
        
        if (checkmateCheck(playerColor)) {
            endGame(opponentColor);
        }
        
    }
    
    /*
    Checks if the king of the given parameter color is in check 
    */
    private boolean inCheck(PlayerColor playerColor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private boolean checkmateCheck(PlayerColor opponentColor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void endGame(PlayerColor winnerColor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        // call end game of game page (add to it that if there is a winner ...)
    }
    
    /*
    Freeze the game board during opponents turn to not allow this player to move.
    */
    public void freezeBoard() {
        boardGUI.getChessBoardPane().addEventFilter(MouseEvent.ANY, handler);
    }
    
    /*
    Unfreeze the game board when its this players turn
    */
    private void unfreezeBoard() {
        boardGUI.getChessBoardPane().removeEventFilter(MouseEvent.ANY, handler);
    }
    
    private void changeTurn() {
        if (turn.equals(PlayerColor.WHITE)) {
            turn = PlayerColor.BLACK;
            turnProperty.set("TURN: " + turn);
        }
        else {
            turn = PlayerColor.WHITE;
            turnProperty.set("TURN: " + turn);
        }
        if (turn.equals(playerColor))
            unfreezeBoard();
        else
            freezeBoard();
    }

    public StringProperty getTurnProperty() {
        return turnProperty;
    }
      
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public ChessBoardGUI getBoardGUI() {
        return boardGUI;
    }

    public StringProperty getMoveProperty() {
        return moveProperty;
    }

    public PlayerColor getTurn() {
        return turn;
    }

    public String getLastMove() {
        return lastMove;
    }

    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }

    public ChessPiece[][] getBoard() {
        return board;
    }
    
}
