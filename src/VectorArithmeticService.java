public class VectorArithmeticService {

    public Vector vectorArithmetics(Word first, Word second, Word third) {
        if (first == null || second == null || third == null) {
            throw new IllegalArgumentException("Words cannot be null");
        }

        return third.getfullVector()
                .add(second.getfullVector())
                .subtract(first.getfullVector());
    }
}