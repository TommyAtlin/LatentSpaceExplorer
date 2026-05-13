public class Cosine implements DistanceStrategy {
    @Override
    public double distanceCheck(Vector a, Vector b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Vectors cannot be null");
        }

        if (a.size() != b.size()) {
            throw new IllegalArgumentException("Different dimensions");
        }

        double product = 0.0;
        double normOfA = 0.0;
        double normOfB = 0.0;

        for (int i = 0; i < a.size(); i++) {
            double firstValue = a.getValue(i);
            double secondValue = b.getValue(i);

            product += firstValue * secondValue;
            normOfA += firstValue * firstValue;
            normOfB += secondValue * secondValue;
        }

        if (normOfA == 0 || normOfB == 0) {
            throw new IllegalArgumentException("Vectors cannot be zero length");
        }

        double similarity = product / (Math.sqrt(normOfA) * Math.sqrt(normOfB));

        return 1.0 - similarity;
    }

    @Override
    public boolean higherIsBetter() {
        return false;
    }
}