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

  float epsilon = 0.5f;
  float gamma = 0.4f;
  float alpha = 0.1f;

  public QTable(float epsilon){
    this.epsilon = epsilon;
  }

  public QTable(float epsilon, float gamma, float alpha){
    this.epsilon = epsilon;
    this.gamma = gamma;
    this.alpha = alpha;
  }

  private String getActionsHash(boolean[] action) {
    return "moves=" + action[0] + "+" + action[1] + "+" + action[2] + "+" + action[3] + "+" + action[4];
  }

  public boolean[] getMaxQValueMove(String state, ArrayList<boolean[]> posibleActions) {
    float maxValue = 0f;
    int maxIndex = 0;
    String hash = "";
    int index = 0;
    // ArrayList<boolean[]> posibleActions = MarioRandom.marioMoves;
    // log(""+posibleActions.toString());
    for (boolean[] action : posibleActions) {
      hash += state + "+" + getActionsHash(action);
      if (QTable.qValues.containsKey(hash)) {
        float value = QTable.qValues.get(hash);
        if (value > maxValue) {
          maxValue = value;
          maxIndex = index;
        }
      }
      hash = "";
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
      hash = "";
    }
    if (maxValue == 0.0) {
      return 0f;
    }
    return maxValue;
  }

  public void updateQValues(String prevState, boolean[] action, float reward, String nextState) {
    String prevStateKey = "" + prevState + "_" + action;
    float oldValue = QTable.qValues.containsKey(prevStateKey) ? QTable.qValues.get(prevStateKey) : 0f;
    float newValue = ((1 - this.alpha) * oldValue) + this.alpha * (reward + this.gamma * getMaxQValue(nextState, MarioRandom.marioMoves));
    QTable.qValues.put(prevStateKey, newValue);
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
    this.readQTable("./Q-Table-Results.txt");
  }

  @SuppressWarnings("unchecked")
  void readQTable(String file) {
    try {
      FileInputStream fileIn = new FileInputStream(file);
      ObjectInputStream in = new ObjectInputStream(fileIn);
      QTable.qValues = (HashMap<String, Float>) in.readObject();
      // qValues.forEach((k,v) -> System.out.println("Key: " + k + ": Value: " + v));
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
    this.saveQTable("./Q-Table-Results.txt");
  }

  void saveQTable(String file) {
    try {
      FileOutputStream fileOut =
      new FileOutputStream(file);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(QTable.qValues);
      out.close();
      fileOut.close();
   } catch (IOException i) {
      i.printStackTrace();
   }
  }

  void clearQTable() {
    this.clearQTable("./Q-Table-Results.txt");
  }

  void clearQTable(String file) {
    try {
      FileOutputStream fileOut =
      new FileOutputStream(file);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(new HashMap<String, Float>());
      out.close();
      fileOut.close();
   } catch (IOException i) {
      i.printStackTrace();
   }
  }
}
