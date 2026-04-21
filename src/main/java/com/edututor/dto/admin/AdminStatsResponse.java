package com.edututor.dto.admin;

public class AdminStatsResponse {

    private long totalUsers;
    private long totalCourses;
    private long totalResources;
    private long monthlyActiveUsers;

    public AdminStatsResponse(long totalUsers, long totalCourses, long totalResources, long monthlyActiveUsers) {
        this.totalUsers = totalUsers;
        this.totalCourses = totalCourses;
        this.totalResources = totalResources;
        this.monthlyActiveUsers = monthlyActiveUsers;
    }

    public long getTotalUsers() { return totalUsers; }
    public long getTotalCourses() { return totalCourses; }
    public long getTotalResources() { return totalResources; }
    public long getMonthlyActiveUsers() { return monthlyActiveUsers; }
}
