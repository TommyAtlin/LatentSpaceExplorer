public class Word {
    private final String word;
    private final Vector fullVector;
    private final Vector pcaVector;

    public Word(String word, Vector firstVector, Vector secondVector) {
        if (word == null || firstVector == null || secondVector == null) {
            throw new IllegalArgumentException("Word fields cannot be null");
        }
        this.word = word;
        this.fullVector = firstVector;
        this.pcaVector = secondVector;
    }
    public String getWord() {
        return word;
    }
    public Vector getfullVector() {
        return fullVector;
    }
    public Vector getPcaVector() {
        return pcaVector;
    }
}
