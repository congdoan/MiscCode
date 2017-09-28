import java.util.*;

public class ConnectedComponents {
  
  public static List<List<Integer>> computeComponents(Digraph g) {
    // Backward pass (i.e. pass on the reverse graph)
    List<Integer> seq
    
    // Forward pass to compute components
    
    return scc;
  }
  
  public static void main(String[] args) throws IOException {
    Scanner s = new Scanner(new File(args[0]));
    int V = s.nextInt();
    Digraph g = new Digraph(V);
    while (s.hasNextInt()) {
      g.addEdge(s.nextInt(), s.nextInt());
    }
    s.close();
  }

}
