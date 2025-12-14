package com.fitplanhub.servlet;

import com.fitplanhub.dao.AuthTokenDAO;
import com.fitplanhub.dao.FollowDAO;
import com.fitplanhub.dao.FitnessPlanDAO;
import com.fitplanhub.model.FitnessPlan;
import com.fitplanhub.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@WebServlet("/api/follows")
public class FollowServlet extends HttpServlet {

    private FollowDAO followDAO;
    private FitnessPlanDAO planDAO;
    private AuthTokenDAO tokenDAO;

    @Override
    public void init() {
        followDAO = new FollowDAO();
        planDAO = new FitnessPlanDAO();
        tokenDAO = new AuthTokenDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        try {
            int userId = getUserIdFromToken(request);

            if (userId == -1) {
                responseJson.put("success", false);
                responseJson.put("message", "Unauthorized");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print(responseJson.toString());
                out.flush();
                return;
            }

            String action = request.getParameter("action");

            if ("trainers".equals(action)) {
                getFollowedTrainers(userId, responseJson, response);
            } else if ("feed".equals(action)) {
                getPersonalizedFeed(userId, responseJson, response);
            } else if ("checkFollow".equals(action)) {
                checkFollowStatus(userId, request, responseJson, response);
            } else {
                responseJson.put("success", false);
                responseJson.put("message", "Invalid action");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.print(responseJson.toString());
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        try {
            int userId = getUserIdFromToken(request);

            if (userId == -1) {
                responseJson.put("success", false);
                responseJson.put("message", "Unauthorized");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print(responseJson.toString());
                out.flush();
                return;
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }

            JSONParser parser = new JSONParser();
            JSONObject requestJson = (JSONObject) parser.parse(sb.toString());

            int trainerId = ((Number) requestJson.get("trainerId")).intValue();

            boolean followed = followDAO.followTrainer(userId, trainerId);

            if (followed) {
                responseJson.put("success", true);
                responseJson.put("message", "Successfully followed trainer");
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                responseJson.put("success", false);
                responseJson.put("message", "Failed to follow trainer");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.print(responseJson.toString());
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        try {
            int userId = getUserIdFromToken(request);

            if (userId == -1) {
                responseJson.put("success", false);
                responseJson.put("message", "Unauthorized");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print(responseJson.toString());
                out.flush();
                return;
            }

            String trainerIdStr = request.getParameter("trainerId");
            int trainerId = Integer.parseInt(trainerIdStr);

            boolean unfollowed = followDAO.unfollowTrainer(userId, trainerId);

            if (unfollowed) {
                responseJson.put("success", true);
                responseJson.put("message", "Successfully unfollowed trainer");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                responseJson.put("success", false);
                responseJson.put("message", "Failed to unfollow trainer");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.print(responseJson.toString());
        out.flush();
    }

    private void getFollowedTrainers(int userId, JSONObject responseJson,
            HttpServletResponse response) {

        List<User> trainers = followDAO.getFollowedTrainers(userId);
        JSONArray trainersArray = new JSONArray();

        for (User trainer : trainers) {
            JSONObject trainerJson = new JSONObject();
            trainerJson.put("trainerId", trainer.getUserId());
            trainerJson.put("fullName", trainer.getFullName());
            trainerJson.put("email", trainer.getEmail());
            trainersArray.add(trainerJson);
        }

        responseJson.put("success", true);
        responseJson.put("trainers", trainersArray);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void getPersonalizedFeed(int userId, JSONObject responseJson,
            HttpServletResponse response) {

        List<FitnessPlan> plans = planDAO.getPlansFromFollowedTrainers(userId);
        JSONArray plansArray = new JSONArray();

        for (FitnessPlan plan : plans) {
            JSONObject planJson = new JSONObject();
            planJson.put("planId", plan.getPlanId());
            planJson.put("title", plan.getTitle());
            planJson.put("description", plan.getDescription());
            planJson.put("trainerName", plan.getTrainerName());
            planJson.put("trainerId", plan.getTrainerId());
            planJson.put("price", plan.getPrice());
            planJson.put("durationDays", plan.getDurationDays());
            plansArray.add(planJson);
        }

        responseJson.put("success", true);
        responseJson.put("feed", plansArray);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void checkFollowStatus(int userId, HttpServletRequest request,
            JSONObject responseJson, HttpServletResponse response) {

        String trainerIdStr = request.getParameter("trainerId");
        int trainerId = Integer.parseInt(trainerIdStr);

        boolean isFollowing = followDAO.isFollowing(userId, trainerId);

        responseJson.put("success", true);
        responseJson.put("isFollowing", isFollowing);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private int getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return tokenDAO.validateToken(token);
        }

        return -1;
    }
}