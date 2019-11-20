import java.util.*;

public class HashListAutocomplete implements Autocompletor {

    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize = 0;

    public HashListAutocomplete (String[] terms, double[] weights) {
        if (terms == null || weights == null) {
            throw new NullPointerException("One or more arguments null");
        }
        if (terms.length != weights.length) {
            throw new IllegalArgumentException("terms and weights are not the same length");
        }
        initialize(terms, weights);
    }

    @Override
    public List<Term> topMatches(String prefix, int k) {
        if (prefix.length() > MAX_PREFIX) {
            prefix = prefix.substring(0, MAX_PREFIX+1);
        }
        if (prefix == null) {
            throw new NullPointerException();
        }
        if (myMap.containsKey(prefix)) {
            List<Term> all = myMap.get(prefix);
            List<Term> list = all.subList(0, Math.min(k, all.size()));
            return list;
        }
        return null;
    }

    @Override
    public void initialize(String[] terms, double[] weights) {
        myMap = new HashMap<>();

        for (int i = 0; i < terms.length; i++) {
            for (int k = 0; k <= Math.min(MAX_PREFIX, terms[i].length()); k++) {
                String pre = terms[i].substring(0,k);
                Term currentT = new Term(terms[i], weights[i]);
                myMap.putIfAbsent(pre, new ArrayList<Term>());
                myMap.get(pre).add(currentT);
            }
        }

        for (String t: myMap.keySet()) {
            Collections.sort(myMap.get(t),Comparator.comparing(Term::getWeight).reversed());
        }
    }


    @Override
    public int sizeInBytes() {
        if (mySize == 0) {
            for (String s : myMap.keySet()) {
                mySize += BYTES_PER_CHAR* s.length();
                for (Term current: myMap.get(s)) {
                    mySize += BYTES_PER_DOUBLE + BYTES_PER_CHAR*current.getWord().length();
                }
            }
        }
        return mySize;
    }
}
