package com.uep.wap.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admins")
public class Admin extends User {

    @ElementCollection
    @CollectionTable(name = "admin_system_logs", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "log_entry")
    private List<String> systemLogs = new ArrayList<>();

    public Admin() {}

    public List<String> getSystemLogs() { return systemLogs; }
    public void setSystemLogs(List<String> systemLogs) { this.systemLogs = systemLogs; }

    public void manageUsers() {}
    public void viewLogs() {}
    public void suspendUser(Long userId) {}
    public void deleteUser(Long userId) {}
}
