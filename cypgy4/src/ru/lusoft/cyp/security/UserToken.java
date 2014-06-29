package ru.lusoft.surguch.security;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import android.annotation.SuppressLint;
import android.util.Log;

public class UserToken {

	private static final String TAG = "UserToken";

	private static final String KEYSTORE_TYPE = "BKS";
	private static final String KEYSTORE_PASS = "qwerty";
	private static final String KEY_NAME = "android";
	private static final String KEY_PASS = "android";
	
	private static KeyStore.PrivateKeyEntry key;
	private static KeyPair keyPair;
	
	
	public static void initialize(InputStream in) {
//		InputStream in = context.getResources().openRawResource(R.raw.ecypguch); //getAssets().open("debug.keystore");
		try {
			KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
			keyStore.load(in, KEYSTORE_PASS.toCharArray());
//			Log.i(TAG, "Load keystore-inputstream: " + in);
			KeyStore.Entry entry = keyStore.getEntry(KEY_NAME, new KeyStore.PasswordProtection(KEY_PASS.toCharArray()));
			if (entry != null) {
				key = (KeyStore.PrivateKeyEntry) entry;
				Log.d(TAG, "Load alias 'android' from keystore");
				PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
				Log.d(TAG, "Private key:\n[alg=" + privateKey.getAlgorithm() + ", frmt=" + privateKey.getFormat() 
						+ "]\n{" + privateKey + "}");
				PublicKey publicKey = ((KeyStore.PrivateKeyEntry) entry).getCertificate().getPublicKey();
				Log.d(TAG, "Public key:\n[alg=" + publicKey.getAlgorithm() + ", frmt=" + publicKey.getFormat() 
						+ "]\n{" + publicKey + "}");
			} else {
				Log.w(TAG, "Cannot load alias 'android' from keystore");
			}
		} catch (KeyStoreException e) {
			Log.w(TAG, "Cannot get instance keystore", e);
		} catch (NoSuchAlgorithmException e) {
			Log.w(TAG, "Unsupported keystore type '" + KEYSTORE_TYPE + "'", e);
		} catch (CertificateException e) {
			Log.w(TAG, "Uncorrect key password '" + KEY_PASS, e);
		} catch (UnrecoverableEntryException e) {
			Log.w(TAG, "Cannot load alias '" + KEY_NAME + "' from keystore", e);
		} catch (IOException e) {
			Log.w(TAG, "Cannot load keystore from res/raw", e);
		}
	}
	
	
	@SuppressLint("TrulyRandom") 
	public static void initialize() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableEntryException, IOException, URISyntaxException {
//		if (key == null) {
//			key = TokenUtil.getPrivateKey();
//		}
		if (keyPair == null) {
			keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		}
	}
	
	
	public static RSAPrivateKey getPrivate() {
		try {
			initialize();
		} catch (Exception e) {
			Log.w(TAG, "Cannot initialize user-token", e);
		}
		if (key != null) {
			return (RSAPrivateKey) key.getPrivateKey();
		}
		if (keyPair != null) {
			return (RSAPrivateKey) keyPair.getPrivate();
		}
		return null;
	}
	
	
	public static RSAPublicKey getPublic() {
		try {
			initialize();
		} catch (Exception e) {
			Log.w(TAG, "Cannot initialize user-token", e);
		}
		if (key != null) {
			return (RSAPublicKey) key.getCertificate().getPublicKey();
		}
		if (keyPair != null) {
			return (RSAPublicKey) keyPair.getPublic();
		}
		return null;
	}
}
