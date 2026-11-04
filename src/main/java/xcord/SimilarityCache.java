package xcord;

public class SimilarityCache {
    private final boolean ignoreCase;
    private final String[] cache;
    private int index = 0;

    public SimilarityCache(boolean bl, int n) {
        this.ignoreCase = bl;
        this.cache = new String[n];
    }

    public Match findClosest(String string) {
        Match match = null;
        for (int i = 0; i < this.cache.length; ++i) {
            String string2 = this.cache[i];
            if (string2 == null || this.ignoreCase && string2.length() != string.length() || string.equals(string2)) continue;
            double d = this.similarity(string2, string);
            if (match != null && !(d > match.score)) continue;
            match = new Match(d, string2);
        }
        this.cache[this.index] = string;
        this.index = (this.index + 1) % this.cache.length;
        return match;
    }

    private double similarity(String string, String string2) {
        int n;
        String string3 = string;
        String string4 = string2;
        if (string.length() < string2.length()) {
            string3 = string2;
            string4 = string;
        }
        if ((n = string3.length()) == 0) {
            return 1.0;
        }
        return (double)(n - this.levenshtein(string3, string4)) / (double)n;
    }

    private int levenshtein(String string, String string2) {
        int[] nArray = new int[string2.length() + 1];
        for (int i = 0; i <= string.length(); ++i) {
            int n = i;
            for (int j = 0; j <= string2.length(); ++j) {
                if (i == 0) {
                    nArray[j] = j;
                    continue;
                }
                if (j <= 0) continue;
                int n2 = nArray[j - 1];
                if (string.charAt(i - 1) != string2.charAt(j - 1)) {
                    n2 = Math.min(Math.min(n2, n), nArray[j]) + 1;
                }
                nArray[j - 1] = n;
                n = n2;
            }
            if (i <= 0) continue;
            nArray[string2.length()] = n;
        }
        return nArray[string2.length()];
    }

    public static class Match {
        private double score;
        private String value;

        public Match(double d, String string) {
            this.score = d;
            this.value = string;
        }

        public double getScore() {
            return this.score;
        }

        public String getValue() {
            return this.value;
        }
    }
}

