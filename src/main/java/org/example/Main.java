package org.example;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.example.config.ThymeleafConfig;
import org.example.controllers.TaskController;
import org.example.controllers.UserController;
import org.example.persistence.ConnectionPool;

public class Main {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "fourthingsplus";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);


    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            //config.jetty.modifyServletContextHandler(handler ->  handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Routing

        app.get("/", ctx ->  ctx.render("index.html"));
        UserController.addRoutes(app, connectionPool);
        TaskController.addRoutes(app, connectionPool);
    }
}