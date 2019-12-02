package agents.qLearning;

import java.util.ArrayList;
import java.util.Random;

public class MarioRandom {
  public static Random rnd = new Random();
  public static ArrayList<boolean[]> marioMoves = new ArrayList<boolean[]>();
  public static ArrayList<boolean[]> choices = new ArrayList<>();

  public static void init() {
    marioMoves.add(new boolean[] { false, true, false, true, false }); // right run
    marioMoves.add(new boolean[] { false, true, false, true, true }); // right jump and run
    marioMoves.add(new boolean[] { false, true, false, false, false }); // right
    marioMoves.add(new boolean[] { false, true, false, false, true }); // right jump
    marioMoves.add(new boolean[] { true, false, false, false, false }); // left
    marioMoves.add(new boolean[] { true, false, false, true, false }); // left run
    marioMoves.add(new boolean[] { true, false, false, false, true }); // left jump
    marioMoves.add(new boolean[] { true, false, false, true, true }); // left jump and run
    // right run
    for(int i=0;i<8;i++)
      choices.add(marioMoves.get(0));
    // right jump and run
    for(int i=0;i<8;i++)
      choices.add(marioMoves.get(1));
    // right
    for(int i=0;i<4;i++)
      choices.add(marioMoves.get(2));
    // right jump
    for(int i=0;i<4;i++)
      choices.add(marioMoves.get(3));
    // left
    choices.add(marioMoves.get(4));
    // left run
    choices.add(marioMoves.get(5));
    // left jump
    choices.add(marioMoves.get(6));
    // left jump and run
    choices.add(marioMoves.get(7));
  }

  public static boolean[] getRandomActions() {
    return getRandomActions(MarioRandom.choices);
  }

  public static boolean[] getRandomActions(ArrayList<boolean[]> _choices) {
    return _choices.get(rnd.nextInt(_choices.size()));
  }
}
