package utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class Utils {
	public static String generateAppSecretProof(String accessToken, String appSecret){
		Mac mac;
		try {
			mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret = new SecretKeySpec(appSecret.getBytes(), "HmacSHA256");
			mac.init(secret);
			byte[] digest = mac.doFinal(accessToken.getBytes());
			return new String(Hex.encodeHex(digest));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("HmacSHA256: No such algorithm");
		} catch (InvalidKeyException e) {
			System.out.println(appSecret + " is not a valid key");
		}
		
		return null;
		
	}
}
