import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Solution
{
    public static int[] RescueTheBunnies(int[][] times, int times_limit)
    {
        //TODO: handle null case
        ArrayList<Integer> bunniesResult = new ArrayList<>();
        int timeLimit = times_limit;
        if(times.length > 2) {
            if (FindCycleIfExists(times))
            {
                bunniesResult = new ArrayList<>();
                for (int i = 0; i < times.length - 2; i++)
                    bunniesResult.add(i);
            }
            else
            {
                HashMap<Integer, Path> bellmanVertexArr = BellmanFordWithAGivenStart(times, 0);
                int minimumCostVertexChosen = GetMinimumExcludingCurrent(bellmanVertexArr, 0, bunniesResult);
                int origin = 0;
                HashMap<Integer, Path> bellmanVertexArrFuture = BellmanFordWithAGivenStart(times, minimumCostVertexChosen);
                Path pathBeingEvaluated = null;
                int pathNextChoice = 0;
                while ((timeLimit - pathNextChoice >= bellmanVertexArrFuture.get(times.length - 1).cost || bellmanVertexArrFuture.get(times.length - 1).cost == 0 ) && origin != minimumCostVertexChosen & minimumCostVertexChosen != -1)
                {
                    if (pathBeingEvaluated == null)
                    {
                        pathBeingEvaluated = bellmanVertexArr.get(minimumCostVertexChosen);
                        if (pathBeingEvaluated.nextEdge != null)
                            pathBeingEvaluated = pathBeingEvaluated.nextEdge;
                        bellmanVertexArr = BellmanFordWithAGivenStart(times, minimumCostVertexChosen);
                    }
                    else
                    {
                        timeLimit -= times[origin][pathBeingEvaluated.currentVertex];
                        if (pathBeingEvaluated.currentVertex > 0 && pathBeingEvaluated.currentVertex < times.length - 1 && !bunniesResult.contains(pathBeingEvaluated.currentVertex - 1))
                            bunniesResult.add(pathBeingEvaluated.currentVertex - 1);
                        origin = pathBeingEvaluated.currentVertex;
                        minimumCostVertexChosen = GetMinimumExcludingCurrent(bellmanVertexArr, origin, bunniesResult);
                        if(pathBeingEvaluated.nextEdge != null)
                            bellmanVertexArrFuture = BellmanFordWithAGivenStart(times, pathBeingEvaluated.nextEdge.currentVertex);
                        pathBeingEvaluated = pathBeingEvaluated.nextEdge;
                    }
                    if (pathBeingEvaluated != null)
                        pathNextChoice = pathBeingEvaluated.cost;
                    else
                        pathNextChoice = 0;
                }
            }
        }
        int[] result = null;
        if(bunniesResult.size() >= 1)
        {
            result = bunniesResult.stream().mapToInt(i -> i).toArray();
            Arrays.sort(result);
        }
        return result;
    }

    private static int GetMinimumExcludingCurrent(HashMap<Integer, Path> bellmanVertexArr, int excluding, ArrayList<Integer> bunniesPickedUp)
    {
        int rowChosen = -1;
        int maxEdges = 0;
        int minCost = Integer.MAX_VALUE;
        int newTouched = 0;
        for(int i = 0; i < bellmanVertexArr.size(); i++)
        {
            int touched = AmntOfNewVerticesTouched(bunniesPickedUp, bellmanVertexArr.get(i), bellmanVertexArr.size());
            if(i != excluding && touched >= newTouched && touched != 0)
            {
                if(bellmanVertexArr.get(i).edgesTotal > maxEdges)
                {
                    maxEdges = bellmanVertexArr.get(i).edgesTotal;
                    rowChosen = i;
                    minCost = bellmanVertexArr.get(i).cost;
                    newTouched = touched;
                }
                else if(bellmanVertexArr.get(i).edgesTotal == maxEdges && bellmanVertexArr.get(i).cost < minCost)
                {
                    minCost = bellmanVertexArr.get(i).cost;
                    rowChosen = i;
                    newTouched = touched;
                }
            }
        }
        return rowChosen;
    }

    private static int AmntOfNewVerticesTouched(ArrayList<Integer> bunniesPickedUp, Path path, int length)
    {
        Path currentPath = path;
        int amnt = 0;
        while(currentPath != null)
        {
            if(!bunniesPickedUp.contains(currentPath.currentVertex-1) && currentPath.currentVertex != 0 && currentPath.currentVertex != length-1)
                amnt++;
            currentPath = currentPath.nextEdge;
        }
        return amnt;
    }

    private static HashMap<Integer,Path> BellmanFordWithAGivenStart(int[][] times, int start) {
        HashMap<Integer,Path> bellmanVertexArr = new HashMap<>();
        for(int i = 0; i< times[0].length; i++)
        {
            bellmanVertexArr.put(i, new Path(i, Integer.MAX_VALUE,null));
        }
        bellmanVertexArr.put(start,  new Path(start,0,null));
        boolean changesWereMade = true;
        while(changesWereMade)
        {
            boolean wasThereAChange = false;
            for(int i = 0; i < times.length; i++)
            {
                if(bellmanVertexArr.get(i).cost != Integer.MAX_VALUE)
                {
                    for(int j = 0; j < times[i].length; j++)
                    {
                        //TODO: fix bug with edges not being connected correctly.
                        if(j != i && ((times[i][j] + bellmanVertexArr.get(i).cost < bellmanVertexArr.get(j).cost) ))
                        {
                            Path newPath = bellmanVertexArr.get(i).CreateCopy();
                            newPath.cost = newPath.cost + times[i][j];
                            newPath.edgesTotal++;
                            Path currentEdge = newPath;
                            while(currentEdge.nextEdge != null)
                            {
                                currentEdge.nextEdge.cost += times[i][j];
                                currentEdge = currentEdge.nextEdge;
                                currentEdge.edgesTotal++;
                            }
                            currentEdge.nextEdge = new Path(j, times[i][j], null);
                            bellmanVertexArr.put(j, newPath);
                            wasThereAChange = true;
                        }
                    }
                }
            }
            changesWereMade = wasThereAChange;
        }
        return bellmanVertexArr;
    }
    static class Path
    {
        int currentVertex;
        int cost;
        Path nextEdge;
        int edgesTotal;
        Path(int currentVertex, int totalCost, Path next)
        {
            this.currentVertex = currentVertex;
            this.nextEdge = next;
            this.cost = totalCost;
        }

        public Path CreateCopy()
        {
            Path currentPath = this;
            Path newPath = new Path(this.currentVertex, this.cost, null);
            newPath.edgesTotal = this.edgesTotal;
            Path newCurrentPath = newPath;
            currentPath = currentPath.nextEdge;
            while(currentPath != null)
            {
                newCurrentPath.nextEdge = new Path(currentPath.currentVertex, currentPath.cost, null);
                newCurrentPath.nextEdge.edgesTotal = currentPath.edgesTotal;
                newCurrentPath = newCurrentPath.nextEdge;
                currentPath = currentPath.nextEdge;
            }
            return newPath;
        }
    }

    private static boolean DFSForNegativeCycle(int i, boolean[] visited,
                                               boolean[] recStack, int[][] graph, int totalCost)
    {
        if (recStack[i] && totalCost < 0)
            return true;
        if (visited[i])
            return false;
        visited[i] = true;
        recStack[i] = true;
        HashSet<Integer> children = GetEdgesFromVertex(graph,i);

        for (Integer c: children)
        {
            boolean[] newVisited = Arrays.copyOf(visited, graph.length);
            if (DFSForNegativeCycle(c, newVisited, recStack, graph, graph[i][c] + totalCost))
                return true;
        }

        recStack[i] = false;

        return false;
    }

    private static HashSet<Integer> GetEdgesFromVertex(int[][] graph, int i)
    {
        HashSet<Integer> edges = new HashSet<>();
        for(int j = 0; j < graph.length; j++)
        {
            if(j != i)
                edges.add(j);
        }
        return edges;
    }

    private static boolean isNegativeCyclic(int[][]graph)
    {
        boolean[] visited = new boolean[graph.length];
        boolean[] recStack = new boolean[graph.length];
        for (int i = 0; i < graph.length; i++)
            if (DFSForNegativeCycle(i, visited, recStack, graph, 0))
                return true;
        return false;
    }

    public static boolean FindCycleIfExists(int[][] graph)
    {
        int cycleRow = -1;
        for(int i = 0; i < graph.length && cycleRow == -1; i++)
        {
            if(graph[i][i] < 0)
                cycleRow = i;
        }
        if(cycleRow != -1)
            return true;
        else
        {
            return isNegativeCyclic(graph);
        }
    }

}
