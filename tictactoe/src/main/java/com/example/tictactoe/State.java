package com.example.tictactoe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
@Setter
@Getter
public class State {
    public enum GameMode {
        HUMAN_VS_HUMAN
    }

    public enum GameStage {
        MODE_SELECTION,
        PLAYER_IDENTIFICATION,
        IN_GAME,
        POST_GAME
    }

    private String xPlayer;
    private String oPlayer;
    private String gameMsg;
    private String turnMsg;
    private StandingBoard.Mark turn;
    private GameMode mode;
    private GameStage gameStg;
    private StandingBoard standingBoard;

    private static final Logger log = LoggerFactory.getLogger(State.class);

    public State()
    {
        standingBoard = new StandingBoard();

        reset();
    }

    public void reset()
    {
        setXPlayer("X Player");
        setOPlayer("O Player");
        setGameMsg("");
        setTurn(StandingBoard.Mark.X);
        setTurnMsg("Turn: X");
        setMode(mode.HUMAN_VS_HUMAN);
        setGameStg(GameStage.MODE_SELECTION);
        standingBoard.clear();
    }

    public void startNewGame()
    {
        standingBoard.clear();
        setGameMsg("");
        setTurnMsg("Turn: X");
        setTurn(StandingBoard.Mark.X);
        setGameStg(GameStage.IN_GAME);
    }

    @Override
    public String toString() {
        return "GameState [xPlayer=" + xPlayer + ", oPlayer=" + oPlayer + ", gameMsg=" + gameMsg
                + ", turnMsg=" + turnMsg + ", turn=" + turn + ", gameMode=" + mode + ", gameStage="
                + gameStg + ", board=" + standingBoard + "]";
    }
}
