package com.example.teachermanagement.service;

import com.example.teachermanagement.model.ManagementRecord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ManagementService {
    private final Map<String, List<ManagementRecord>> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1000);

    public ManagementService() {
        seed();
    }

    public List<ManagementRecord> list(String module) {
        return new ArrayList<>(records(module));
    }

    public ManagementRecord create(String module, Map<String, Object> fields) {
        ManagementRecord record = new ManagementRecord(idSequence.incrementAndGet(), new LinkedHashMap<>(fields));
        records(module).add(record);
        return record;
    }

    public ManagementRecord update(String module, Long id, Map<String, Object> fields) {
        ManagementRecord record = find(module, id);
        record.setFields(new LinkedHashMap<>(fields));
        return record;
    }

    public void delete(String module, Long id) {
        records(module).removeIf(record -> record.getId().equals(id));
    }

    public Map<String, Integer> statistics() {
        Map<String, Integer> statistics = new LinkedHashMap<>();
        statistics.put("teachers", records("teachers").size());
        statistics.put("students", records("students").size());
        statistics.put("courses", records("courses").size());
        statistics.put("classes", records("classes").size());
        statistics.put("scores", records("scores").size());
        statistics.put("users", records("users").size());
        statistics.put("resources", records("resources").size());
        return statistics;
    }

    private ManagementRecord find(String module, Long id) {
        return records(module).stream()
                .filter(record -> record.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));
    }

    private List<ManagementRecord> records(String module) {
        return store.computeIfAbsent(module, key -> new ArrayList<>());
    }

    private void seed() {
        add("teachers", map("name", "Emma Lin", "department", "Language Arts", "phone", "13800000001", "email", "emma.lin@example.com", "status", "Active"));
        add("teachers", map("name", "Michael Zhou", "department", "Mathematics", "phone", "13800000002", "email", "michael.zhou@example.com", "status", "Active"));
        add("teachers", map("name", "Clara Xu", "department", "English", "phone", "13800000003", "email", "clara.xu@example.com", "status", "On Leave"));
        add("teachers", map("name", "Oliver Reed", "department", "Science", "phone", "13800000004", "email", "oliver.reed@example.com", "status", "Active"));
        add("teachers", map("name", "Sophia Patel", "department", "Computer Science", "phone", "13800000005", "email", "sophia.patel@example.com", "status", "Active"));
        add("teachers", map("name", "Daniel Kim", "department", "Physical Education", "phone", "13800000006", "email", "daniel.kim@example.com", "status", "Active"));
        add("teachers", map("name", "Grace Miller", "department", "Arts", "phone", "13800000007", "email", "grace.miller@example.com", "status", "Active"));
        add("teachers", map("name", "Henry Brown", "department", "History", "phone", "13800000008", "email", "henry.brown@example.com", "status", "Pending"));

        add("students", map("name", "Anna Chen", "studentNo", "S2026001", "className", "Grade 10 Class 1", "phone", "13900000001", "status", "Enrolled"));
        add("students", map("name", "Leo Li", "studentNo", "S2026002", "className", "Grade 10 Class 2", "phone", "13900000002", "status", "Enrolled"));
        add("students", map("name", "Iris Wang", "studentNo", "S2026003", "className", "Grade 11 Class 1", "phone", "13900000003", "status", "Enrolled"));
        add("students", map("name", "Noah Smith", "studentNo", "S2026004", "className", "Grade 11 Class 2", "phone", "13900000004", "status", "Enrolled"));
        add("students", map("name", "Mia Wilson", "studentNo", "S2026005", "className", "Grade 12 Class 1", "phone", "13900000005", "status", "Enrolled"));
        add("students", map("name", "Ethan Davis", "studentNo", "S2026006", "className", "Grade 12 Class 2", "phone", "13900000006", "status", "Enrolled"));
        add("students", map("name", "Ava Taylor", "studentNo", "S2026007", "className", "Grade 10 Class 1", "phone", "13900000007", "status", "Enrolled"));
        add("students", map("name", "Lucas Martin", "studentNo", "S2026008", "className", "Grade 11 Class 1", "phone", "13900000008", "status", "Transfer Review"));

        add("courses", map("name", "Literature Reading", "teacher", "Emma Lin", "credit", "3", "room", "A201", "status", "Open"));
        add("courses", map("name", "Mathematical Modeling", "teacher", "Michael Zhou", "credit", "4", "room", "B305", "status", "Open"));
        add("courses", map("name", "English Speaking", "teacher", "Clara Xu", "credit", "2", "room", "C102", "status", "Open"));
        add("courses", map("name", "Physics Lab", "teacher", "Oliver Reed", "credit", "3", "room", "D208", "status", "Open"));
        add("courses", map("name", "Robotics Lab", "teacher", "Sophia Patel", "credit", "4", "room", "E301", "status", "Open"));
        add("courses", map("name", "World History", "teacher", "Henry Brown", "credit", "2", "room", "B110", "status", "Draft"));
        add("courses", map("name", "Visual Arts Studio", "teacher", "Grace Miller", "credit", "2", "room", "F203", "status", "Open"));

        add("classes", map("name", "Grade 10 Class 1", "headTeacher", "Emma Lin", "grade", "Grade 10", "students", "42", "room", "A101"));
        add("classes", map("name", "Grade 10 Class 2", "headTeacher", "Michael Zhou", "grade", "Grade 10", "students", "39", "room", "A102"));
        add("classes", map("name", "Grade 11 Class 1", "headTeacher", "Clara Xu", "grade", "Grade 11", "students", "41", "room", "B201"));
        add("classes", map("name", "Grade 11 Class 2", "headTeacher", "Oliver Reed", "grade", "Grade 11", "students", "40", "room", "B202"));
        add("classes", map("name", "Grade 12 Class 1", "headTeacher", "Sophia Patel", "grade", "Grade 12", "students", "37", "room", "C301"));
        add("classes", map("name", "Grade 12 Class 2", "headTeacher", "Daniel Kim", "grade", "Grade 12", "students", "36", "room", "C302"));

        add("scores", map("student", "Anna Chen", "course", "Literature Reading", "score", "92", "term", "Spring 2026", "level", "Excellent"));
        add("scores", map("student", "Leo Li", "course", "Mathematical Modeling", "score", "88", "term", "Spring 2026", "level", "Good"));
        add("scores", map("student", "Iris Wang", "course", "English Speaking", "score", "95", "term", "Spring 2026", "level", "Excellent"));
        add("scores", map("student", "Noah Smith", "course", "Physics Lab", "score", "91", "term", "Spring 2026", "level", "Excellent"));
        add("scores", map("student", "Mia Wilson", "course", "Robotics Lab", "score", "86", "term", "Spring 2026", "level", "Good"));
        add("scores", map("student", "Ethan Davis", "course", "World History", "score", "79", "term", "Spring 2026", "level", "Needs Work"));
        add("scores", map("student", "Ava Taylor", "course", "Visual Arts Studio", "score", "93", "term", "Spring 2026", "level", "Excellent"));

        add("users", map("username", "admin", "role", "Administrator", "name", "System Admin", "status", "Enabled"));
        add("users", map("username", "teacher01", "role", "Teacher", "name", "Emma Lin", "status", "Enabled"));
        add("users", map("username", "teacher02", "role", "Teacher", "name", "Michael Zhou", "status", "Enabled"));
        add("users", map("username", "student01", "role", "Student", "name", "Anna Chen", "status", "Enabled"));
        add("users", map("username", "reviewer01", "role", "Academic Reviewer", "name", "Grace Miller", "status", "Pending"));

        add("resources", map("pc", "LAB-A-01", "location", "Teaching Lab A", "level", "Level 1", "area", "Main lab", "zone", "Programming practice", "status", "available", "type", "Windows desktop", "nextChange", "Bookable now"));
        add("resources", map("pc", "LAB-A-02", "location", "Teaching Lab A", "level", "Level 1", "area", "Main lab", "zone", "Programming practice", "status", "occupied", "type", "Windows desktop", "nextChange", "Available 2:30 PM"));
        add("resources", map("pc", "LAB-A-03", "location", "Teaching Lab A", "level", "Level 1", "area", "Main lab", "zone", "Programming practice", "status", "available", "type", "Dual monitor", "nextChange", "Bookable now"));
        add("resources", map("pc", "LAB-B-01", "location", "Science Lab B", "level", "Level 2", "area", "Physics lab", "zone", "Experiment workstation", "status", "maintenance", "type", "Data workstation", "nextChange", "Service desk check"));
        add("resources", map("pc", "LAB-B-02", "location", "Science Lab B", "level", "Level 2", "area", "Physics lab", "zone", "Experiment workstation", "status", "available", "type", "Data workstation", "nextChange", "Bookable now"));
        add("resources", map("pc", "STUDIO-01", "location", "Creative Studio", "level", "Level 3", "area", "Media room", "zone", "Visual arts", "status", "reserved", "type", "Design workstation", "nextChange", "Reserved until 3:00 PM"));
    }

    private void add(String module, Map<String, Object> fields) {
        create(module, fields);
    }

    private Map<String, Object> map(Object... values) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int index = 0; index < values.length; index += 2) {
            map.put(String.valueOf(values[index]), values[index + 1]);
        }
        return map;
    }
}
