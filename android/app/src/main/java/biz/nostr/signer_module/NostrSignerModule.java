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

public class NostrSignerModule extends ReactContextBaseJavaModule implements ActivityEventListener {

	private static final int REQUEST_GET_PUBLIC_KEY = 1001;
	private static final int REQUEST_SIGN_EVENT = 1002;
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
		}

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
