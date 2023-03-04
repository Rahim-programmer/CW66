package controlwork;

public enum TypeOfTask {

    ORDINARY("обычная задача", "blue"),
    URGENT("срочное дело", "red"),
    WORK("работа", "green"),
    SHOPPING("покупка", "black"),
    OTHER("прочее", "brown");


    private String name;
    private String color;

    TypeOfTask(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}

