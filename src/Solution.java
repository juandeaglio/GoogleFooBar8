import java.util.*;

public class Solution
{
    public static int[] RescueTheBunnies(int[][] times, int times_limit)
    {
        ArrayList<Integer> bunniesResult = new ArrayList<>();
        int timeLimit = times_limit;
        if(FindCycleIfExists(times)) // TODO: will not work if the cycle is not reachable w/ the timelimit
        {
            bunniesResult = new ArrayList<>();
            for(int i = 0; i < times.length-2; i++)
                bunniesResult.add(i);
        }
        else
        {
            int[] bellmanVertexArr = BellmanFordWithAGivenStart(times, 0);
            int minimumCostVertexChosen = GetMinimumOf(bellmanVertexArr, 0, bunniesResult);
            int totalCost = 0;
            int origin = 0;
            int[] bellmanVertexArrFuture = BellmanFordWithAGivenStart(times, minimumCostVertexChosen);
            totalCost+= times[origin][minimumCostVertexChosen];
            while(timeLimit - bellmanVertexArr[minimumCostVertexChosen] >= bellmanVertexArrFuture[bellmanVertexArr.length-1])
            {
                int[][] bellmanGraphArr = new int[times.length][times.length];
                //TODO: re-write path decision to consider all possible paths and take the min totalCost that is created within two moves (maybe extend it to n amount of moves)
                timeLimit -= bellmanVertexArr[minimumCostVertexChosen];
                if(minimumCostVertexChosen > 0 && minimumCostVertexChosen < bellmanVertexArr.length-1 && !bunniesResult.contains(minimumCostVertexChosen-1))
                    bunniesResult.add(minimumCostVertexChosen-1);
                for(int i = 0; i < bellmanVertexArr.length; i++)
                {
                    bellmanGraphArr[i] = BellmanFordWithAGivenStart(times, i, times[minimumCostVertexChosen][i]);
                }
                origin = minimumCostVertexChosen;
                minimumCostVertexChosen = GetMinimumExcludingCurrent(bellmanGraphArr, minimumCostVertexChosen, bunniesResult);
                totalCost+= times[origin][minimumCostVertexChosen];
                bellmanVertexArrFuture = BellmanFordWithAGivenStart(times, minimumCostVertexChosen);
            }
        }
        int[] result = null;
        if(bunniesResult.size() >= 1)
            result = bunniesResult.stream().mapToInt(i->i).toArray();
        return result;
    }

    private static int GetMinimumOf(int[] bellmanVertexArr, int excluding, ArrayList<Integer> bunniesPickedUp)
    {
        int rowChosen = -1;
        int minimumVal = Integer.MAX_VALUE;
        for(int i = 0; i < bellmanVertexArr.length; i++)
        {
            if((i != excluding && bellmanVertexArr[i] < minimumVal) ||
                    (bellmanVertexArr[i] == minimumVal &&
                            (rowChosen == 0 && !bunniesPickedUp.contains((i-1)))))
            {
                minimumVal = bellmanVertexArr[i];
                rowChosen = i;
            }
        }
        return rowChosen;
    }
    private static int GetMinimumExcludingCurrent(int[][] bellmanVertexGraph, int excluding, ArrayList<Integer> bunniesPickedUp)
    {
        int rowChosen = -1;
        int minimumVal = Integer.MAX_VALUE;
        for(int i = 0; i < bellmanVertexGraph.length; i++)
        {
            int minimumValInRow = Integer.MAX_VALUE;
            if(i != excluding)
            {
                for(int j = 0; j < bellmanVertexGraph[i].length; j++)
                {
                    if(bellmanVertexGraph[i][j] != 0)
                        minimumValInRow = Math.min(minimumValInRow, bellmanVertexGraph[i][j]);
                }
            }
            if((minimumValInRow < minimumVal)
                    || (minimumValInRow == minimumVal && !bunniesPickedUp.contains(i-1) && bunniesPickedUp.contains(rowChosen-1)
                    || (rowChosen == 0 && !bunniesPickedUp.contains(i-1))))
            {
                minimumVal = minimumValInRow;
                rowChosen = i;
            }
        }
        return rowChosen;
    }
    private static int[] BellmanFordWithAGivenStart(int[][] times, int start) {
        int[] bellmanVertexArr = new int[times.length];
        Arrays.fill(bellmanVertexArr, Integer.MAX_VALUE);
        bellmanVertexArr[start] = 0;
        boolean changesWereMade = true;
        while(changesWereMade)
        {
            boolean wasThereAChange = false;
            for(int i = 0; i < bellmanVertexArr.length; i++)
            {
                if(bellmanVertexArr[i] != Integer.MAX_VALUE)
                {
                    for(int j = 0; j < times[i].length; j++)
                    {
                        if(j != i && times[i][j] < bellmanVertexArr[j])
                        {
                            bellmanVertexArr[j] = times[i][j];
                            wasThereAChange = true;
                        }
                    }
                }
            }
            changesWereMade = wasThereAChange;
        }
        return bellmanVertexArr;
    }
    private static int[] BellmanFordWithAGivenStart(int[][] times, int start, int cost) {
        int[] bellmanVertexArr = new int[times.length];
        Arrays.fill(bellmanVertexArr, Integer.MAX_VALUE);
        bellmanVertexArr[start] = 0;
        boolean changesWereMade = true;
        while(changesWereMade)
        {
            boolean wasThereAChange = false;
            for(int i = 0; i < bellmanVertexArr.length; i++)
            {
                if(bellmanVertexArr[i] != Integer.MAX_VALUE)
                {
                    for(int j = 0; j < times[i].length; j++)
                    {
                        if(j != i && times[i][j] < bellmanVertexArr[j])
                        {
                            bellmanVertexArr[j] = times[i][j];
                            wasThereAChange = true;
                        }
                    }
                }
            }
            changesWereMade = wasThereAChange;
        }
        for(int j = 0; j < bellmanVertexArr.length; j++)
        {
            bellmanVertexArr[j] += cost;
        }
        return bellmanVertexArr;
    }
    private static int FindMinimumCycleRow(int[][] graph)
    {
        int rowToReturn = -1;
        int row = 0;
        int maximum = 0;
        for(int j = 0; j < graph[0].length; j++)
        {
            maximum += graph[0][j] + graph[j][0];
        }
        int sum = 0;
        for(int i = 0; i < graph.length; i++)
        {
            int sumOfCycles = 0;
            for(int j = 0; j < graph[i].length; j++)
            {
                sumOfCycles += graph[i][j] + graph[j][i];
            }
            if(sumOfCycles <= maximum)
            {
                sum = sumOfCycles;
                row = i;
            }
        }
        if(sum != maximum)
            rowToReturn = row;
        return rowToReturn;
    }

    public static boolean  FindCycleIfExists(int[][] graph)
    {
        int cycleRow = -1;
        for(int i = 0; i < graph.length && cycleRow == -1; i++)
        {
            if(graph[i][i] < 0)
                cycleRow = i;
        }
        return cycleRow != -1;
    }

    static class Graph
    {
        private final Map<Integer, Vertex> adjVertices;
        Graph(int[][] asIntArr)
        {
            adjVertices = new HashMap<>();
            for(int i = 0; i < asIntArr.length; i++)
            {
                addVertex(i);
                for(int j = 0; j < asIntArr[i].length; j++)
                {
                    if(asIntArr[i][j] != 0)
                    {
                        Edge newEdge = addEdge(i, j, asIntArr[i][j]);
                    }
                }
            }
        }
        void addVertex(int room)
        {
            adjVertices.putIfAbsent(room, new Vertex(room));
        }
        Edge addEdge(int from, int to, int cost)
        {
            return adjVertices.get(from).AddEdge(to, cost);
        }
    }
    static class Edge
    {
        int leadsToRoom;
        int startsFromRoom;
        int cost;
        Edge(int fromNumber, int toRoomNumber, int weight)
        {
            startsFromRoom = fromNumber;
            leadsToRoom = toRoomNumber;
            cost = weight;
        }
    }
    static class Vertex
    {
        int room;
        List<Edge> edges;
        Vertex(int roomNumber)
        {
            this.room = roomNumber;
            edges = new ArrayList<>();
        }
        public Edge AddEdge(int to, int cost)
        {
            Edge newEdge = new Edge(room,to,cost);
            edges.add(newEdge);
            return newEdge;
        }
        public Edge GetEdge(int to)
        {
            Edge found = null;
            for (Edge edge: edges)
            {
                if(edge.leadsToRoom == to)
                    found = edge;
            }
            return found;
        }
    }
}
