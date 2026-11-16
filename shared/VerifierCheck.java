import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Cross-verifies the shared Ed25519 test vector using only the JDK's built-in
 * provider (java.security) — the same API path the Android companion uses.
 * Args: <pubKeyB64> <messageB64> <signatureB64> <tamperedMessageB64>
 */
public class VerifierCheck {
    // SPKI (X.509 SubjectPublicKeyInfo) prefix for a raw Ed25519 public key.
    static final byte[] SPKI_PREFIX = {
        0x30, 0x2a, 0x30, 0x05, 0x06, 0x03, 0x2b, 0x65, 0x70, 0x03, 0x21, 0x00
    };

    static PublicKey ed25519PublicKey(byte[] raw) throws Exception {
        byte[] der = new byte[SPKI_PREFIX.length + raw.length];
        System.arraycopy(SPKI_PREFIX, 0, der, 0, SPKI_PREFIX.length);
        System.arraycopy(raw, 0, der, SPKI_PREFIX.length, raw.length);
        return KeyFactory.getInstance("Ed25519")
                .generatePublic(new X509EncodedKeySpec(der));
    }

    static boolean verify(PublicKey pk, byte[] msg, byte[] sig) throws Exception {
        Signature s = Signature.getInstance("Ed25519");
        s.initVerify(pk);
        s.update(msg);
        return s.verify(sig);
    }

    public static void main(String[] args) throws Exception {
        Base64.Decoder d = Base64.getDecoder();
        PublicKey pk = ed25519PublicKey(d.decode(args[0]));
        byte[] msg = d.decode(args[1]);
        byte[] sig = d.decode(args[2]);
        byte[] tampered = d.decode(args[3]);

        boolean validOk = verify(pk, msg, sig);
        boolean tamperedRejected = !verify(pk, tampered, sig);

        System.out.println("valid signature verifies:   " + validOk);
        System.out.println("tampered claim rejected:     " + tamperedRejected);
        if (validOk && tamperedRejected) {
            System.out.println("JAVA_ED25519_CROSSCHECK: PASS");
        } else {
            System.out.println("JAVA_ED25519_CROSSCHECK: FAIL");
            System.exit(1);
        }
    }
}
