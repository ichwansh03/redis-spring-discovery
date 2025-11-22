package com.redislabs.edu.redi2read.boot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.models.Category;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CreateBooks implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (bookRepository.count() == 0) {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Book>> typeReference = new TypeReference<>() {
            };

            try {
                List<File> files =
                        Files.list(Paths.get(getClass().getResource("/data/books").toURI()))
                                .filter(Files::isRegularFile)
                                .filter(path -> path.toString().endsWith(".json"))
                                .map(Path::toFile)
                                .toList();

                Map<String, Category> categories = new HashMap<>();

                files.forEach(file -> {
                    log.info(">>> Processing file: {}", file.getPath());
                    String categoryName = file.getName().substring(0, file.getName().lastIndexOf("_"));
                    log.info(">>> Category name: {}", categoryName);
                    Category category;
                    if (!categories.containsKey(categoryName)) {
                        category = Category.builder().name(categoryName).build();
                        categoryRepository.save(category);
                        categories.put(categoryName, category);
                    } else {
                        category = categories.get(categoryName);
                    }

                    try {
                        InputStream inputStream = new FileInputStream(file);
                        List<Book> books = mapper.readValue(inputStream, typeReference);
                        books.forEach((book) -> {
                            book.addCategory(category);
                            bookRepository.save(book);
                        });
                        log.info(">>> {} books read from JSON file {}", books.size(), file.getName());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                });
            } catch (IOException e) {
                log.info(">>> unable to read books: {}", e.getMessage());
            }
        }
    }
}
