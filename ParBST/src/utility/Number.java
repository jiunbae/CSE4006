package utility;

public class Number {

    public enum Operator {
        LT,     // Lower than
        GT,     // Greater than
        LTE,    // Lower than equal
        GTE,    // Greater than equal
        EQ,     // Equal
        NEQ,    // Not equal
    }

    public static final boolean eval(java.lang.Number lhs, java.lang.Number rhs, Operator op) {
        switch (op) {
            case LT:
                return lhs.doubleValue() < rhs.doubleValue();
            case GT:
                return lhs.doubleValue() > rhs.doubleValue();
            case LTE:
                return lhs.doubleValue() <= rhs.doubleValue();
            case GTE:
                return lhs.doubleValue() >= rhs.doubleValue();
            case EQ:
                return lhs.doubleValue() == rhs.doubleValue();
            case NEQ:
                return lhs.doubleValue() != rhs.doubleValue();
        }
        return false;
    }
}

