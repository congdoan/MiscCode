import java.util.*;
import java.io.File;
import java.io.IOException;

public class Digraph {
  
  // Number of vertices
  private int V;
  
  // Number of edges
  private int E;
  
  // adj[i] holds list of adjacent vertices from node i
  private List<Integer>[] adj;
  
  /**
   * Constructs a digraph with V number of vertices.
   */
  public Digraph(int V) {
    this.V = V;
    this.E = 0;
    adj = new List[V];
    for (int i = 0; i < V; i++) {
      adj[i] = new LinkedList<>();
    }
  }
  
  /**
   * Adds an directed edge from u to v.
   */
  public void addEdge(int u, int v) {
    validate(u);
    validate(v);
    adj[u].add(v);
    E++;
  }
  
  public int V() {
    return V;
  }
  
  public int E() {
    return E;
  }
  
  private void validate(int v) {
    if (v < 0 || v >= V) {
      throw new IllegalArgumentException();
    }
  }
  
  
  public static void main(String[] args) throws IOException {
    Scanner s = new Scanner(new File(args[0]));
    int V = s.nextInt();
    Digraph g = new Digraph(V);
    while (s.hasNextInt()) {
      g.addEdge(s.nextInt(), s.nextInt());
    }
    s.close();
    System.out.println("V = " + g.V());
    System.out.println("E = " + g.E());
  }
  
}
