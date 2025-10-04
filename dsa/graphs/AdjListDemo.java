package graphs;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AdjListDemo {

    public static void main(String[] args) {
        System.out.println(buildGraph(4, new char[][]{
                {'A', 'B'},
                {'A', 'C'},
                {'B', 'C'},
                {'B', 'D'}
        }));

        System.out.println(buildGraph(4, new int[][]{
                {0, 1},
                {0, 2},
                {1, 2},
                {1, 3}
        }));
    }

    private static Map<Character, List<Character>> buildGraph(int n, char[][] edges) {
        List<Character> nodes = IntStream.range(0,n)
                .mapToObj(i -> (char)('A' +i))
                .toList();
        Map<Character, List<Character>> adjList = nodes.stream()
                .collect(Collectors.toMap(Function.identity(), c -> new ArrayList<>()));

        Arrays.stream(edges).forEach(edge -> {
            adjList.get(edge[0]).add(edge[1]);
            adjList.get(edge[1]).add(edge[0]);
        });
        return adjList;
    }

    private static List<List<Integer>> buildGraph(int n, int[][] edges) {
        List<List<Integer>> adjList = IntStream.range(0,n)
                .mapToObj(i -> new ArrayList<Integer>())
                .collect(Collectors.toList());

        Arrays.stream(edges).forEach(edge -> {
            adjList.get(edge[0]).add(edge[1]);
            adjList.get(edge[1]).add(edge[0]);
        });
        return adjList;
    }

}
