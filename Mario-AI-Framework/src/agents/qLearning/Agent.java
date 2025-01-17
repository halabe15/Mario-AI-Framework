package agents.qLearning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.GameStatus;
import engine.helper.MarioActions;

public class Agent implements MarioAgent {
  public static int instanceCount = 0;
  public static float maxReward= 0f;
  int count = 0;
  int printOffset = 5;
  float totalReward = 0;

  QTable table;
  private float epsilonDecay = 0.0005f;

  boolean play = false;

  public Agent(){
    super();
  }
  public Agent(boolean play){
    super();
    this.play = play;
  }

  @Override
  public void initialize(MarioForwardModel model, MarioTimer timer) {
    // float base = 0.75f - (this.epsilonDecay * (Agent.instanceCount*0.3f));
    // if(base < 0.2f){
    //   table = new QTable(0.2f);
    // } else {
    //   table = new QTable(base);
    // }
    table = new QTable(0);
    Agent.instanceCount++;
    this.totalReward = 0f;
    this.log("Game init => " + Agent.instanceCount);
    this.log("Q-Values size => " + table.qValues.size());
    MarioRandom.init();
    this.table.readQTable();
  }

  void printStateArray(int[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
      this.log("" + i + "-  " + Arrays.toString(matrix[i]));
    }
  }

  void printState(MarioForwardModel model) {
    this.log("" + this.count + "****************************************************************");
    this.log("*****************************************************" + model.getGameStatus());
  }

  String getMarioStadistics(MarioForwardModel model) {
    String marioStatus = "jumpHigher=" + (model.getMarioCanJumpHigher() ? "1" : "0");
    String marioCompleteObs = "observation=";
    int[][] completeObs = model.getMarioCompleteObservation();

    // Flaten
    for (int[] val1 : completeObs) {
      for (int val2 : val1) {
          marioCompleteObs += val2;
      }
    }

    return marioStatus + "&" + marioCompleteObs;
  }

  int zeroCount = 0;
  private float calculateReward(MarioForwardModel model) {
    float f = 0f;
    float v = model.getMarioFloatVelocity()[0] * 2;

    if((int)v <= 1){
      if(zeroCount > 2) {
        v = -15f;
      }
      zeroCount++;
    }else {
      zeroCount = 0;
    }
    if (model.getGameStatus() == GameStatus.LOSE) {
      f = -10f;
    }
    if (model.getGameStatus() == GameStatus.TIME_OUT) {
      f = -2f;
    }
    return 0f + v + f;
  }

  ArrayList<boolean[]> getPosibleMoves(MarioForwardModel model) {
    ArrayList<boolean[]> result = new ArrayList<boolean[]>();
    int length = MarioRandom.marioMoves.size();
    if(zeroCount < 2){
      for (int i = 0; i < length; i++) {
        if (i == 1 || i == 3) {
          result.add(MarioRandom.marioMoves.get(i));
        }
      }
    }
    return result;
  }

  @Override
  public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
    this.count++;

    // Q-Learning
    boolean[] selectedMove;
    String state = getMarioStadistics(model);
    ArrayList<boolean[]> posibleActions = this.getPosibleMoves(model);
    selectedMove = table.getAction(state, this.play);
    if (this.play) {
      return selectedMove;
    }
    model.advance(selectedMove);
    float reward = calculateReward(model);
    this.totalReward += reward;
    String nextStateHash = getMarioStadistics(model);
    table.updateQValues(state, selectedMove, reward, nextStateHash);

    if (reward > Agent.maxReward) {
      Agent.maxReward = reward;
    }

    if (model.getGameStatus() != GameStatus.RUNNING) {
      this.log("Eps => " + this.table.epsilon);
      this.log("Reward => " + reward);
      this.log("Max Reward => " + Agent.maxReward);
      this.log("Total reward => " + this.totalReward);
      this.table.saveQTable();
    }

    return selectedMove;
  }

  @Override
  public String getAgentName() {
    return "QLearningAgent";
  }

  void log(String message) {
    System.out.println(message);
  }
}
