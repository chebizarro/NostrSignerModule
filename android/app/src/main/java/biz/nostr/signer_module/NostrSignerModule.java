package biz.nostr.signer_module;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biz.nostr.android.nip55.AppInfo;
import biz.nostr.android.nip55.IntentBuilder;
import biz.nostr.android.nip55.Signer;

/**
 * NostrSignerModule for React Native
 * Implements bridging between JS and native Android NIP55 functionality
 */
public class NostrSignerModule extends ReactContextBaseJavaModule implements ActivityEventListener {

	private static final int REQUEST_GET_PUBLIC_KEY = 1001;
	private static final int REQUEST_SIGN_EVENT = 1002;
	private static final int REQUEST_NIP04_ENCRYPT = 1003;
	private static final int REQUEST_NIP04_DECRYPT = 1004;
	private static final int REQUEST_NIP44_ENCRYPT = 1005;
	private static final int REQUEST_NIP44_DECRYPT = 1006;
	private static final int REQUEST_DECRYPT_ZAP_EVENT = 1007;
	private static final int REQUEST_GET_RELAYS = 1008;

	private Promise pendingPromise;
	private int pendingRequestCode;

	private final ReactApplicationContext reactContext;

	// Store a default or user-set package name for the signer
	private String signerPackageName = null;

	public NostrSignerModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
		reactContext.addActivityEventListener(this);
	}

	@NonNull
	@Override
	public String getName() {
		return "NostrSignerModule";
	}

	/**
	 * Returns the installed signer apps on the device.
	 */
	@ReactMethod
	public void getInstalledSignerApps(Promise promise) {
		List<AppInfo> signerAppInfos = Signer.getInstalledSignerApps(reactContext);
		List<Map<String, Object>> appsList = new ArrayList<>();

		for (AppInfo signerAppInfo : signerAppInfos) {
			Map<String, Object> appInfo = new HashMap<>();
			appInfo.put("name", signerAppInfo.name);
			appInfo.put("packageName", signerAppInfo.packageName);
			appInfo.put("iconData", signerAppInfo.iconData);
			appInfo.put("iconUrl", signerAppInfo.iconUrl);
			appsList.add(appInfo);
		}

		promise.resolve(appsList);
	}

	/**
	 * Sets the default package name for the signer application.
	 */
	@ReactMethod
	public void setPackageName(String packageName, Promise promise) {
		if (packageName == null || packageName.isEmpty()) {
			promise.reject("ERROR", "Missing or empty packageName parameter");
			return;
		}
		signerPackageName = packageName;
		promise.resolve(null);
	}

	/**
	 * Helper method to retrieve the effective package name from the call,
	 * falling back to signerPackageName if none is provided.
	 */
	private String getPackageNameFromCall(String paramPackageName) {
		if (paramPackageName == null || paramPackageName.isEmpty()) {
			return signerPackageName;
		}
		return paramPackageName;
	}

	@ReactMethod
	public void getPublicKey(String packageName, Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		}
		packageName = getPackageNameFromCall(packageName);
		if (packageName == null || packageName.isEmpty()) {
			promise.reject("ERROR", "Signer package name not set. Call setPackageName first.");
			return;
		}

		String publicKey = Signer.getPublicKey(reactContext, packageName);
		if (publicKey != null) {
			WritableMap map = Arguments.createMap();
			map.putString("npub", publicKey);
			map.putString("package", packageName);
			promise.resolve(map);
		} else {
			// Launch Intent
			Intent intent = IntentBuilder.getPublicKeyIntent(packageName, null);
			pendingPromise = promise;
			pendingRequestCode = REQUEST_GET_PUBLIC_KEY;
			try {
				currentActivity.startActivityForResult(intent, REQUEST_GET_PUBLIC_KEY);
			} catch (Exception e) {
				pendingPromise = null;
				promise.reject("ERROR", "Failed to start activity: " + e.getMessage());
			}
		}
	}

	@ReactMethod
	public void signEvent(String packageName, String eventJson, String eventId, String npub, Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		}
		packageName = getPackageNameFromCall(packageName);
		if (packageName == null || packageName.isEmpty()) {
			promise.reject("ERROR", "Signer package name not set. Call setPackageName first.");
			return;
		}

		String[] signedEventJson = Signer.signEvent(reactContext, packageName, eventJson, npub);
		if (signedEventJson != null) {
			WritableMap map = Arguments.createMap();
			map.putString("signature", signedEventJson[0]);
			map.putString("id", eventId);
			map.putString("event", signedEventJson[1]);
			promise.resolve(map);
		} else {
			Intent intent = IntentBuilder.signEventIntent(packageName, eventJson, eventId, npub);
			pendingPromise = promise;
			pendingRequestCode = REQUEST_SIGN_EVENT;
			try {
				currentActivity.startActivityForResult(intent, REQUEST_SIGN_EVENT);
			} catch (Exception e) {
				pendingPromise = null;
				promise.reject("ERROR", "Failed to start activity: " + e.getMessage());
			}
		}
	}

	@ReactMethod
	public void nip04Encrypt(String packageName, String plainText, String id, String pubKey, String npub,
			Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		}
		packageName = getPackageNameFromCall(packageName);
		if (plainText == null || pubKey == null || npub == null) {
			promise.reject("ERROR", "Missing parameters");
			return;
		}

		String encryptedText = Signer.nip04Encrypt(reactContext, packageName, plainText, pubKey, npub);
		if (encryptedText != null) {
			WritableMap map = Arguments.createMap();
			map.putString("result", encryptedText);
			map.putString("id", id);
			promise.resolve(map);
		} else {
			Intent intent = IntentBuilder.nip04EncryptIntent(packageName, plainText, id, npub, pubKey);
			pendingPromise = promise;
			pendingRequestCode = REQUEST_NIP04_ENCRYPT;
			try {
				currentActivity.startActivityForResult(intent, REQUEST_NIP04_ENCRYPT);
			} catch (Exception e) {
				pendingPromise = null;
				promise.reject("ERROR", "Failed to start activity: " + e.getMessage());
			}
		}
	}

	@ReactMethod
	public void nip04Decrypt(String packageName, String encryptedText, String id, String pubKey, String npub,
			Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		}
		packageName = getPackageNameFromCall(packageName);
		if (encryptedText == null || pubKey == null || npub == null) {
			promise.reject("ERROR", "Missing parameters");
			return;
		}

		String decryptedText = Signer.nip04Decrypt(reactContext, packageName, encryptedText, pubKey, npub);
		if (decryptedText != null) {
			WritableMap map = Arguments.createMap();
			map.putString("result", decryptedText);
			map.putString("id", id);
			promise.resolve(map);
		} else {
			Intent intent = IntentBuilder.nip04DecryptIntent(packageName, encryptedText, id, pubKey, npub);
			pendingPromise = promise;
			pendingRequestCode = REQUEST_NIP04_DECRYPT;
			try {
				currentActivity.startActivityForResult(intent, REQUEST_NIP04_DECRYPT);
			} catch (Exception e) {
				pendingPromise = null;
				promise.reject("ERROR", "Failed to start activity: " + e.getMessage());
			}
		}
	}

	@ReactMethod
	public void nip44Encrypt(String packageName, String plainText, String id, String pubKey, String npub,
			Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		}
		packageName = getPackageNameFromCall(packageName);
		if (plainText == null || pubKey == null || npub == null) {
			promise.reject("ERROR", "Missing parameters");
			return;
		}

		String encryptedText = Signer.nip44Encrypt(reactContext, packageName, plainText, pubKey, npub);
		if (encryptedText != null) {
			WritableMap map = Arguments.createMap();
			map.putString("result", encryptedText);
			map.putString("id", id);
			promise.resolve(map);
		} else {
			Intent intent = IntentBuilder.nip44EncryptIntent(packageName, plainText, id, npub, pubKey);
			pendingPromise = promise;
			pendingRequestCode = REQUEST_NIP44_ENCRYPT;
			try {
				currentActivity.startActivityForResult(intent, REQUEST_NIP44_ENCRYPT);
			} catch (Exception e) {
				pendingPromise = null;
				promise.reject("ERROR", "Failed to start activity: " + e.getMessage());
			}
		}
	}

	@ReactMethod
	public void nip44Decrypt(String packageName, String encryptedText, String id, String pubKey, String npub,
			Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		}
		packageName = getPackageNameFromCall(packageName);
		if (encryptedText == null || pubKey == null || npub == null) {
			promise.reject("ERROR", "Missing parameters");
			return;
		}

		String decryptedText = Signer.nip44Decrypt(reactContext, packageName, encryptedText, pubKey, npub);
		if (decryptedText != null) {
			WritableMap map = Arguments.createMap();
			map.putString("result", decryptedText);
			map.putString("id", id);
			promise.resolve(map);
		} else {
			Intent intent = IntentBuilder.nip44DecryptIntent(packageName, encryptedText, id, npub, pubKey);
			pendingPromise = promise;
			pendingRequestCode = REQUEST_NIP44_DECRYPT;
			try {
				currentActivity.startActivityForResult(intent, REQUEST_NIP44_DECRYPT);
			} catch (Exception e) {
				pendingPromise = null;
				promise.reject("ERROR", "Failed to start activity: " + e.getMessage());
			}
		}
	}

	@ReactMethod
	public void decryptZapEvent(String packageName, String eventJson, String id, String npub, Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		}
		packageName = getPackageNameFromCall(packageName);
		if (eventJson == null || npub == null) {
			promise.reject("ERROR", "Missing parameters");
			return;
		}

		String decryptedEventJson = Signer.decryptZapEvent(reactContext, packageName, eventJson, npub);
		if (decryptedEventJson != null) {
			WritableMap map = Arguments.createMap();
			map.putString("result", decryptedEventJson);
			map.putString("id", id);
			promise.resolve(map);
		} else {
			Intent intent = IntentBuilder.decryptZapEventIntent(packageName, eventJson, id, npub);
			pendingPromise = promise;
			pendingRequestCode = REQUEST_DECRYPT_ZAP_EVENT;
			try {
				currentActivity.startActivityForResult(intent, REQUEST_DECRYPT_ZAP_EVENT);
			} catch (Exception e) {
				pendingPromise = null;
				promise.reject("ERROR", "Failed to start activity: " + e.getMessage());
			}
		}
	}

	@ReactMethod
	public void getRelays(String packageName, String id, String npub, Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		}
		packageName = getPackageNameFromCall(packageName);
		if (npub == null) {
			promise.reject("ERROR", "Missing parameters");
			return;
		}

		String relayJson = Signer.getRelays(reactContext, packageName, npub);
		if (relayJson != null) {
			WritableMap map = Arguments.createMap();
			map.putString("result", relayJson);
			map.putString("id", id);
			promise.resolve(map);
		} else {
			Intent intent = IntentBuilder.getRelaysIntent(packageName, id, npub);
			pendingPromise = promise;
			pendingRequestCode = REQUEST_GET_RELAYS;
			try {
				currentActivity.startActivityForResult(intent, REQUEST_GET_RELAYS);
			} catch (Exception e) {
				pendingPromise = null;
				promise.reject("ERROR", "Failed to start activity: " + e.getMessage());
			}
		}
	}

	@Override
	public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		if (pendingPromise == null || requestCode != pendingRequestCode) {
			return;
		}

		if (resultCode == Activity.RESULT_OK && data != null) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				// Create a map to pass back to JS
				WritableMap map = Arguments.createMap();
				for (String key : extras.keySet()) {
					Object value = extras.get(key);
					if (value instanceof String) {
						map.putString(key, (String) value);
					} else if (value instanceof Boolean) {
						map.putBoolean(key, (Boolean) value);
					} else if (value instanceof Integer) {
						map.putInt(key, (Integer) value);
					} else if (value instanceof Double) {
						map.putDouble(key, (Double) value);
					} else if (value != null) {
						// For other data types, handle as needed
						map.putString(key, value.toString());
					} else {
						map.putNull(key);
					}
				}
				pendingPromise.resolve(map);
			} else {
				pendingPromise.reject("ERROR", "No extras returned from activity");
			}
		} else {
			pendingPromise.reject("ERROR", "Operation canceled or failed");
		}

		// Reset
		pendingPromise = null;
		pendingRequestCode = 0;
	}

	@Override
	public void onNewIntent(Intent intent) {
		// Not used
	}
}
