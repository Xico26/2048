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

    // Swipe up in a column, returns true if anything changed
    private boolean processColumnUp(int col) {
        boolean[] merged = {false, false, false, false};
        boolean changed = false;

        for (int r = 1; r < 4; r++) {
            if (board[r][col] == 0) {
                continue;
            }

            int y = r;

            while (y != 0) {
                int tileUp = board[y-1][col];
                if (tileUp != 0) {
                    // above has same tile and hasnt merged before -> merge
                    if (tileUp == board[y][col] && !merged[y-1]) {
                        board[y][col] = 0;
                        board[y-1][col] = tileUp * 2;
                        merged[y-1] = true;
                        changed = true;
                        break;
                    } else {
                        // cant merge, stop
                        break;
                    }
                } else {
                    // tile above is empty -> move up
                    board[y-1][col] = board[y][col];
                    board[y][col] = 0;
                    y = y-1;
                    changed = true;
                }
            }
        }

        return changed;
    }

    // Swipe down in a column, returns true if anything changed
    private boolean processColumnDown(int col) {
        boolean[] merged = {false, false, false, false};
        boolean changed = false;

        for (int r = 2; r >= 0; r--) {
            if (board[r][col] == 0) {
                continue;
            }

            int y = r;

            while (y != 3) {
                int tileDown = board[y+1][col];
                if (tileDown != 0) {
                    // below has same tile and hasnt merged before -> merge
                    if (tileDown == board[y][col] && !merged[y+1]) {
                        board[y][col] = 0;
                        board[y+1][col] = tileDown * 2;
                        merged[y+1] = true;
                        changed = true;
                        break;
                    } else {
                        // cant merge, stop
                        break;
                    }
                } else {
                    // tile above is empty -> move up
                    board[y+1][col] = board[y][col];
                    board[y][col] = 0;
                    y = y+1;
                    changed = true;
                }
            }
        }

        return changed;
    }

    // Swipe right in a column, returns true if anything changed
    private boolean processColumnRight(int row) {
        boolean[] merged = {false, false, false, false};
        boolean changed = false;

        for (int c = 2; c >= 0; c--) {
            if (board[row][c] == 0) {
                continue;
            }

            int x = c;

            while (x != 3) {
                int tileRight = board[row][x+1];
                if (tileRight != 0) {
                    // right has same tile and hasnt merged before -> merge
                    if (tileRight == board[row][x] && !merged[x+1]) {
                        board[row][x] = 0;
                        board[row][x+1] = tileRight * 2;
                        merged[x+1] = true;
                        changed = true;
                        break;
                    } else {
                        // cant merge, stop
                        break;
                    }
                } else {
                    // tile above is empty -> move up
                    board[row][x+1] = board[row][x];
                    board[row][x] = 0;
                    x = x+1;
                    changed = true;
                }
            }
        }

        return changed;
    }

    // Swipe left in a column, returns true if anything changed
    private boolean processColumnLeft(int row) {
        boolean[] merged = {false, false, false, false};
        boolean changed = false;

        for (int c = 1; c < 4; c++) {
            if (board[row][c] == 0) {
                continue;
            }

            int x = c;

            while (x != 0) {
                int tileLeft = board[row][x-1];
                if (tileLeft != 0) {
                    // right has same tile and hasnt merged before -> merge
                    if (tileLeft == board[row][x] && !merged[x-1]) {
                        board[row][x] = 0;
                        board[row][x-1] = tileLeft * 2;
                        merged[x-1] = true;
                        changed = true;
                        break;
                    } else {
                        // cant merge, stop
                        break;
                    }
                } else {
                    // tile above is empty -> move up
                    board[row][x-1] = board[row][x];
                    board[row][x] = 0;
                    x = x-1;
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
                    res = processColumnUp(i);
                    changed = res || changed;
                    break;
                case "s":
                    res = processColumnDown(i);
                    changed = res || changed;
                    break;
                case "d":
                    res = processColumnRight(i);
                    changed = res || changed;
                    break;
                case "a":
                    res = processColumnLeft(i);
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
