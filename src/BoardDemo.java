public class BoardDemo {
    public static void main(String[] args) {
        Board board = new Board(6, 7, 4); // standard connect 4 size
        System.out.println("Here is the board:");
        System.out.println(board.to2DString());

        // MAX (X) makes a move:
        board = board.makeMove(3);
        System.out.println("Here is the updated board:");
        System.out.println(board.to2DString());

        // MIN (O) makes a move:
        board = board.makeMove(2);
        System.out.println("Here is the updated board:");
        System.out.println(board.to2DString());

        // Check the game progress:
        System.out.println("State of the game: " + board.getGameState());

        // Demo with a smaller board:
        board = new Board(3, 3, 2); // This a rather silly game.
        System.out.println("Here is the board:");
        System.out.println(board.to2DString());

        // MAX (X) makes a move:
        board = board.makeMove(2);
        System.out.println("Here is the updated board:");
        System.out.println(board.to2DString());

        // MIN (O) makes a move:
        board = board.makeMove(1);
        System.out.println("Here is the updated board:");
        System.out.println(board.to2DString());

        // MAX (X) makes a move:
        board = board.makeMove(2);
        System.out.println("Here is the updated board:");
        System.out.println(board.to2DString());

        // Check the game progress:
        System.out.println("State of the game: " + board.getGameState());
    }
}
