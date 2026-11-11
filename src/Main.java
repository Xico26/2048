void main() {
    Game g = new Game();
    Scanner scanner = new Scanner(System.in);

    g.startGame();

    String input;
    boolean running = true;
    State s;

    String[] validMoves = {"w", "a", "s", "d"};

    while (running) {
        System.out.print("Enter your move (w/a/s/d): ");
        input = scanner.nextLine();

        if (!Arrays.asList(validMoves).contains(input)) {
            System.out.println("Invalid input, try again!");
            continue;
        }

        s = g.move(input);

        switch (s) {
            case WON -> {
                System.out.println("\033[38;2;237;194;46m Congrats, you have won!!");
                running = false;
                break;
            }
            case LOST -> {
                System.out.println("\033[38;2;246;124;95m No moves possible, you have lost :(");
                running = false;
                return;
            }
            case CHANGED -> {
                break;
            }
            case UNCHANGED -> {
                System.out.println("Nothing changed!");
            }
        }
    }
}
