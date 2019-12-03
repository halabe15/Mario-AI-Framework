import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import engine.core.MarioGame;
import engine.core.MarioResult;

import agents.qLearning.Agent;

public class PlayLevel {
  public static void printResults(MarioResult result) {
    System.out.println("****************************************************************");
    System.out.println("Game Status: " + result.getGameStatus().toString() + " Percentage Completion: "
        + result.getCompletionPercentage());
    System.out.println("Lives: " + result.getCurrentLives() + " Coins: " + result.getCurrentCoins()
        + " Remaining Time: " + (int) Math.ceil(result.getRemainingTime() / 1000f));
    System.out.println("Mario State: " + result.getMarioMode() + " (Mushrooms: " + result.getNumCollectedMushrooms()
        + " Fire Flowers: " + result.getNumCollectedFireflower() + ")");
    System.out.println("Total Kills: " + result.getKillsTotal() + " (Stomps: " + result.getKillsByStomp()
        + " Fireballs: " + result.getKillsByFire() + " Shells: " + result.getKillsByShell() + " Falls: "
        + result.getKillsByFall() + ")");
    System.out.println("Bricks: " + result.getNumDestroyedBricks() + " Jumps: " + result.getNumJumps() + " Max X Jump: "
        + result.getMaxXJump() + " Max Air Time: " + result.getMaxJumpAirTime());
    System.out.println("****************************************************************");
  }

  public static String getLevel(String filepath) {
    String content = "";
    try {
      content = new String(Files.readAllBytes(Paths.get(filepath)));
    } catch (IOException e) {
    }
    return content;
  }

  public static void main(String[] args) {
    int count = 0;
    MarioGame game  = new MarioGame();
    while (true) {
      printResults(game.runGame(new agents.qLearning.Agent(), getLevel("levels/original/lvl-3.txt"), 15, 0, true));
      count++;
    }
  }
}