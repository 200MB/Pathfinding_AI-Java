package AI.controller.threadController;

import AI.controller.fxmlController.MainMenuController;
import AI.directions.Directions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class BranchThread extends MainMenuController implements Runnable {
    int index;
    int prevIndex;
    Integer moveCount = 1;
    ArrayList<Integer> pathHistory = new ArrayList<>();
    Directions direction;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";


    public void setDirection(Directions direction) {
        this.direction = direction;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setMoveCount(Integer moveCount) {
        this.moveCount = moveCount;
    }

    public void setPathHistory(ArrayList<Integer> pathHistory) {
        this.pathHistory = pathHistory;
    }


    public void run() {
        System.out.println("Created " + Thread.currentThread().getName() + " with Direction " + direction);
        System.out.println("index at " + index);
        do {
            moveCount++;
            if (isFinish(index)) {
                System.out.println(ANSI_YELLOW + Thread.currentThread().getName() + " found finish line!" + ANSI_RESET + "\n");
                System.out.println(pathHistory);
                pathHistory.add(index);
                paths.add(this.pathHistory);
                displayShortest();
                return;
            }
            pathHistory.add(index);
            Directions[] allBranches = getAllAvailable4Bit(index);
            setTrailMark(index);
            int nulls = (int) Arrays.stream(allBranches).
                    filter(Objects::isNull).count();

            if (4 - nulls >= 1) {
                createChildThread(allBranches);
            }
            prevIndex = index;
            index = move(index, direction);

        } while (canMoveTo(index,prevIndex,direction));
        System.out.println(ANSI_PURPLE + Thread.currentThread().getName() + " ENDED AT " + index + "\n" + ANSI_RESET);
    }

    private void createChildThread(Directions[] allBranches) {
        for (Directions direction : allBranches) {
            if (direction == null || direction == this.direction) continue;
            BranchThread childBranch = new BranchThread();
            childBranch.setDirection(direction);
            childBranch.setIndex(move(index, direction));
            childBranch.setMoveCount(moveCount);
            childBranch.setPathHistory(pathHistory);
            Thread childThread = new Thread(childBranch);
            childThread.start();
        }
    }

}
