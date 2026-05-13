public interface DistanceStrategy {
    double distanceCheck(Vector a, Vector b);

    boolean higherIsBetter();
}