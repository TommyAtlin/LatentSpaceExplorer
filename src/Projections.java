import java.util.List;

public class Projections {

    public Point2D projectWord(Word word, int iofX, int iofY) {
        if (word == null) {
            throw new IllegalArgumentException("Word is null");
        }

        if (word.getPcaVector() == null) {
            throw new IllegalArgumentException("PCA vector is null");
        }

        if (iofX < 0 || iofY < 0) {
            throw new IllegalArgumentException("Axis index must be non-negative");
        }

        if (iofX >= word.getPcaVector().size() || iofY >= word.getPcaVector().size()) {
            throw new IllegalArgumentException("Axis index out of bounds");
        }

        double x = word.getPcaVector().getValue(iofX);
        double y = word.getPcaVector().getValue(iofY);

        return new Point2D(x, y);
    }

    public double projectX(Word word, Word start, Word end) {
        if (word == null || start == null || end == null) {
            throw new IllegalArgumentException("Words cannot be null");
        }

        Vector target = word.getfullVector();
        Vector startVector = start.getfullVector();
        Vector endVector = end.getfullVector();

        if (target.size() != startVector.size() || target.size() != endVector.size()) {
            throw new IllegalArgumentException("All vectors must have the same dimension");
        }

        Vector directionVec = endVector.subtract(startVector);
        Vector relativeTarget = target.subtract(startVector);

        double scalarMultiplier = 0;
        double lengthTimes2 = 0;

        for (int i = 0; i < target.size(); i++) {
            scalarMultiplier += relativeTarget.getValue(i) * directionVec.getValue(i);
            lengthTimes2 += directionVec.getValue(i) * directionVec.getValue(i);
        }

        if (lengthTimes2 == 0) {
            throw new IllegalArgumentException("Projection axis cannot be zero");
        }

        return scalarMultiplier / Math.sqrt(lengthTimes2);
    }

    public Vector calculateCentroid(List<Word> words) {
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("Words list cannot be empty");
        }

        int vectorSize = words.get(0).getfullVector().size();
        double[] sums = new double[vectorSize];

        for (Word word : words) {
            if (word == null) {
                throw new IllegalArgumentException("Word cannot be null");
            }

            Vector vector = word.getfullVector();

            if (vector.size() != vectorSize) {
                throw new IllegalArgumentException("All vectors must have the same dimension");
            }

            for (int i = 0; i < vectorSize; i++) {
                sums[i] += vector.getValue(i);
            }
        }

        for (int i = 0; i < vectorSize; i++) {
            sums[i] /= words.size();
        }

        return new Vector(sums);
    }
}