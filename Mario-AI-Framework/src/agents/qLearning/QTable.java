package agents.qLearning;

import java.util.HashMap;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class QTable{
  static HashMap<String, Float> qValues = new HashMap<>();

  float epsilon = 0.5f; // Explore prob ()
  float gamma = 0.4f; // Discount factor (important of future rewards, 0 short)
  float alpha = 0.1f; // Learning rate (1 - deterministic, 0.1 - stochastic)

  public QTable(float epsilon){
    this.epsilon = epsilon;
  }

  public QTable(float epsilon, float gamma, float alpha){
    this.epsilon = epsilon;
    this.gamma = gamma;
    this.alpha = alpha;
  }

  private String getActionsHash(boolean[] action) {
    return "a_" + action[0] + "_" + action[1] + "_" + action[2] + "_" + action[3] + "_" + action[4];
  }

  public boolean[] getMaxQValueMove(String state, ArrayList<boolean[]> posibleActions) {
    float maxValue = 0f;
    int maxIndex = 0;
    String hash = "";
    int index = 0;
    // ArrayList<boolean[]> posibleActions = MarioRandom.marioMoves;
    // log(""+posibleActions.toString());
    for (boolean[] action : posibleActions) {
      hash += state + "_" + getActionsHash(action);
      if (QTable.qValues.containsKey(hash)) {
        float value = QTable.qValues.get(hash);
        if (value > maxValue) {
          maxValue = value;
          maxIndex = index;
        }
      }
      hash = ""; // reset
      index++;
    }

    if (maxValue == 0.0) {
      return MarioRandom.getRandomActions();
    }
    return posibleActions.get(maxIndex);
  }

  public float getMaxQValue(String state, ArrayList<boolean[]> posibleActions) {
    float maxValue = -1;
    String hash = "";
    // ArrayList<boolean[]> posibleActions = MarioRandom.marioMoves;
    for (boolean[] action : posibleActions) {
      hash += state + "_" + getActionsHash(action);
      if (QTable.qValues.containsKey(hash)) {
        float value = QTable.qValues.get(hash);
        if (value > maxValue) {
          maxValue = value;
        }
      }
      hash = ""; // reset
    }
    if (maxValue == 0.0) {
      return 0f;
    }
    return maxValue;
  }

  public void updateQValues(String prevState, boolean[] action, float reward, String nextState) {
    String prevStateHash = "" + prevState + "_" + getActionsHash(action);
    float oldValue = QTable.qValues.containsKey(prevStateHash) ? QTable.qValues.get(prevStateHash) : 0f;

    // float newValue = oldValue
        // + this.alpha * (reward + this.gamma * getMaxQValue(nextState, MarioRandom.marioMoves) - oldValue);
    float newValue = ((1 - this.alpha) * oldValue)
        + this.alpha * (reward + this.gamma * getMaxQValue(nextState, MarioRandom.marioMoves));
    QTable.qValues.put(prevStateHash, newValue);
  }

  boolean[] getAction(String state, boolean play){
    return this.getAction(state, play, MarioRandom.marioMoves);
  }

  boolean[] getAction(String state, boolean play, ArrayList<boolean[]> posibleActions){
    if (MarioRandom.rnd.nextFloat() < this.epsilon && !play) {
      // Explore
      // return MarioRandom.getRandomActions();
      return MarioRandom.getRandomActions(posibleActions);
    } else {
      // Exploit (largest q value)
      return getMaxQValueMove(state, posibleActions);
    }
  }

  void printQTable(){
    System.out.println(QTable.qValues.toString());
  }

  void readQTable() {
    this.readQTable("./qtable.db");
  }

  @SuppressWarnings("unchecked")
  void readQTable(String file) {
    try {
      FileInputStream fileIn = new FileInputStream(file);
      ObjectInputStream in = new ObjectInputStream(fileIn);
      QTable.qValues = (HashMap<String, Float>) in.readObject();
      in.close();
      fileIn.close();
   } catch (IOException i) {
      i.printStackTrace();
      return;
   } catch (ClassNotFoundException c) {
      System.out.println("HashMap class not found");
      c.printStackTrace();
      return;
   }
  }

  void saveQTable() {
    this.saveQTable("./qtable.db");
  }

  void saveQTable(String file) {
    try {
      FileOutputStream fileOut =
      new FileOutputStream(file);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(QTable.qValues);
      out.close();
      fileOut.close();
      System.out.println("Serialized data is saved in " + file);
   } catch (IOException i) {
      i.printStackTrace();
   }
  }

  void clearQTable() {
    this.clearQTable("./qtable.db");
  }

  void clearQTable(String file) {
    try {
      FileOutputStream fileOut =
      new FileOutputStream(file);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(new HashMap<String, Float>());
      out.close();
      fileOut.close();
      System.out.println("Cleared QTable data " + file);
   } catch (IOException i) {
      i.printStackTrace();
   }
  }
}
