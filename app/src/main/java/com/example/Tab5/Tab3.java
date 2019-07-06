package com.example.Tab5;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Timer;
import java.util.TimerTask;


public class Tab3 extends Fragment implements View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {

    private static int mMineCount = 22;
    private static int mTick = -1;
    private static TextView mMineCounter;
    private static TextView mTimerView;
    private static Timer mTimer = new Timer();
    private static TimerTask mTimerTask;
    private static View mRootView;
    private static int mCellSize=80;
    private static float mMineRatio = 0.1f;

    private static Tab3 sTab3;

    private enum GAME_MODE {NOT_STARTED, GAMING, ENDED};
    private static GAME_MODE mGameMode = GAME_MODE.NOT_STARTED;

    private static boolean mInitialized = false;
    public enum DIFFICULTY {MEDIUM}
    public enum CELL_SIZE {MEDIUM}

    View rootView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sTab3 = this;
        getChildFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
    }


    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.mines_main, container, false);
            ImageButton newgame = (ImageButton) v.findViewById(R.id.btnStartNewGame);
            ImageButton setmine = (ImageButton) v.findViewById(R.id.btnSetMarkerMode);

            newgame.setOnClickListener(new ImageButton.OnClickListener(){
                @Override
                public void onClick(View view){
                    startNewGame(view);
                }
            });
            setmine.setOnClickListener(new ImageButton.OnClickListener(){
                @Override
                public void onClick(View view){
                    setMarkerMode(view);
                }
            });

            v.getViewTreeObserver().addOnGlobalLayoutListener(sTab3);
            return v;
        }
    }

    public static void setMarkerMode(View v) {
        Drawable d;
        MineButton.BombMode bombMode = MineButton.toggleBombMode();
        Activity activity = (Activity) mRootView.getContext();
        if (bombMode == MineButton.BombMode.MARK)
            d = activity.getApplication().getResources().getDrawable(R.drawable.marker);
        else
            d = activity.getApplication().getResources().getDrawable(R.drawable.bomb);

        ((ImageButton)v).setImageDrawable(d);
    }

/*
    public void openSetup(View v) {

        DIFFICULTY df = mMineRatio <= 0.20f ? DIFFICULTY.MEDIUM;
        CELL_SIZE sz = mCellSize <= 80 ? CELL_SIZE.MEDIUM;

        //SetupDialog dlg = new SetupDialog(getContext(), df, sz);
        //dlg.setTitle(v.getResources().getString(R.string.setup));
        //dlg.setOwnerActivity(getActivity());
        //dlg.show();
    }
*/
    /** Called when the user clicks the Send button */

    public static void startNewGame(View view) {
        mGameMode = GAME_MODE.GAMING;

        // Do something in response to button
        if (mTimer != null) mTimer.cancel();
        if (mTimerTask != null) mTimerTask.cancel();

        MineButton.initAllMines(mMineCount);

        mTick = 0;

        startTimer();

//        setMineCounter(mMineCount);
        setTimer();
    }

    private static void startTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Activity activity = (Activity) mRootView.getContext();
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        mTick++;
                        mTimerView.setText(String.format("%03d", mTick));
                    }
                });
            }

        };
        mTimer.scheduleAtFixedRate (mTimerTask, 1000, 1000);
    }

    public void stopGame() {
        stopTimer();

        mGameMode = GAME_MODE.ENDED;

        if (MineButton.getFoundCount() == mMineCount) {  // found all
            AlertDialog.Builder ad = new AlertDialog.Builder(mRootView.getContext());
            ad.setTitle(mRootView.getContext().getResources().getString(R.string.result_title));
            ad.setPositiveButton("OK", null);
            ad.setCancelable(false);
            ad.setMessage(String.format("%d " + mRootView.getContext().getResources().getString(R.string.sec), mTick));
            ad.create().show();
        }
    }

    private static void setTimer() {
        mTimerView.setText(String.format("%03d", mTick));
    }

//    private static void setMineCounter(int count) {
//        mMineCounter.setText(String.format("%03d", count));
//    }

    private void stopTimer() {
        if (mTimerTask != null) mTimerTask.cancel();
        mTimer.cancel();
        mTimer.purge();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopTimer();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        stopTimer();
        mInitialized = false;
    }


    @Override
    public void onPause() {

        super.onPause();
        stopTimer();
    }


    @Override
    public void onResume() {

        super.onResume();
        if (mTick >= 0)
            startTimer();
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onClick(View v) {

        if (mGameMode == GAME_MODE.NOT_STARTED)  // if not started, start new game
            startNewGame(v);

        if (mGameMode != GAME_MODE.GAMING) // if not gaming, just ignore
            return;

        MineButton btn = (MineButton)v;
        boolean bRet = btn.clickMine();  // MineButton class handles click


        //setMineCounter(mMineCount - btn.getMarkedCount()); // display remaining mines count
        if (!bRet)   // if game ends with success or failure, stop the game
            stopGame();

    }
/*
    public void setup(DIFFICULTY df, CELL_SIZE sz) {

        mCellSize = sz == CELL_SIZE.SMALL ? 70 : sz == CELL_SIZE.MEDIUM ? 80 : 90;
        mMineRatio = (df == DIFFICULTY.EASY ? 0.15f : df == DIFFICULTY.MEDIUM ? 0.20f : 0.25f);

        initGame();
    }
*/

    private void initGame() {

        LinearLayout myLayout = mRootView.findViewById(R.id.panel);
        int w = myLayout.getWidth();
        int h = myLayout.getHeight();

        int cols = w / mCellSize;
        int rows = (h - 40) / mCellSize;
        mMineCount = (int)((cols * rows) * mMineRatio);

        myLayout.removeAllViews();

        MineButton.resetAllMines();

        for(int i=0; i<rows; i++) {
            LinearLayout layout = new LinearLayout(mRootView.getContext());
            layout.setPadding(0, 0, 0, 0);
            layout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            myLayout.addView(layout);
            for(int j=0; j<cols; j++) {
                MineButton myButton = new MineButton(mRootView.getContext(), i, j);
                myButton.setLayoutParams(new RelativeLayout.LayoutParams(mCellSize, mCellSize));
                myButton.setPadding(0, 0, 0, 0);
                myButton.setOnClickListener(this);
                layout.addView(myButton);
            }

        }
        MineButton.initAllMines(mMineCount);

    }

    @Override
    public void onGlobalLayout() {

        if (mInitialized) return;

        mInitialized = true;

        mRootView = getView().findViewById(R.id.mines_main);
        mMineCounter = mRootView.findViewById(R.id.mine_counter);
        //Typeface tf = Typeface.createFromAsset(mRootView.getContext().getAssets(),);
        //mMineCounter.setTypeface(tf);
        mTimerView = mRootView.findViewById(R.id.timer);
        //mTimerView.setTypeface(tf);

        initGame();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3, container, false);
        return rootView;
    }
}

