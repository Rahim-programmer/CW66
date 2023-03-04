package controlwork;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;

public class Day {
    private ArrayList<Task> tasks = new ArrayList<>();
    private final LocalDate date;
    private String nameOfDate;

    public String getNameOfDate() {
        return nameOfDate;
    }

    public void setNameOfDate(String nameOfDate) {
        this.nameOfDate = nameOfDate;
    }

    public Day() {
        this(LocalDate.now());
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public LocalDate getDate() {
        return date;
    }

    public Day(LocalDate date) {
        this.date = date;
        nameOfDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(new Locale("ru")));
    }
}
