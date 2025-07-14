package com.route.xogame;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class XoGameActivity extends AppCompatActivity {
    TextView timerTextView;
    TextView playerTurnTextView;
    private int seconds=0;
    String currentPlayerSymbol="x";
    String[] boardSymbols = {"","","","","","","","",""};
    private Handler handler;
    private Runnable stopWatchRunnable;
    ConstraintLayout boardConstraintLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_xo_game);
        setViews();
        startStopWatch();
    }


    private void setViews() {
        timerTextView= findViewById(R.id.timerTv);
        boardConstraintLayout = findViewById(R.id.boardContainer);
        playerTurnTextView = findViewById(R.id.playerTurnTv);
        playerTurnTextView.setText("Player X's turn");
    }
    private void startStopWatch() {
        if (handler == null){
            handler = new Handler();
        }else{
            handler.removeCallbacks(stopWatchRunnable);
        }
        stopWatchRunnable= new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,1000);
                seconds++;
                calculateNewTime();
                if (checkWinner()){
                    handler.removeCallbacks(this);
                }
            }
        };
        handler.post(stopWatchRunnable);
    }

    private void calculateNewTime() {
        int minutes = seconds/60;
        timerTextView.setText(String.format("%02d",minutes)+ ":"+ String.format("%02d",seconds%60)); // to make minutes and seconds occupy two digits
    }

    public void onItemClickListener (View view){
        if (!(view instanceof ImageView)){
            return;
        } // to make sure that the view pressed is an image placeholder inside the constrains layout
        if (!boardSymbols[Integer.parseInt(view.getTag().toString())].isEmpty()){
            return;
        } // check if this place was already pressed
        if (currentPlayerSymbol.equals("x")){
            ((ImageView) view).setImageResource(R.drawable.x_icon);
            boardSymbols[Integer.parseInt(view.getTag().toString())]="x";
        } // fill the space where player x pressed
        else{
            ((ImageView) view).setImageResource(R.drawable.o_icon);
            boardSymbols[Integer.parseInt(view.getTag().toString())]="o";
        } // fill the space where player o pressed
        boolean winnerFound = checkWinner();
        if (!winnerFound){
            if (currentPlayerSymbol.equals("x")){
                playerTurnTextView.setText("Player O's turn");
                currentPlayerSymbol="o";
            } // switch turn to player o
            else {
                playerTurnTextView.setText("Player X's turn");
                currentPlayerSymbol="x";
            } // switch turn to player x
            if (!allElementsFilled(boardSymbols)){
                return;
            } // check if the board is full and no one wins
        } // switch the player turn in case there's no winner
        else{
            Toast.makeText(XoGameActivity.this,"The player "+ currentPlayerSymbol+" Won!!",Toast.LENGTH_LONG).show();
        }
        stopMatch();
    }

    private void stopMatch() {
        for (int i=0 ; i<9 ; i++){
            boardSymbols[i]=""; // empty the array containing game steps
        }
        for (int i=0;i<  boardConstraintLayout.getChildCount();i++){
            View view = boardConstraintLayout.getChildAt(i) ;
            if (view instanceof ImageView){
                ((ImageView) view).setImageDrawable(null);
            }
        } // clear board
        seconds=0;
        startStopWatch(); // clear time and re-initiate the game
    }

    private boolean checkWinner() {
        if (seconds==0){
            return false;
        } // break if the game just started
        for (int i=0; i<9; i+=3){
            if (boardSymbols[i].equals(boardSymbols[i+1]) && boardSymbols[i+2].equals(boardSymbols[i+1]) && boardSymbols[i].equals(currentPlayerSymbol)){
                return true;
            }
        } // check for rows
        for (int i=0; i<3;i++){
            if (boardSymbols[i].equals(boardSymbols[i+3]) && boardSymbols[i+3].equals(boardSymbols[i+6]) && boardSymbols[i].equals(currentPlayerSymbol)){
                return true;
            }
        } // check for columns
        // check for diagonals
        if ((boardSymbols[0].equals(boardSymbols[4]) && boardSymbols[4].equals(boardSymbols[8] )
        || boardSymbols[2].equals(boardSymbols[4]) && boardSymbols[4].equals(boardSymbols[6])) && boardSymbols[4].equals(currentPlayerSymbol)){
            return true;
        }
        return false;
    }
    public boolean allElementsFilled(String[] array) {
        for (String s : array) {
            if (s == null || s.trim().isEmpty()) {
                return false; // Found an empty or null string
            }
        }
        return true; // All elements are non-empty
    }
}
