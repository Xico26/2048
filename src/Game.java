import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Game {
    private static int[][] board = new int[4][4]; // [Y][X]
    private static Random r = new Random();

    // Colors
    private Map<Integer, String> colorMap = new HashMap<>();
    private final static String LIGHT_BEIGE = "\033[38;2;238;228;218m";
    private final static String BEIGE = "\033[38;2;237;224;200m";
    private final static String ORANGE = "\033[38;2;242;177;121m";
    private final static String DARK_ORANGE = "\033[38;2;245;149;99m";
    private final static String RED = "\033[38;2;246;124;95m";
    private final static String DARK_RED = "\033[38;2;246;94;59m";
    private final static String GOLD = "\033[38;2;237;207;114m";
    private final static String DARK_GOLD = "\033[38;2;237;204;97m";
    private final static String GOLDEN = "\033[38;2;237;200;80m";
    private final static String DARK_GOLDEN = "\033[38;2;237;197;63m";
    private final static String BRIGHT_GOLD = "\033[38;2;237;194;46m";
    private final static String WHITE = "\033[38;2;255;255;255m";

    // Constructor, initialises game board and colors map
    public Game() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = 0;
            }
        }

        colorMap.put(0, WHITE);
        colorMap.put(2, LIGHT_BEIGE);
        colorMap.put(4, BEIGE);
        colorMap.put(8, ORANGE);
        colorMap.put(16, DARK_ORANGE);
        colorMap.put(32, RED);
        colorMap.put(64, DARK_RED);
        colorMap.put(128, GOLD);
        colorMap.put(256, DARK_GOLD);
        colorMap.put(512, GOLDEN);
        colorMap.put(1024, DARK_GOLDEN);
        colorMap.put(2048, BRIGHT_GOLD);
    }

    // Starts a game by picking a tile
    public void startGame() {
        pickTile();
        printBoard();
    }

    // Picks empty tile randomly and spawns a 2
    private void pickTile() {
        int x = r.nextInt(4);
        int y = r.nextInt(4);

        while (board[y][x] != 0) {
            x = r.nextInt(4);
            y = r.nextInt(4);
        }

        board[y][x] = 2;
    }

    // Checks win (there's a 2048)
    private boolean checkWin() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j] == 2048) return true;
            }
        }

        return false;
    }

    // Checks if a tile can merge with right or bottom
    private boolean checkCanMerge(int i, int j) {
        // i = row, j = col

        if (i == 3 && j == 3) {
            return false;
        }

        if (i == 3) {
            return board[i][j] == board[i][j+1];
        }

        if (j == 3) {
            return board[i][j] == board[i+1][j];
        }

        return (board[i][j] == board[i+1][j]) || (board[i][j] == board[i][j+1]);
    }

    // Checks if lost (no empty tiles and can't merge anything)
    private boolean checkLoss() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j] == 0) return false;
                boolean canMerge = checkCanMerge(i, j);

                if (canMerge) return false;
            }
        }

        return true;
    }

    // Print board
    private void printBoard() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(colorMap.get(board[i][j]) + board[i][j] + "\033[0m ");
            }
            System.out.println();
        }
    }

    private boolean processColumn(int col, int step) {
        boolean[] merged = {false, false, false, false};
        boolean changed = false;

        // up (-1): 1 -> 4
        // down (1): 2 -> 0
        int firstElement = (step == 1) ? 2 : 1;

        // r -= step -> r++ for up, r-- for down
        for (int r = firstElement; r >= 0 && r < 4; r -= step) {
            if (board[r][col] == 0) continue;

            int y = r;

            while (y + step >= 0 && y + step < 4) {
                // up: y - 1; down: y + 1
                int nextIndex = y + step;
                int nextTile = board[nextIndex][col];

                if (nextTile != 0) {
                    // next tile is the same and hasnt merged before -> merge
                    if (nextTile == board[y][col] && !merged[nextIndex]) {
                        board[y][col] = 0;
                        board[nextIndex][col] = nextTile * 2;
                        merged[nextIndex] = true;
                        changed = true;
                        break;
                    }
                    // either merged or blocked, can't do anything
                    break;
                } else {
                    // next tile is empty -> move there
                    board[nextIndex][col] = board[y][col];
                    board[y][col] = 0;
                    y = nextIndex;
                    changed = true;
                }
            }
        }

        return changed;
    }

    private boolean processRow(int row, int step) {
        boolean[] merged = {false, false, false, false};
        boolean changed = false;

        // left (-1): 1 -> 4
        // right (1): 2 -> 0
        int firstElement = (step == 1) ? 2 : 1;

        // c -= step -> c++ for left, c-- for right
        for (int c = firstElement; c >= 0 && c < 4; c -= step) {
            if (board[row][c] == 0) continue;

            int x = c;

            while (x + step >= 0 && x + step < 4) {
                // left: x - 1; right: x + 1
                int nextIndex = x + step;
                int nextTile = board[row][nextIndex];

                if (nextTile != 0) {
                    // next tile is the same and hasnt merged before -> merge
                    if (nextTile == board[row][x] && !merged[nextIndex]) {
                        board[row][x] = 0;
                        board[row][nextIndex] = nextTile * 2;
                        merged[nextIndex] = true;
                        changed = true;
                        break;
                    }
                    // either merged or blocked, can't do anything
                    break;
                } else {
                    // next tile is empty -> move there
                    board[row][nextIndex] = board[row][x];
                    board[row][x] = 0;
                    x = nextIndex;
                    changed = true;
                }
            }
        }

        return changed;
    }

    // Returns game state, performs movement in direction in every column
    public State move(String direction) {
        boolean changed = false;

        for (int i = 0; i < 4; i++) {
            boolean res;
            switch (direction) {
                case "w":
                    res = processColumn(i, -1);
                    changed = res || changed;
                    break;
                case "s":
                    res = processColumn(i, 1);
                    changed = res || changed;
                    break;
                case "d":
                    res = processRow(i, 1);
                    changed = res || changed;
                    break;
                case "a":
                    res = processRow(i, -1);
                    changed = res || changed;
                    break;
                default:
                    return State.UNCHANGED;
            }
        }

        if (checkWin()) {
            printBoard();
            return State.WON;
        }

        if (changed) {
            pickTile();
            printBoard();

            if (checkLoss()) {
                return State.LOST;
            }

            return State.CHANGED;
        }

        return State.UNCHANGED;
    }
}
