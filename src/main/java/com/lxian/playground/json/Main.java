package com.lxian.playground.json;

import com.lxian.playground.json.mapper.DefaultDeserializer;
import com.lxian.playground.json.mapper.error.JsonDeserializationError;
import com.lxian.playground.json.mapper.field.LowerSnakeCaseToCamelFieldNameResolver;
import com.lxian.playground.json.parser.DefaultJsonParser;
import com.lxian.playground.json.parser.error.InvalidJsonError;
import com.lxian.playground.json.parser.object.JsObject;

import java.io.IOException;
import java.util.List;


public class Main {

    public static class Person {
        private String name;

        private String address;

        private boolean hasMailBox;

        private List<Job> jobs;

        public Person() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public boolean isHasMailBox() {
            return hasMailBox;
        }

        public void setHasMailBox(boolean hasMailBox) {
            this.hasMailBox = hasMailBox;
        }

        public List<Job> getJobs() {
            return jobs;
        }

        public void setJobs(List<Job> jobs) {
            this.jobs = jobs;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", address='" + address + '\'' +
                    ", hasMailBox=" + hasMailBox +
                    ", job=" + jobs +
                    '}';
        }
    }

    public static class Job {
        String title;

        public Job() {
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "Job{" +
                    "title='" + title + '\'' +
                    '}';
        }
    }

    public static void main(String args[]) {
        testDeserialization("{\"name\": \"SomeOne\", \"address\": null, \"has_mail_box\": true, \"jobs\": [{\"title\": \"engineer\"}, {\"title\": \"manager\"}]}", Person.class);
//        tc1();
//        tc2();
    }

    static <T> void testDeserialization(String s, Class<T> clazz) {
        try {
            T obj = new DefaultDeserializer(new DefaultJsonParser(), new LowerSnakeCaseToCamelFieldNameResolver())
                    .read(s, clazz);
            System.out.println(obj);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonDeserializationError jsonDeserializationError) {
            jsonDeserializationError.printStackTrace();
        }
    }

    static void testParser(String s) {
        DefaultJsonParser jsonParser = new DefaultJsonParser();
        JsObject jsObject;
        try {
            jsObject = jsonParser.parse(s);
            System.out.println(s);
            System.out.println(jsObject);
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidJsonError invalidJsonError) {
            invalidJsonError.printStackTrace();
        }
    }

    static void tc1() {
        testParser("  {\"key1\": true, \"key2\": false, \"key3\": null }  ");
        testParser("  {\"key1\": 134134124, \"key2\": -1134, \"key3\": +123.214324 ,\"key3\": 1.2324E-5, \"key3\": 1.2324e+5 }");
        testParser("{\"key1\": true, \"key2\": [\"a\", \"\", \"\\\\\"], \"key3\": [] }  ");
        testParser("{\"key1\": {\"ik1\": \"bbb\",\"ik2\": [null], \"ik3\": {}}, \"key2\": [\"a\", {\"ik1\": 21}, false], \"key3\": null }  ");
    }

    static void tc2() {
        testParser(" \"adff\"");
        testParser("  {\"key1\": 123E1a}");
        testParser("  {\"key1\": true, ,\"key2\": false, \"key3\": null }  ");
        testParser("  {\"key1\": true, \"key2\": false, \"key3\": nall }  ");
        testParser("  {\"key1\": true, \"key2\": false, \"\"key3\": }  ");
    }
}
