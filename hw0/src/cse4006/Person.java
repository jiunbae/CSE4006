package cse4006;

public class Person {
    private String name;

    /**
     * @param size: string length
     * @return random string
     */
    public String nextName(int size) { //@Improving: use *static* keyword if there is no constraint
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lower = upper.toLowerCase();
        final String alpha = upper + lower;

        String name = "";
        for (int i = 0; i < size; i++) {
            name += alpha.charAt((int) (Math.random() * alpha.length()));
        }
        return name;
    }

    /**
     * recall nextName(4~12)
     * @return random string(length: 4 ~ 12)
     */
    public String nextName() { //@Improving: use *static* keyword if there is no constraint
        return nextName((int) (Math.random() * 8) + 4);
    }

    public Person () {
        this.name = nextName();
    }

    public Person (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * @param obj: comparison
     * @return if obj is same class and have same name
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!Person.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Person other = (Person) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }

        return true;
    }
}
