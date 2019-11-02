package com.mypro.spark.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/7/25
 */
public class ShellUtil {

    public static String executeShell(String shellCommand) throws Exception{
        String returnString = "";
        Runtime runTime = Runtime.getRuntime();
        Process pro = runTime.exec(shellCommand);
        BufferedReader input = new BufferedReader(new InputStreamReader(pro.getInputStream()));
        PrintWriter output = new PrintWriter(new OutputStreamWriter(pro.getOutputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            returnString = returnString + line + "\n";
        }
        input.close();
        output.close();
        pro.destroy();
        return returnString;
    }

}
