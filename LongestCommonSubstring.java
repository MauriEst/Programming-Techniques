public class LongestCommonSubstring {

    /**
     * Finds the longest common substring between two strings using dynamic programming.
     *
     * @param x The first string.
     * @param y The second string.
     * @return A string containing the longest common substring.
     */
    public String findLongestCommonSubstring(String x, String y) {
        int m = x.length();
        int n = y.length();
        int[][] dp = new int[m + 1][n + 1];
        int maxLength = 0;
        int endPosInX = 0;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (x.charAt(i - 1) == y.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    if (dp[i][j] > maxLength) {
                        maxLength = dp[i][j];
                        endPosInX = i; // the ending index in string X
                    }
                } else {
                    dp[i][j] = 0;
                }
            }
        }

        System.out.println("DP Table:");
        printTable(dp, x, y);

        if (maxLength == 0) {
            return "";
        } else {
            return x.substring(endPosInX - maxLength, endPosInX);
        }
    }

    /**
     * Helper function to print the DP table.
     */
    private void printTable(int[][] table, String x, String y) {
        System.out.print("       ");
        for (char c : y.toCharArray()) {
            System.out.printf("%-3c", c);
        }
        System.out.println();
        System.out.print("    ");
        for (int val : table[0]) {
            System.out.printf("%-3d", val);
        }
        System.out.println();
        for (int i = 1; i < table.length; i++) {
            System.out.printf("%-3c ", x.charAt(i - 1));
            for (int j = 0; j < table[i].length; j++) {
                System.out.printf("%-3d", table[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        LongestCommonSubstring lcs = new LongestCommonSubstring();

        String x1 = "ABCBDAB";
        String y1 = "BDCABA";
        System.out.println("--> Test Case 1 <--");
        System.out.println("Input X: " + x1);
        System.out.println("Input Y: " + y1);
        String result1 = lcs.findLongestCommonSubstring(x1, y1);
        System.out.println("\nOutput (Longest Common Substring): " + result1);
        System.out.println("---------------------\n");

        String x2 = "banana";
        String y2 = "ananas";
        System.out.println("--> Test Case 2 <--");
        System.out.println("Input X: " + x2);
        System.out.println("Input Y: " + y2);
        String result2 = lcs.findLongestCommonSubstring(x2, y2);
        System.out.println("\nOutput (Longest Common Substring): " + result2);
        System.out.println("---------------------\n");

        String x3 = "abc";
        String y3 = "def";
        System.out.println("--> Test Case 3 <--");
        System.out.println("Input X: " + x3);
        System.out.println("Input Y: " + y3);
        String result3 = lcs.findLongestCommonSubstring(x3, y3);
        System.out.println("\nOutput (Longest Common Substring): " + result3);
        System.out.println("---------------------\n");

        String x4 = "whatchamacallit";
        String y4 = "youmeantosaywhatnow";
        System.out.println("--> Test Case 4 <--");
        System.out.println("Input X: " + x4);
        System.out.println("Input Y: " + y4);
        String result4 = lcs.findLongestCommonSubstring(x4, y4);
        System.out.println("\nOutput (Longest Common Substring): " + result4);
        System.out.println("---------------------\n");
    }
}