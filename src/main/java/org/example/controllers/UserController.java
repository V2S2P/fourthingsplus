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
        app.get("logout",ctx -> logout(ctx));
        app.get("createuser", ctx -> ctx.render("createuser.html"));
        app.post("createuser", ctx -> createUser(ctx, connectionPool));
    }
    private static void createUser(Context ctx, ConnectionPool connectionPool){
        String username = ctx.formParam("username");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if (password1.equals(password2)){
            try {
                UserMapper.createuser(username, password1, connectionPool);
                ctx.attribute("message", "Congratulations, you have created an account!");
                ctx.render("index.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", "Username already exists. Try again.");
                ctx.render("createuser.html");
            }
        }else {
            ctx.attribute("message", "Your passwords don't match");
            ctx.render("createuser.html");
        }
    }
    public static void logout(Context ctx){
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
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
