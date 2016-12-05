package com.example.daft.connect4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private List<Byte> players;
    private Connect4Game game;
    private List<ImageView> columns;
    private List<LinearLayout> colContainers;
    private TextView[] pv;
    private ImageView disc;
    private MediaPlayer mediaPlayer;
    public static boolean isSoundOff=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Log.i("Game", "Initializing");

        Log.i("Game", "Creating player id");
        players = new ArrayList<>();
        //random id, make sure 2 are different and not 0, 1, 41
        players.add((byte) 11);
        players.add((byte) 22);

        Log.i("Game", "Creating game");
        game = new Connect4Game(7, 6, players);

        columns = new ArrayList<>();
        columns.add((ImageView) findViewById(R.id.col0));
        columns.add((ImageView) findViewById(R.id.col1));
        columns.add((ImageView) findViewById(R.id.col2));
        columns.add((ImageView) findViewById(R.id.col3));
        columns.add((ImageView) findViewById(R.id.col4));
        columns.add((ImageView) findViewById(R.id.col5));
        columns.add((ImageView) findViewById(R.id.col6));

        colContainers = new ArrayList<>();
        colContainers.add((LinearLayout) findViewById(R.id.contain0));
        colContainers.add((LinearLayout) findViewById(R.id.contain1));
        colContainers.add((LinearLayout) findViewById(R.id.contain2));
        colContainers.add((LinearLayout) findViewById(R.id.contain3));
        colContainers.add((LinearLayout) findViewById(R.id.contain4));
        colContainers.add((LinearLayout) findViewById(R.id.contain5));
        colContainers.add((LinearLayout) findViewById(R.id.contain6));

        pv = new TextView[] { (TextView) findViewById(R.id.p1),
                (TextView) findViewById(R.id.p2) };

        Log.i("Game", "Preparing listener");
        for (ImageView col: columns) {
            col.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    byte col = (byte) columns.indexOf(view);
                    if (game.put(game.currentPlayer(), col)) {
                        putDisc(col);
                        byte result = game.judge();
                        switch(result) {
                            case Connect4Game.DRAW:
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("Draw")
                                        .setMessage("Play again")
                                        .setNeutralButton("Restart", null)
                                        .show();
                                recreate();
                                break;
                            case Connect4Game.NEXT:
                                break;
                            default: //someone wins
                                int winner = players.indexOf(result);
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("Congraduation")
                                        .setMessage(String.format("Player %d Wins", winner+1))
                                        .setNeutralButton("Restart", null)
                                        .show();
                                recreate();
                        }
                    }
                }
            });
        }
        Log.i("Game", "Start");
    }


    public void putDisc(byte col) {
        playSound();
        Log.i("Game UI", "Putting disc");
        int currentPlayerIndex = players.indexOf(game.currentPlayer());
        pv[currentPlayerIndex].setVisibility(View.VISIBLE);
        pv[(currentPlayerIndex+1) % pv.length].setVisibility(View.INVISIBLE);

        disc = new ImageView(this);
        disc.setAdjustViewBounds(true);
        disc.setLayoutParams(new LinearLayout.LayoutParams(
                columns.get(col).getWidth(),
                LinearLayout.LayoutParams.WRAP_CONTENT));
        Animation am = new TranslateAnimation(0.0f, 0.0f, -800.0f, 0.0f);
        am.setDuration(1000);
        if (currentPlayerIndex == 1) //this disc is put in last turn
            disc.setImageResource(R.drawable.disc1_transp);
        else disc.setImageResource(R.drawable.disc2_transp);

        disc.startAnimation(am);
        colContainers.get(col).addView(disc, 0);
    }

    public void playSound(){
        if(mediaPlayer==null) {
            mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.coin);
            if(isSoundOff){
                mediaPlayer.setVolume(0.0f,0.0f);
            }
        }

            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
            mediaPlayer.start();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(GameActivity.this);
        quitDialog.setTitle("Are you sure to quit the game?");

        quitDialog.setPositiveButton("YES", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                finish();
            }});

        quitDialog.setNegativeButton("NO", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                onResume();
            }});

        quitDialog.show();
    }
}
