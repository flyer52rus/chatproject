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
        while(true) {
            try {
                return bufferedReader.readLine();
            } catch (IOException ex) {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }
    }


    public static int readInt() {
        while(true) {
            try {
                return Integer.parseInt(readString());
            } catch (NumberFormatException ex) {
                System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            }
        }
    }


}


//class Test1 {
//    public static void main(String[] args) {
//        ConsoleHelper consoleHelper = new ConsoleHelper();
//        String str = consoleHelper.readString();
//
//
//
//        System.out.println("воу, readString работает и вернул - " + str);
//
//        int i = consoleHelper.readInt();
//
//        System.out.println("воу, readInt работает и вернул - " + i);
//
//
//    }
//}