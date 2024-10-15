import java.util.*;
//I did not give nor receive unauthorized aid on this project. Colin Farley
public class Main {

    public enum GameState {
        IN_PROGRESS,//game has not ended
        MAX_WIN, //game was won by player
        MIN_WIN, //game was won by computer
        TIE; //game has tied
    }


    public enum Player {
        MAX(1), MIN(-1);

        private final int number; //number represnted by player

        Player(int n) {
            number = n; //constructor to assign number to player
        }

        //get opposite player
        public Player otherPlayer() {
            return this == MAX ? MIN : MAX;
        }

        //get numeric valueof player
        public int getNumber() {
            return number;
        }
    }


    //board class. Simply updates the board after every move. Returns winner when game is over.
    private static class Board {
        private final int rows;
        private final int cols;
        private final int[][] grid;
        private final int winLength;

        public Board(int rows, int cols, int winLength) {
            this.rows = rows;
            this.cols = cols;
            this.winLength = winLength;
            this.grid = new int[rows][cols];
        }

        public boolean makeMove(int col, int player) {
            for (int row = rows - 1; row >= 0; row--) {
                if (grid[row][col] == 0) {
                    grid[row][col] = player;
                    return true;
                }
            }
            return false;
        }

        public boolean isFull() {
            for (int col = 0; col < cols; col++) {
                if (grid[0][col] == 0) {
                    return false;
                }
            }
            return true;
        }

        public GameState checkGameState() {
            // Check horizontal, vertical, and diagonal
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    if (grid[row][col] != 0) {
                        int player = grid[row][col];
                        if (checkDirection(row, col, player, 1, 0) ||
                                checkDirection(row, col, player, 0, 1) ||
                                checkDirection(row, col, player, 1, 1) ||
                                checkDirection(row, col, player, 1, -1)) {
                            return player == 1 ? GameState.MAX_WIN : GameState.MIN_WIN;
                        }
                    }
                }
            }
            return isFull() ? GameState.TIE : GameState.IN_PROGRESS;
        }

        private boolean checkDirection(int row, int col, int player, int dRow, int dCol) {
            int count = 0;
            for (int i = 0; i < winLength; i++) {
                int r = row + i * dRow;
                int c = col + i * dCol;
                if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] != player) {
                    break;
                }
                count++;
            }
            return count == winLength;
        }

        public void print() {
            for (int[] row : grid) {
                for (int cell : row) {
                    System.out.print(cell == 0 ? ". " : (cell == 1 ? "X " : "O "));
                }
                System.out.println();
            }
            for (int col = 0; col < cols; col++) {
                System.out.print(col + " ");
            }
            System.out.println();
        }
    }

    //transposition table class. Store game data. helps avoid redundant calculations
    private static class TranspositionTable {
        private final Map<String, MinimaxInfo> table = new HashMap<>();

        public void put(String key, MinimaxInfo value) {
            table.put(key, value);
        }

        public MinimaxInfo get(String key) {
            return table.get(key);
        }

        public int size() {
            return table.size();
        }
    }

    private static class MinimaxInfo {
        int value;
        int action;

        public MinimaxInfo(int value, int action) {
            this.value = value;
            this.action = action;
        }
    }

    private TranspositionTable transpositionTable;
    private int pruningCount = 0;  // Counter for pruning

    public static void main(String[] args) {
        new Main().run();
    }

    //Prompts the user, updates the screen, alternates whose turn it is, and ends the game when it is over.
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Run part A, B, or C? ");
        String part = scanner.nextLine().toUpperCase();
        System.out.print("Include debugging info? (y/n) ");
        boolean debug = scanner.nextLine().equalsIgnoreCase("y");
        System.out.print("Enter rows: ");
        int rows = scanner.nextInt();
        System.out.print("Enter columns: ");
        int cols = scanner.nextInt();
        System.out.print("Enter number in a row to win: ");
        int winLength = scanner.nextInt();

        // Part C: Ask for depth
        int searchDepth = 0;
        if (part.equals("C")) {
            System.out.print("Enter search depth for Part C: ");
            searchDepth = scanner.nextInt();
        }

        transpositionTable = new TranspositionTable();

        // Initialize the game board
        Board board = new Board(rows, cols, winLength);
        board.print();

        System.out.print("Who plays first? 1=human, 2=computer: ");
        int firstPlayer = scanner.nextInt();

        boolean isComputerTurn = firstPlayer == 2;

        // Game loop
        while (true) {
            if (isComputerTurn) {
                if (part.equals("A")) {
                    makeComputerMovePartA(board);
                } else if (part.equals("B")) {
                    makeComputerMovePartB(board);
                } else if (part.equals("C")) {
                    makeComputerMovePartC(board, searchDepth);
                }
                GameState gameState = board.checkGameState();
                if (gameState != GameState.IN_PROGRESS) break; // Check after computer's move
            } else {
                System.out.print("Enter move: ");
                int userMove = scanner.nextInt();
                if (board.makeMove(userMove, Player.MIN.getNumber())) {
                    board.print();
                    GameState gameState = board.checkGameState();
                    if (gameState != GameState.IN_PROGRESS) break; // Check after user's move
                } else {
                    System.out.println("Invalid move. Try again.");
                    continue; // Skip computer move if the user makes an invalid move
                }
            }

            // Alternate turn
            isComputerTurn = !isComputerTurn;
        }

        System.out.println("Game over!");
        GameState finalState = board.checkGameState();
        if (finalState == GameState.MAX_WIN) {
            System.out.println("The winner is MAX (computer)");
        } else if (finalState == GameState.MIN_WIN) {
            System.out.println("The winner is MIN (human)");
        } else {
            System.out.println("The game is a draw.");
        }

        System.out.print("Play again? (y/n): ");
        if (scanner.next().equalsIgnoreCase("y")) {
            run();
        }
    }

    //specified method for part C. uses Alpha-Beta hueristic. Also uses depth evaluation.
    private void makeComputerMovePartC(Board board, int searchDepth) {
        System.out.println("Computer's turn (Part C: Alpha-Beta with Heuristic).");
        MinimaxInfo result = alphaBetaWithHeuristic(board, Player.MAX, Integer.MIN_VALUE, Integer.MAX_VALUE, searchDepth, 0);
        if (result.action != -1) {
            board.makeMove(result.action, Player.MAX.getNumber());
            board.print();
            System.out.printf("Heuristic-based Alpha-Beta value: %d, optimal move: %d\n", result.value, result.action);
        } else {
            System.out.println("No valid moves available for the computer.");
        }
    }

    //alpha-Beta with heuristic (Part C)
    private MinimaxInfo alphaBetaWithHeuristic(Board board, Player player, int alpha, int beta, int maxDepth, int currentDepth) {
        GameState state = board.checkGameState();

        //terminal state check
        if (state == GameState.MAX_WIN) {
            return new MinimaxInfo(10000, -1);
        } else if (state == GameState.MIN_WIN) {
            return new MinimaxInfo(-10000, -1);
        } else if (state == GameState.TIE) {
            return new MinimaxInfo(0, -1);
        }

        //if we reach the max depth, evaluate with heuristic
        if (currentDepth == maxDepth) {
            int heuristicValue = evaluateBoard(board);
            return new MinimaxInfo(heuristicValue, -1);
        }

        int bestValue = player == Player.MAX ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        //tracks bestMove, if not found then -1
        int bestMove = -1;

        //searches all columns
        for (int col = 0; col < board.cols; col++) {
            //copy the board to simulate moves
            Board copy = new Board(board.rows, board.cols, board.winLength);
            for (int r = 0; r < board.rows; r++) {
                System.arraycopy(board.grid[r], 0, copy.grid[r], 0, board.cols);
            }

            //try making move in current column
            if (copy.makeMove(col, player.getNumber())) {
                //recursively evaluate move using Alpha Beta pruning
                MinimaxInfo childInfo = alphaBetaWithHeuristic(copy, player.otherPlayer(), alpha, beta, maxDepth, currentDepth + 1);
                int value = childInfo.value;

                //update best move and value based on player
                if (player == Player.MAX) {
                    if (value > bestValue) {
                        bestValue = value;
                        bestMove = col;
                    }
                    alpha = Math.max(alpha, bestValue);
                    if (beta <= alpha) {
                        pruningCount++;
                        break;
                    }
                } else {
                    if (value < bestValue) {
                        bestValue = value;
                        bestMove = col;
                    }
                    beta = Math.min(beta, bestValue);
                    if (beta <= alpha) {
                        pruningCount++;
                        break;
                    }
                }
            }
        }

        return new MinimaxInfo(bestValue, bestMove);
    }

    //heuristic function to evaluate board strength
    private int evaluateBoard(Board board) {
        int score = 0;

        //simple heuristic. evaluate based on number of potential winning lines
        for (int row = 0; row < board.rows; row++) {
            for (int col = 0; col < board.cols; col++) {
                if (board.grid[row][col] == Player.MAX.getNumber()) {
                    score += countPotentialWins(board, row, col, Player.MAX);
                } else if (board.grid[row][col] == Player.MIN.getNumber()) {
                    score -= countPotentialWins(board, row, col, Player.MIN);
                }
            }
        }
        return score;
    }

    //count potential winning lines from a given position
    private int countPotentialWins(Board board, int row, int col, Player player) {
        int totalWins = 0;
        totalWins += checkPotentialWin(board, row, col, player, 1, 0);  // Horizontal
        totalWins += checkPotentialWin(board, row, col, player, 0, 1);  // Vertical
        totalWins += checkPotentialWin(board, row, col, player, 1, 1);  // Diagonal /
        totalWins += checkPotentialWin(board, row, col, player, 1, -1); // Diagonal \
        return totalWins;
    }

    //check if there's a potential winning line
    private int checkPotentialWin(Board board, int row, int col, Player player, int dRow, int dCol) {
        int count = 0;
        for (int i = 0; i < board.winLength; i++) {
            int r = row + i * dRow;
            int c = col + i * dCol;
            if (r >= 0 && r < board.rows && c >= 0 && c < board.cols && (board.grid[r][c] == 0 || board.grid[r][c] == player.getNumber())) {
                count++;
            }
        }
        return (count == board.winLength) ? 1 : 0;
    }

    //computer move for part A
    private void makeComputerMovePartA(Board board) {
        System.out.println("Computer's turn (Part A: Minimax).");
        MinimaxInfo result = minimaxSearch(board, Player.MAX);
        if (result.action != -1) {
            board.makeMove(result.action, Player.MAX.getNumber());
            board.print();
            System.out.printf("Minimax value for this state: %d, optimal move: %d\n", result.value, result.action);
        } else {
            System.out.println("No valid moves available for the computer.");
        }
    }

    //computer move for part B
    private void makeComputerMovePartB(Board board) {
        System.out.println("Computer's turn (Part B: Alpha-Beta Pruning).");
        MinimaxInfo result = alphaBetaSearch(board, Player.MAX, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (result.action != -1) {
            board.makeMove(result.action, Player.MAX.getNumber());
            board.print();
            System.out.printf("Alpha-Beta value for this state: %d, optimal move: %d\n", result.value, result.action);
            System.out.printf("Pruning occurred %d times.\n", pruningCount);
        } else {
            System.out.println("No valid moves available for the computer.");
        }
    }

    //minimax function (Part A)
    private MinimaxInfo minimaxSearch(Board board, Player player) {
        //checks game state
        GameState state = board.checkGameState();

        //check if state is already in the transposition table
        String boardKey = Arrays.deepToString(board.grid);
        MinimaxInfo cachedInfo = transpositionTable.get(boardKey);
        if (cachedInfo != null) {
            return cachedInfo;
        }

        //if terminal state, return utility
        if (state == GameState.MAX_WIN) {
            return new MinimaxInfo(10000, -1);
        } else if (state == GameState.MIN_WIN) {
            return new MinimaxInfo(-10000, -1);
        } else if (state == GameState.TIE) {
            return new MinimaxInfo(0, -1);
        }

        //initialize best move and value
        int bestValue = player == Player.MAX ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bestMove = -1;

        //explore all possible moves
        for (int col = 0; col < board.cols; col++) {
            //creates copy of board
            Board copy = new Board(board.rows, board.cols, board.winLength);
            for (int r = 0; r < board.rows; r++) {
                System.arraycopy(board.grid[r], 0, copy.grid[r], 0, board.cols);
            }

            //if move is valid, evaluate recursively
            if (copy.makeMove(col, player.getNumber())) {
                MinimaxInfo childInfo = minimaxSearch(copy, player.otherPlayer());
                int value = childInfo.value;

                //update best value and move based on player
                if (player == Player.MAX) {
                    if (value > bestValue) {
                        bestValue = value;
                        bestMove = col;
                    }
                } else {
                    if (value < bestValue) {
                        bestValue = value;
                        bestMove = col;
                    }
                }
            }
        }

        //put result into transposition table and return best move
        MinimaxInfo info = new MinimaxInfo(bestValue, bestMove);
        transpositionTable.put(boardKey, info);
        return info;
    }

    //alpha-beta function (Part B)
    private MinimaxInfo alphaBetaSearch(Board board, Player player, int alpha, int beta) {
        //check the current game state
        GameState state = board.checkGameState();

        //check if state is already in the transposition table
        String boardKey = Arrays.deepToString(board.grid);
        MinimaxInfo cachedInfo = transpositionTable.get(boardKey);
        if (cachedInfo != null) {
            return cachedInfo;
        }

        //if terminal state, return utility
        if (state == GameState.MAX_WIN) {
            return new MinimaxInfo(10000, -1);
        } else if (state == GameState.MIN_WIN) {
            return new MinimaxInfo(-10000, -1);
        } else if (state == GameState.TIE) {
            return new MinimaxInfo(0, -1);
        }

        //initialize best move and value
        int bestValue = player == Player.MAX ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bestMove = -1;

        //explore every move
        for (int col = 0; col < board.cols; col++) {
            //copy board
            Board copy = new Board(board.rows, board.cols, board.winLength);
            for (int r = 0; r < board.rows; r++) {
                System.arraycopy(board.grid[r], 0, copy.grid[r], 0, board.cols);
            }

            //if move is valid, evaluate recursively using alpha-beta pruning.
            if (copy.makeMove(col, player.getNumber())) {
                MinimaxInfo childInfo = alphaBetaSearch(copy, player.otherPlayer(), alpha, beta);
                int value = childInfo.value;

                //update best move and value
                if (player == Player.MAX) {
                    if (value > bestValue) {
                        bestValue = value;
                        bestMove = col;
                    }
                    alpha = Math.max(alpha, bestValue);
                    if (beta <= alpha) {
                        pruningCount++;
                        break;
                    }
                } else {
                    if (value < bestValue) {
                        bestValue = value;
                        bestMove = col;
                    }
                    beta = Math.min(beta, bestValue);
                    if (beta <= alpha) {
                        pruningCount++;
                        break;
                    }
                }
            }
        }

        //put it into transposition table and return best move
        MinimaxInfo info = new MinimaxInfo(bestValue, bestMove);
        transpositionTable.put(boardKey, info);
        return info;
    }
}