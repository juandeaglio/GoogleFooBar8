import java.util.*;

public class Solution
{
    public static int[] RescueTheBunnies(int[][] times, int times_limit)
    {
        int[][] paths = new int[times.length][times.length];
        int[][] floydWarshallGraph = convertToFloydWarshall(times, paths);
        HashMap<Integer, Path> bellmanVertexArr = BellmanFordWithAGivenStart(times, 3);
        int[] result = null;
        if(times.length > 2)
        {
            if (FindCycleIfExists(times) || HasNegativeCycle(floydWarshallGraph, times_limit))
            {
                result = new int[times.length-2];
                for (int i = 0; i < times.length - 2; i++)
                    result[i] = i;
            }
            else
            {
                //result = BFSFloydWarshall(floydWarshallGraph, paths, times_limit, times);
            }
        }
        return result;
    }

    private static boolean HasNegativeCycle(int[][] floydWarshallGraph, int timeLimit)
    {
        int max = Integer.MAX_VALUE;
        for(int i = 0; i < floydWarshallGraph.length; i++)
            max = Math.min(max, floydWarshallGraph[i][i]);
        return max < 0;
    }

    private static int[][] convertToFloydWarshall(int[][] times, int[][] paths)
    {
        int[][] distances = new int[times.length][times.length];
        for(int i = 0; i < distances.length; i++)
        {
            distances[i][i] = 0;
        }
        for(int i = 0; i < distances.length; i++)
        {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
            for(int j = 0; j < distances.length; j++)
            {
                distances[i][j] = times[i][j];
            }
        }
        for(int i = 0; i < distances.length; i++)
        {
            for(int j = 0; j < distances.length; j++)
            {
                for(int k = 0; k < distances.length; k++)
                {
                    if(distances[j][k] > distances[j][i] + distances[i][k])
                    {
                        distances[j][k] = distances[j][i] + distances[i][k];
                    }
                }
            }
        }
        return distances;
    }
    private static int[] BFSFloydWarshall(int[][]floydWarshallGraph, int[][] paths, int timeLimit, int[][] times)
    {
        Queue<HashMap<Integer,HashSet<Integer>>> visitedQueue = new ArrayDeque<>();
        Queue<Path> pathTaken = new ArrayDeque<>();
        pathTaken.add(new Path(0, 0, null));
        visitedQueue.add(new HashMap<Integer,HashSet<Integer>>());
        HashMap<Integer, HashSet<Integer>> visitedTemp = visitedQueue.poll();
        visitedTemp.put(0, new HashSet<>());
        visitedQueue.add(visitedTemp);
        int edgesUsed = 0;
        int maxVerticesVisited = 0;
        int max = 0;
        for(int i = 0; i < floydWarshallGraph.length; i++)
            max = Math.max(max, paths[0][i])+1;
        Path temp = null;
        Path bestPath = null;
        int verticesExitedWith = 0;
        while ((!pathTaken.isEmpty() || temp != null) && pathTaken.peek().edgesTotal <= max)
        {
            temp = pathTaken.poll();
            HashMap<Integer, HashSet<Integer>> currentVisited = visitedQueue.poll();
            if (temp != null && currentVisited != null)
            {
                for (int i = 0; i < floydWarshallGraph.length; i++)
                {
                    int current = getLastVertexOf(temp);
                    currentVisited.computeIfAbsent(current, k -> new HashSet<>());
                    if (i != current && !currentVisited.get(current).contains(i))
                    {
                        HashMap<Integer, HashSet<Integer>> visited = CopyHashMap((currentVisited));
                        Path newPath = temp.CreateCopy();
                        newPath.edgesTotal++;
                        newPath.cost += times[current][i];
                        Path currentPath = newPath;
                        while (currentPath.nextEdge != null)
                        {
                            currentPath.nextEdge.edgesTotal++;
                            currentPath.nextEdge.cost += times[current][i];
                            currentPath = currentPath.nextEdge;
                        }
                        currentPath.nextEdge = new Path(i, 0, null);
                        visited.get(current).add(i);
                        pathTaken.add(newPath);
                        visitedQueue.add(visited);
                        if (EndsAtTerminal(newPath, floydWarshallGraph.length) && verticesTouched(newPath, times.length) > verticesExitedWith && newPath.cost <= timeLimit)
                        {
                            bestPath = newPath;
                            verticesExitedWith = verticesTouched(bestPath,times.length);
                        }
                    }
                }
            }
        }

        int[] result = null;
        HashSet<Integer> bunniesFound = new HashSet<>();
        if(bestPath != null && bestPath.edgesTotal > 2)
        {
            int count = 0;
            while(bestPath != null)
            {
                if(bestPath.currentVertex != 0 && bestPath.currentVertex != times.length-1)
                {
                    bunniesFound.add(bestPath.currentVertex-1);
                }
                bestPath = bestPath.nextEdge;
            }
            result = new int[bunniesFound.size()];
            for (Integer vertex: bunniesFound)
            {
                result[count] = vertex;
                count++;
            }
            Arrays.sort(result);
        }
        return result;
    }

    private static int verticesTouched(Path newPath, int length)
    {
        HashSet<Integer> bunniesFound = new HashSet<>();
        Path path1 = newPath;
        if(newPath != null && newPath.edgesTotal > 2)
        {
            while (path1 != null)
            {
                if (path1.currentVertex != 0 && path1.currentVertex != length - 1)
                {
                    bunniesFound.add(path1.currentVertex - 1);
                }
                path1 = path1.nextEdge;
            }
        }
        return bunniesFound.size();
    }

    private static boolean HasSmallerPathThanOther(Path newPath, Path bestPath, int length)
    {
        int[] result = null;
        int[] result2 = null;
        HashSet<Integer> bunniesFound = new HashSet<>();
        Path path1 = newPath;
        Path path2 = bestPath;
        if(bestPath != null && bestPath.edgesTotal > 2)
        {
            while(path1 != null)
            {
                if(path1.currentVertex != 0 && path1.currentVertex != length-1)
                {
                    bunniesFound.add(path1.currentVertex-1);
                }
                path1 = path1.nextEdge;
            }
            result = new int[bunniesFound.size()];
            for (Integer vertex: bunniesFound)
            {
                result[0] = vertex;
            }
            Arrays.sort(result);
            bunniesFound = new HashSet<>();
            while(path2 != null)
            {
                if(path2.currentVertex != 0 && path2.currentVertex != length-1)
                {
                    bunniesFound.add(path2.currentVertex-1);
                }
                path2 = path2.nextEdge;
            }
            result2 = new int[bunniesFound.size()];
            for (Integer vertex: bunniesFound)
            {
                result2[0] = vertex;
            }
            Arrays.sort(result2);
            for(int i = 0; i < result.length; i++)
            {
                if(result[i] < result2[i])
                    return true;
            }
        }

        return false;
    }

    private static HashMap<Integer, HashSet<Integer>> CopyHashMap(HashMap<Integer, HashSet<Integer>> currentVisited)
    {
        HashMap<Integer, HashSet<Integer>> newMap = new HashMap<>();
        currentVisited.forEach((k,v) ->
        {
            HashSet<Integer> copy = new HashSet<>(v);
            newMap.put(k,copy);
        });
        return newMap;
    }

    private static int getLastVertexOf(Path temp)
    {
        Path newPath = temp;
        int lastVertex = 0;
        while(newPath != null)
        {
            lastVertex = newPath.currentVertex;
            newPath = newPath.nextEdge;
        }
        return lastVertex;
    }

    private static boolean EndsAtTerminal(Path newPath, int endLength)
    {
        Path currentPath = newPath;
        int lastVertex = 0;
        while(currentPath.nextEdge != null)
        {
            currentPath = currentPath.nextEdge;
            lastVertex = currentPath.currentVertex;
        }
        return lastVertex == endLength - 1;
    }

    public static int[] FindBunniesUsingBellman(int[][] times, int times_limit)
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
                int[][] bellmanGraph = new int[times.length][times.length];
                for(int i = 0; i < times.length; i++)
                {
                    for(int j = 0; j < times.length; j++)
                    {
                        bellmanGraph[i][j] = BellmanFordWithAGivenStart(times, i).get(j).cost;
                    }
                }
                int minimumCostVertexChosen = GetMinimumExcludingCurrent(bellmanVertexArr, 0, bunniesResult,timeLimit);
                int origin = 0;
                HashMap<Integer, Path> bellmanVertexArrFuture = BellmanFordWithAGivenStart(times, minimumCostVertexChosen);
                Path pathBeingEvaluated = null;
                int pathNextChoice = 0;
                while ((timeLimit - pathNextChoice >= bellmanVertexArrFuture.get(times.length - 1).cost) && minimumCostVertexChosen != -1)
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
                        minimumCostVertexChosen = GetMinimumExcludingCurrent(bellmanVertexArr, origin, bunniesResult,timeLimit);
                        if(pathBeingEvaluated.nextEdge != null)
                            bellmanVertexArrFuture = BellmanFordWithAGivenStart(times, pathBeingEvaluated.nextEdge.currentVertex);
                        pathBeingEvaluated = pathBeingEvaluated.nextEdge;
                    }
                    if (pathBeingEvaluated != null)
                        pathNextChoice = times[origin][pathBeingEvaluated.currentVertex];
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

    private static int GetMinimumExcludingCurrent(HashMap<Integer, Path> bellmanVertexArr, int excluding, ArrayList<Integer> bunniesPickedUp, int timeLimit)
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
        int timesDone = 0;
        while(timesDone < times[0].length )
        {
            for(int i = 0; i < times.length; i++)
            {
                if(bellmanVertexArr.get(i).cost != Integer.MAX_VALUE)
                {
                    for(int j = 0; j < times[i].length; j++)
                    {
                        if(j != i && ((bellmanVertexArr.get(i).cost + times[i][j] < bellmanVertexArr.get(j).cost) || ((bellmanVertexArr.get(i).cost + times[i][j] == bellmanVertexArr.get(j).cost) && IsNewAndNotCyclical(bellmanVertexArr.get(i), times.length))))
                        {
                            Path newPath;
                            newPath = bellmanVertexArr.get(i).CreateCopy();
                            newPath.cost = newPath.cost + times[i][j];
                            newPath.edgesTotal++;
                            Path currentEdge = newPath;
                            while(currentEdge.nextEdge != null)
                            {
                                currentEdge.nextEdge.cost += times[i][j];
                                currentEdge = currentEdge.nextEdge;
                                currentEdge.edgesTotal++;
                            }
                            currentEdge.nextEdge = new Path(j, 0, null);
                            bellmanVertexArr.put(j, newPath);
                        }
                    }
                }
            }
            timesDone++;
        }
        return bellmanVertexArr;
    }

    private static boolean IsNewAndNotCyclical(Path currentPath, int maxLen)
    {
        boolean[] verticesVisited = new boolean[maxLen];
        boolean[] compareVertices = new boolean[maxLen];
        boolean cycleDetected = false;
        boolean cycleEnded = false;
        int count = 0;
        int start = 0;
        int iterations = 0;
        //if(verticesTouched(currentPath, maxLen) + IsVertexABunny(currentPath, currentJ, maxLen) > verticesTouched(currentPath, maxLen))
        Path tempPath = currentPath;

        while(tempPath != null && !cycleEnded)
        {
            if(verticesVisited[tempPath.currentVertex])
            {
                if(!cycleDetected)
                {
                    start = iterations;
                    cycleDetected = true;
                }
                count++;
            }
            else
            {
                verticesVisited[tempPath.currentVertex] = true;
                if(cycleDetected)
                {
                    cycleEnded = true;
                    cycleDetected = false;
                }
            }
            tempPath = tempPath.nextEdge;
            iterations++;
        }
        tempPath = currentPath;
        while(tempPath != null && !cycleDetected)
        {
            if(compareVertices[tempPath.currentVertex])
            {
                cycleDetected = true;
            }
            tempPath = tempPath.nextEdge;
        }
        boolean sameOrIsSubsetOf = true;
        for(int i = 0; i < compareVertices.length && sameOrIsSubsetOf; i++)
        {
            if (compareVertices[i] != verticesVisited[i])
                sameOrIsSubsetOf = false;
        }
        return sameOrIsSubsetOf;
    }

    private static int IsVertexABunny(Path pathTaken , int j, int length)
    {
        Path tempPath = pathTaken;
        boolean contains = false;
        while(tempPath != null)
        {
            if (tempPath.currentVertex == j) {
                contains = true;
                break;
            }
            tempPath = tempPath.nextEdge;
        }
        if (j != 0 && j != length-1 && !contains)
            return 1;
        return 0;
    }

    private static boolean DoesPathCycle(int[][] floydWarGraph, Path path, int toAdd)
    {
        int totalCost = path.cost;
        Path tempPath = path;
        HashSet<Integer> verticesVisited = new HashSet<>();
        while(tempPath != null)
        {
            if(verticesVisited.contains(tempPath.currentVertex))
                return true;
            verticesVisited.add(tempPath.currentVertex);
            tempPath = tempPath.nextEdge;
        }
        return verticesVisited.contains(toAdd);
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
