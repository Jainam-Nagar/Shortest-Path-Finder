import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.*;

public class MapNavigator extends Application {
    private Graph map;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a sample map graph
        map = createMap();

        primaryStage.setTitle("Map Navigator");

        // Set up the GUI components
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label startLabel = new Label("Start Node:");
        GridPane.setConstraints(startLabel, 0, 0);

        TextField startField = new TextField();
        startField.setMaxWidth(150);
        GridPane.setConstraints(startField, 1, 0);

        Label endLabel = new Label("End Node:");
        GridPane.setConstraints(endLabel, 0, 1);

        TextField endField = new TextField();
        endField.setMaxWidth(150);
        GridPane.setConstraints(endField, 1, 1);

        Button findPathButton = new Button("Find Shortest Path");
        GridPane.setConstraints(findPathButton, 1, 2);

        Label resultLabel = new Label();
        GridPane.setConstraints(resultLabel, 1, 3);
        resultLabel.setWrapText(true);

        // Event handler for the button
        findPathButton.setOnAction(e -> {
            String startNodeName = startField.getText();
            String endNodeName = endField.getText();

            Node startNode = getNodeByName(map, startNodeName);
            Node endNode = getNodeByName(map, endNodeName);

            if (startNode != null && endNode != null) {
                // Reset the graph state before finding the new path
                resetGraph(map);

                // Run Dijkstra's algorithm starting from the user-provided start node
                dijkstra(map, startNode);

                // Display the shortest path from the start node to the user-provided end node
                List<Node> shortestPath = getShortestPath(endNode);
                displayShortestPath(shortestPath, resultLabel);
            } else {
                resultLabel.setText("Invalid nodes. Please check the node names.");
            }
        });

        grid.getChildren().addAll(startLabel, startField, endLabel, endField, findPathButton, resultLabel);

        primaryStage.setScene(new Scene(grid, 350, 200)); // Increase the width of the window
        primaryStage.show();
    }

    // Implement Dijkstra's algorithm
    public static void dijkstra(Graph graph, Node start) {
        // Reset distances for all nodes
        for (Node node : graph.nodes) {
            node.distance = Integer.MAX_VALUE;
            node.visited = false;
        }

        start.distance = 0;
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        priorityQueue.addAll(graph.nodes);

        while (!priorityQueue.isEmpty()) {
            Node current = priorityQueue.poll();
            current.visited = true;

            for (Edge neighborEdge : current.neighbors) {
                Node neighbor = neighborEdge.destination;

                if (!neighbor.visited) {
                    int newDistance = current.distance + neighborEdge.weight;

                    if (newDistance < neighbor.distance) {
                        priorityQueue.remove(neighbor);
                        neighbor.distance = newDistance;
                        neighbor.previous = current;
                        priorityQueue.add(neighbor);
                    }
                }
            }
        }
    }

    // Create a sample map graph
    public static Graph createMap() {
        Graph graph = new Graph();

        Node A = new Node("A");
        Node B = new Node("B");
        Node C = new Node("C");
        Node D = new Node("D");
        Node E = new Node("E");

        A.neighbors.add(new Edge(B, 1));
        A.neighbors.add(new Edge(C, 4));
        B.neighbors.add(new Edge(C, 2));
        B.neighbors.add(new Edge(D, 5));
        C.neighbors.add(new Edge(D, 1));
        D.neighbors.add(new Edge(E, 7));

        graph.addNode(A);
        graph.addNode(B);
        graph.addNode(C);
        graph.addNode(D);
        graph.addNode(E);

        return graph;
    }

    // Display the shortest path
    public static void displayShortestPath(List<Node> shortestPath, Label resultLabel) {
        if (shortestPath.isEmpty()) {
            resultLabel.setText("No path found.");
        } else {
            StringBuilder pathString = new StringBuilder("Shortest Path: ");
            int pathSize = shortestPath.size();

            for (int i = 0; i < pathSize; i++) {
                if (i == pathSize - 1) {
                    // Exclude the start node only if it is not the same as the end node
                    if (!shortestPath.get(i).name.equals(shortestPath.get(0).name)) {
                        pathString.append(shortestPath.get(i).name);
                    }
                } else {
                    pathString.append(shortestPath.get(i).name).append(" -> ");
                }
            }

            resultLabel.setText(pathString.toString());
        }
    }

    // Get a node by name
    public static Node getNodeByName(Graph graph, String nodeName) {
        for (Node node : graph.nodes) {
            if (node.name.equals(nodeName)) {
                return node;
            }
        }
        return null;
    }

    // Retrieve the shortest path from the end node to the start node
    public static List<Node> getShortestPath(Node endNode) {
        List<Node> path = new ArrayList<>();
        for (Node current = endNode; current != null; current = current.previous) {
            path.add(current);
        }
        Collections.reverse(path);
        return path;
    }

    // Reset the state of the graph
    public static void resetGraph(Graph graph) {
        for (Node node : graph.nodes) {
            node.distance = Integer.MAX_VALUE;
            node.visited = false;
            node.previous = null;
        }
    }

    static class Node {
        String name;
        List<Edge> neighbors;
        int distance;
        Node previous;
        boolean visited;

        public Node(String name) {
            this.name = name;
            this.neighbors = new ArrayList<>();
            this.distance = Integer.MAX_VALUE;
            this.previous = null;
            this.visited = false;
        }
    }

    static class Edge {
        Node destination;
        int weight;

        public Edge(Node destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    static class Graph {
        List<Node> nodes;

        public Graph() {
            this.nodes = new ArrayList<>();
        }

        public void addNode(Node node) {
            nodes.add(node);
        }
    }
}
