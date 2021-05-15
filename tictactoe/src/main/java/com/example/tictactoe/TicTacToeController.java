package com.example.tictactoe;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class TicTacToeController {

    @RequestMapping("/")
    public String index() {
        return "redirect:/tictactoe";
    }

//    @RequestMapping("/error")
//    @ResponseBody
//    public String errorPath() {
//        return "No Mapping Found";
//    }

    @RequestMapping(value = "/tictactoe", method = RequestMethod.GET)
    public String game (HttpSession session, Model model) {
        State state = getSessionPlayer(session);
        if (state == null) {
            state = new State();
            state.startNewGame();
            putSession(session, state);
        }
        model.addAttribute(ApplicationConstant.GAME_STATE, state);

        return ApplicationConstant.GAME;
    }

    @RequestMapping(value = "/tictactoe/reset", method = RequestMethod.GET)
    public String reset(HttpSession session, Model model) {
        State state = new State();
        state.startNewGame();
        putSession(session, state);
        model.addAttribute(ApplicationConstant.GAME_STATE, state);

        return ApplicationConstant.GAME;
    }

    @RequestMapping(value = "/tictactoe/new", method = RequestMethod.GET)
    public String newGame(HttpSession session, Model model) {
        State state = getSessionPlayer(session);
        state.startNewGame();
        model.addAttribute(ApplicationConstant.GAME_STATE, state);

        return ApplicationConstant.GAME;
    }

    @RequestMapping(value = "/tictactoe/mode", method = RequestMethod.GET)
    public String modeSelected(HttpSession session,
                               @RequestParam(value = "mode", required = true) String mode,
                               Model model) {
        State state = getSessionPlayer(session);
        if (mode.equals("player")) {
            state.setMode(State.GameMode.HUMAN_VS_HUMAN);
        } else {
            throw new RuntimeException("Invalid selected game mode:" + mode);
        }
        model.addAttribute(ApplicationConstant.GAME_STATE, state);

        return "redirect:/tictactoe/new";
    }

    @RequestMapping(value = "/tictactoe/move", method = RequestMethod.GET)
    public String playerMove(HttpSession session,
                             @RequestParam(value = "row", required = true) Integer row,
                             @RequestParam(value = "col", required = true) Integer col,
                             Model model) {
        State state = getSessionPlayer(session);
        model.addAttribute(ApplicationConstant.GAME_STATE, state);

        if (!state.getGameStg().equals(State.GameStage.IN_GAME)) {
            return ApplicationConstant.GAME;
        }

        StandingBoard standingBoard = state.getStandingBoard();

        try {
            standingBoard.move(row, col, state.getTurn());
            evaluateBoard(state);

        } catch (Exception e) {

        }

        return ApplicationConstant.GAME;
    }

    public void evaluateBoard(State state) {
        StandingBoard board = state.getStandingBoard();
        // First, check for a draw
        if(board.draw()) {
            state.setGameMsg("It's a draw!");
            state.setGameStg(State.GameStage.POST_GAME);
        }
        else if(board.winner(state.getTurn())) {
            if(state.getTurn().equals(StandingBoard.Mark.O)) {
                state.setGameMsg("O wins!");
            }
            else {
                state.setGameMsg("X wins!");
            }
            state.setGameStg(State.GameStage.POST_GAME);
        }
        else
        {
            if(state.getTurn() == StandingBoard.Mark.X) {
                state.setTurn(StandingBoard.Mark.O);
                state.setTurnMsg("Turn: O");
            }
            else {
                state.setTurn(StandingBoard.Mark.X);
                state.setTurnMsg("Turn: X");
            }
        }
    }

    public void determineMove (State state) {
        StandingBoard.Mark board[][] = state.getStandingBoard().standingBoard;
        StandingBoard.Mark playerMarker = state.getTurn();
        StandingBoard.Mark opponentMarker = playerMarker.equals(StandingBoard.Mark.X) ? StandingBoard.Mark.O : StandingBoard.Mark.X;

        if( board[1][1].equals(StandingBoard.Mark.BLANK)) {
            if((board[0][1].equals(opponentMarker) &&
                    board[2][1].equals(opponentMarker)) ||
                    (board[1][0].equals(opponentMarker) &&
                            board[1][2].equals(opponentMarker)) ||
                    (board[0][0].equals(opponentMarker) &&
                            board[2][2].equals(opponentMarker)) ||
                    (board[0][2].equals(opponentMarker) &&
                            board[2][0].equals(opponentMarker))) {

                try {
                    state.getStandingBoard().move(1, 1, playerMarker );
                    return;
                }
                catch(Exception e) {
                    // Since we already checked, swallow
                }
            }
        }

        for(int i = 0; i < 3; i++) {
            int bCount = 0;
            int oCount = 0;
            for(int j = 0; j < 3; j++) {
                if(board[i][j].equals(opponentMarker)) {
                    ++oCount;
                }
                if(board[i][j].equals(StandingBoard.Mark.BLANK)) {
                    ++bCount;
                }
            }

            // If there were two opponent markers and a blank,
            // move to the blank spot.
            if((oCount == 2) && (bCount == 1)) {
                for(int j = 0; j < 3; j++) {
                    if(board[i][j].equals(StandingBoard.Mark.BLANK)) {
                        try {
                            state.getStandingBoard().move(i, j, playerMarker);
                            return;
                        }
                        catch(Exception e) {
                            // Since we already checked, swallow
                        }
                    }
                }
            }
        }

        // Next, check rows for blockers.
        for(int j = 0; j < 3; j++) {
            int bCount = 0;
            int oCount = 0;
            for(int i = 0; i < 3; i++) {
                if(board[i][j].equals(opponentMarker)) {
                    ++oCount;
                }
                if(board[i][j].equals(StandingBoard.Mark.BLANK)) {
                    ++bCount;
                }
            }

            // If there were two opponent markers and a blank,
            // move to the blank spot.
            if((oCount == 2) && (bCount == 1)) {
                for(int i = 0; i < 3; i++) {
                    if(board[i][j].equals(StandingBoard.Mark.BLANK)) {
                        try {
                            state.getStandingBoard().move(i, j, playerMarker);
                            return;
                        } catch(Exception e) {

                        }
                    }
                }
            }
        }

        // And lastly for blockers, check for diagonals
        int bCount = 0;
        int oCount = 0;
        int r = 0;
        int c = 0;
        for(int i = 0; i < 3; ++i) {
            if(board[r][c].equals(opponentMarker)) {
                ++oCount;
            }
            if(board[r][c].equals(StandingBoard.Mark.BLANK)) {
                ++bCount;
            }
            ++r;
            ++c;
        }
        if((oCount == 2) && (bCount == 1)) {
            r = 0;
            c = 0;
            for(int i = 0; i < 3; ++i) {
                if(board[r][c].equals(StandingBoard.Mark.BLANK)) {
                    try {
                        state.getStandingBoard().move(r, c, playerMarker);
                        return;
                    }
                    catch(Exception e) {
                        // Since we already checked, swallow
                    }
                }
                ++r;
                ++c;
            }
        }
        r = 0;
        c = 2;
        bCount = 0;
        oCount = 0;
        for(int i = 0; i < 3; ++i) {
            if(board[r][c].equals(opponentMarker)) {
                ++oCount;
            }
            if(board[r][c].equals(StandingBoard.Mark.BLANK)) {
                ++bCount;
            }
            ++r;
            --c;
        }
        if((oCount == 2) && (bCount == 1)) {
            r = 0;
            c = 2;
            for(int i = 0; i < 3; ++i) {
                if(board[r][c].equals(StandingBoard.Mark.BLANK)) {
                    try {
                        state.getStandingBoard().move(r, c, playerMarker);
                        return;
                    } catch(Exception e) {

                    }
                }
                ++r;
                --c;
            }
        }

        // If still available, take the center; always a good move.
        if(board[1][1].equals(StandingBoard.Mark.BLANK)) {
            try {
                state.getStandingBoard().move(1, 1, playerMarker );
                return;
            }
            catch(Exception e) {
                // Since we already checked, swallow
            }
        }

        // human to make a block move.

        // Keep generating random positions until a blank spot is found
        boolean found = false;
        Random random = new Random();
        while(!found) {
            r = random.nextInt(3);
            c = random.nextInt(3);
            if(board[r][c].equals(StandingBoard.Mark.BLANK)) {
                try {
                    state.getStandingBoard().move(r, c, playerMarker );
                    found = true;
                } catch(Exception e) {

                }
            }
        }
    }

    private State getSessionPlayer(HttpSession session) {
        State state = (State)session.getAttribute(ApplicationConstant.GAME_STATE);
        if (state == null) {
            state = new State();
            putSession(session, state);
        }
        return state;
    }

    private void putSession(HttpSession session, State state) {
        session.setAttribute(ApplicationConstant.GAME_STATE, state);
    }
}
