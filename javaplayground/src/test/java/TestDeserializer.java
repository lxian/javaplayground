import com.lxian.playground.json.mapper.DefaultDeserializer;
import com.lxian.playground.json.mapper.field.LowerSnakeCaseToCamelFieldNameResolver;
import com.lxian.playground.json.parser.DefaultJsonParser;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class TestDeserializer {

    public static class Person<T> {
        private T info;

        public Person() {
        }

        public Person(T info) {
            this.info = info;
        }

        public T getInfo() {
            return info;
        }

        public void setInfo(T info) {
            this.info = info;
        }
    }


    public static class EmployeeInfo {
        private String name;

        private String dept;

        private BigDecimal salary;

        public EmployeeInfo() {
        }

        public EmployeeInfo(String name, String dept, BigDecimal salary) {
            this.name = name;
            this.dept = dept;
            this.salary = salary;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDept() {
            return dept;
        }

        public void setDept(String dept) {
            this.dept = dept;
        }

        public BigDecimal getSalary() {
            return salary;
        }

        public void setSalary(BigDecimal salary) {
            this.salary = salary;
        }
    }


    public static class Employee extends Person<EmployeeInfo> {

        public Employee() {
        }

        public Employee(EmployeeInfo info) {
            super(info);
        }
    }

    public static class Manager extends Employee {
        private List<Employee> subordinates;

        private int subordinatesCount;

        public Manager() {
        }

        public Manager(List<Employee> subordinates) {
            this.subordinates = subordinates;
        }

        public Manager(EmployeeInfo info, List<Employee> subordinates) {
            super(info);
            this.subordinates = subordinates;
        }

        public List<Employee> getSubordinates() {
            return subordinates;
        }

        public void setSubordinates(List<Employee> subordinates) {
            this.subordinates = subordinates;
        }

        public int getSubordinatesCount() {
            return subordinatesCount;
        }

        public void setSubordinatesCount(int subordinatesCount) {
            this.subordinatesCount = subordinatesCount;
        }
    }

    @Test
    public void testManager() {

        String input = "{\"info\":{\"name\":\"Tom\",\"dept\":\"CS\", \"salary\": 10000},\"subordinates_count\": 2, \"subordinates\":[{\"info\":{\"name\":\"Bob\",\"dept\":\"CS\", \"salary\": 143413.2143}},{\"info\":{\"name\":\"Alice\",\"dept\":\"CS\", \"salary\": 0.1231314}}]}";

        DefaultDeserializer deserializer = new DefaultDeserializer(new DefaultJsonParser(), new LowerSnakeCaseToCamelFieldNameResolver());

        try {
            Manager manager = deserializer.read(input, Manager.class);
            System.out.println(manager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class DictEntry {
        private String name;

        private String desc;

        public DictEntry() {
        }

        public DictEntry(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public static class Dict {

        private Map<String, DictEntry> indexes;

        public Dict() {
        }

        public Dict(Map<String, DictEntry> indexes) {
            this.indexes = indexes;
        }

        public Map<String, DictEntry> getIndexes() {
            return indexes;
        }

        public void setIndexes(Map<String, DictEntry> indexes) {
            this.indexes = indexes;
        }
    }

    @Test
    public void testDict() {

        String input = "{\"indexes\":{\"Tea\":{\"name\":\"Tea\",\"desc\":\"tea.\"},\"Coffee\":{\"name\":\"Coffee\",\"desc\":\"coffee.\"}}}";

        DefaultDeserializer deserializer = new DefaultDeserializer(new DefaultJsonParser(), new LowerSnakeCaseToCamelFieldNameResolver());

        try {
            Dict dict = deserializer.read(input, Dict.class);
            System.out.println(dict);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
