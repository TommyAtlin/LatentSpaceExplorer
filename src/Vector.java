public class Vector {
    private final double[] vectors;

    public Vector(double[] vectors) {
        if (vectors == null) {
            throw new IllegalArgumentException("Vector array cannot be null");
        }

        this.vectors = vectors.clone();
    }

    public int size() {
        return vectors.length;
    }

    public double getValue(int i) {
        if (i < 0 || i >= vectors.length) {
            throw new IllegalArgumentException("Index out of bounds");
        }

        return vectors[i];
    }
    // it returns a copy to protect the internal array so it won't get modified by external forces .
    public double[] getVectors() {
        return vectors.clone();
    }

    public Vector add(Vector v) {
        sameSizeCheck(v);

        double[] result = new double[vectors.length];

        for (int i = 0; i < vectors.length; i++) {
            result[i] = this.vectors[i] + v.vectors[i];
        }

        return new Vector(result);
    }

    public Vector subtract(Vector v) {
        sameSizeCheck(v);

        double[] result = new double[vectors.length];

        for (int i = 0; i < vectors.length; i++) {
            result[i] = this.vectors[i] - v.vectors[i];
        }

        return new Vector(result);
    }
    // Returns a new vector divided by a scalar, not in use for now but add as an normal arithmetic operation   .
    public Vector divide(double number) {
        if (number == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }

        double[] result = new double[vectors.length];

        for (int i = 0; i < vectors.length; i++) {
            result[i] = vectors[i] / number;
        }

        return new Vector(result);
    }

    private void sameSizeCheck(Vector v) {
        if (v == null) {
            throw new IllegalArgumentException("Other vector cannot be null");
        }

        if (this.size() != v.size()) {
            throw new IllegalArgumentException("Vectors must be in the same dimension");
        }
    }
}