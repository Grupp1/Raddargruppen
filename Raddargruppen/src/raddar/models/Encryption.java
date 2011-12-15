package raddar.models;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * I denna klassen finns krypteringsmetoder samlade
 * @author andbo265
 *
 */
public class Encryption {

	/**
	 * En metod som saltar och krypterar ett l�senord.
	 * Saltet som anv�nds �r det som tas in som en parametar, och 
	 * hashfunktionen som anv�nds �r SHA-1.
	 * @param password L�senordet i klartext
	 * @param salt Saltet som skall anv�ndas
	 * @return Det krypterade l�senordet
	 */
	public static String encrypt(String password, String salt) {
		return byteArrayToHexString(computeHash(password, salt));
	}
	
	/**
	 * Skapar ett nytt 4-tecken (32-bitars) l�ngt salt
	 * @return Ett nyskapat salt
	 */
	public static String newSalt() {
		StringBuilder sb = new StringBuilder("");
		
		for (int i = 0; i < 4; i++)
			sb.append((int) (Math.random() * 10));
		return sb.toString();
	}
	
	/*
	 * Saltar och krypterar
	 */
	private static byte[] computeHash(String str, String salt) {
		try {
			MessageDigest md = null;
			md = MessageDigest.getInstance("SHA-1");
			md.reset();
			byte[] b = str.getBytes("UTF-8");
			md.update(b);
			//md.reset();
			md.update(salt.getBytes("UTF-8"));
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * �vers�tter en byte-array till hexadecimalt format
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}
}
