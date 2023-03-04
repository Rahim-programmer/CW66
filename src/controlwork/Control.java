package controlwork;

import com.sun.net.httpserver.HttpExchange;
import server.BasicServer;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import server.ContentType;
import server.ResponseCodes;
import server.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Control extends BasicServer {
    private static CalendarModel model = new CalendarModel();
    private final static Configuration freemarker = initFreeMarker();

    public Control(String host, int port) throws IOException {
        super(host, port);
        registerGet("/", this::calendarHandler);
        registerGet("/tasks", this::tasksHandler);
        registerGet("/task/add", this::taskAddHandler);
        registerPost("/task/add", this::taskPostHandler);
        registerGet("/task/delete", this::taskDelete);
        registerGet("/task", this::taskHandler);
        registerPost("/task", this::taskChange);
    }

    private void taskHandler(HttpExchange exchange) {
        Map<String,Object> put = new HashMap<>();
        String quer = getQueryParams(exchange);
        Map<String, String> params = Utils.parseUrlEncoded(quer, "&");
        Task task;
        for (var day: model.getDays()) {
            if(params.get("date").equalsIgnoreCase(day.getDate().toString())){
                for (int j = 0; j < day.getTasks().size(); j++){
                    if (params.get("taskId").equalsIgnoreCase(day.getTasks().get(j).getTaskId())){
                        task = day.getTasks().get(j);
                        put.put("day", day);
                        put.put("task", task);
                        put.put("types", TypeOfTask.values());
                        break;
                    }
                }
                break;
            }
        }
        renderTemplate(exchange, "task.html", put);
    }

    private void taskDelete(HttpExchange exchange) {
        String msg = getQueryParams(exchange);
        Map<String, String> params = Utils.parseUrlEncoded(msg, "&");
        for (int f = 0; f < model.getDays().size(); f++){
            if(params.get("date").equalsIgnoreCase(model.getDays().get(f).getDate().toString())){
                for (int j = 0; j < model.getDays().get(f).getTasks().size(); j++){
                    if (params.get("taskId").equalsIgnoreCase(model.getDays().get(f).getTasks().get(j).getTaskId())){
                        model.getDays().get(f).getTasks().remove(j);
                        break;
                    }
                }
                break;
            }
        }
        CalendarModel.writeFile(model.getDays());
        redirect303(exchange, "/tasks?date" +
                "=" + params.get("date"));
    }

    private void taskChange(HttpExchange exchange) {
        String msg = getBody(exchange);
        Map<String, String> url = Utils.parseUrlEncoded(msg, "&");
        for (int f = 0; f < model.getDays().size(); f++){
            if(url.get("day").equalsIgnoreCase(model.getDays().get(f).getDate().toString())){
                for (int j = 0; j < model.getDays().get(f).getTasks().size(); j++){
                    if (url.get("taskId").equalsIgnoreCase(model.getDays().get(f).getTasks().get(j).getTaskId())){
                        model.getDays().get(f).getTasks().set(j, new Task(url.get("taskId"), url.get("name"), url.get("description"), Arrays.stream(TypeOfTask.values()).filter(e -> e.getName().equalsIgnoreCase(url.get("selected"))).findFirst().get()));
                        break;
                    }
                }
                break;
            }
        }
        CalendarModel.writeFile(model.getDays());
        redirect303(exchange, "/tasks?date=" + url.get("day"));
    }

    private void taskPostHandler(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");
        for (int i = 0; i < model.getDays().size(); i++){
            if(parsed.get("day").equalsIgnoreCase(model.getDays().get(i).getDate().toString())){
                model.getDays().get(i).getTasks().add(new Task(parsed.get("taskId"), parsed.get("name"), parsed.get("description"), Arrays.stream(TypeOfTask.values()).filter(e -> e.getName().equalsIgnoreCase(parsed.get("selected"))).findFirst().get()));
                break;
            }
        }
        CalendarModel.writeFile(model.getDays());
        redirect303(exchange, "/tasks?date=" + parsed.get("day"));
    }

    private void taskAddHandler(HttpExchange exchange) {
        Map<String,Object> data = new HashMap<>();
        String queryParams = getQueryParams(exchange);
        Map<String, String> params = Utils.parseUrlEncoded(queryParams, "&");
        Task task = new Task();

        for (var day: model.getDays()) {
            if(params.get("date").equalsIgnoreCase(day.getDate().toString())){
                data.put("day", day);
                data.put("task", task);
                data.put("types", TypeOfTask.values());
                break;
            }
        }
        renderTemplate(exchange, "add.html", data);
    }

    private void tasksHandler(HttpExchange exchange) {
        String queryParams = getQueryParams(exchange);
        Map<String, String> params = Utils.parseUrlEncoded(queryParams, "&");
        Day currentDay = new Day();
        for (var day: model.getDays()) {
            if(params.get("date").equalsIgnoreCase(day.getDate().toString())){
                currentDay = day;
            }
        }
        renderTemplate(exchange, "tasks.html", currentDay);
    }
    private void calendarHandler(HttpExchange exchange) {
        renderTemplate(exchange, "index.html", new CalendarModel());
    }
    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setDirectoryForTemplateLoading(new File("data"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            Template temp = freemarker.getTemplate(templateFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                temp.process(dataModel, writer);
                writer.flush();
                var data = stream.toByteArray();
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}
