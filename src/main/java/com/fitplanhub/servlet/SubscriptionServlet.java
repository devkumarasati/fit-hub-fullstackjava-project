package com.fitplanhub.servlet;

import com.fitplanhub.dao.AuthTokenDAO;
import com.fitplanhub.dao.FitnessPlanDAO;
import com.fitplanhub.dao.SubscriptionDAO;
import com.fitplanhub.model.FitnessPlan;

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

@WebServlet("/api/subscriptions")
public class SubscriptionServlet extends HttpServlet {

    private SubscriptionDAO subscriptionDAO;
    private FitnessPlanDAO planDAO;
    private AuthTokenDAO tokenDAO;

    @Override
    public void init() {
        subscriptionDAO = new SubscriptionDAO();
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

            List<FitnessPlan> subscribedPlans = subscriptionDAO.getUserSubscribedPlans(userId);
            JSONArray plansArray = new JSONArray();

            for (FitnessPlan plan : subscribedPlans) {
                JSONObject planJson = new JSONObject();
                planJson.put("planId", plan.getPlanId());
                planJson.put("title", plan.getTitle());
                planJson.put("description", plan.getDescription());
                planJson.put("trainerName", plan.getTrainerName());
                planJson.put("price", plan.getPrice());
                planJson.put("durationDays", plan.getDurationDays());
                plansArray.add(planJson);
            }

            responseJson.put("success", true);
            responseJson.put("subscriptions", plansArray);
            response.setStatus(HttpServletResponse.SC_OK);

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

            int planId = ((Number) requestJson.get("planId")).intValue();

            if (subscriptionDAO.isUserSubscribed(userId, planId)) {
                responseJson.put("success", false);
                responseJson.put("message", "Already subscribed to this plan");
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print(responseJson.toString());
                out.flush();
                return;
            }

            boolean subscribed = subscriptionDAO.subscribeToPlan(userId, planId);

            if (subscribed) {
                responseJson.put("success", true);
                responseJson.put("message", "Successfully subscribed");
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                responseJson.put("success", false);
                responseJson.put("message", "Subscription failed");
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

    private int getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return tokenDAO.validateToken(token);
        }

        return -1;
    }
}