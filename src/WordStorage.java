import java.util.*;
public class WordStorage {
    private List<Word> words;
    private Map<String,Word> hashMapword;

    public WordStorage() {
        this.words = new ArrayList<>();
        this.hashMapword = new HashMap<>();
    }
    public void addWords(Word enteredWord) {
        this.words.add(enteredWord);
        this.hashMapword.put(enteredWord.getWord(), enteredWord);
    }
    public List<Word> getWords() {
        return new ArrayList<>(words);
    }
    public Word getWord(String word) {
        return this.hashMapword.get(word);
    }
    public int size(){
        return this.words.size();
    }

}
