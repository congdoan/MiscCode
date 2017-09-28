import java.io.*;
import java.util.*;


public class TspAssignmentMethod {
  
  private static final double INF = Double.MAX_VALUE;
  
  private final int n;
  private double[][] d;
  private final Set<Integer> rows;
  private final Set<Integer> cols;
  
  public TspAssignmentMethod(double[][] distMatrix) {
    n = distMatrix.length;
    d = distMatrix;
    rows = new HashSet<>(n);
    cols = new HashSet<>(n);
  }
  
  public double minDistTour(Integer[] tour) {
    double[][] dcopy = new double[n][n];
    for (int i = 0; i < n; i++) {
      System.arraycopy(d[i], 0, dcopy[i], 0, n);
    }
    for (int city = 0; city < n; city++) {
      rows.add(city);
      cols.add(city);
    }
    
    Map<Integer, Integer> assigns = new HashMap<>(n);
    while (rows.size() > 1) {
      // Minimize distance matrix so that all rows and cols have at least one zero
      minimizeMatrix();
      // Find out the location (row, col) of a zero with maximum penalty
      Integer[] loc = locateMaxPenaltyZero();
      // Make assignment row -> col and reduce distance matrix
      assigns.put(loc[0], loc[1]);
      reduceMatrix(loc[0], loc[1]);
    }
    //+ debug
    if (cols.size() != 1) {
      throw new RuntimeException(String.format("Something Wrong with code. # of rows/cols = %d/%d", rows.size(), cols.size()));
    }
    //-
    
    Integer[] lastRow = rows.toArray(new Integer[1]);
    Integer[] lastCol = cols.toArray(new Integer[1]);
    Integer from = lastRow[0];
    Integer to = lastCol[0];
    d = dcopy;
    double dist = d[from][to];
    tour[0] = from;
    tour[1] = to;
    for (int i = 2; i <= n; i++) {
      from = to;
      to = assigns.get(from);
      dist += d[from][to];
      tour[i] = to;
    }
    return dist;
  }
  
  private void minimizeMatrix() {
    Set<Integer> colsHasZero = new HashSet<>();
    for (Integer row: rows) {
      minimizeRow(row, colsHasZero);
    }
    for (Integer col: cols) {
      if (!colsHasZero.contains(col)) {
        minimizeCol(col);
      }
    }
  }
  
  private void minimizeRow(Integer row, Set<Integer> colsHasZero) {
    double min = INF;
    for (Integer col: cols) {
      min = Math.min(min, d[row][col]);
    }
    for (Integer col: cols) {
      if (d[row][col] != INF) {
        if (d[row][col] == min) {
          colsHasZero.add(col);
        }
        d[row][col] -= min;
      }
    }
  }
  
  private void minimizeCol(Integer col) {
    double min = INF;
    for (Integer row: rows) {
      min = Math.min(min, d[row][col]);
    }
    for (Integer row: rows) {
      if (d[row][col] != INF) {
        d[row][col] -= min;
      }
    }
  }
  
  private Integer[] locateMaxPenaltyZero() {
    Integer i = -1, j = -1;
    double maxPen = Double.MIN_VALUE;
    for (Integer row: rows) {
      for (Integer col: cols) {
        if (d[row][col] == 0.0) {
          double pen = calcPenalty(row, col);
          if (pen > maxPen) {
            i = row;
            j = col;
            maxPen = pen;
          }
        }
      }
    }
    return new Integer[] {i, j};
  }
  
  private double calcPenalty(Integer row, Integer col) {
    double minValOfRow = INF;
    for (Integer j: cols) {
      if (j != col) {
        minValOfRow = Math.min(minValOfRow, d[row][j]);
      }
    }
    double minValOfCol = INF;
    for (Integer i: cols) {
      if (i != row) {
        minValOfCol = Math.min(minValOfCol, d[i][col]);
      }
    }
    return minValOfRow + minValOfCol;
  }
  
  private void reduceMatrix(Integer row, Integer col) {
    rows.remove(row);
    cols.remove(col);
    if (rows.contains(col) && cols.contains(row)) {
      d[col][row] = INF;
    }
  }
  
  
  private static class City {
    private final double x;
    private final double y;
    
    private City(double xCoordinate, double yCoordinate) {
      x = xCoordinate;
      y = yCoordinate;
    }
  }  
  
  private static double distance(City p, City q) {
    double dx = p.x - q.x, dy = p.y - q.y;
    return Math.sqrt(dx * dx + dy * dy);
  }
  
  private static double[][] distMatrix(City[] cities) {
    final int n = cities.length;
    double[][] d = new double[n][n];
    for (int i = 0; i < n; i++) {
      d[i][i] = INF;
    }
    for (int i = 0; i < n - 1; i++) {
      for (int j = i + 1; j < n; j++) {
        double distance = distance(cities[i], cities[j]);
        d[i][j] = distance;
        d[j][i] = distance;
      }
    }
    return d;
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Execution: java TspAssignmentMethod <cities-location-file>");
      System.exit(1);
    }
    
    Scanner s = new Scanner(new File(args[0]));
    int n = s.nextInt();
    //+ debug
    if (args.length > 1) {
      n = Integer.valueOf(args[1]);
    }
    //-
    City[] cities = new City[n];
    for (int i = 0; i < n; i++) {
      cities[i] = new City(s.nextDouble(), s.nextDouble());
    }
    s.close();
    
    double[][] d = distMatrix(cities);
    Integer[] tour = new Integer[n + 1];
    double minDist = (new TspAssignmentMethod(d)).minDistTour(tour);
    System.out.printf("TSP distance: %f%n", minDist);
    System.out.printf("TSP tour: %s%n", Arrays.toString(tour));
  }
  
}
