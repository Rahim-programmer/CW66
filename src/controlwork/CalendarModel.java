package controlwork;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CalendarModel {
    private final LocalDate currentDate = LocalDate.now();
    private int daysInMonth;
    private int currentDay;
    private String currentMonth;
    private int dayOfWeek;
    private ArrayList<Day> days = new ArrayList<>();

    public int getDaysInMonth() {
        return daysInMonth;
    }

    public void setDaysInMonth(int daysInMonth) {
        this.daysInMonth = daysInMonth;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public String getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(String currentMonth) {
        this.currentMonth = currentMonth;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public ArrayList<Day> getDays() {
        return days;
    }

    public void setDays(ArrayList<Day> days) {
        this.days = days;
    }

    public static void writeFile(ArrayList<Day> days){
        Path path = Paths.get("./days.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Day[] result = days.stream().toArray(Day[]::new);
        String json = gson.toJson(result);
        try{
            byte[] arr = json.getBytes();
            Files.write(path, arr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Day[] readDays(){
        Path path = Paths.get("./days.json");
        String json = "";
        try{
            json = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, Day[].class);
    }

    public CalendarModel() {
        daysInMonth = currentDate.lengthOfMonth();
        currentDay = currentDate.getDayOfMonth();
        currentMonth = currentDate.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
        dayOfWeek = currentDate.minusDays(currentDate.getDayOfMonth() - 1).getDayOfWeek().getValue();
        for(int i = 1; i <= currentDate.lengthOfMonth(); i++){
            days.add(new Day(LocalDate.of(currentDate.getYear(), currentDate.getMonth(), i)));
        }
        days = new ArrayList(List.of(readDays()));
    }
}
