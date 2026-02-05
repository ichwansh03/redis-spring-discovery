package com.ichwan.shopper.controller;

import com.ichwan.shopper.operations.lists.TaskConsumer;
import com.ichwan.shopper.operations.lists.TaskProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskProducer producer;
    private final TaskConsumer consumer;

    @PostMapping
    public String addTask(@RequestParam String task) {
        producer.pushTask(task);
        return "task added";
    }

    @GetMapping("/consume")
    public String consume(){
        String task = consumer.consumeTask();
        return task != null ? task : "no task";
    }
}
