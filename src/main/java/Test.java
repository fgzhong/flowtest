import java.io.*;

/**
 * @author fgzhong
 * @since 2018/12/28
 */
public class Test implements Serializable{

    public static void main(String[] args) throws Exception{

        new Thread(() ->{
            while (true) {
                try {
//                    Thread.sleep(10);
                    int i=0;
                    i++;
                } catch (Exception e) {}
            }
        }).start();
        new Thread(() ->{
            while (true) {
                try {
//                    Thread.sleep(50);
                    int i=0;
                    i++;
                } catch (Exception e) {}
            }
        }).start();
    }

    public static double minDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();
        int sum = len1 + len2;

        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        // iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            char c1 = word1.charAt(i);
            for (int j = 0; j < len2; j++) {
                char c2 = word2.charAt(j);

                // if last two chars equal
                if (c1 == c2) {
                    // update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 2;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }

        int idist = dp[len1][len2];
        double ratio = (double) (sum - idist) / sum;
        return ratio;
    }
}
