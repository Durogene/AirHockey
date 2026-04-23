package tp.game;

public class State {

    private int scorePlayer1;
    private int scorePlayer2;
    private final int maxScore = 12;
    private boolean gameOver;

    public State() {
        this.scorePlayer1 = 0;
        this.scorePlayer2 = 0;
        this.gameOver = false;
    }

    public void player1Scores() {
        if (!gameOver) {
            scorePlayer1++;
            checkWinner();
        }
    }

    public void player2Scores() {
        if (!gameOver) {
            scorePlayer2++;
            checkWinner();
        }
    }

    public void player1OwnGoal() {
        if (!gameOver) {
            scorePlayer1 = Math.max(0, scorePlayer1 - 1);
            //scorePlayer2++;
            checkWinner();
        }
    }

    public void player2OwnGoal() {
        if (!gameOver) {
            scorePlayer2 = Math.max(0, scorePlayer2 - 1);
            //scorePlayer1++;
            checkWinner();
        }
    }

    private void checkWinner() {
        if (scorePlayer1 >= maxScore || scorePlayer2 >= maxScore) {
            gameOver = true;
        }
    }

    public int getScorePlayer1() {
        return scorePlayer1;
    }

    public int getScorePlayer2() {
        return scorePlayer2;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getWinnerText() {
        if (scorePlayer1 >= maxScore) {
            return "Joueur 1 gagne !";
        }
        if (scorePlayer2 >= maxScore) {
            return "Joueur 2 gagne !";
        }
        return "";

    }

    public void reset() {
        scorePlayer1 = 0;
        scorePlayer2 = 0;
        gameOver = false;
    }

}