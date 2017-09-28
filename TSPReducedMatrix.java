import java.io.*;
import java.util.*;


public class TSPReducedMatrix {
  
  private final int n;
  private int[][] d;
  
  private final Set<Integer> reducedRows;
  private final Set<Integer> reducedCols;
  
  private final int fieldWidth; // for displaying matrix (for debugging)

  
  public TSPReducedMatrix(int n, int[][] d) {
    this.n = n;
    this.d = new int[n][n];
    for (int i = 0; i < n; i++) {
      System.arraycopy(d[i], 0, this.d[i], 0, n);
    }
    
    reducedRows = new HashSet<>(n);
    reducedCols = new HashSet<>(n);
    
    // Calculate the number of decimal digits of the largest distance value
    int maxVal = Integer.MIN_VALUE;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (d[i][j] != Integer.MAX_VALUE) {
          maxVal = Math.max(maxVal, d[i][j]);
        }
      }
    }
    int numDigits = 0;
    do {
      numDigits++;
      maxVal /= 10;
    } while (maxVal != 0);
    fieldWidth = numDigits + 2;
  }

  
  public long assignmentMethod(Integer[] tour) {
    //DEBUG: Print input distance matrix
    System.out.println("input distance matrix:");
    printMatrix();
    System.out.println();
    
    final int[][] dcopy = new int[n][n];
    for (int i = 0; i < n; i++) {
      System.arraycopy(d[i], 0, dcopy[i], 0, n);
    }
    for (int i = 0; i < n; i++) {
      reducedRows.add(i);
    }
    for (int j = 0; j < n; j++) {
      reducedCols.add(j);
    }
    
    /* Initialization: ensure that all the rows and columns have at least one zero */
    Set<Integer> zeroCols = new HashSet<>(n);
    Set<Integer> zeroCellIdxs = new HashSet<>();
    minimizeRows(zeroCols, zeroCellIdxs);
    minimizeCols(zeroCols, zeroCellIdxs);
    
    //DEBUG: Print distance matrix after initial rows & columns minimization
    System.out.println("distance matrix after initial rows & columns minimization:");
    printMatrix();
    System.out.printf("%n*******************************************************************************************%n%n");
    
    /* Main loop */
    Map<Integer, Integer> assigns = new HashMap<>(n);
    for (int idx = 0; idx < n-1; idx++) {
      //DEBUG: Print zeroCellIdxs
      System.out.printf("before assignment %d, zeroCellIdxs = %s (%d 0s)%n", idx, zeroCellIdxs, zeroCellIdxs.size());
      
      // Find out the (row,col) index of zero-valued entry that has maximum penalty
      int[] rowcol = computeMaxPenaltyZero(zeroCellIdxs);
      assigns.put(rowcol[0], rowcol[1]);
    
      // Update the reduced rows and columns
      reducedRows.remove(rowcol[0]);
      reducedCols.remove(rowcol[1]);
      
      // Mark the assignment
      if (reducedRows.contains(rowcol[1]) && reducedCols.contains(rowcol[0])) {
        if (d[rowcol[1]][rowcol[0]] == 0) {
          // Remove (rowcol[1], rowcol[0]) from zeroCellIdxs
          int cellIdx = rowcol[1] * n + rowcol[0];
          zeroCellIdxs.remove(cellIdx);
        }
        d[rowcol[1]][rowcol[0]] = Integer.MAX_VALUE;
      }

      //DEBUG: Print the reduced matrix after each assignment
      System.out.printf("reduced matrix after assignment %d (city %c -> city %c):%n", idx, (char) ('A'+rowcol[0]), (char) ('A'+rowcol[1]));
      printMatrix();
      System.out.println();
      
      // Ensure all the reduced rows and columns have at least one zero
      minimizeReducedMatrix(rowcol[0], rowcol[1], zeroCellIdxs);
    
      //DEBUG: Print the reduced matrix after each transform
      System.out.printf("reduced matrix after transform %d:%n", idx);
      printMatrix();
      System.out.println("*****************************************************");
    }
    
    if (reducedRows.size() != 1 || reducedCols.size() != 1) {
      throw new RuntimeException("Something goes wrong with code!");
    }
    
    // Make last assignment
    Integer[] lastReducedRow = reducedRows.toArray(new Integer[1]);
    Integer[] lastReducedCol = reducedCols.toArray(new Integer[1]);
    assigns.put(lastReducedRow[0], lastReducedCol[0]);
    reducedRows.clear();
    reducedCols.clear();

    //DEBUG: Print the reduced matrix after last assignment
    System.out.printf("reduced matrix after assignment %d (city %c -> city %c):%n", n-1, (char) ('A'+lastReducedRow[0]), (char) ('A'+lastReducedCol[0]));
    printMatrix();
    System.out.printf("%n*******************************************************************************************%n%n");
    
    d = dcopy;
    long tourDist = 0L;
    for (Map.Entry<Integer, Integer> assign: assigns.entrySet()) {
      Integer from = assign.getKey();
      Integer to = assign.getValue();
      tour[0] = from;
      tour[1] = to;
      tourDist += d[from][to];
      //DEBUG: Print assignment
      System.out.printf("%c ->  %c%n", (char) ('A'+from), (char) ('A'+to));
      for (int idx = 2; idx <= n; idx++) {
        from = to;
        to = assigns.get(from);
        tour[idx] = to;
        tourDist += d[from][to];
        //DEBUG: Print assignment
        System.out.printf("%c ->  %c%n", (char) ('A'+from), (char) ('A'+to));
      }
      break;
    }    
    return tourDist;
  }
  
  private void printMatrix() {
    // Column labels
    System.out.print(" ");
    for (int j = 0; j < n; j++) {
      if (reducedCols.contains(j)) {
        System.out.printf("%" + fieldWidth + "c", (char) ('A' + j));
      }
    }
    
    System.out.printf("%n");
    
    //Rows: label + value
    for (int i = 0; i < n; i++) {
      if (reducedRows.contains(i)) {
        System.out.print((char) ('A' + i));
        for (int j = 0; j < n; j++) {
          if (reducedCols.contains(j)) {
            if (d[i][j] == Integer.MAX_VALUE) {
              System.out.printf("%" + fieldWidth + "c", '-');
            } else {
              System.out.printf("%" + fieldWidth + "d", d[i][j]);
            }
          }
        }
        System.out.println();
      }
    }
  }
  
  private int[] computeMaxPenaltyZero(Set<Integer> zeroCellIdxs) {
    int row = -1;
    int col = -1;
    int maxPenalty = -1;
    for (int cellIdx: zeroCellIdxs) {
      int i = cellIdx / n;
      int j = cellIdx % n;
      int penalty = calcPenalty(i, j);
      if (penalty > maxPenalty) {
        row = i;
        col = j;
        maxPenalty = penalty;
      }
    }
    return new int[] {row, col};
  }
  
  private int calcPenalty(int row, int col) {
    int minValOfRow = Integer.MAX_VALUE;
    for (int j: reducedCols) {
      if (j != col) {
        minValOfRow = Math.min(minValOfRow, d[row][j]);
      }
    }
    int minValOfCol = Integer.MAX_VALUE;
    for (int i: reducedRows) {
      if (i != row) {
        minValOfCol = Math.min(minValOfCol, d[i][col]);
      }
    }
    return minValOfRow + minValOfCol;
  }
  
  private void minimizeReducedMatrix(int assignedRow, int assignedCol, Set<Integer> zeroCellIdxs) {
    // Remove (assignedRow, assignedCol) from zeroCellIdxs
    int prevAssignedCellIdx = assignedRow * n + assignedCol;
    zeroCellIdxs.remove(prevAssignedCellIdx);
    
    if (reducedRows.contains(assignedCol) && reducedCols.contains(assignedRow)) {
      minimizeRow(assignedCol, zeroCellIdxs);
      minimizeCol(assignedRow, zeroCellIdxs);
    }
      
    // Minimize the reduced rows if needed
    for (int row: reducedRows) {
      if (row != assignedRow && d[row][assignedCol] == 0) {
        // Remove (row, assignedCol) from zeroCellIdxs
        int cellIdx = row * n + assignedCol;
        zeroCellIdxs.remove(cellIdx);
        
        minimizeRow(row, zeroCellIdxs);
      }
    }
    
    // Minimize the reduced columns if needed
    for (int col: reducedCols) {
      if (col != assignedCol && d[assignedRow][col] == 0) {
        // Remove (assignedRow, col) from zeroCellIdxs
        int cellIdx = assignedRow * n + col;
        zeroCellIdxs.remove(cellIdx);
        
        minimizeCol(col, zeroCellIdxs);
      }
    }
  }
  
  private void minimizeRow(int row, Set<Integer> zeroCellIdxs) {
    int minValCol = -1;
    for (int col: reducedCols) {
      if (minValCol == -1 || d[row][col] < d[row][minValCol]) {
        minValCol = col;
      }
    }
    int minValOfRow = d[row][minValCol];
    for (int col: reducedCols) {
      if (d[row][col] != Integer.MAX_VALUE) {
        d[row][col] -= minValOfRow;
        if (d[row][col] == 0) {
          int cellIdx = row * n + col;
          zeroCellIdxs.add(cellIdx);
        }
      }
    }
  }
  
  private void minimizeCol(int col, Set<Integer> zeroCellIdxs) {
    int minValRow = -1;
    for (int row: reducedRows) {
      if (minValRow == -1 || d[row][col] < d[minValRow][col]) {
        minValRow = row;
      }
    }
    int minValOfCol = d[minValRow][col];
    for (int row: reducedRows) {
      if (d[row][col] != Integer.MAX_VALUE) {
        d[row][col] -= minValOfCol;
        if (d[row][col] == 0) {
          int cellIdx = row * n + col;
          zeroCellIdxs.add(cellIdx);
        }
      }
    }
  }
  
  private void minimizeRows(Set<Integer> zeroCols, Set<Integer> zeroCellIdxs) {
    for (int i = 0; i < n; i++) {
      int col = 0;
      for (int j = 1; j < n; j++) {
        if (d[i][j] < d[i][col]) {
          col = j;
        }
      }
      int minValOfRow = d[i][col];
      for (int j = 0; j < n; j++) {
        if (d[i][j] != Integer.MAX_VALUE) {
          d[i][j] -= minValOfRow;
          if (d[i][j] == 0) {
            zeroCols.add(j);
            int cellIdx = i * n + j;
            zeroCellIdxs.add(cellIdx);
          }
        }
      }
    }
  }
  
  private void minimizeCols(Set<Integer> zeroCols, Set<Integer> zeroCellIdxs) {
    for (int j = 0; j < n; j++) {
      if (!zeroCols.contains(j)) {
        int row = 0;
        for (int i = 1; i < n; i++) {
          if (d[i][j] < d[row][j]) {
            row = i;
          }
        }
        int minValOfCol = d[row][j];
        for (int i = 0; i < n; i++) {
          if (d[i][j] != Integer.MAX_VALUE) {
            d[i][j] -= minValOfCol;
            if (d[i][j] == 0) {
              int cellIdx = i * n + j;
              zeroCellIdxs.add(cellIdx);
            }
          }
        }
      }
    }
  }
  
  
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Execution: java TSPReducedMatrix <distance-matrix-file>");
      System.exit(1);
    }
    
    Scanner s = new Scanner(new File(args[0]));
    int n = s.nextInt();
    int[][] d = new int[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (j != i) {
          d[i][j] = s.nextInt();
        } else {
          d[j][j] = Integer.MAX_VALUE;
        }
      }
    }
    s.close();
    
    TSPReducedMatrix tsp = new TSPReducedMatrix(n, d);
    Integer[] tspTour = new Integer[n+1];
    long tspDistance = tsp.assignmentMethod(tspTour);
    System.out.printf("TSP Distance: %d%n", tspDistance);
    char[] tspTourInChar = new char[n+1];
    for (int i = 0; i <= n; i++) {
      tspTourInChar[i] = (char) (tspTour[i] + 'A');
    }
    System.out.printf("TSP Tour: %s%n", Arrays.toString(tspTourInChar));
  }
  
}
