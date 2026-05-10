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
        add("teachers", map("name", "林樱", "department", "语文组", "phone", "13800000001", "email", "linying@example.com", "status", "在职"));
        add("teachers", map("name", "周明", "department", "数学组", "phone", "13800000002", "email", "zhouming@example.com", "status", "在职"));
        add("teachers", map("name", "许晴", "department", "英语组", "phone", "13800000003", "email", "xuqing@example.com", "status", "请假"));

        add("students", map("name", "陈安安", "studentNo", "S2026001", "className", "高一一班", "phone", "13900000001", "status", "正常"));
        add("students", map("name", "李星河", "studentNo", "S2026002", "className", "高一二班", "phone", "13900000002", "status", "正常"));
        add("students", map("name", "王若溪", "studentNo", "S2026003", "className", "高二一班", "phone", "13900000003", "status", "正常"));

        add("courses", map("name", "语文阅读", "teacher", "林樱", "credit", "3", "room", "A201", "status", "开课"));
        add("courses", map("name", "数学建模", "teacher", "周明", "credit", "4", "room", "B305", "status", "开课"));
        add("courses", map("name", "英语口语", "teacher", "许晴", "credit", "2", "room", "C102", "status", "开课"));

        add("classes", map("name", "高一一班", "headTeacher", "林樱", "grade", "高一", "students", "42", "room", "A101"));
        add("classes", map("name", "高一二班", "headTeacher", "周明", "grade", "高一", "students", "39", "room", "A102"));
        add("classes", map("name", "高二一班", "headTeacher", "许晴", "grade", "高二", "students", "41", "room", "B201"));

        add("scores", map("student", "陈安安", "course", "语文阅读", "score", "92", "term", "2026春季", "level", "优秀"));
        add("scores", map("student", "李星河", "course", "数学建模", "score", "88", "term", "2026春季", "level", "良好"));
        add("scores", map("student", "王若溪", "course", "英语口语", "score", "95", "term", "2026春季", "level", "优秀"));

        add("users", map("username", "admin", "role", "管理员", "name", "系统管理员", "status", "启用"));
        add("users", map("username", "teacher01", "role", "教师", "name", "林樱", "status", "启用"));
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

