package library.Additions;

public final class KmpSearcher {
    private KmpSearcher() {}
    public static boolean contains(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) return true;
        int[] lps = lps(pattern);
        int i = 0, j = 0;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) { i++; j++; if (j == pattern.length()) return true; }
            else if (j != 0) j = lps[j - 1];
            else i++;
        }
        return false;
    }
    private static int[] lps(String p) {
        int[] lps = new int[p.length()];
        for (int i = 1, len = 0; i < p.length();) {
            if (p.charAt(i) == p.charAt(len)) lps[i++] = ++len;
            else if (len != 0) len = lps[len - 1];
            else lps[i++] = 0;
        }
        return lps;
    }
}

