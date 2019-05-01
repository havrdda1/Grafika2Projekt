package util;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that generates string to draw based on initial axiom and rules.
 */

public class LRules {

    public LRules() {
    }


    public Map<Character, char[]> prepareRules(final String[] rules) {
        Map<Character, char[]> preparedRules = new HashMap<>();
        for (String rule : rules) {
            rule = rule.replaceAll("[:\\s]", "");
            char[] preparedRule = rule.toCharArray();
            char constant = preparedRule[0];
            char[] production = Arrays.copyOfRange(preparedRule, 1, preparedRule.length);
            preparedRules.put(constant, production);

        }

        return preparedRules;
    }

    public String applyRules(final Map<Character, char[]> rules, final String state) {
        StringBuilder newState = new StringBuilder();

        for (char item : state.toCharArray()) {
            if (rules.get(item) != null) {
                newState.append(rules.get(item));
            } else {
                newState.append(item);
            }
        }
        return newState.toString();


    }
}
