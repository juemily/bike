package com.example.bike.application.service.filter;

import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the Specification spring data jpa interface, so we can use it
 * with the spring repositories to build a query with some conditions to apply. It can be
 * used with any domain class, only need is to parametrize this class when used.
 *
 * If the class is constructed with only one Condition param, then it returns a Predicate
 * that match with that condition:<br/>
 * {@code attributeName OP value}<br/><br/>
 *
 * It it has more than one condition, then the predicate is build doing an AND for
 * all the Conditions, like:<br/><br/>
 * {@code (attributeName1 OP1 value1) AND (attributeName2 OP2 value2) AND ... (attributeNameN OPN valueN) }<br/>
 *
 * More than one Condition is useful when we want to do a Predicate for a internal object. Think we have something
 * like that (not complete...)
 * <pre>
 * {
 *   "category": "Residential",
 *   "externalId": "SOXXX",
 *   "notificationContact": "email@mail.com",
 *   "orderItem": [
 *     {
 *       "action": "add",
 *       "id": "001",
 *       "itemPrice": [
 *
 *       ],
 *       "product": {
 *         "characteristic": [
 *           {
 *             "name": "MSISDN",
 *             "value": "8266971001"
 *           },
 *           {
 *             "name": "usageLimit",
 *             "value": "0"
 *           },
 *           {
 *             "name": "serviceClass",
 *             "value": "1310"
 *           },
 *           {
 *             "name": "billCycle",
 *             "value": "15"
 *           },
 *           {
 *             "name": "buId",
 *             "value": "900"
 *           }
 *         ]....
 * }
 * </pre>
 *
 * If we want to search in the database the orders that have an the product characteristic with name 'MSISDN' and
 * value 8266971001, then the filter must me like<br/><br/>
 * {@code orderItem.product.characteristic.name=MSISDN&orderItem.product.characteristic.value=8266971000}
 * <br/><br/>
 *
 * This comes to build a GenericSpecification object with two Condition objects. And in this case we are using
 * the path prefix (orderItem.product.characteristic) to build a join path from which to get the name and value
 * columns to build the Predicate.
 *
 * For more than one condition, the prefix path (the path until the last dot) must be the same.
 *
 *
 * @param <T> The domain class to generate the Predicate.
 * @see Predicate
 * @see Condition
 */

@Slf4j
public class GenericSpecification<T> implements Specification<T> {

    private final List<Condition> conditionList;

    public GenericSpecification(Condition condition) {
        this.conditionList = new ArrayList<>();
        this.conditionList.add(condition);
    }

    public GenericSpecification(List<Condition> conditionList) {
        this.conditionList = conditionList;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);
        Path<?> path = buildPathFromAttributeName(root);
        if (conditionList != null) {
            if (conditionList.size() == 1) {
                return getPredicateFromCondition(path, criteriaBuilder, conditionList.get(0));
            } else {
                Predicate[] predicates = new Predicate[conditionList.size()];
                for (int i = 0; i < conditionList.size(); i++) {
                    predicates[i] = getPredicateFromCondition(path, criteriaBuilder, conditionList.get(i));
                }
                return criteriaBuilder.and(predicates);
            }
        }
        return null;
    }

    /**
     * Returns the right predicate depending on the operation defined in the condition.
     *
     * @param path The path to the table from the to get the attribute value.
     * @param criteriaBuilder The object used to build the predicate.
     * @param condition The condition describing the operation to convert into a predicate.
     * @return The Predicate associated to the condition.
     */
    private Predicate getPredicateFromCondition(Path<?> path, CriteriaBuilder criteriaBuilder, Condition condition) {
        switch (condition.getOperator()) {
            case EQ:
                return eq(path, criteriaBuilder, condition);

            case GT:
                return gt(path, criteriaBuilder, condition);

            case GTE:
                return gte(path, criteriaBuilder, condition);

            case LT:
                return lt(path, criteriaBuilder, condition);

            case LTE:
                return lte(path, criteriaBuilder, condition);

            case LIKE:
                return criteriaBuilder.like(path.get(condition.getAttributeName()), condition.getValue());
        }
        throw new IllegalArgumentException(String.format("Operation not supported: %s", condition.getOperator()));
    }

    /**
     * If the attribute is simple then return the root path. If the attribute is a path
     * like {@code att1.att2.att3}, then return the path until the last att (att1.att2).
     * Later qhen we build the query we get the att3 from the path.
     *
     * @param root The root table from where we start to build the path.
     * @return The path for the attribute.
     */
    private Path<?> buildPathFromAttributeName(Root<T> root) {
        Join<?, ?> joinChain;
        Path<?> path;
        Condition condition = conditionList.get(0);

        if (condition.isPath()) {
            String[] atts = condition.getPath();
            joinChain = root.join(atts[0]);
            for (int i = 1; i < atts.length; i++) {
                joinChain = joinChain.join(atts[i]);
            }
            path = joinChain;
        } else {
            path = root;
        }
        return path;
    }

    /**
     * Returns an eq predicate for the path using the attribute name and value in the condition.
     *
     * @param path The path to the table from where to get the value.
     * @param criteriaBuilder Used to build the predicate.
     * @param condition The condition used to get the attribute name and value for the predicate.
     * @return The Predicate that represent the condition.
     */
    private Predicate eq(Path<?> path, CriteriaBuilder criteriaBuilder, Condition condition) {
        String attName = condition.getAttributeName();

        if (path.get(attName).getJavaType().equals(ZonedDateTime.class)) {
            return criteriaBuilder.equal(path.get(attName), parseDate(cleanSimpleQuotes(condition.getValue())));
        }

        if (path.get(attName).getJavaType().isEnum()) {
            return criteriaBuilder.equal(path.get(attName), parseEnum(path.get(attName).getJavaType(), condition));
        }

        if (path.get(attName).getJavaType().equals(Boolean.class)) {
            return criteriaBuilder.equal(path.get(attName), Boolean.parseBoolean(condition.getValue()));
        }

        return criteriaBuilder.equal(path.get(attName), condition.getValue());
    }

    /**
     * Returns an gt predicate for the path using the attribute name and value in the condition.
     *
     * @param path The path to the table from where to get the value.
     * @param criteriaBuilder Used to build the predicate.
     * @param condition The condition used to get the attribute name and value for the predicate.
     * @return The Predicate that represent the condition.
     */
    private Predicate gt(Path<?> path, CriteriaBuilder criteriaBuilder, Condition condition) {
        String attName = condition.getAttributeName();

        if (path.get(attName).getJavaType().equals(ZonedDateTime.class)) {
            return criteriaBuilder.greaterThan(path.get(attName), parseDate(cleanSimpleQuotes(condition.getValue())));
        }

        return criteriaBuilder.greaterThan(path.get(attName), condition.getValue());
    }

    /**
     * Returns an gte predicate for the path using the attribute name and value in the condition.
     *
     * @param path The path to the table from where to get the value.
     * @param criteriaBuilder Used to build the predicate.
     * @param condition The condition used to get the attribute name and value for the predicate.
     * @return The Predicate that represent the condition.
     */
    private Predicate gte(Path<?> path, CriteriaBuilder criteriaBuilder, Condition condition) {
        String attName = condition.getAttributeName();

        if (path.get(attName).getJavaType().equals(ZonedDateTime.class)) {
            return criteriaBuilder.greaterThanOrEqualTo(path.get(attName), parseDate(cleanSimpleQuotes(condition.getValue())));
        }

        return criteriaBuilder.greaterThanOrEqualTo(path.get(attName), condition.getValue());
    }

    /**
     * Returns an lt predicate for the path using the attribute name and value in the condition.
     *
     * @param path The path to the table from where to get the value.
     * @param criteriaBuilder Used to build the predicate.
     * @param condition The condition used to get the attribute name and value for the predicate.
     * @return The Predicate that represent the condition.
     */
    private Predicate lt(Path<?> path, CriteriaBuilder criteriaBuilder, Condition condition) {
        String attName = condition.getAttributeName();

        if (path.get(attName).getJavaType().equals(ZonedDateTime.class)) {
            return criteriaBuilder.lessThan(path.get(attName), parseDate(cleanSimpleQuotes(condition.getValue())));
        }

        return criteriaBuilder.lessThan(path.get(attName), condition.getValue());
    }

    /**
     * Returns an lte predicate for the path using the attribute name and value in the condition.
     *
     * @param path The path to the table from where to get the value.
     * @param criteriaBuilder Used to build the predicate.
     * @param condition The condition used to get the attribute name and value for the predicate.
     * @return The Predicate that represent the condition.
     */
    private Predicate lte(Path<?> path, CriteriaBuilder criteriaBuilder, Condition condition) {
        String attName = condition.getAttributeName();

        if (path.get(attName).getJavaType().equals(ZonedDateTime.class)) {
            return criteriaBuilder.lessThanOrEqualTo(path.get(attName), parseDate(cleanSimpleQuotes(condition.getValue())));
        }

        return criteriaBuilder.lessThanOrEqualTo(path.get(attName), condition.getValue());
    }

    /**
     * This method tries to build an enumerated object using a constructor or a method that receives
     * a String (the enum string representation).
     *
     * @param type The type of the enum to build.
     * @param condition The condition with the string value for the enum to build.
     * @return The enum value.
     */
    private Object parseEnum(Class<?> type, Condition condition) {
        Object obj = null;
        if (type.isEnum()) {
            Constructor<?> constructor = null;
            boolean constructorFound = false;
            try {
                constructor = type.getDeclaredConstructor(String.class);
                constructorFound = true;
            } catch (NoSuchMethodException e) {
                log.debug("No constructor found with a String parameter for {}. Looking for other methods...", type);
            }

            if (constructorFound) {
                try {
                    obj = constructor.newInstance(condition.getValue());
                } catch (Exception e) {
                    log.error("Error creating new instance of object {}: {}", type, e.getMessage());
                }
            } else {
                Method[] enumMethods = type.getMethods();
                for (Method m : enumMethods) {
                    if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == String.class && m.getReturnType() == type) {
                        try {
                            log.trace("Trying to create {} object using method {}", type, m.getName());
                            obj = m.invoke(null, condition.getValue());
                            break;
                        } catch (Exception e) {
                            log.trace("Error calling enum method {} with parameter {}: {}. Trying next method...", m.getName(), condition.getValue(), e.getMessage());
                        }
                    }
                }
            }

        }
        return obj;
    }

    /**
     * Parse a date in this two formats:
     * <ul>
     *     <li>{@code 2020-03-30T12:24:02.031823+02:00}</li>
     *     <li>{@code 2020-03-30}</li>
     * </ul>
     * <p>
     * For the second type, use the system Zone. If the date can't be parsed because there
     * is an error in the format, then this method return a ZonedDateTime for
     * 1900-01-01T00:00:00 using the system time zone.
     *
     * @param date The string to parse.
     * @return A ZonedDateTime.
     */
    private ZonedDateTime parseDate(String date) {
        ZonedDateTime resultDate = ZonedDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());

        boolean parsed = false;

        try {
            resultDate = ZonedDateTime.parse(date);
            parsed = true;
        } catch (DateTimeParseException e) {
            log.error("Error parsing date to ZonedDateTime: {}", e.getMessage());
        }

        if (!parsed) {
            try {
                LocalDate localDate = LocalDate.parse(date);
                resultDate = ZonedDateTime.of(localDate.atTime(0, 0), ZoneId.systemDefault());
            } catch (DateTimeParseException e) {
                log.error("Error parsing LocalDate: {}", e.getMessage());
            }
        }

        return resultDate;
    }

    private String cleanSimpleQuotes(String dateAsString) {
        String clean = dateAsString;
        if (dateAsString != null && dateAsString.startsWith("'") && dateAsString.endsWith("'")) {
            clean = dateAsString.substring(1);
            clean = clean.substring(0, clean.lastIndexOf("'"));
        }
        return clean;
    }

}
