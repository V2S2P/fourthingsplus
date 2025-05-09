package org.example.controllers;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.example.entities.Task;
import org.example.entities.User;
import org.example.exceptions.DatabaseException;
import org.example.persistence.ConnectionPool;
import org.example.persistence.TaskMapper;

import java.util.List;

public class TaskController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool){
        app.post("addtask",ctx -> addtask(ctx, connectionPool));
        app.post("done", ctx -> done(ctx, true, connectionPool));
        app.post("undo", ctx -> done(ctx, false, connectionPool));
        app.post("deletetask",ctx -> deletetask(ctx, connectionPool));
    }

    private static void deletetask(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        try {
            int taskId = Integer.parseInt(ctx.formParam("taskId"));
            TaskMapper.delete(taskId, connectionPool);
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
            ctx.attribute("taskList", taskList);
            ctx.render("task.html");
        }catch (DatabaseException | NumberFormatException e){
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }
    }

    private static void done(Context ctx, boolean done, ConnectionPool connectionPool){
        User user = ctx.sessionAttribute("currentUser");
        try {
            int taskId = Integer.parseInt(ctx.formParam("taskId"));
            TaskMapper.setDoneTo(done, taskId, connectionPool);
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
            ctx.attribute("taskList", taskList);
            ctx.render("task.html");
        }catch (DatabaseException | NumberFormatException e){
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }
    }
    private static void addtask(Context ctx, ConnectionPool connectionPool){
        String taskName = ctx.formParam("taskname");
        if (taskName.length() > 3){
            User user = ctx.sessionAttribute("currentUser");
            try {
                Task newTask = TaskMapper.addTask(user,taskName,connectionPool);
                List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
                ctx.attribute("taskList", taskList);
                ctx.render("task.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", "Something went wrong, please try again.");
                ctx.render("task.html");
            }
        }
    }
}
