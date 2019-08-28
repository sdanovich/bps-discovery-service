
package com.mastercard.labs.bps.discovery.security;

import com.mastercard.labs.bps.discovery.exceptions.SignatureVerificationException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Webhook {
    private static final long DEFAULT_TOLERANCE = 300L;

    public Webhook() {
    }

    public static final class Util {
        public Util() {
        }

        public static String computeHmacSha256(String key, String message) throws NoSuchAlgorithmException, InvalidKeyException {
            Mac hasher = Mac.getInstance("HmacSHA256");
            hasher.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = hasher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            String result = "";
            byte[] var5 = hash;
            int var6 = hash.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                byte b = var5[var7];
                result = result + Integer.toString((b & 255) + 256, 16).substring(1);
            }

            return result;
        }

        public static boolean secureCompare(String a, String b) {
            byte[] digesta = a.getBytes(StandardCharsets.UTF_8);
            byte[] digestb = b.getBytes(StandardCharsets.UTF_8);
            return MessageDigest.isEqual(digesta, digestb);
        }

        public static long getTimeNow() {
            long time = System.currentTimeMillis() / 1000L;
            return time;
        }
    }

    public static final class Signature {
        public static final String EXPECTED_SCHEME = "v1";

        public Signature() {
        }

        public static boolean verifyHeader(String payload, String sigHeader, String secret, long tolerance) throws SignatureVerificationException {
            long timestamp = getTimestamp(sigHeader);
            List<String> signatures = getSignatures(sigHeader, "v1");
            if (timestamp <= 0L) {
                throw new SignatureVerificationException("Unable to extract timestamp and signatures from header");
            } else if (signatures.size() == 0) {
                throw new SignatureVerificationException("No signatures found with expected scheme");
            } else {
                String signedPayload = String.format("%d.%s", new Object[]{Long.valueOf(timestamp), payload});

                String expectedSignature;
                try {
                    expectedSignature = computeSignature(signedPayload, secret);
                } catch (Exception var13) {
                    throw new SignatureVerificationException("Unable to compute signature for payload");
                }

                boolean signatureFound = false;
                Iterator var11 = signatures.iterator();

                while (var11.hasNext()) {
                    String signature = (String) var11.next();
                    if (Webhook.Util.secureCompare(expectedSignature, signature)) {
                        signatureFound = true;
                        break;
                    }
                }

                if (!signatureFound) {
                    throw new SignatureVerificationException("No signatures found matching the expected signature for payload");
                } else if (tolerance > 0L && timestamp < Webhook.Util.getTimeNow() - tolerance) {
                    throw new SignatureVerificationException("Timestamp outside the tolerance zone");
                } else {
                    return true;
                }
            }
        }

        private static long getTimestamp(String sigHeader) {
            String[] items = sigHeader.split(",", -1);
            String[] var2 = items;
            int var3 = items.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String item = var2[var4];
                String[] itemParts = item.split("=", 2);
                if (itemParts[0].equals("t")) {
                    return Long.parseLong(itemParts[1]);
                }
            }

            return -1L;
        }

        private static List<String> getSignatures(String sigHeader, String scheme) {
            List<String> signatures = new ArrayList();
            String[] items = sigHeader.split(",", -1);
            String[] var4 = items;
            int var5 = items.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                String item = var4[var6];
                String[] itemParts = item.split("=", 2);
                if (itemParts[0].equals(scheme)) {
                    signatures.add(itemParts[1]);
                }
            }

            return signatures;
        }

        private static String computeSignature(String payload, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
            return Webhook.Util.computeHmacSha256(secret, payload);
        }
    }
}
