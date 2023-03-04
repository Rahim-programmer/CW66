import controlwork.CalendarModel;
import controlwork.Control;

import java.io.IOException;

public class Main {
    private static CalendarModel calendarModel = new CalendarModel();
    public static void main(String[] args){
        try {
            new Control("localhost", 9889).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
