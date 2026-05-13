public class Euclidean implements DistanceStrategy {
    @Override
    public double distanceCheck(Vector a, Vector b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Vectors cannot be null");
        }

        if (a.size() != b.size()) {
            throw new IllegalArgumentException("Different dimensions");
        }
        double sum = 0.0;
        for (int i = 0; i < a.size(); i++) {
            double dif = a.getValue(i) - b.getValue(i);
            sum += dif * dif;
        }
        return Math.sqrt(sum);

    }

    @Override
    public boolean higherIsBetter() {
        return false;
    }
}