package app;

public class Main {

    public static void main(String[] args) {
        Graph graph = new Graph();

        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);

        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);

        System.out.println("Graph after adding vertices and edges: " + graph);

        System.out.println("Has vertex 3: " + graph.hasVertex(3));
        System.out.println("Has vertex 5: " + graph.hasVertex(5));
        System.out.println("Has edge 1-2: " + graph.hasEdge(1, 2));
        System.out.println("Has edge 2-3: " + graph.hasEdge(2, 3));

        graph.removeEdge(1, 3);
        System.out.println("Graph after removing edge 1-3: " + graph);
        System.out.println("Has edge 1-3: " + graph.hasEdge(1, 3));

        graph.removeVertex(2);
        System.out.println("Graph after removing vertex 2: " + graph);
        System.out.println("Has vertex 2: " + graph.hasVertex(2));
        System.out.println("Has edge 1-2: " + graph.hasEdge(1, 2));
    }
}
