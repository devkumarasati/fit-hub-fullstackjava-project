package com.fitplanhub.servlet;

import com.fitplanhub.dao.AuthTokenDAO;
import com.fitplanhub.dao.UserDAO;
import com.fitplanhub.model.User;
import com.fitplanhub.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@WebServlet("/api/auth")
public class AuthServlet extends HttpServlet {

    private UserDAO userDAO;
    private AuthTokenDAO tokenDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        tokenDAO = new AuthTokenDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        try {

            String action = request.getParameter("action");

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }

            JSONParser parser = new JSONParser();
            JSONObject requestJson = (JSONObject) parser.parse(sb.toString());

            if ("signup".equals(action)) {
                handleSignup(requestJson, responseJson, response);
            } else if ("login".equals(action)) {
                handleLogin(requestJson, responseJson, response);
            } else if ("logout".equals(action)) {
                handleLogout(request, responseJson, response);
            } else {
                responseJson.put("success", false);
                responseJson.put("message", "Invalid action");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "Server error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.print(responseJson.toString());
        out.flush();
    }

    private void handleSignup(JSONObject requestJson, JSONObject responseJson,
            HttpServletResponse response) {

        String fullName = (String) requestJson.get("fullName");
        String email = (String) requestJson.get("email");
        String password = (String) requestJson.get("password");
        String userType = (String) requestJson.get("userType");

        if (fullName == null || email == null || password == null || userType == null) {
            responseJson.put("success", false);
            responseJson.put("message", "All fields are required");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (userDAO.emailExists(email)) {
            responseJson.put("success", false);
            responseJson.put("message", "Email already registered");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User(fullName, email, hashedPassword, userType);

        boolean registered = userDAO.registerUser(user);

        if (registered) {
            responseJson.put("success", true);
            responseJson.put("message", "Registration successful");
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            responseJson.put("success", false);
            responseJson.put("message", "Registration failed");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleLogin(JSONObject requestJson, JSONObject responseJson,
            HttpServletResponse response) {

        String email = (String) requestJson.get("email");
        String password = (String) requestJson.get("password");

        if (email == null || password == null) {
            responseJson.put("success", false);
            responseJson.put("message", "Email and password required");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User user = userDAO.loginUser(email, password);

        if (user != null) {

            String token = tokenDAO.createToken(user.getUserId());

            if (token != null) {
                responseJson.put("success", true);
                responseJson.put("message", "Login successful");
                responseJson.put("token", token);
                responseJson.put("userId", user.getUserId());
                responseJson.put("fullName", user.getFullName());
                responseJson.put("userType", user.getUserType());
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                responseJson.put("success", false);
                responseJson.put("message", "Token creation failed");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            responseJson.put("success", false);
            responseJson.put("message", "Invalid email or password");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void handleLogout(HttpServletRequest request, JSONObject responseJson,
            HttpServletResponse response) {

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            tokenDAO.deleteToken(token);
        }

        responseJson.put("success", true);
        responseJson.put("message", "Logged out successfully");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}