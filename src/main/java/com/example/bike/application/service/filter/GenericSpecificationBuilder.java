package com.example.bike.application.service.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is used to build the complete specification with all the filters. Adds all the filters
 * needed and than call the build to get the complete Specification that can be used with the Spring
 * repository.
 *
 * @param <T> The Class of the Specification returned, so that can be used with the {@code T} repository.
 */
public class GenericSpecificationBuilder<T> {

    private enum ExpressionComponent {
        NAME,
        OPERATOR,
        VALUE
    }

    private Specification<T> poSpec;

    private final Map<String, List<Condition>> andParameters = new HashMap<>();

    private final Map<String, List<Condition>> orParameters = new HashMap<>();

    private final String atFieldsPrefix;

    public GenericSpecificationBuilder() {
        this("at");
    }

    public GenericSpecificationBuilder(String atFieldsPrefix) {
        this.atFieldsPrefix = atFieldsPrefix;
    }

    /**
     * For the name and value(s) passed as parameters, parse and add all the conditions
     * to the builder.</br></br>
     * <p>
     * For a name, we can have all these scenarios depending on how the query param
     * is used:
     *
     * <ul>
     *     <li>att=value -> name: att, value: ["value"]</li>
     *     <li>att.gt=value -> name: att.gt, value: ["value"]</li>
     *     <li>att%3Evalue -> name:att>value, value: [""]</li>
     *     <li>att=value1,value2 -> name: att, value:["value1,value2"]</li>
     *     <li>att=value1;value2 -> name: att, value:["value1;value2"]</li>
     *     <li>att%3Dvalue1;att%3Dvalue2 -> name: att, value:["value1;value2"]</li>
     *     <li>att%3Dvalue1&att%3Dvalue2 -> name: att, value: [""]</li>
     * </ul>
     *
     * @param name  The parameter value.
     * @param value The value.
     */
    public void with(String name, String[] value) {
        name = changeAtPrefix(name);
        List<String> valuesRemovedEmpty = Arrays.stream(value).filter(StringUtils::isNotEmpty).collect(Collectors.toList());

        if (valuesRemovedEmpty.size() >= 1) {
            //Every array position can hold a string with a simple value or something like v1,v2 (v1;v2)
            String processedName;
            Operation operation;
            if (StringUtils.containsAny(name, Operation.allOperatorsNotUrlEncoded)) {
                processedName = extractName(name, Operation.allOperatorsNotUrlEncoded);
                operation = Operation.fromString(extractOperator(name, Operation.allOperatorsNotUrlEncoded));
            } else if (StringUtils.containsAny(name, Operation.allOperatorsUrlEncoded)) {
                processedName = extractName(name, Operation.allOperatorsUrlEncoded);
                operation = Operation.fromString(extractOperator(name, Operation.allOperatorsUrlEncoded));
            } else {
                processedName = name;
                operation = Operation.EQ;
            }

            for (String v : valuesRemovedEmpty) {
                addParameter(processedName, v, operation);
            }

        } else {
            //Operator urlencoded, so attribute, operator and value are in the name
            //like att<value or we have something like att=v1;att=v2 (att=v1,att=v2)
            String[] paramList;
            if (StringUtils.contains(name, ",")) {
                paramList = StringUtils.split(name, ",");
            } else if (StringUtils.contains(name, ";")) {
                paramList = StringUtils.split(name, ";");
            } else {
                paramList = new String[]{name};
            }

            if (paramList.length == 1) {
                addCondition(andParameters, buildConditionFromString(paramList[0]));
            } else {
                for (String s : paramList) {
                    addCondition(orParameters, buildConditionFromString(s));
                }
            }
        }

    }

    /**
     * Builds and return the specification using all the conditions.
     *
     * @return A Specification with all the conditions applied.
     */
    public Specification<T> build() {
        for (String s : andParameters.keySet()) {
            addSpecification(new GenericSpecification<T>(andParameters.get(s)));
        }

        List<String> paramNames = new ArrayList<>(orParameters.keySet());

        for (String param : paramNames) {
            List<Condition> conditionList = orParameters.get(param);
            if (conditionList != null) {
                addSpecification(getORSpecification(conditionList));
            }
        }

        return poSpec;
    }

    /**
     * Adds the parameter to the right map. Here, the paramValue can be a string like
     * {@code val1,val2} or {@code val1;val2}. In this case, we need to add and OR condition
     * using all the values for the parameter. If the value is only one, then adds as an AND
     * condition.
     *
     * @param paramName  The name of the parameter.
     * @param paramValue The value of the parameter. Can be one value or more using {@code ,} or {@code ;} as
     *                   separator.
     * @param operation  The operation used.
     */
    private void addParameter(String paramName, String paramValue, Operation operation) {
        if (StringUtils.containsAny(paramValue, ",", ";")) {
            String[] values = StringUtils.contains(paramValue, ",") ? StringUtils.split(paramValue, ",") : StringUtils.split(paramValue, ";");
            for (String v : values) {
                addCondition(orParameters, new Condition(paramName, operation, v));
            }
        } else {
            addCondition(andParameters, new Condition(paramName, operation, paramValue));
        }
    }

    /**
     * Parse the expression string and builds a new Condition using the name, operation and value
     * extracted from the expression.</br></br>
     * <p>
     * A operator inside an expression can be:
     * <ul>
     *     <li>{@code =} or {@code .eq}</li>
     *     <li>{@code <} or {@code .lt}</li>
     *     <li>{@code <=} or {@code .lte}</li>
     *     <li>{@code >} or {@code .gt}</li>
     *     <li>{@code >=} or {@code .gte}</li>
     *     <li>{@code *=} or {@code =~}</li>
     * </ul>
     * <p>
     * Some expression examples:
     * <ul>
     *     <li><{@code att=val} or <{@code att.eqval}</li>
     *     <li><{@code att<=val} or {@code att.lteval}</li>
     * </ul>
     *
     * @param expression The expression to parse.
     * @return The condition build with the components from the expression.
     */
    private Condition buildConditionFromString(String expression) {
        String name, value, operator;
        if (StringUtils.containsAny(expression, Operation.allOperatorsNotUrlEncoded)) {
            name = extractName(expression, Operation.allOperatorsNotUrlEncoded);
            value = extractValue(expression, Operation.allOperatorsNotUrlEncoded);
            operator = extractOperator(expression, Operation.allOperatorsNotUrlEncoded);
        } else {
            name = extractName(expression, Operation.allOperatorsUrlEncoded);
            value = extractValue(expression, Operation.allOperatorsUrlEncoded);
            operator = extractOperator(expression, Operation.allOperatorsUrlEncoded);
        }
        return new Condition(name, Operation.fromString(operator), value);
    }

    /**
     * Adds the condition parameter to the list of conditions associated to the condition name in
     * the map parameter. If no list for that condition name in the map, a new list is created.
     *
     * @param params    The map that contains the condition list associated to the condition name.
     * @param condition The new condition to add.
     */
    private void addCondition(Map<String, List<Condition>> params, Condition condition) {
        String name = condition.isPath() ? condition.getPathAsString() : condition.getAttributeName();
        List<Condition> conditionList;
        if (params.containsKey(name)) {
            conditionList = params.get(name);
        } else {
            conditionList = new ArrayList<>();
            params.put(name, conditionList);
        }
        conditionList.add(condition);
    }


    /**
     * Extract the name from a string with name, operation and value like {@code att=val} or {@code att.eqval}
     *
     * @param nameOpAndValue The string to parse.
     * @param operators      A list of possible operators that can be in the string.
     * @return The name or an empty string if the value can't be found.
     */
    private String extractName(String nameOpAndValue, String[] operators) {
        return extractFromString(nameOpAndValue, operators, ExpressionComponent.NAME);
    }

    /**
     * Extract the operator from a string with name, operation and value like {@code att=val} or {@code att.eqval}
     *
     * @param nameOpAndValue The string to parse.
     * @param operators      A list of possible operators that can be in the string.
     * @return The operator or an empty string if the value can't be found.
     */
    private String extractOperator(String nameOpAndValue, String[] operators) {
        return extractFromString(nameOpAndValue, operators, ExpressionComponent.OPERATOR);
    }

    /**
     * Extract the value from a string with name, operation and value like {@code att=val} or {@code att.eqval}
     *
     * @param nameOpAndValue The string to parse.
     * @param operators      A list of possible operators that can be in the string.
     * @return The value or an empty string if the value can't be found.
     */
    private String extractValue(String nameOpAndValue, String[] operators) {
        return extractFromString(nameOpAndValue, operators, ExpressionComponent.VALUE);
    }

    /**
     * Extract the component from a string composed by a name, operation and value like {@code att=val} or {@code att.eqval}
     *
     * @param nameOpAndValue The string to parse.
     * @param operators      A list of possible operators that can be in the string.
     * @param component      The component to extract from string.
     * @return The component value or an empty string if the component can't be found.
     */
    private String extractFromString(String nameOpAndValue, String[] operators, ExpressionComponent component) {
        for (String operator : operators) {
            if (StringUtils.contains(nameOpAndValue, operator)) {
                int index = StringUtils.indexOf(nameOpAndValue, operator);
                if (index != -1) {
                    switch (component) {
                        case NAME:
                            return StringUtils.substring(nameOpAndValue, 0, index);
                        case OPERATOR:
                            return StringUtils.substring(nameOpAndValue, index, index + operator.length());
                        case VALUE:
                            return StringUtils.substring(nameOpAndValue, index + operator.length());
                    }
                }
            }
        }

        return "";
    }

    /**
     * Creates an OR specification using the condition list parameter.
     *
     * @param values The list of conditions used to create the OR.
     * @return A Specification with all the conditions.
     */
    private Specification<T> getORSpecification(List<Condition> values) {
        GenericSpecification<T> spec = new GenericSpecification<>(values.get(0));
        Specification<T> orSpec = Specification.where(spec);
        for (int i = 1; i < values.size(); i++) {
            GenericSpecification<T> specAux = new GenericSpecification<>(values.get(i));
            orSpec = Specification.where(orSpec).or(specAux);
        }
        return orSpec;
    }

    /**
     * Adds the specification parameter using AND to the actual specification.
     *
     * @param specification The specification to add.
     */
    private void addSpecification(Specification<T> specification) {
        if (poSpec == null) {
            poSpec = specification;
        } else {
            poSpec = Specification.where(poSpec).and(specification);
        }
    }

    /**
     * @param attName
     * @return
     */
    private String changeAtPrefix(String attName) {
        String newAttName = attName;
        if (attName != null && attName.startsWith("@")) {
            if (StringUtils.isNotBlank(atFieldsPrefix)) {
                newAttName = atFieldsPrefix.concat(StringUtils.capitalize(StringUtils.removeStart(attName, "@")));
            } else {
                newAttName = StringUtils.removeStart(attName, "@");
            }
        }
        return newAttName;
    }

}
