package xcord.antibot;

public class SimilarityCache {
    private final boolean sameLengthOnly;
    private final String[] cache;
    private int index = 0;

    public SimilarityCache(boolean sameLengthOnly, int cacheSize) {
        this.sameLengthOnly = sameLengthOnly;
        this.cache = new String[cacheSize];
    }

    public Match findClosest(String name) {
        Match bestMatch = null;
        for (int i = 0; i < this.cache.length; ++i) {
            String cached = this.cache[i];
            if (cached == null || (this.sameLengthOnly && cached.length() != name.length()) || name.equals(cached)) {
                continue;
            }
            double score = this.similarity(cached, name);
            if (bestMatch != null && !(score > bestMatch.score)) {
                continue;
            }
            bestMatch = new Match(score, cached);
        }
        this.cache[this.index] = name;
        this.index = (this.index + 1) % this.cache.length;
        return bestMatch;
    }

    private double similarity(String first, String second) {
        String longer = first;
        String shorter = second;
        if (first.length() < second.length()) {
            longer = second;
            shorter = first;
        }
        int length = longer.length();
        if (length == 0) {
            return 1.0;
        }
        return (double)(length - this.levenshtein(longer, shorter)) / (double)length;
    }

    private int levenshtein(String source, String target) {
        int[] distances = new int[target.length() + 1];
        for (int i = 0; i <= source.length(); ++i) {
            int current = i;
            for (int j = 0; j <= target.length(); ++j) {
                if (i == 0) {
                    distances[j] = j;
                    continue;
                }
                if (j <= 0) {
                    continue;
                }
                int next = distances[j - 1];
                if (source.charAt(i - 1) != target.charAt(j - 1)) {
                    next = Math.min(Math.min(next, current), distances[j]) + 1;
                }
                distances[j - 1] = current;
                current = next;
            }
            if (i <= 0) {
                continue;
            }
            distances[target.length()] = current;
        }
        return distances[target.length()];
    }

    public static class Match {
        private double score;
        private String value;

        public Match(double score, String value) {
            this.score = score;
            this.value = value;
        }

        public double getScore() {
            return this.score;
        }

        public String getValue() {
            return this.value;
        }
    }
}
