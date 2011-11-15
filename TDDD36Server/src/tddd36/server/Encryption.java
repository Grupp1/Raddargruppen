package tddd36.server;

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
	public static String hashPassword(String password, String salt) {
		return byteArrayToHexString(computeHash(password, salt));
	}
	
	/*
	 * Saltar och krypterar
	 */
	private static byte[] computeHash(String str, String salt) {
		try {
			MessageDigest md = null;
			md = MessageDigest.getInstance("SHA-1");
			md.reset();
			md.update(str.getBytes("UTF-8"));
			md.reset();
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
		System.out.println("B-length: " + b.length);
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			System.out.println((int)b[i]);
			int v = b[i] & 0xff;
			System.out.println(v);
			if (v < 16) {
				sb.append('0');
				System.out.println("Appending 0: " + sb);
			}
			sb.append(Integer.toHexString(v));
			System.out.println(sb);
		}
		return sb.toString().toUpperCase();
	}
}
