package AI.controller.threadController;

import AI.controller.fxmlController.MainMenuController;
import AI.directions.Directions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class BranchThread extends MainMenuController implements Runnable {
    int index;
    Integer moveCount = 1;
    ArrayList<Integer> pathHistory = new ArrayList<>();
    Directions direction;

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

    //needs branching and trail leaving
    public void run() {
        System.out.println("Created a new thread with Direction " + direction);
        System.out.println("index at " + index);
        do {
            moveCount++;
            pathHistory.add(index);
            Directions[] allBranches = getAllAvailable4Bit(index);
            int nulls = (int) Arrays.stream(allBranches).
                    filter(Objects::isNull).count();
            setTrailMark(index);

            if (4 - nulls >= 1) { // needs a different check cuz it doesnt work if it hits deadend
                createChildThread(allBranches);
            }

            index = move(index, direction);

            if (isFinish(index)) {
                System.out.println("Thread found finish line!");
                pathHistory.add(index);
                return;
            }
        } while (canMoveTo(move(index, direction)));
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
