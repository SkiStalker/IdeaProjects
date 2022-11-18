package com.company;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Stack;

public class Main {

    public static void main(String[] args){
        try {
            Map<Character, Character> brackets = BracketParser.parseBracketsFromFile(
                    "conf.json");
            String file =
                    Files.readString(Path.of(
                            "text.txt"));
            Stack<Pair<Character, Integer>> bracketsStack = new Stack<>();
            boolean success = true;
            for (int i = 0; i < file.length(); i++) {
                if (brackets.containsValue(file.charAt(i))) {
                    bracketsStack.push(new ImmutablePair<>(file.charAt(i), i));
                } else if (brackets.containsKey(file.charAt(i))) {
                    if (bracketsStack.isEmpty() || (brackets.get(file.charAt(i)) != bracketsStack.peek().getLeft())) {
                        if (bracketsStack.size() < 2 || (brackets.get(bracketsStack.pop().getLeft()) !=
                                bracketsStack.pop().getLeft())) {
                            System.out.printf("Wrong bracket %s at %d position\n", file.charAt(i), i);
                            success = false;
                            break;
                        } else if (bracketsStack.isEmpty() || (brackets.get(file.charAt(i)) != bracketsStack.pop().getLeft())) {
                            System.out.printf("Wrong bracket at %d position\n", i);
                            success = false;
                            break;
                        }
                    } else {
                        bracketsStack.pop();
                    }
                }
            }
            if (success) {
                if (!bracketsStack.isEmpty()) {
                    System.out.printf("Wrong bracket at %d position\n", bracketsStack.pop().getRight());
                } else {
                    System.out.println("Success check");
                }
            }
        }
        catch (IOException ex) {
            System.out.println("Can not find specified file");
        }
        catch (ParseException ex) {
            System.out.println("Wrong configuration file syntax");
        }
    }
}
