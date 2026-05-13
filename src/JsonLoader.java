import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class JsonLoader {

    public WordStorage load(String fullPath, String pcaPath) throws Exception {
        Map<String, double[]> fullMap = parseJsonArray(fullPath);
        Map<String, double[]> pcaMap = parseJsonArray(pcaPath);

        WordStorage storage = new WordStorage();

        for (String word : fullMap.keySet()) {
            double[] fullVectorArray = fullMap.get(word);
            double[] pcaVectorArray = pcaMap.get(word);

            if (pcaVectorArray == null) {
                continue;
            }

            Vector fullVector = new Vector(fullVectorArray);
            Vector pcaVector = new Vector(pcaVectorArray);

            Word wordObject = new Word(word, fullVector, pcaVector);
            storage.addWords(wordObject);
        }

        return storage;
    }

    private Map<String, double[]> parseJsonArray(String path) throws Exception {
        StringBuilder jsonBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }
        }

        String json = jsonBuilder.toString();

        Map<String, double[]> map = new HashMap<>();

        if (json.length() < 2 || !json.startsWith("[") || !json.endsWith("]")) {
            throw new IllegalArgumentException("Invalid JSON array format in file: " + path);
        }

        json = json.substring(1, json.length() - 1).trim();

        if (json.isEmpty()) {
            return map;
        }

        String[] objects = splitObjects(json);

        for (String object : objects) {
            String word = extractWord(object);
            double[] vector = extractVector(object);

            map.put(word, vector);
        }

        return map;
    }

    private String[] splitObjects(String json) {
        return json.split("\\},\\s*\\{");
    }

    private String extractWord(String object) {
        object = cleanObject(object);

        String wordKey = "\"word\"";
        int wordKeyIndex = object.indexOf(wordKey);

        if (wordKeyIndex == -1) {
            throw new IllegalArgumentException("Missing word field");
        }

        int colonIndex = object.indexOf(":", wordKeyIndex);
        int firstQuote = object.indexOf("\"", colonIndex + 1);
        int secondQuote = object.indexOf("\"", firstQuote + 1);

        if (firstQuote == -1 || secondQuote == -1) {
            throw new IllegalArgumentException("Invalid word field");
        }

        return object.substring(firstQuote + 1, secondQuote);
    }

    private double[] extractVector(String object) {
        object = cleanObject(object);

        String vectorKey = "\"vector\"";
        int vectorKeyIndex = object.indexOf(vectorKey);

        if (vectorKeyIndex == -1) {
            throw new IllegalArgumentException("Missing vector field");
        }

        int startBracket = object.indexOf("[", vectorKeyIndex);
        int endBracket = object.indexOf("]", startBracket);

        if (startBracket == -1 || endBracket == -1) {
            throw new IllegalArgumentException("Invalid vector field");
        }

        String vectorContent = object.substring(startBracket + 1, endBracket).trim();

        if (vectorContent.isEmpty()) {
            return new double[0];
        }

        String[] numbers = vectorContent.split(",");
        double[] vector = new double[numbers.length];

        for (int i = 0; i < numbers.length; i++) {
            vector[i] = Double.parseDouble(numbers[i].trim());
        }

        return vector;
    }

    private String cleanObject(String object) {
        object = object.trim();

        if (!object.startsWith("{")) {
            object = "{" + object;
        }

        if (!object.endsWith("}")) {
            object = object + "}";
        }

        return object;
    }
}