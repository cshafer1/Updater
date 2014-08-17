package com.srlupdater.deob.ControlFlowCorrection;

import java.util.Iterator;

/**
 * @Author: 200_success
 */
public class GraphTest {

    public static Graph graph1;

    public static void makeGraphs() {
        Graph g = graph1 = new Graph();
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("B", "D");
        g.addEdge("B", "A");
        g.addEdge("B", "E");
        g.addEdge("B", "F");
        g.addEdge("C", "A");
        g.addEdge("D", "C");
        g.addEdge("E", "B");
        g.addEdge("F", "B");
    }

    public static void iterationIs(String answer, Iterator<String> it) {
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            sb.append(' ').append(it.next());
        }
        System.out.println(answer.equals(sb.substring(1)));
        System.out.println(sb.substring(1));
    }

    public static void preOrderIterationOfIsolatedVertex() {
        iterationIs("Z", new PreOrderDFSIterator(graph1, "Z"));
    }

    public static void preOrderIterationFromA() {
        iterationIs("A B C D E F", new PreOrderDFSIterator(graph1, "A"));
        iterationIs("A B C D E F", new PreOrderDFSIterator(graph1, "Z"));
    }
}
