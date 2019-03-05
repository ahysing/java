package sudokuv2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    private int height, width;
    private Field[] board;
    private String initBoard;
    private Log log;

    /**
     * Generating board with a default value. If file does not exist.
     */
    public Board() {
        this(".....2..38.273.45....6..87.9.8..5367..6...1..4513..9.8.84..3....79.512.62..8.....");
    }

    /**
     * Generating board from a string and initializing an empty log.
     *
     * @param board = a string
     */
    public Board(String board) {
        width = 9;
        height = 9;
        this.initBoard = board;
        this.board = StrToFields(this.initBoard);
        log = new Log();
    }

    /**
     * Generating board from a string and adding log to log object from log.
     *
     * @param board = a string
     */
    public Board(String board, String log, int step) {
        this(board);
        List<String> moves = new ArrayList<>(Arrays.asList(log.split(",")));
        this.log.setLog(moves, step);
        this.addLogToBoard(this.log);
    }

    /**
     * Parsing an initial board in a string into a list of fields.
     *
     * @param str = a board from file
     * @return = list of fields with different states
     */
    public Field[] StrToFields(String str) {
        String[] chars = str.split("");
        Field[] fields = new Field[chars.length];

        for (int i = 0; i < chars.length; i++) {

            if (chars[i].matches("\\.")) {
                fields[i] = new Field(0, 2);
            } else {
                fields[i] = new Field(Integer.parseInt(chars[i]), 1);
            }
        }

        return fields;
    }

    /**
     * Makes the initial board public.
     *
     * @return = the initial board
     */
    public String getBoard () {
        return this.initBoard;
    }

    /**
     * Sets a field on the board given by the coordinates.
     *
     * @param x = between 0-9
     * @param y = between 0-9
     * @param val = between 0-9
     * @return = true if the field was valid
     */
    public boolean setField(int x, int y, int val) {

        if (validField(x, y)) {
            board[x + y * width].setField(val);
            log.append("" + x + y + val);

            return true;
        }

        return false;
    }

    /**
     * Returns the field on the board given by the coordinates.
     *
     * @param x = a number between 0-9
     * @param y = a number between 0-9
     * @return = the value of the given field
     */
    public int getField(int x, int y) {
        return board[x + y * width].getVal();
    }

    /**
     * Returns the type of the field.
     * 0 = empty
     * 1 = initial block
     * 2 = input from user and/or from log
     *
     * @param x = a number between 0-9
     * @param y = a number between 0-9
     * @return = an integer of the field type
     */
    public int getFieldType(int x, int y) {
        return board[x + y * width].getType();
    }

    /**
     * Checks if the field is taken by an initial block.
     *
     * @param x = a number between 0-9
     * @param y = a number between 0-9
     * @return = true if valid field given by coordinates
     */
    public boolean validField(int x, int y) {
        return (board[x + y * width].getType() != 1);
    }

    /**
     * Checks if the coordinates conflicts with other numbers on the board.
     *
     * @param x = a number between 0-9
     * @param y = a number between 0-9
     * @return = what state the field first matched
     */
    public int conflict(int x, int y) {

        for (int i = 0; i < width; i++) {

            if (i != x && board[x + y * width].getVal() == board[i + y * width].getVal()) {
                return 1;
            }
        }

        for (int i = 0; i < height; i++) {

            if (i != y && board[x + y * width].getVal() == board[x + i * width].getVal()) {
                return 2;
            }
        }

        for (int i = 0, posY = (y / 3) * 3; i < height / 3; i++) {

            for (int j = 0, posX = (x / 3) * 3; j < width / 3; j++) {

                if (j + posX != x && i + posY != y
                        && board[j + posX + (i + posY) * width].getVal() == board[x + y * width].getVal()) {
                    return 3;
                }
            }
        }

        return 0;
    }

    /**
     * Checks if the game is finished or not.
     *
     * @return = true if game is solved
     */
    public boolean isGameOver() {

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                if (conflict(x, y) != 0 || board[x + y * width].getVal() == 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Rebuilding the whole board from scratch.
     * First by refactoring the initial board - then build by the log, like Git.
     */
    public void rebuild () {
        this.board = StrToFields(this.initBoard);
        this.addLogToBoard(this.log);
    }

    /**
     * Adds the input log to the board. This can be used to build board from the start.
     *
     * @param log = list of history elements in the game
     */
    public void addLogToBoard (Log log) {

        List<String> list = log.getLog();
        int moves = log.getStep();

        System.out.println(list.size() + "," + moves);
        for (int i = 0; i <= list.size() && i < moves; i++) {
            String step = list.get(i);
            if (step.matches("[0-8][0-8][0-9]")) {
                String[] pieces = step.split("");
                int x = Integer.parseInt(pieces[0]);
                int y = Integer.parseInt(pieces[1]);
                int val = (pieces[2].matches("\\.") ? 0 : Integer.parseInt(pieces[2]));
                this.board[x + y * this.width].setField(val);
            }
        }
    }

    /**
     * Returns the private stepper.
     *
     * @return = the stepper
     */
    public int getStep () {
        return this.log.getStep();
    }

    /**
     * Sets the private stepper.
     *
     * @return = the stepper
     */
    public boolean setStep (int step) {
        return this.log.setStep(step);
    }

    /**
     * Returns the private stepper.
     *
     * @return = the size of the que
     */
    public int getSizeOfLog () {
        return this.log.getSize();
    }

    /**
     * Returns the log as one string. It can then be saved.
     * Format: a00,a01...i89
     *
     * @return = a string of the log
     */
    public String getLogFormatted () {
        return this.log.getLogFormatted();
    }

    /**
     * Returns the log as one string. It can then be saved.
     * Format: a00,a01...i89
     *
     * @return = a string of the log
     */
    public String getLogFormatted (int step) {
        return this.log.getLogFormatted(step);
    }

    /**
     * Undo last step from the board. Reversing the build process by one step.
     */
    public boolean undo () {
        if (log.undo()) {
            return true;
        }

        return false;
    }

    /**
     * Undo last step from the board. Reversing the build process by one step.
     */
    public boolean redo () {
        if (log.redo()) {
            return true;
        }

        return false;
    }

    /**
     * Returns a drwaing of the board with all the essentials.
     *
     * @return = a string of the board and its components
     */
    public String toString() {
        String print = "  +-------------+-------------+-------------+\n";

        for (int y = 0; y < height; y++) {
            print += y + " |";

            for (int x = 0; x < width; x++) {
                print += " " + board[x + y * width].toString();

                if (board[x + y * width].getType() == 2 && board[x + y * width].getVal() != 0) {
                    print += (conflict(x, y) != 0 ? "*" : " ");
                }

                if ((x + 1) % 3 == 0) {
                    print += " |";
                }
            }

            print += "\n";

            if ((y + 1) % 3 == 0) {
                print += "  +-------------+-------------+-------------+\n";
            }
        }

        print += "     a   b   c     d   e   f     g   h   i";

        return print;
    }
}
