import java.util.ArrayList;
import java.util.List;

public class KNearestWords {
    private DistanceStrategy distanceStrategy;

    public KNearestWords(DistanceStrategy distanceStrategy) {
        if (distanceStrategy == null) {
            throw new IllegalArgumentException("Distance strategy cannot be null");
        }
        this.distanceStrategy = distanceStrategy;
    }

    public void setStrategy(DistanceStrategy distanceStrategy) {
        if (distanceStrategy == null) {
            throw new IllegalArgumentException("Distance strategy cannot be null");
        }
        this.distanceStrategy = distanceStrategy;
    }

    public List<Word> findNearest(Word word, WordStorage holder, int k) {
        if (word == null || holder == null) {
            throw new IllegalArgumentException("Word or WordHolder is null");
        }

        List<Word> candidates = new ArrayList<>();

        for (Word w : holder.getWords()) {
            if (w != word) {
                candidates.add(w);
            }
        }

        return sortAndLimit(word.getfullVector(), candidates, k);
    }

    public List<Word> findNearest(Vector targetVector, WordStorage holder, int k) {
        if (targetVector == null || holder == null) {
            throw new IllegalArgumentException("Target vector or WordHolder is null");
        }

        List<Word> candidates = new ArrayList<>(holder.getWords());
        return sortAndLimit(targetVector, candidates, k);
    }

    private List<Word> sortAndLimit(Vector targetVector, List<Word> candidates, int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("K must be positive");
        }

        candidates.sort((w1, w2) -> {
            double first = distanceStrategy.distanceCheck(targetVector, w1.getfullVector());
            double second = distanceStrategy.distanceCheck(targetVector, w2.getfullVector());

            if (distanceStrategy.higherIsBetter()) {
                return Double.compare(second, first);
            }

            return Double.compare(first, second);
        });

        int limit = Math.min(k, candidates.size());
        return new ArrayList<>(candidates.subList(0, limit));
    }
}