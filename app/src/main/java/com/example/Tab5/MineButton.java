package com.example.Tab5;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.appcompat.widget.AppCompatImageButton;

import java.util.Random;
import java.util.Vector;

public class MineButton extends AppCompatImageButton {

    private static Vector<MineButton> sBtnV = new Vector<MineButton>();
    public enum BombMode {OPEN, MARK};
    private static BombMode sBombMode = BombMode.OPEN;

    private static int sCellCount;
    private static int sMineCount = 0;
    private static int sMarkedCount;
    private static int sFoundMineCount;
    private static int sOpenedCellCount;

    public enum State { OPENED, MARKED, CLOSED, WRONG_MARKED };

    int mCol, mRow;
    State mState;
    int mValue;

    private static int sCols=0, sRows=0;

    private static boolean sInitialized = false;
    private static Drawable sDs[] = new Drawable[8];
    private static Drawable sMarker;
    private static Drawable sBomb;
    private static Drawable sBtn;
    private static Drawable sBombX;

    public MineButton(Context context, int row, int col) {
        // TODO Auto-generated constructor stub
        this(context, row, col, State.CLOSED, 0);
    }

    private MineButton(Context context, int row, int col, State state, int value) {
        super(context);

        mCol = col;
        mRow = row;
        mState = state;
        mValue = value;
        sBtnV.add(this);

        if (col+1 > sCols)
            sCols = col+1;
        if (row+1 > sRows)
            sRows = row+1;

        if (!sInitialized) {
            sDs[0] = context.getResources().getDrawable(R.drawable.d0);
            sDs[1] = context.getResources().getDrawable(R.drawable.d1);
            sDs[2] = context.getResources().getDrawable(R.drawable.d2);
            sDs[3] = context.getResources().getDrawable(R.drawable.d3);
            sDs[4] = context.getResources().getDrawable(R.drawable.d4);
            sDs[5] = context.getResources().getDrawable(R.drawable.d5);
            sDs[6] = context.getResources().getDrawable(R.drawable.d6);
            sDs[7] = context.getResources().getDrawable(R.drawable.d7);
            sMarker = context.getResources().getDrawable(R.drawable.marker);
            sBomb = context.getResources().getDrawable(R.drawable.bomb);
            sBombX = context.getResources().getDrawable(R.drawable.bombx);
            sBtn = context.getResources().getDrawable(R.drawable.db);
            sInitialized = true;
        }
    }

    public static void initAllMines(int totMineCnt) {

        sMineCount = totMineCnt;

        sMarkedCount = sOpenedCellCount = sFoundMineCount = 0;

        sCellCount = sBtnV.size();

        for(int i=0; i<sBtnV.size(); i++)
            sBtnV.get(i).init();

        Random r = new Random();
        int mineCnt = 0;
        int allCnt = sBtnV.size();
        do {
            int i = Math.abs((int)(r.nextLong() % allCnt));
            MineButton btn = sBtnV.get(i);
            if (btn.getValue() == 0) {
                btn.setBomb();
                mineCnt++;
            }
        } while(mineCnt < totMineCnt);

        for(int i=0; i<sRows; i++) {
            for(int j=0; j<sCols; j++) {
                int idx = i*sCols+j;
                MineButton btn = sBtnV.get(idx);
                if (btn.getValue() == 0) { // not bomb
                    int mines = 0;
                    // count neighbor cells if bomb
                    for(int k=-1; k<=1; k++) {
                        for(int l=-1; l<=1; l++) {
                            int row = i+k;
                            int col = j+l;
                            if (row>=0 && row<sRows && col>=0 && col<sCols && !(k==0&&l==0)) {
                                int idx1 = row*sCols+col;
                                if (sBtnV.get(idx1).isBomb())
                                    mines++;
                            }
                        }
                    }
                    btn.setValue(mines);
                }
                btn.setImageDrawable(btn.getContext().getResources().getDrawable(R.drawable.db));
            }

        }
    }

    private void init() {
        mValue = 0;
        mState = State.CLOSED;
        setImageDrawable(getContext().getResources().getDrawable(R.drawable.db));
    }

    public static BombMode toggleBombMode() {
        if (sBombMode == BombMode.MARK)
            sBombMode = BombMode.OPEN;
        else
            sBombMode = BombMode.MARK;

        return sBombMode;
    }

    private int getValue() {
        return mValue;
    }

    private boolean isBomb() {
        return mValue == -1;
    }

    private boolean isNull() {
        return mValue == 0;
    }

    private boolean isOpened() {
        return mState.equals(State.OPENED);
    }

    private boolean isMarked() {
        return mState.equals(State.MARKED);
    }

    private boolean isClosed() {
        return mState.equals(State.CLOSED);
    }

    private void setBomb() {
        mValue = -1;
    }

    private void setValue(int value) {
        this.mValue = value;
    }

    private int getPos() {
        return mRow * sCols + mCol;
    }

    private void setState(State state) {

        if (state.equals(State.MARKED)) {
            if (mState.equals(MineButton.State.MARKED)) {
                mState = State.CLOSED;
                setImageDrawable(sBtn);
                sMarkedCount--;
                if (isBomb()) {
                    sFoundMineCount--;
                }
            } else {
                mState = state;
                setImageDrawable(sMarker);
                sMarkedCount++;
                if (isBomb()) {
                    sFoundMineCount++;
                }
            }
        } else if (state.equals(State.WRONG_MARKED)) {
            mState = state;
            setImageDrawable(sBombX);
        } else { // open
            mState = state;
            setImageDrawable(sDs[mValue]);
            sOpenedCellCount++;
        }

    }

    private State getState() {
        return mState;
    }

    private void bombAllMines() {
        for(int i=0; i<sBtnV.size(); i++) {
            MineButton btn = sBtnV.get(i);
            if (btn.isBomb() && btn.isClosed())
                btn.setImageDrawable(sBomb);
        }
        return;

    }

    private void openNullCells() {
        setState(MineButton.State.OPENED);
        for(int k=-1; k<=1; k++) {
            for(int l=-1; l<=1; l++) {
                int row1 = mRow+k;
                int col1 = mCol+l;
                if (row1>=0 && row1<sRows && col1>=0 && col1<sCols && !(k==0&&l==0)) {
                    int idx = row1*sCols+col1;
                    MineButton btn = sBtnV.get(idx);
                    if (!(btn.getState().equals(State.OPENED))) {
                        if (btn.isNull())
                            btn.openNullCells();
                        else if (btn.getValue()>0) {
                            btn.setState(State.OPENED);
                        }
                    }
                }
            }
        }
    }

    private boolean openNearCells() {
        for(int k=-1; k<=1; k++) {
            for(int l=-1; l<=1; l++) {
                int row1 = mRow+k;
                int col1 = mCol+l;
                if (row1>=0 && row1<sRows && col1>=0 && col1<sCols && !(k==0&&l==0)) {
                    int idx = row1*sCols+col1;
                    MineButton btn = sBtnV.get(idx);
                    if (!btn.isBomb() && btn.isMarked()) {
                        btn.setState(State.WRONG_MARKED);
                        bombAllMines();
                        return false;
                    }
                    if (btn.isClosed()) {
                        if (btn.isBomb()) {
                            bombAllMines();
                            return false;
                        } else {
                            btn.setState(State.OPENED);
                            if (btn.isNull()) // �� ���̶��
                                if (!btn.openNearCells()) // �� �ֺ����� �� �����.
                                    return false;
                        }
                    }

                }
            }
        }

        return true;
    }

    private void openRemainingCells() {
        for(int i=0; i<sBtnV.size(); i++) {
            if (sBtnV.get(i).isClosed())
                sBtnV.get(i).setState(State.OPENED);
        }
    }

    private int getMarkedCellCount() {
        int count = 0;
        for(int k=-1; k<=1; k++) {
            for(int l=-1; l<=1; l++) {
                int row1 = mRow+k;
                int col1 = mCol+l;
                if (row1>=0 && row1<sRows && col1>=0 && col1<sCols && !(k==0&&l==0)) {
                    int idx = row1*sCols+col1;
                    MineButton btn = sBtnV.get(idx);
                    if (btn.getState().equals(State.MARKED))
                        count++;
                }
            }
        }
        return count;
    }

    // return false if game ended
    public boolean clickMine() {
        if (sBombMode == BombMode.OPEN) { // open mode
            if (isOpened() || isMarked()) return true;  // return if already opened or marked
            if (isBomb()) {  // bomb all mines if bomb opened
                bombAllMines();
                return false;
            }
            if (isNull()) {  // if null cell, open recursively all near null cells
                openNullCells();
            }
            else {  // if not null, open the cell
                setState(State.OPENED);
            }
        } else { // mark mode
            if (isOpened()) { // if mark already opened and nearhear bombs are marked, open remaining near cells
                if (!isNull()) {
                    if (getValue() == getMarkedCellCount()) {
                        if (!openNearCells())
                            return false;
                    }
                }
            } else {
                setState(State.MARKED);
            }
        }
        // end if remaining mine count equals remaining unopened cell count
        if (sMineCount - sFoundMineCount == sCellCount - (sOpenedCellCount + sMarkedCount)) {
            for(int i=0; i<sBtnV.size(); i++) {
                MineButton btn1 = sBtnV.get(i);
                if (btn1.isBomb() && btn1.isClosed()) {
                    btn1.setState(MineButton.State.MARKED);
                }
            }
        }
        if (getFoundCount() == sMineCount) {  // found all
            openRemainingCells();
            return false;
        }

        return true;
    }

    public static int getMarkedCount() {
        return sMarkedCount;
    }

    public static int getFoundCount() {
        return sFoundMineCount;
    }

    public static int getOpenedCellCount() {
        return sOpenedCellCount;
    }

    public static int getCellCount() {
        return sCellCount;
    }

    public static void resetAllMines() {
        sBtnV.clear();
        sCols = 0;
        sRows = 0;
    }
}
