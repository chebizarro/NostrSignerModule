package biz.nostr.signer_module;

import com.facebook.react.ReactActivity;
import android.os.Bundle; // Required for React Native Navigation

public class MainActivity extends ReactActivity {

    @Override
    protected String getMainComponentName() {
        return "NostrSignerModule";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
    }
}
