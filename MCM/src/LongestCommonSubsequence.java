public class LongestCommonSubsequence {
    //动态规划
    public int longestCommonSubsequence(String str1, String str2) {
        char[] arr1 = str1.toCharArray();
        char[] arr2 = str2.toCharArray();
        int m = arr1.length;
        int n = arr2.length;
        //转移矩阵
        int[][] dp = new int[m+1][n+1];
        //核心思想为，如果str1[i]=str2[j]，那么dp[i][j] = dp[i-1][j-1]+1，否则dp[i][j] = max(dp[i-1][j], dp[i][j-1])
        for (int i=1; i<m+1; i++) {
            for (int j=1; j<n+1; j++) {
                if (arr1[i-1] == arr2[j-1]) {
                    dp[i][j] = dp[i-1][j-1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
                }
            }
        }
        return dp[m][n];
    }

    //递归
    public int longestCommonSubsequence2(String str1, String str2) {
        if (str1.isEmpty() || str2.isEmpty()) return 0;
        int m = str1.length();
        int n = str2.length();
        if (str1.charAt(m-1) == str2.charAt(n-1)) {
            return longestCommonSubsequence2(str1.substring(0, m-1), str2.substring(0, n-1)) + 1;
        }
        return Math.max(longestCommonSubsequence2(str1.substring(0, m), str2.substring(0, n-1)),
                longestCommonSubsequence2(str1.substring(0, m-1), str2.substring(0, n)));
    }

    public static void main(String[] args) {
        LongestCommonSubsequence test = new LongestCommonSubsequence();
        String str1 = "springtime";
        String str2 = "printing";
        System.out.println("str1:" + str1 + "; str2:" + str2);
        System.out.println("longestCommonSubsequence:" + test.longestCommonSubsequence2(str1, str2));
    }

}
