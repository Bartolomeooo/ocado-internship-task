package org.example.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.PaymentMethod;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PaymentMethodParser {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<PaymentMethod> parse(String filePath) {
        try {
            return objectMapper.readValue(
                    new File(filePath),
                    new TypeReference<>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read payment methods from " + filePath, e);
        }
    }
}