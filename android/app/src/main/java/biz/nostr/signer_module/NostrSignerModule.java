package biz.nostr.signer_module;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import biz.nostr.android.nip55.Signer;
import biz.nostr.android.nip55.IntentBuilder;
import biz.nostr.android.nip55.AppInfo;

public class NostrSignerModule extends ReactContextBaseJavaModule implements ActivityEventListener {

	private static final int REQUEST_GET_PUBLIC_KEY = 1001;
	private static final int REQUEST_SIGN_EVENT = 1002;
	private static final int REQUEST_NIP04_ENCRYPT = 1003;
	private static final int REQUEST_NIP04_DECRYPT = 1004;
	private static final int REQUEST_NIP44_ENCRYPT = 1005;
	private static final int REQUEST_NIP44_DECRYPT = 1006;
	private Promise pendingPromise;
	private int pendingRequestCode;

	private ReactApplicationContext reactContext;

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

	@ReactMethod
	public void getPublicKey(String packageName, Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		}
		if (packageName == null || packageName.isEmpty()) {
			promise.reject("ERROR", "Signer package name not set. Call setPackageName first.", null);
			return;
		}
		String publicKey = Signer.getPublicKey(context, packageName);
		if (publicKey != null) {
			WritableMap map = Arguments.createMap();
			map.putString("npub", publicKey);
			map.putString("package", packageName);
			promise.resolve(map);
		} else {
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
		} else if (packageName == null || packageName.isEmpty()) {
			promise.reject("ERROR", "Signer package name not set. Call setPackageName first.");
			return;
		}
		String[] signedEventJson = Signer.signEvent(context, packageName, eventJson, npub);
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
	public void nip04Encrypt(String packageName, String plainText, String id, String pubKey, String npub, Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		} else if (plainText == null || pubKey == null || npub == null) {
			promise.reject("ERROR", "Missing parameters");
			return;
		}
		String encryptedText = Signer.nip04Encrypt(context, packageName, plainText, pubKey, npub);
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
	public void nip04Decrypt(String packageName, String encryptedText, String id, String pubKey, String npub, Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		} else if (encryptedText == null || pubKey == null || npub == null) {
			promise.reject("ERROR", "Missing parameters");
			return;
		}
		String decryptedText = Signer.nip04Decrypt(context, packageName, encryptedText, pubKey, npub);
		if (decryptedText != null) {
			WritableMap map = Arguments.createMap();
			map.putString("result", decryptedText);
			map.putString("id", id);
			promise.resolve(map);
		} else {
			Intent intent = IntentBuilder.nip04EncryptIntent(packageName, encryptedText, id, npub, pubKey);
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
	public void nip44Encrypt(String packageName, String plainText, String id, String pubKey, String npub, Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		} else if (plainText == null || pubKey == null || npub == null) {
			promise.reject("ERROR", "Missing parameters");
			return;
		}
		String encryptedText = Signer.nip44Encrypt(context, packageName, plainText, pubKey, npub);
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
	public void nip44Decrypt(String packageName, String encryptedText, String id, String pubKey, String npub, Promise promise) {
		Activity currentActivity = getCurrentActivity();
		if (currentActivity == null) {
			promise.reject("NO_ACTIVITY", "Activity doesn't exist");
			return;
		} else if (encryptedText == null || pubKey == null || npub == null) {
			promise.reject("ERROR", "Missing parameters");
			return;
		}
		String decryptedText = Signer.nip44Decrypt(context, packageName, encryptedText, pubKey, npub);
		if (decryptedText != null) {
			WritableMap map = Arguments.createMap();
			map.putString("result", decryptedText);
			map.putString("id", id);
			promise.resolve(map);
		} else {
			Intent intent = IntentBuilder.nip44EncryptIntent(packageName, encryptedText, id, npub, pubKey);
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

	// Implement the ActivityEventListener methods
	@Override
	public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		if (pendingPromise == null || requestCode != pendingRequestCode) {
			return;
		}

		if (resultCode == Activity.RESULT_OK && data != null) {
			if (requestCode == REQUEST_GET_PUBLIC_KEY) {
				NostrResultParser.GetPublicKeyResult result = NostrResultParser.parseGetPublicKeyResult(data);
				if (result != null) {
					WritableMap map = Arguments.createMap();
					map.putString("npub", result.npub);
					map.putString("package", result.packageName);
					pendingPromise.resolve(map);
				} else {
					pendingPromise.reject("ERROR", "Failed to parse public key result");
				}
			} else if (requestCode == REQUEST_SIGN_EVENT) {
				NostrResultParser.SignEventResult result = NostrResultParser.parseSignEventResult(data);
				if (result != null) {
					WritableMap map = Arguments.createMap();
					map.putString("signature", result.signature);
					map.putString("id", result.id);
					map.putString("event", result.event);
					pendingPromise.resolve(map);
				} else {
					pendingPromise.reject("ERROR", "Failed to parse sign event result");
				}
			} else if (requestCode == REQUEST_NIP04_ENCRYPT) {
				
			}
		} else {
			pendingPromise.reject("ERROR", "Operation canceled or failed");
		}
		pendingPromise = null;
		pendingRequestCode = 0;
	}

	@Override
	public void onNewIntent(Intent intent) {
		// Not used
	}
}
