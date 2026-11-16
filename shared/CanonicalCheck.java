import java.util.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Mirrors the Kotlin CanonicalJson for the vector's claim types, to prove byte-equality. */
public class CanonicalCheck {
    static String enc(Object v) {
        if (v == null) return "null";
        if (v instanceof String s) return "\"" + s.replace("\\","\\\\").replace("\"","\\\"") + "\"";
        if (v instanceof Boolean b) return b ? "true" : "false";
        if (v instanceof Integer || v instanceof Long) return v.toString();
        if (v instanceof TreeMap<?,?> m) {
            StringBuilder sb = new StringBuilder("{"); boolean first = true;
            for (var e : m.entrySet()) { if(!first) sb.append(','); first=false;
                sb.append(enc(e.getKey().toString())).append(':').append(enc(e.getValue())); }
            return sb.append('}').toString();
        }
        return "\"" + v + "\"";
    }
    public static void main(String[] a) {
        TreeMap<String,Object> claims = new TreeMap<>();
        claims.put("name","Jane Doe"); claims.put("credential","BSc Computer Science");
        claims.put("issuer","Derry"); claims.put("year", 2024);
        byte[] got = enc(claims).getBytes(StandardCharsets.UTF_8);
        String expectB64 = a[0];
        byte[] expect = Base64.getDecoder().decode(expectB64);
        boolean eq = Arrays.equals(got, expect);
        System.out.println("canonical bytes match vector: " + eq);
        System.out.println(eq ? "CANONICAL_CHECK: PASS" : "got=" + new String(got));
        if(!eq) System.exit(1);
    }
}
