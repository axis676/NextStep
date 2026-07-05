package com.nextstep.formops.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ConditionEvaluator {
    public boolean allMatch(List<String> conditions, Map<String, Object> context) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        return conditions.stream().allMatch(condition -> evaluate(condition, context));
    }

    public boolean evaluate(String expression, Map<String, Object> context) {
        if (expression == null || expression.isBlank()) {
            return true;
        }
        String[] orTerms = expression.split("\\|\\|");
        for (String orTerm : orTerms) {
            if (evaluateAndTerms(orTerm, context)) {
                return true;
            }
        }
        return false;
    }

    private boolean evaluateAndTerms(String expression, Map<String, Object> context) {
        String[] andTerms = expression.split("&&");
        for (String term : andTerms) {
            if (!evaluateSimpleTerm(term.trim(), context)) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateSimpleTerm(String term, Map<String, Object> context) {
        if (term.contains("==")) {
            String[] parts = term.split("==", 2);
            Object actual = context.get(parts[0].trim());
            String expected = parts[1].trim();
            return compare(actual, expected);
        }
        if (term.contains("!=")) {
            String[] parts = term.split("!=", 2);
            Object actual = context.get(parts[0].trim());
            String expected = parts[1].trim();
            return !compare(actual, expected);
        }
        Object value = context.get(term);
        return Boolean.TRUE.equals(value);
    }

    private boolean compare(Object actual, String expectedLiteral) {
        if (actual == null) {
            return false;
        }
        if ("true".equalsIgnoreCase(expectedLiteral) || "false".equalsIgnoreCase(expectedLiteral)) {
            return actual instanceof Boolean actualBoolean && Boolean.parseBoolean(expectedLiteral) == actualBoolean;
        }
        String normalizedExpected = expectedLiteral.replace("\"", "").replace("'", "");
        return normalizedExpected.equals(String.valueOf(actual));
    }
}
