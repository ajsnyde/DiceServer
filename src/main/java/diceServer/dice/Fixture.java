package diceServer.dice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Fixture {
  public int sq = Die.outerSquare;
  public String name = "";
  public ArrayList<Coord> positions = new ArrayList<Coord>();
  // dimensions of template in pixels - positions must be contained within these limits.
  public int xSize = 4500;
  public int ySize = 3500;

  public Fixture() {

  }

  public Fixture(File file) {
    loadPositions(file);
  }

  public void loadPositions(File file) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      // Convert JSON string from file to Object
      positions = mapper.readValue(file, new TypeReference<ArrayList<Coord>>() {
      });
    } catch (JsonGenerationException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public int getMaxDice() {
    return positions.size();
  }
}
