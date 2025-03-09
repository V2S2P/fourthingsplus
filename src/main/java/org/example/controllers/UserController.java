package org.example.controllers;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.example.entities.Task;
import org.example.entities.User;
import org.example.exceptions.DatabaseException;
import org.example.persistence.ConnectionPool;
import org.example.persistence.TaskMapper;
import org.example.persistence.UserMapper;

import java.util.List;

public class UserController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool){
        app.post("login",ctx -> login(ctx, connectionPool));
    }
    public static void login(Context ctx, ConnectionPool connectionPool){
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(username, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
            ctx.attribute("taskList", taskList);
            ctx.render("task.html");
        } catch (DatabaseException e) {
            ctx.attribute("messages", e.getMessage());
            ctx.render("index.html");
        }
    }
}
