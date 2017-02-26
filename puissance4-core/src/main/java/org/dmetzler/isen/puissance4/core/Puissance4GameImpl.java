package org.dmetzler.isen.puissance4.core;

import java.util.ArrayList;
import java.util.List;
import org.dmetzler.isen.puissance4.*;

/**
 * Created by dmetzler on 03/09/2014.
 */
public class Puissance4GameImpl implements Puissance4Game {

    public static final int COLUMNS_NUMBER = 10;
    public static final int ROWS_NUMBER = 10;
    public static final int NUMBER_OF_CHIP_TO_ALIGN = 4;
    public static final String OUTSIDE_OF_BOARD_ERROR = "It is not possible to play outside of the board";


    List<List<ChipColour>> board = new ArrayList<>();

    public Puissance4GameImpl() {
        initBoard();
    }

    private void initBoard() {
        setupPawnsPlayer(ChipColour.BLACK, 0, 3);
        for(int rowIndex = 4; rowIndex < 6 ; rowIndex++){
        	 List<ChipColour> row = new ArrayList<ChipColour>();
             for (int colIndex = 0  ; colIndex < COLUMNS_NUMBER ;colIndex++) {
                 row.add(ChipColour.NULL);
             }
             this.board.add(row);
        }
        setupPawnsPlayer(ChipColour.WHITE, 6, 9);
    }
    
    private void setupPawnsPlayer(ChipColour player, Integer rowBegin, Integer rowEnd) {
        for (int rowIndex = rowBegin; rowIndex <= rowEnd; rowIndex++) {
            List<ChipColour> row = new ArrayList<ChipColour>();
 
            for (int colIndex = 0  ; colIndex < COLUMNS_NUMBER ;colIndex++) {
            	ChipColour cell;
                if (hasPawnAtInit(rowIndex, colIndex)) {
                    cell = player;
                }else{
                	cell = ChipColour.NULL;
                }
                row.add(cell);
            }
            this.board.add(row);
        }
        
        
    }
    
    private boolean hasPawnAtInit(int row, int col) {
        Boolean response = false;

        Integer rowModule = row % 2;
        Integer colModule = col % 2;

        if (rowModule == 0) {
            if (colModule != 0) {
                response = true;
            }
        } else {
            if (colModule == 0) {
               response = true;
            }
        }

        return response;
    }


    @Override
    public void play(ChipColour colour,int sourceColumn, int sourceRow, int destColumn, int destRow) {
        if (sourceColumn > getColumnsNumber() - 1 && sourceRow > getRowsNumber() -1 ) {
            throw new GameException(OUTSIDE_OF_BOARD_ERROR);
        }
        List<ChipColour> col = board.get(sourceColumn);
        if (col.size() >= ROWS_NUMBER) {
            throw new GameException(OUTSIDE_OF_BOARD_ERROR);
        }
        col.add(colour);
    }

    @Override
    public ChipColour getCell(int i, int j) {
        if (i < 0 || i >= getColumnsNumber()) {
            return null;
        }
        List<ChipColour> column = board.get(i);
        return j < column.size() && j >= 0 ? column.get(j) : null;
    }

    @Override
    public int getColumnsNumber() {
        return COLUMNS_NUMBER;
    }

    @Override
    public int getRowsNumber() {
        return ROWS_NUMBER;
    }

    @Override
    public ChipColour getWinner() {

        for (int i = 0; i < getColumnsNumber(); i++) {
            for (int j = 0; j < getRowsNumber(); j++) {
                if (getCell(i, j) != null) {
                    if (isWinningPosition(i, j)) {
                        return getCell(i, j);
                    }
                }
            }
        }
        return null;
    }

    private boolean isWinningPosition(int i, int j) {
        if(getCell(i,j) == null) {
            return false;
        }
        return isVerticalWinningPosition(i, j) ||
                isDiagonalWinningPosition(i, j) ||
                isHorizontalWinningPosition(i, j);
    }

    private boolean isDiagonalWinningPosition(int i, int j) {
        return isDiagonalWinningPosition(i, j, true) ||
                isDiagonalWinningPosition(i, j, false);
    }

    private boolean isHorizontalWinningPosition(int i, int j) {
        if (isFirstChipTooRightToWin(i)) {
            return false;
        }

        ChipColour cell = getCell(i, j);
        boolean result = true;
        for (int k = 1; result == true && k < NUMBER_OF_CHIP_TO_ALIGN ; k++) {
            result = result && cell == getCell(i + k, j);
        }
        return result;
    }

    private boolean isDiagonalWinningPosition(int i, int j, boolean direction) {
        if (isFisrtChipTooHighToWin(j)) {
            return false;
        }

        if((!direction && i <= NUMBER_OF_CHIP_TO_ALIGN) || isFirstChipTooRightToWin(i)) {
            return false;
        }
        ChipColour cell = getCell(i, j);
        boolean result = true;
        for (int k = 1; result == true && k < NUMBER_OF_CHIP_TO_ALIGN ; k++) {
            int sign = direction ? 1 : -1;
            result = result && cell == getCell(i + k * sign, j + k * sign);
        }
        return result;
    }

    private boolean isFirstChipTooRightToWin(int i) {
        return (i> getColumnsNumber() - NUMBER_OF_CHIP_TO_ALIGN);
    }

    private boolean isVerticalWinningPosition(int i, int j) {
        if (isFisrtChipTooHighToWin(j)) {
            return false;
        }

        ChipColour cell = getCell(i, j);
        boolean result = true;
        for (int k = 1; result == true && k < NUMBER_OF_CHIP_TO_ALIGN ; k++) {
            result = result && cell == getCell(i, j + k);
        }
        return result;

    }

    private boolean isFisrtChipTooHighToWin(int j) {
        return j > getRowsNumber() - NUMBER_OF_CHIP_TO_ALIGN;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = getRowsNumber() - 1; i >= 0; i--) {
            sb.append("|");
            for (int j = 0; j < getColumnsNumber(); j++) {
                if (getCell(j, i) == ChipColour.WHITE) {
                    sb.append("O");
                } else if (getCell(j, i) == ChipColour.BLACK) {
                    sb.append("X");
                } else {
                    sb.append(" ");
                }
                sb.append("|");
            }
            sb.append("\n");
        }
        for (int i = 0; i < getRowsNumber() * 2 + 3; i++) {
            sb.append("-");
        }
        return sb.toString();
    }
}
