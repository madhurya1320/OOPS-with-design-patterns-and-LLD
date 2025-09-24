// Enum for possible symbols on the board
public enum Symbol {
    X, O, EMPTY
}

// Player class represents a game player with a symbol and strategy
public class Player {
    Symbol symbol;
    PlayerStrategy playerStrategy;

    public Player(Symbol symbol, PlayerStrategy playerStrategy) {
        this.symbol = symbol;
        this.playerStrategy = playerStrategy;
    }

    // Returns player's symbol (X or O)
    public Symbol getSymbol() {
        return symbol;
    }

    // Returns the strategy used by the player (Human, AI, etc.)
    public PlayerStrategy getPlayerStrategy(){
        return playerStrategy;
    }
}

// Position class represents a row and column on the board
public class Position {
    public int row;
    public int col; 

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
}

// Strategy interface that all player strategies must implement
public interface PlayerStrategy {
    Position makeMove(Board board);
}

// Human strategy implementation where moves are entered by the user
public class HumanPlayerStrategy implements PlayerStrategy {
  private Scanner scanner;
  private String playerName;

  public HumanPlayerStrategy(String playerName) {
    this.playerName = playerName;
    this.scanner = new Scanner(System.in);
  }

  @Override
  public Position makeMove(Board board) {
    while (true) {
      System.out.printf("%s, enter your move (row [0-2] and column [0-2]): ", playerName);
      try {
        // Read row and column from user
        int row = scanner.nextInt();
        int col = scanner.nextInt();
        Position move = new Position(row, col);

        // Validate the move
        if (board.isValidMove(move)) {
          return move; // Valid move
        }
        System.out.println("Invalid move. Try again.");
      } catch (Exception e) {
        // Handles non-numeric input
        System.out.println("Invalid input. Please enter row and column as numbers.");
        scanner.nextLine(); // Clear invalid input
      }
    }
  }
}

// ---------------- Game States ----------------

// State when it's X's turn
public class XTurnState implements GameState {
    @Override
    public void next(GameContext context, Player player , boolean hasWon) {
        if(hasWon){
            // Switch to winning state if player has won
            context.setState(player.getSymbol() == Symbol.X ? new XWonState() : new OWonState());
        } else {
            // Otherwise switch to O's turn
            context.setState(new OTurnState());
        }
    }

    @Override
    public boolean isGameOver() {
        return false;
    }
}

// State when it's O's turn
public class OTurnState implements GameState {
    @Override
    public void next(GameContext context, Player player , boolean hasWon) {
        if(hasWon){
            context.setState(player.getSymbol() == Symbol.X ? new XWonState() : new OWonState());
        }
        // Otherwise switch back to X's turn
        context.setState(new XTurnState());
    }

    @Override
    public boolean isGameOver() {
        return false;
    }
}

// State when X has won
public class XWonState implements GameState {
    @Override
    public void next(GameContext context, Player player , boolean hasWon) {
       // Game is over, no next state
    }

    @Override
    public boolean isGameOver() {
        return true;
    }
}

// State when O has won
public class OWonState implements GameState {
    @Override
    public void next(GameContext context, Player player, boolean hasWon) {
        // Game is over, no next state
    }

    @Override
    public boolean isGameOver() {
        return true;
    }
}

// Context class that manages current game state
public class GameContext {
    private GameState currentState;

    public GameContext() {
        currentState = new XTurnState(); // Start with X's turn
    }

    public void setState(GameState state) {
        this.currentState = state;
    }

    public void next(Player player, boolean hasWon) {
        currentState.next(this, player , hasWon);
    }

    public boolean isGameOver() {
        return currentState.isGameOver();
    }

    public GameState getCurrentState() {
        return currentState;
    }
}

// ---------------- Board ----------------
public class Board {
  private final int rows;
  private final int columns;
  private Symbol[][] grid;

  // Create a board with given rows and columns
  public Board(int rows, int columns) {
    this.rows = rows;
    this.columns = columns;
    grid = new Symbol[rows][columns];

    // Initialize all cells to EMPTY
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        grid[i][j] = Symbol.EMPTY;
      }
    }
  }

  // Checks if a position is inside the board and empty
  public boolean isValidMove(Position pos) {
    return pos.row >= 0 && pos.row < rows && pos.col >= 0 && pos.col < columns
        && grid[pos.row][pos.col] == Symbol.EMPTY;
  }

  // Place a symbol on the board
  public void makeMove(Position pos, Symbol symbol) {
    grid[pos.row][pos.col] = symbol;
  }

  // Check if the game has been won
  public void checkGameState(GameContext context) {
    // Check rows
    for (int i = 0; i < rows; i++) {
      if (grid[i][0] != Symbol.EMPTY && isWinningLine(grid[i])) {
        context.next(currentPlayer, true);
        return;
      }
    }

    // Check columns
    for (int i = 0; i < columns; i++) {
      Symbol[] column = new Symbol[rows];
      for (int j = 0; j < rows; j++) {
        column[j] = grid[j][i];
      }
      if (column[0] != Symbol.EMPTY && isWinningLine(column)) {
        context.next(currentPlayer, true);
        return;
      }
    }

    // Check diagonals
    Symbol[] diagonal1 = new Symbol[Math.min(rows, columns)];
    Symbol[] diagonal2 = new Symbol[Math.min(rows, columns)];
    for (int i = 0; i < Math.min(rows, columns); i++) {
      diagonal1[i] = grid[i][i];
      diagonal2[i] = grid[i][columns - 1 - i];
    }
    if (diagonal1[0] != Symbol.EMPTY && isWinningLine(diagonal1)) {
       context.next(currentPlayer, true);
      return;
    }
    if (diagonal2[0] != Symbol.EMPTY && isWinningLine(diagonal2)) {
       context.next(currentPlayer, true);
      return;
    }
    // Could add draw logic here
  }

  // Helper to check if all symbols in a line are the same
  private boolean isWinningLine(Symbol[] line) {
    Symbol first = line[0];
    for (Symbol s : line) {
      if (s != first) {
        return false;
      }
    }
    return true;
  }

  // Prints the board to console
  public void printBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Symbol symbol = grid[i][j];
                switch (symbol) {
                    case X:
                        System.out.print(" X ");
                        break;
                    case O:
                        System.out.print(" O ");
                        break;
                    case EMPTY:
                    default:
                        System.out.print(" . ");
                }
                if (j < columns - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (i < rows - 1) {
                System.out.println("---+---+---");
            }
        }
        System.out.println();
    }
}

// ---------------- Game Interface ----------------
interface BoardGames {
  void play();
}

// TicTacToe game implementation
public class TicTacToeGame implements BoardGames {
  private Board board;
  private Player playerX;
  private Player playerO;
  private Player currentPlayer;
  private GameContext gameContext;

  // Initialize board and players
  public TicTacToeGame(PlayerStrategy xStrategy, PlayerStrategy oStrategy,
      int rows, int columns) {
    board = new Board(rows, columns);
    playerX = new Player(Symbol.X, xStrategy);
    playerO = new Player(Symbol.O, oStrategy);
    currentPlayer = playerX;
    gameContext = new GameContext();
  }

  @Override
  public void play() {
    do {
      board.printBoard(); // Print current board
      // Current player chooses a move
      Position move = currentPlayer.getPlayerStrategy().makeMove(board);
      board.makeMove(move, currentPlayer.getSymbol());
      // Check for win/draw
      board.checkGameState(gameContext);
      switchPlayer();
    } while (!gameContext.isGameOver()); // Continue until game ends
    announceResult();
  }

  // Switch to the other player
  private void switchPlayer() {
    currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
  }

  // Print the game result
  private void announceResult() {
    GameState state = gameContext.getCurrentState();
    if (state instanceof XWonState) {
      System.out.println("Player X wins!");
    } else if (state instanceof OWonState) {
      System.out.println("Player O wins!");
    } else {
      System.out.println("It's a draw!");
    }
  }
}

// Main entry point
public class TicTacToe1 {
  public static void main(String[] args) {
    PlayerStrategy playerXStrategy = new HumanPlayerStrategy("Player X");
    PlayerStrategy playerOStrategy = new HumanPlayerStrategy("Player O");
    TicTacToeGame game = new TicTacToeGame(playerXStrategy, playerOStrategy, 3, 3);
    game.play();
  }
}
