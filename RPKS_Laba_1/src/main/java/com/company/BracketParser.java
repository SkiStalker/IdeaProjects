package com.company;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BracketParser {
    static Map<Character, Character> parseBracketsFromFile(String configPath) throws IOException, ParseException {
        Map<Character, Character> brackets = new HashMap<>();
        Object obj = new JSONParser().parse(new FileReader(configPath));
        JSONObject conf = (JSONObject) obj;
        JSONArray jsonBrackets = (JSONArray) conf.get("bracket");
        jsonBrackets.forEach((item) -> {
            JSONObject jsonBracket = (JSONObject) item;
            brackets.put(((String) jsonBracket.get("right")).charAt(0), ((String) jsonBracket.get("left")).charAt(0));
        });
        return brackets;
    }
}
