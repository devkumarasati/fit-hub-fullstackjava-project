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

@WebServlet("/api/plans")
public class PlanServlet extends HttpServlet {

    private FitnessPlanDAO planDAO;
    private AuthTokenDAO tokenDAO;
    private SubscriptionDAO subscriptionDAO;

    @Override
    public void init() {
        planDAO = new FitnessPlanDAO();
        tokenDAO = new AuthTokenDAO();
        subscriptionDAO = new SubscriptionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        try {
            String action = request.getParameter("action");

            if ("all".equals(action)) {
                getAllPlans(request, responseJson, response);
            } else if ("byId".equals(action)) {
                getPlanById(request, responseJson, response);
            } else if ("myPlans".equals(action)) {
                getTrainerPlans(request, responseJson, response);
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

            createPlan(userId, requestJson, responseJson, response);

        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.print(responseJson.toString());
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
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

            updatePlan(userId, requestJson, responseJson, response);

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

            String planIdStr = request.getParameter("planId");
            int planId = Integer.parseInt(planIdStr);

            deletePlan(userId, planId, responseJson, response);

        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.print(responseJson.toString());
        out.flush();
    }

    private void getAllPlans(HttpServletRequest request, JSONObject responseJson,
            HttpServletResponse response) {

        int userId = getUserIdFromToken(request);
        List<FitnessPlan> plans = planDAO.getAllPlans();

        JSONArray plansArray = new JSONArray();

        for (FitnessPlan plan : plans) {
            JSONObject planJson = new JSONObject();
            planJson.put("planId", plan.getPlanId());
            planJson.put("title", plan.getTitle());
            planJson.put("trainerName", plan.getTrainerName());
            planJson.put("price", plan.getPrice());
            planJson.put("durationDays", plan.getDurationDays());

            boolean isSubscribed = false;
            if (userId != -1) {
                isSubscribed = subscriptionDAO.isUserSubscribed(userId, plan.getPlanId());
            }

            if (isSubscribed) {
                planJson.put("description", plan.getDescription());
                planJson.put("isSubscribed", true);
            } else {
                planJson.put("isSubscribed", false);
            }

            plansArray.add(planJson);
        }

        responseJson.put("success", true);
        responseJson.put("plans", plansArray);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void getPlanById(HttpServletRequest request, JSONObject responseJson,
            HttpServletResponse response) {

        String planIdStr = request.getParameter("planId");
        int planId = Integer.parseInt(planIdStr);
        int userId = getUserIdFromToken(request);

        FitnessPlan plan = planDAO.getPlanById(planId);

        if (plan != null) {
            JSONObject planJson = new JSONObject();
            planJson.put("planId", plan.getPlanId());
            planJson.put("title", plan.getTitle());
            planJson.put("trainerName", plan.getTrainerName());
            planJson.put("trainerId", plan.getTrainerId());
            planJson.put("price", plan.getPrice());
            planJson.put("durationDays", plan.getDurationDays());

            boolean isSubscribed = false;
            if (userId != -1) {
                isSubscribed = subscriptionDAO.isUserSubscribed(userId, planId);
            }

            if (isSubscribed) {
                planJson.put("description", plan.getDescription());
                planJson.put("isSubscribed", true);
            } else {
                planJson.put("isSubscribed", false);
            }

            responseJson.put("success", true);
            responseJson.put("plan", planJson);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            responseJson.put("success", false);
            responseJson.put("message", "Plan not found");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void getTrainerPlans(HttpServletRequest request, JSONObject responseJson,
            HttpServletResponse response) {

        int userId = getUserIdFromToken(request);

        if (userId == -1) {
            responseJson.put("success", false);
            responseJson.put("message", "Unauthorized");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        List<FitnessPlan> plans = planDAO.getPlansByTrainer(userId);
        JSONArray plansArray = new JSONArray();

        for (FitnessPlan plan : plans) {
            JSONObject planJson = new JSONObject();
            planJson.put("planId", plan.getPlanId());
            planJson.put("title", plan.getTitle());
            planJson.put("description", plan.getDescription());
            planJson.put("price", plan.getPrice());
            planJson.put("durationDays", plan.getDurationDays());
            plansArray.add(planJson);
        }

        responseJson.put("success", true);
        responseJson.put("plans", plansArray);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void createPlan(int trainerId, JSONObject requestJson,
            JSONObject responseJson, HttpServletResponse response) {

        String title = (String) requestJson.get("title");
        String description = (String) requestJson.get("description");
        double price = ((Number) requestJson.get("price")).doubleValue();
        int durationDays = ((Number) requestJson.get("durationDays")).intValue();

        FitnessPlan plan = new FitnessPlan(trainerId, title, description, price, durationDays);

        boolean created = planDAO.createPlan(plan);

        if (created) {
            responseJson.put("success", true);
            responseJson.put("message", "Plan created successfully");
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            responseJson.put("success", false);
            responseJson.put("message", "Failed to create plan");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void updatePlan(int trainerId, JSONObject requestJson,
            JSONObject responseJson, HttpServletResponse response) {

        int planId = ((Number) requestJson.get("planId")).intValue();
        String title = (String) requestJson.get("title");
        String description = (String) requestJson.get("description");
        double price = ((Number) requestJson.get("price")).doubleValue();
        int durationDays = ((Number) requestJson.get("durationDays")).intValue();

        FitnessPlan plan = new FitnessPlan();
        plan.setPlanId(planId);
        plan.setTitle(title);
        plan.setDescription(description);
        plan.setPrice(price);
        plan.setDurationDays(durationDays);

        boolean updated = planDAO.updatePlan(plan);

        if (updated) {
            responseJson.put("success", true);
            responseJson.put("message", "Plan updated successfully");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            responseJson.put("success", false);
            responseJson.put("message", "Failed to update plan");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void deletePlan(int trainerId, int planId, JSONObject responseJson,
            HttpServletResponse response) {

        boolean deleted = planDAO.deletePlan(planId);

        if (deleted) {
            responseJson.put("success", true);
            responseJson.put("message", "Plan deleted successfully");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            responseJson.put("success", false);
            responseJson.put("message", "Failed to delete plan");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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