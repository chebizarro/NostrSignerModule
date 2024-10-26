# React Native Nostr Signer Module

A React Native module that provides signing capabilities for Nostr applications, implementing [NIP-55](https://github.com/nostr-protocol/nips/blob/master/55.md). This module allows developers to sign Nostr events through Android Signer apps securely and efficiently.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Setup](#setup)
- [Usage](#usage)
- [API Reference](#api-reference)
- [Example](#example)
- [NIP-55 Compliance](#nip-55-compliance)
- [Contributing](#contributing)
- [License](#license)

## Introduction

The React Native Nostr Signer Module enables React Native applications to interact with [Nostr](https://nostr.com/) Signer apps using Intents and the Content Manager. By adhering to [NIP-55](https://github.com/nostr-protocol/nips/blob/master/55.md), this module ensures secure and standardized signing of Nostr events, facilitating seamless integration with the Nostr network.

## Features

- **Sign Nostr Events**: Securely sign events to interact with the Nostr protocol.
- **Retrieve Public Keys**: Access the user's public key for identity verification.
- **NIP-55 Compliance**: Fully implements the NIP-55 specification for application-level signing.
- **Cross-Platform Support**: Currently supports Android; iOS support is planned for future releases.

## Installation

Using npm:

```bash
npm install react-native-nostr-signer-module
```

Or using Yarn:

```bash
yarn add react-native-nostr-signer-module
```

## Setup

### Android

1. **Add the Module to Your Project**

   If you're using React Native version 0.60 or higher, the module should be linked automatically. For older versions, you'll need to link the module manually.

2. **Update `MainApplication.java`**

   Add the module package to your list of packages:

   ```java
   import com.nostrsignermodule.NostrSignerPackage; // Import the module package

   public class MainApplication extends Application implements ReactApplication {
       // ...

       @Override
       protected List<ReactPackage> getPackages() {
           @SuppressWarnings("UnnecessaryLocalVariable")
           List<ReactPackage> packages = new PackageList(this).getPackages();
           // Add the NostrSignerPackage
           packages.add(new NostrSignerPackage());
           return packages;
       }

       // ...
   }
   ```

3. **Update `AndroidManifest.xml`**

   Ensure that the package name and permissions are correctly set in your `AndroidManifest.xml` if required by the module.

### iOS

iOS support is currently under development and will be available in future releases.

## Usage

Import the module in your JavaScript or TypeScript code:

```javascript
import NostrSignerModule from 'react-native-nostr-signer-module';
```

### Initialize the Module

You can set the package name of the external signer app if required:

```javascript
NostrSignerModule.setPackageName('com.example.signerapp');
```

### Check if External Signer is Installed

```javascript
const isInstalled = await NostrSignerModule.isExternalSignerInstalled('com.example.signerapp');
if (isInstalled) {
  console.log('Signer app is installed');
} else {
  console.log('Signer app is not installed');
}
```

### Get Public Key

Retrieve the user's public key:

```javascript
try {
  const result = await NostrSignerModule.getPublicKey();
  const publicKey = result.npub;
  console.log('Public Key:', publicKey);
} catch (error) {
  console.error('Failed to get public key:', error);
}
```

### Sign Event

Sign a Nostr event represented as a JSON string:

```javascript
try {
  const eventJson = '{"content": "Hello, Nostr!"}';
  const eventId = 'event123';
  const npub = 'npub1examplepublickey';

  const result = await NostrSignerModule.signEvent(eventJson, eventId, npub);
  const signature = result.signature;
  console.log('Signature:', signature);
} catch (error) {
  console.error('Failed to sign event:', error);
}
```

## API Reference

### Methods

#### `setPackageName(packageName: string): Promise<void>`

Sets the package name of the external signer app.

**Parameters:**

- `packageName` (`string`): The package name of the signer app.

**Returns:**

- `Promise<void>`: Resolves when the package name is set.

#### `isExternalSignerInstalled(packageName: string): Promise<boolean>`

Checks if an external signer app is installed.

**Parameters:**

- `packageName` (`string`): The package name of the signer app.

**Returns:**

- `Promise<boolean>`: `true` if the signer app is installed, `false` otherwise.

#### `getPublicKey(permissions?: string): Promise<{ npub: string; package: string }>`

Retrieves the user's public key.

**Parameters:**

- `permissions` (`string`, optional): Permissions requested from the signer app.

**Returns:**

- `Promise<object>`: An object containing the public key and package name.

#### `signEvent(eventJson: string, eventId: string, npub: string): Promise<{ signature: string; id: string; event: string }>`

Signs a Nostr event.

**Parameters:**

- `eventJson` (`string`): A JSON string representing the event to be signed.
- `eventId` (`string`): The ID of the event.
- `npub` (`string`): The public key of the user.

**Returns:**

- `Promise<object>`: An object containing the signature, event ID, and signed event JSON.

#### `getInstalledSignerApps(): Promise<SignerAppInfo[]>`

Retrieves a list of installed signer apps.

**Returns:**

- `Promise<SignerAppInfo[]>`: An array of `SignerAppInfo` objects.

### Types

#### `SignerAppInfo`

Represents information about an installed signer app.

```typescript
interface SignerAppInfo {
  name: string;
  packageName: string;
  iconData: string; // Base64-encoded icon data
  iconUrl?: string;
}
```

## Example

Below is a complete example demonstrating how to use the module:

```javascript
import React, { useEffect, useState } from 'react';
import { View, Text, Button, Image, FlatList } from 'react-native';
import NostrSignerModule from 'react-native-nostr-signer-module';

const App = () => {
  const [publicKey, setPublicKey] = useState('Unknown');
  const [signature, setSignature] = useState('Unknown');
  const [signerApps, setSignerApps] = useState([]);

  useEffect(() => {
    initSigner();
  }, []);

  const initSigner = async () => {
    try {
      // Set package name if needed
      await NostrSignerModule.setPackageName('com.example.signerapp');

      // Check if signer is installed
      const isInstalled = await NostrSignerModule.isExternalSignerInstalled('com.example.signerapp');
      if (isInstalled) {
        // Get public key
        const pubKeyResult = await NostrSignerModule.getPublicKey();
        setPublicKey(pubKeyResult.npub);

        // Sign event
        const signResult = await NostrSignerModule.signEvent('{"content":"Hello Nostr"}', 'event123', pubKeyResult.npub);
        setSignature(signResult.signature);
      } else {
        setPublicKey('Signer app not installed');
        setSignature('Cannot sign event');
      }

      // Get installed signer apps
      const apps = await NostrSignerModule.getInstalledSignerApps();
      setSignerApps(apps);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const renderSignerApp = ({ item }) => (
    <View style={{ flexDirection: 'row', alignItems: 'center', margin: 8 }}>
      {item.iconData ? (
        <Image
          source={{ uri: `data:image/png;base64,${item.iconData}` }}
          style={{ width: 40, height: 40, marginRight: 8 }}
        />
      ) : null}
      <View>
        <Text>{item.name}</Text>
        <Text>{item.packageName}</Text>
      </View>
    </View>
  );

  return (
    <View style={{ padding: 16 }}>
      <Text>Public Key: {publicKey}</Text>
      <Text>Signature: {signature}</Text>

      <FlatList
        data={signerApps}
        keyExtractor={(item) => item.packageName}
        renderItem={renderSignerApp}
      />

      <Button title="Refresh" onPress={initSigner} />
    </View>
  );
};

export default App;
```

## NIP-55 Compliance

This module implements the [NIP-55](https://github.com/nostr-protocol/nips/blob/master/55.md) specification, which defines the protocol for application-level signing of Nostr events. By adhering to NIP-55, the module ensures secure and standardized interactions with the Nostr network, promoting interoperability between different Nostr clients and services.

## Contributing

Contributions are welcome! If you'd like to contribute to this project, please follow these steps:

1. **Fork the Repository**: Click the 'Fork' button at the top right of the repository page.

2. **Clone Your Fork**:

   ```bash
   git clone https://github.com/chebizarro/react-native-nostr-signer.git
   cd react-native-nostr-signer
   ```

3. **Create a New Branch**:

   ```bash
   git checkout -b feature/your-feature-name
   ```

4. **Make Your Changes**: Implement your feature or bug fix.

5. **Commit Your Changes**:

   ```bash
   git commit -am 'Add some feature'
   ```

6. **Push to the Branch**:

   ```bash
   git push origin feature/your-feature-name
   ```

7. **Open a Pull Request**: Go to the repository on GitHub and click 'New pull request'.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

**Note**: For any issues or questions, please open an issue on the [GitHub repository](https://github.com/chebizarro/react-native-nostr-signer/issues).

## Additional Information

### Dependencies

- **React Native**: ^0.70.0 or higher
- **Android SDK**: API Level 21 or higher

### Supported Platforms

- **Android**: Supported
- **iOS**: Coming soon

### Permissions

Ensure that any required permissions are declared in your application's `AndroidManifest.xml` file.

### Testing

To test the module, you can run the example app provided in the repository:

```bash
cd example
npm install
npm run android
```

### Feedback

We appreciate feedback and contributions. If you encounter any issues or have suggestions for improvements, please open an issue on the GitHub repository.

---

Thank you for using the React Native Nostr Signer Module!