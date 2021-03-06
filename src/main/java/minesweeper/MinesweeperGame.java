package minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private final GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countClosedTiles = SIDE * SIDE;
    private int countMinesOnField;
    private int countFlags;
    private boolean isGameStopped;
    private int score;


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x, y, "");
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.LIGHTBLUE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.getY() - 1; y <= gameObject.getY() + 1; y++) {
            for (int x = gameObject.getX() - 1; x <= gameObject.getX() + 1; x++) {
                if ((y < 0 || y >= SIDE) || (x < 0 || x >= SIDE) || (gameField[y][x] == gameObject)) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        for (GameObject[] cell : gameField) {
            for (GameObject gameObject : cell) {
                if (!gameObject.isMine()) {
                    List<GameObject> neighbors = getNeighbors(gameObject);
                    for (GameObject neighbor : neighbors) {
                        if (neighbor.isMine()) {
                            gameObject.setCountMineNeighbors(gameObject.getCountMineNeighbors() + 1);
                        }
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        if (gameField[y][x].isOpen()) return;
        if (gameField[y][x].isFlag()) return;
        if (isGameStopped) return;

        if (gameField[y][x].isMine()) {
            setCellValue(x, y, MINE);
            gameField[y][x].setOpen(true);
            countClosedTiles--;
            setCellColor(x, y, Color.ALICEBLUE);
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        } else {
            setCellNumber(x, y, gameField[y][x].getCountMineNeighbors());
            gameField[y][x].setOpen(true);
            countClosedTiles--;
            score += 5;
            setScore(score);
            if (countClosedTiles == countMinesOnField) {
                win();
            }
            setCellColor(x, y, Color.ALICEBLUE);
            if (gameField[y][x].getCountMineNeighbors() == 0) {
                setCellValue(x, y, "");
                for (GameObject gameobject : getNeighbors(gameField[y][x])) {
                    if (!gameobject.isOpen()) openTile(gameobject.getX(), gameobject.getY());

                }
            }
        }
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
            return;
        }
        openTile(x, y);
    }

    private void markTile(int x, int y) {
        if (isGameStopped) return;
        if (gameField[y][x].isOpen()) return;
        if (countFlags == 0 && !gameField[y][x].isFlag()) return;
        if (!gameField[y][x].isFlag()) {
            gameField[y][x].setFlag(true);
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.LIGHTBLUE);
        } else {
            gameField[y][x].setFlag(false);
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.BLUE);
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BEIGE, "???????? ????????????????!", Color.BLACK, 20);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BEIGE, "?????????????????????? ?? ??????????????!", Color.BLACK, 20);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }

}