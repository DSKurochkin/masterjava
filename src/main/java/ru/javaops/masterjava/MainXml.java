package ru.javaops.masterjava;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.Group;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainXml {
    public static void main(String[] args) throws IOException, JAXBException {
        printUsersFomProject(args[0]);
    }

    static void printUsersFomProject(String projectName) throws IOException, JAXBException {
        JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
        Payload payload = JAXB_PARSER.unmarshal(
                Resources.getResource("payload4main.xml").openStream());
        List<String> groupNamesOfProject = payload.getProjects().getProject().stream()
                .filter(p -> p.getName().equalsIgnoreCase(projectName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Argument must be project name"))
                .getGroups().getGroup().stream()
                .map(Group::getName)
                .collect(Collectors.toList());
        payload.getUsers().getUser().stream()
                .filter(u -> u.getGroups().stream()
                        .map(Group::getName)
                        .anyMatch(groupNamesOfProject::contains))
                .sorted(Comparator.comparing(User::getFullName))
                .forEach(System.out::println);
    }
}
