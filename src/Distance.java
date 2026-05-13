public class Distance {

    private DistanceStrategy strategy;

    public Distance(DistanceStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Distance strategy cannot be null");
        }

        this.strategy = strategy;
    }
    // it allows KNearestWords to switch between distance strategies at runtime, for now not in use
    public void setStrategy(DistanceStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Distance strategy cannot be null");
        }

        this.strategy = strategy;
    }

    public double computeDistance(Word w1, Word w2) {
        if (w1 == null || w2 == null) {
            throw new IllegalArgumentException("Words cannot be null");
        }

        return strategy.distanceCheck(w1.getfullVector(), w2.getfullVector());
    }
}