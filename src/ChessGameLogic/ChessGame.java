package ChessGameLogic;

import ChessGUI.ChessBoardGUI;
import ChessGUI.PawnPromotionDialog;
import ChessGUI.PieceImageView.PieceType;
import java.io.Serializable;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ChoiceDialog;

/**
 *
 * @author dapfel
 */
public class ChessGame implements Serializable {
    
    public enum PlayerColor {WHITE, BLACK};
    
    private final PlayerColor playerColor; // color of the client player
    private final PlayerColor opponentColor;
    private PlayerColor turn; // whoose turn it is 
    private String lastMove;
    private transient StringProperty turnProperty;
    private final ChessBoard board;
    private transient ChessBoardGUI boardGUI;
    private transient StringProperty moveProperty; 
    private transient StringProperty gameOverProperty;
    private final King playersKing;
    private final King opponentsKing;
    
    public ChessGame(PlayerColor playerColor) {
        this.playerColor = playerColor;
        if (playerColor.equals(PlayerColor.WHITE))
            opponentColor = PlayerColor.BLACK;
        else
            opponentColor = PlayerColor.WHITE;
        
        board = new ChessBoard(); 
        
        if (playerColor.equals(ChessGame.PlayerColor.WHITE)) {
            playersKing = (King) board.getPiece('e', 1);
            opponentsKing = (King) board.getPiece('e', 8);
        }
        else {
            playersKing = (King) board.getPiece('e', 8);
            opponentsKing = (King) board.getPiece('e', 1);
        }
        
        boardGUI = new ChessBoardGUI(playerColor, this);
        
        moveProperty = new SimpleStringProperty();
        gameOverProperty = new SimpleStringProperty();
        turnProperty = new SimpleStringProperty();
        
        turn = PlayerColor.WHITE;
        if (!playerColor.equals(turn))
            boardGUI.freezeBoard();       
        
        turnProperty.set("TURN: " + turn);
    }
    
    public boolean makeMove(int fromRank, char fromFile, int toRank, char toFile) {
        // check if if out of board
        if (toRank > 8 || toRank < 1 || toFile < 'a' || toFile > 'h')
            return false;
        
        //if same square - don't change turn. 
        if (fromRank == toRank && fromFile == toFile)
             return true;
        
        ChessPiece playersPiece = board.getPiece(fromFile, fromRank);
        ChessPiece opponentPiece = board.getPiece(toFile, toRank);
        
        // check if taking piece of own color
        if (opponentPiece != null && opponentPiece.getColor().equals(playerColor))
            return false;
        
        // check if legal move for specific piece
        if (!playersPiece.isLegalMove(toRank, toFile, board))
            return false;
        
        //check if move puts or keeps own king in check   
        movePiece(fromRank, fromFile, toRank, toFile);
        if (playersKing.inCheck(board)) { //if in check - reverse the move
            movePiece(toRank, toFile, fromRank, fromFile);
            board.setPiece(toFile, toRank, opponentPiece);
            King.setCastle(false);
            return false;
        }
        
        String additionalMoveInfo = ""; // to add info to move sent to server about pawn promotion or castle
        if (playersPiece.getClass().equals(Pawn.class) && ((playerColor.equals(PlayerColor.WHITE) && toRank == 8) ||
                                                           (playerColor.equals(PlayerColor.BLACK) && toRank == 1)))
            additionalMoveInfo = promotePawn(toRank, toFile);
        if (King.isCastle()) 
            additionalMoveInfo = castle(toRank, toFile);
        
        if (playersPiece.equals(playersKing)) 
            playersKing.setHasBeenMoved(true);
  
        Rook playersRook;
        if (playersPiece.getClass().equals(Rook.class)) {
            playersRook = ((Rook) playersPiece);
            playersRook.setHasBeenMoved(true);
        }  
        
        changeTurn();               
        //send move to server for opponent
        lastMove = "" + fromRank + fromFile + toRank + toFile + additionalMoveInfo;
        moveProperty.set(lastMove);  
        
        if (opponentsKing.inCheckmate(board)) {
            endGame(playerColor);
        }
        return true;
    }
    
    public void processOpponentsMove(String move) {
        int fromRank = Integer.parseInt(move.substring(0, 1));
        char fromFile = move.charAt(1);
        int toRank = Integer.parseInt(move.substring(2, 3));
        char toFile = move.charAt(3);
        String additionalMoveInfo = move.substring(4);
        
        movePiece(fromRank, fromFile, toRank, toFile);
        boardGUI.processOpponentsMove(fromRank, fromFile, toRank, toFile);
        
        // pawn promotion
        if (additionalMoveInfo.equals("promotion-Q"))  
            promoteOpponentPawn(toRank, toFile, PieceType.QUEEN);
        if (additionalMoveInfo.equals("promotion-K"))  
            promoteOpponentPawn(toRank, toFile, PieceType.KNIGHT);
        if (additionalMoveInfo.equals("promotion-R"))  
            promoteOpponentPawn(toRank, toFile, PieceType.ROOK);
        if (additionalMoveInfo.equals("promotion-B"))  
            promoteOpponentPawn(toRank, toFile, PieceType.BISHOP);
        
        // castle
        if (additionalMoveInfo.equals("castle-K"))  
            opponentCastle("castle-K");
        if (additionalMoveInfo.equals("castle-Q"))  
            opponentCastle("castle-Q");       
        
        changeTurn();
        
        if (playersKing.inCheckmate(board)) {
            boardGUI.freezeBoard();
            endGame(opponentColor);    
        }
    }
    
    private String promotePawn(int toRank, char toFile) {
        Optional<String> result;
        do {
            ChoiceDialog promotionDialog = (new PawnPromotionDialog()).getPromotionDialog();
            result = promotionDialog.showAndWait();
        } while (!result.isPresent());
        String promotionString = null;
        switch (result.get()) {
            case "Queen":
                board.setPiece(toFile, toRank, new Queen(playerColor,toRank ,toFile));
                promotionString = "promotion-Q";
                break;
            case "Knight":
                board.setPiece(toFile, toRank, new Knight(playerColor,toRank ,toFile));
                promotionString = "promotion-K";
                break;
            case "Rook":
                board.setPiece(toFile, toRank, new Rook(playerColor,toRank ,toFile));
                promotionString = "promotion-R";
                break;
            case "Bishop":
                board.setPiece(toFile, toRank, new Bishop(playerColor,toRank ,toFile));
                promotionString = "promotion-B";
                break;
        } 
        return promotionString;
    }
    
    private String castle(int toRank, char toFile) {
        String castleString;
        if (playerColor.equals(PlayerColor.WHITE)) {
            if (toRank == 1 && toFile == 'g') { // king side
                Rook rook = (Rook) board.getPiece('h', 1);
                rook.setFile(toFile);
                rook.setRank(toRank);
                board.setPiece('h', 1, null);
                board.setPiece('f', 1, rook);
                castleString = "castle-K";
            }
            else { // (toRank == 1 && toFile == 'c') - queen side
                Rook rook = (Rook) board.getPiece('a', 1);
                rook.setFile(toFile);
                rook.setRank(toRank);
                board.setPiece('a', 1, null);
                board.setPiece('d', 1, rook);  
                castleString = "castle-Q";
            }
        }
        else {// black
            if (toRank == 8 && toFile == 'g') { // king side
                Rook rook = (Rook) board.getPiece('h', 8);
                rook.setFile(toFile);
                rook.setRank(toRank);               
                board.setPiece('h', 8, null);
                board.setPiece('f', 8, rook);
                castleString = "castle-K";
            }
            else { // (toRank == 8 && toFile == 'c') - queen side
                Rook rook = (Rook) board.getPiece('a', 8);
                rook.setFile(toFile);
                rook.setRank(toRank);                
                board.setPiece('a', 8, null);
                board.setPiece('d', 8, rook);
                castleString = "castle-Q";
            }
        }           
        King.setCastle(false);
        return castleString;
    }
    
    private void promoteOpponentPawn(int rank, char file, PieceType type) {
        switch (type) {
            case QUEEN : board.setPiece(file, rank, new Queen(opponentColor, rank, file)); break;
            case ROOK : board.setPiece(file, rank, new Rook(opponentColor, rank, file)); break;
            case BISHOP : board.setPiece(file, rank, new Bishop(opponentColor, rank, file)); break;
            case KNIGHT : board.setPiece(file, rank, new Knight(opponentColor, rank, file)); break;
        }
        boardGUI.promoteOpponentPawn(rank, file, board.getPiece(file, rank));
    }

    private void opponentCastle(String castleSide) {
        if (opponentColor.equals(PlayerColor.WHITE)) {
            if (castleSide.equals("castle-K")) {
                Rook rook = (Rook) board.getPiece('h', 1);
                rook.setFile('f');
                rook.setRank(1);
                board.setPiece('h', 1, null);
                board.setPiece('f', 1, rook);
                boardGUI.processOpponentsMove(1, 'h', 1, 'f');
            }
            else if (castleSide.equals("castle-Q")) {
                Rook rook = (Rook) board.getPiece('a', 1);
                rook.setFile('a');
                rook.setRank(1);
                board.setPiece('a', 1, null);
                board.setPiece('d', 1, rook);               
                boardGUI.processOpponentsMove(1, 'a', 1, 'd');
            } 
        }
        else { // opponentColor.equals(PlayerColor.WHITE)
            if (castleSide.equals("castle-K")) {
                Rook rook = (Rook) board.getPiece('h', 1);
                rook.setFile('f');
                rook.setRank(8);
                board.setPiece('h', 8, null);
                board.setPiece('f', 8, rook);               
                boardGUI.processOpponentsMove(8, 'h', 8, 'f');
            }
            else if (castleSide.equals("castle-Q")) {
                Rook rook = (Rook) board.getPiece('h', 8);
                rook.setFile('a');
                rook.setRank(8);
                board.setPiece('a', 8, null);
                board.setPiece('d', 8, rook);             
                boardGUI.processOpponentsMove(8, 'a', 8, 'd');
            }                 
        } 
    }

    private void endGame(PlayerColor winnerColor) {
        gameOverProperty.set(winnerColor.toString());
    }
    
    private void movePiece(int fromRank, char fromFile, int toRank, char toFile)  {
        board.setPiece(toFile, toRank, board.getPiece(fromFile, fromRank));
        board.setPiece(fromFile, fromRank, null);
        board.getPiece(toFile, toRank).setFile(toFile);
        board.getPiece(toFile, toRank).setRank(toRank);
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
    }
    
    public void loadPieceImages() {
        for (char i = 'a'; i < 'i'; i++)
            for (int j = 1; j < 9; j++)
                if (board.getPiece(i, j) != null)
                    board.getPiece(i, j).loadPieceImage();
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

    public StringProperty getGameOverProperty() {
        return gameOverProperty;
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

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoardGUI(ChessBoardGUI boardGUI) {
        this.boardGUI = boardGUI;
    } 

    public void setTurnProperty(StringProperty turnProperty) {
        this.turnProperty = turnProperty;
    }

    public void setMoveProperty(StringProperty moveProperty) {
        this.moveProperty = moveProperty;
    }

    public void setGameOverProperty(StringProperty gameOverProperty) {
        this.gameOverProperty = gameOverProperty;
    }
    
}