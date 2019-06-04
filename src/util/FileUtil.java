package util;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtil {
    public static List<Integer> readFile(String filePath) {
        List<Integer> re = new ArrayList<>();
        Scanner scanner;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            scanner = new Scanner(new FileInputStream(new File(filePath)));

            while (scanner.hasNextLine()) {
                stringBuffer.append(scanner.nextLine());
            }
            String[] ints = stringBuffer.toString().split(",");
            for (String oneInt : ints) {
                re.add(Integer.valueOf(oneInt));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return re;
    }

    @Test
    public void testFileRead() {
        List<Integer> re = readFile("F:\\java_IDEA\\AlogrithmAnalysis\\src\\list");
       re.forEach(System.out::println);
    }

}
