package org.haic.browser;

import org.haic.browser.dpapi.WinDPAPI;
import org.haic.often.function.StringFunction;
import org.haic.often.util.Base64Util;
import org.haic.often.util.ReadWriteUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.util.Arrays;

/**
 * 谷歌内核本地浏览器解密类
 *
 * @author haicdust
 * @version 1.0
 * @since 2022/10/18 10:26
 */
public class Decrypt {

	/**
	 * Get user encryptedKey
	 *
	 * @param userHome userData home
	 * @return encryptedKey
	 */
	public static byte[] getEncryptedKey(File userHome) {
		return ReadWriteUtil.orgin(new File(userHome, "Local State")).readJSON().getJSONObject("os_crypt").getString("encrypted_key").getBytes();
	}

	/**
	 * Get Decrypt Value
	 *
	 * @param encryptedValue 加密值
	 * @param encryptedKey   密钥
	 * @return decrypt Value
	 */
	public static byte[] DPAPIDecode(byte[] encryptedValue, byte[] encryptedKey) {
		int keyLength = 256 / 8;
		int nonceLength = 96 / 8;
		String kEncryptionVersionPrefix = "v10";
		int GCM_TAG_LENGTH = 16;
		try {
			byte[] encryptedKeyBytes = Base64Util.decode(encryptedKey);
			assert new String(encryptedKeyBytes).startsWith("DPAPI");
			encryptedKeyBytes = Arrays.copyOfRange(encryptedKeyBytes, "DPAPI".length(), encryptedKeyBytes.length);
			WinDPAPI winDPAPI = WinDPAPI.newInstance(WinDPAPI.CryptProtectFlag.CRYPTPROTECT_UI_FORBIDDEN);
			byte[] keyBytes = winDPAPI.unprotectData(encryptedKeyBytes);
			assert keyLength == keyBytes.length;
			byte[] nonce = Arrays.copyOfRange(encryptedValue, kEncryptionVersionPrefix.length(), kEncryptionVersionPrefix.length() + nonceLength);
			encryptedValue = Arrays.copyOfRange(encryptedValue, kEncryptionVersionPrefix.length() + nonceLength, encryptedValue.length);
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
			GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
			encryptedValue = cipher.doFinal(encryptedValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedValue;
	}

	/**
	 * levelDB decode
	 *
	 * @param bytes encrypted Value
	 * @return decrypt Value
	 */
	public static String levelDBDecode(byte[] bytes) {
		StringFunction<String> unsigned = unsignedInt -> unsignedInt.length() == 1 ? "0" + unsignedInt : unsignedInt;
		StringBuilder result = new StringBuilder();
		if (bytes[0] == 0) {
			for (int i = 1; i < bytes.length - 1; i += 2) {
				result.append((char) Integer.parseInt(unsigned.apply(Integer.toHexString(Byte.toUnsignedInt(bytes[i + 1]))) + unsigned.apply(Integer.toHexString(Byte.toUnsignedInt(bytes[i]))), 16));
			}
		} else {
			for (int i = 1; i < bytes.length; i++) {
				result.append((char) Integer.parseInt(Integer.toHexString(Byte.toUnsignedInt(bytes[i])), 16));
			}
		}
		return String.valueOf(result);
	}

}
