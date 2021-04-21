import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SolutionUnitTests
{
    @Test
    public void ShouldReturnNull()
    {
        int[][]graph = {
                {0, 1, 1, 1, 1},
                {1, 0, 1, 1, 1},
                {1, 1, 0, 1, 1},
                {1, 1, 1, 0, 1},
                {1, 1, 1, 1, 0}};
        int timeLimit = -1;
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertNull(actualResult);
    }
    @Test
    public void ShouldReturnTwoBunnies()
    {
        int[][]graph = {
                {0, 1, 1, 1, 1},
                {1, 0, 1, 1, 1},
                {1, 1, 0, 1, 1},
                {1, 1, 1, 0, 1},
                {1, 1, 1, 1, 0}};
        int timeLimit = -1;
        int[] expectedResult = {0,1};
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertArrayEquals(expectedResult, actualResult);
    }
    @Test
    public void ShouldReturnTwoBunniesWithCycles()
    {
        int[][]graph = {
                {0, 2, 2, 2, -1},
                {9, 0, 2, 2, -1},
                {9, 3, 0, 2, -1},
                {9, 3, 2, 0, -1},
                {9, 3, 2, 2, 0}
        };
        int timeLimit = -1;
        int[] expectedResult = {1,2};
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertArrayEquals(expectedResult, actualResult);
    }
    @Test
    public void ShouldFindACycleWithinAnIncompleteGraph()
    {
        int[][]graph = {
                {0, 1, 1, 1, 1},
                {1, 0, 1, 1, 1},
                {1, 1, 0, 1, 1},
                {1, 1, 1, 0, 1},
                {1, 1, 1, 1, -1}};
        int expectedResult = graph.length-1;
        int actualResult = Solution.FindCycleIfExists(graph);
        Assertions.assertEquals(expectedResult, actualResult);
    }
    @Test
    public void ShouldFindAnInfiniteCycle()
    {
        int[][]graph = {
                {0, 1, 1, 1, 1},
                {1, 0, 1, 1, 1},
                {1, 1, 0, 1, 1},
                {1, 1, 1, 0, 1},
                {1, 1, 1, 1, -1}};
        boolean expectedResult = true;
        boolean actualResult = Solution.IsCycleInfinite(graph, Solution.FindCycleIfExists(graph));
        Assertions.assertEquals(expectedResult, actualResult);
    }
    @Test
    public void ShouldReturnAllPossibleBunnies()
    {
        int[][]graph = {
                {0, 1, 1, 1, 1},
                {1, 0, 1, 1, 1},
                {1, 1, 0, 1, 1},
                {1, 1, 1, 0, 1},
                {1, 1, 1, 1, -1}};
        int timeLimit = -100;
        int[] expectedResult = {0,1,2};
        int[]actualResult = Solution.RescueTheBunnies(graph, timeLimit);
        Assertions.assertArrayEquals(expectedResult, actualResult);
    }
}
