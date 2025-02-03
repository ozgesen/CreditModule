package org.credit.module.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
public class Status {

    private HashMap<Integer, String> status;
    private static Status instance;

    private Status() {
        status = new HashMap<>();
        status.put(0, "Loan created successfully.");
        status.put(1, "User has no access.");
        status.put(2, "Customer not found.");
        status.put(3, "Operation failed.");
        status.put(4, "Loan successfully created.");
        status.put(403, "Forbidden");
    }

    public static Status getInstance() {
        if (instance == null) {
            instance = new Status();
        }
        return instance;
    }

    public HashMap<Integer, String> getStatus() {
        return status;
    }

    public String getStatusMessage(int code) {
        return status.getOrDefault(code, "Unknown error");
    }
}
