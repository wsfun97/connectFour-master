package com.example.daft.connect4;

import android.util.Log;

/**
 * Created by dixon on 2/12/2016.
 */

public class AI extends Thread {
    private Connect4Game game;
    private int id;
    private Tree<Move> forecasts;
    public static final int PINCH = -1;
    public static final int CHANCE = 2;
    public static final int DRAW = 1;
    public static final int STANDARD_DEPTH = 7;

    public AI(Connect4Game game, int id) {
        this.game = game; //get access to the game like human player
        this.id = id;
        forecasts = new Tree<>();
    }

    public void run() {
        while (forecasts.size() < game.size()) {
            try {
                think();
            } catch(InterruptedException e) {
                observe();
                decide();
            }
        }
        Log.i("AI: ", "So easy. I've already forecast all situations.");
    }

    public void think() throws InterruptedException {
        think(STANDARD_DEPTH);
    }
    public void think(int depth) throws InterruptedException {
        think(forecasts, game, game.width(), depth);
    }
    public void think(Tree<Move> root,
                      Connect4Game game, int col, int depth) throws InterruptedException {
        //escape recursion
        if (col-- < 0) return;
        //clone new game and put a disc
        Connect4Game forecast = game.clone();
        if (!forecast.put(forecast.currentPlayer(), (byte)col))
            return; //the column is full;
        //add new tree node
        Tree<Move> node = new Tree<>();
        int situation = 0;
        switch (forecast.judge()) {
            case Connect4Game.DRAW:
                situation = DRAW;
                break;
            case Connect4Game.NEXT:
                if (depth > 0) {
                    think(node, forecast, forecast.width(), depth - 1);
                }
                break;
            default:
                if (forecast.judge() == id) { //AI wins
                    situation = CHANCE;
                } else { //opponent wins
                    situation = PINCH;
                }
        }
        Move move = new Move(forecast.lastTurn(), situation);
        node.setValue(move);
        root.add(node);
        think(root, forecast, col, depth);
    }

    public void observe() {
        Turn lastTurn = game.lastTurn();
        for (Tree<Move> child: forecasts.children()) {
            if (child.getValue().turn.equals(lastTurn)) {
                forecasts = child;
                return;
            }
        }
    }

    public void decide() {
        Tree<Move> bestMove = new Tree<>();
        int bestScore = Integer.MIN_VALUE;
        for (Tree<Move> child: forecasts.children()) {
            int score = 0;
            for (Move move: child.toList()) {
                score += move.situation;
            }
            if (score > bestScore) {
                bestScore = score;
                bestMove = child;
            }
        }
        game.put((byte)id, bestMove.getValue().turn.getColumn());
    }

    static class Move {
        Turn turn;
        int situation;
        Move(Turn turn, int situation) {
            this.turn = turn;
            this.situation = situation;
        }
    }
}
