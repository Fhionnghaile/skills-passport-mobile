import Foundation

/// Deterministic canonical JSON — keys sorted, no whitespace — byte-identical to
/// Python's `json.dumps(sort_keys=True, separators=(",",":"))`, which is how
/// Veritas signs. (Verified byte-for-byte against the shared test vector.)
public enum CanonicalJSON {
    public static func encode(_ claims: [String: ClaimValue]) -> Data {
        var out = "{"
        for (i, key) in claims.keys.sorted().enumerated() {
            if i > 0 { out += "," }
            out += encodeString(key) + ":" + encodeValue(claims[key]!)
        }
        out += "}"
        return Data(out.utf8)
    }

    private static func encodeValue(_ v: ClaimValue) -> String {
        switch v {
        case .string(let s): return encodeString(s)
        case .int(let n): return String(n)
        case .bool(let b): return b ? "true" : "false"
        }
    }

    private static func encodeString(_ s: String) -> String {
        var r = "\""
        for c in s {
            switch c {
            case "\"": r += "\\\""
            case "\\": r += "\\\\"
            case "\n": r += "\\n"
            case "\r": r += "\\r"
            case "\t": r += "\\t"
            default: r.append(c)
            }
        }
        return r + "\""
    }
}
