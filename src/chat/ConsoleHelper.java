package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));


    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        String str = null;
        do {
            try {
                str = bufferedReader.readLine();
            } catch (IOException ex) {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        } while (str == null);
        return str;
    }

    public static int readInt() {
        int numb = -2_111_222_333;
        do {
            try {
                numb = Integer.parseInt(readString());

            } catch (NumberFormatException ex) {
                System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            }
        } while (numb == -2_111_222_333);
        return numb;
    }


}
