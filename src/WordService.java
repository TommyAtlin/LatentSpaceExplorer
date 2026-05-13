import java.util.List;

public class WordService {
    private final WordStorage storage;
    private final KNearestWords nearestWords;
    private final VectorArithmeticService vectorArithmeticService;

    public WordService(WordStorage storage, DistanceStrategy distanceStrategy) {
        if (storage == null) {
            throw new IllegalArgumentException("WordStorage cannot be null");
        }

        if (distanceStrategy == null) {
            throw new IllegalArgumentException("DistanceStrategy cannot be null");
        }

        this.storage = storage;
        this.nearestWords = new KNearestWords(distanceStrategy);
        this.vectorArithmeticService = new VectorArithmeticService();
    }

    public Word findWord(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Word text cannot be empty");
        }

        Word word = storage.getWord(text.trim());

        if (word == null) {
            throw new IllegalArgumentException("Word not found: " + text);
        }

        return word;
    }

    public List<Word> findNearestToWord(String text, int k) {
        Word word = findWord(text);
        return nearestWords.findNearest(word, storage, k);
    }

    public List<Word> findNearestToVector(Vector vector, int k) {
        if (vector == null) {
            throw new IllegalArgumentException("Vector cannot be null");
        }

        return nearestWords.findNearest(vector, storage, k);
    }

    public List<Word> findAnalogy(String firstText, String secondText, String thirdText, int k) {
        Word first = findWord(firstText);
        Word second = findWord(secondText);
        Word third = findWord(thirdText);

        Vector resultVector = vectorArithmeticService.vectorArithmetics(first, second, third);

        return nearestWords.findNearest(resultVector, storage, k);
    }

    public double calculateDistance(String firstText, String secondText, DistanceStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Distance strategy cannot be null");
        }

        Word first = findWord(firstText);
        Word second = findWord(secondText);

        Distance distance = new Distance(strategy);

        return distance.computeDistance(first, second);
    }
    // Left it as an option it allows me to change the distance strategy at runtime for example from the UI
    public void setDistanceStrategy(DistanceStrategy distanceStrategy) {
        if (distanceStrategy == null) {
            throw new IllegalArgumentException("DistanceStrategy cannot be null");
        }

        nearestWords.setStrategy(distanceStrategy);
    }
}