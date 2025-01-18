# React Native Nostr Signer Module

A React Native module that provides signing capabilities for Nostr applications, implementing [NIP-55](https://github.com/nostr-protocol/nips/blob/master/55.md). This module allows developers to securely and efficiently sign Nostr events and perform encryption/decryption operations via Android Signer apps.

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

The React Native Nostr Signer Module enables React Native applications to interact with [Nostr](https://nostr.com/) Signer apps using Android Intents and Content Providers. By adhering to [NIP-55](https://github.com/nostr-protocol/nips/blob/master/55.md), this module ensures secure and standardized signing of Nostr events and related operations, facilitating seamless integration with the Nostr network.

## Features

- **Sign Nostr Events**: Securely sign events to interact with the Nostr protocol.
- **Retrieve Public Keys**: Access the user's public key for identity verification.
- **Encryption & Decryption**: Perform NIP-04 and NIP-44 encryption and decryption operations.
- **Handle Zap Events**: Decrypt Zap events.
- **Retrieve Relay Information**: Get information about relays from the Signer app.
- **Retrieve Installed Signer Apps**: Get a list of installed signer applications with details.
- **Set Package Name**: Configure the default signer app package name for subsequent operations.
- **Check Signer Installation**: Verify if a specific external signer app is installed.
- **NIP-55 Compliance**: Fully implements the NIP-55 specification for application-level signing.

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

1. **Auto-linking (React Native 0.60+):**  
   The module should be linked automatically for React Native 0.60 or higher. If not, follow manual linking instructions for older versions.

2. **Update `MainApplication.java`:**  
   Ensure that the package is added to your React Native app. If auto-linking doesn't work, manually add the module package to your list of packages:

   ```java
   import biz.nostr.signer_module.NostrSignerModulePackage; 

   @Override
   protected List<ReactPackage> getPackages() {
       @SuppressWarnings("UnnecessaryLocalVariable")
       List<ReactPackage> packages = new PackageList(this).getPackages();
       packages.add(new NostrSignerModulePackage()); // Add the signer module package
       return packages;
   }
   ```

3. **AndroidManifest.xml:**  
   Ensure that any necessary permissions or intent filters required by the module are declared in your `AndroidManifest.xml`.

### iOS

NIP-55 is not currently supported on iOS.

## Usage

Import the module in your JavaScript/TypeScript code:

```typescript
import NostrSignerModule from 'react-native-nostr-signer-module';
```

### Setting Up the Signer

**Set the Default Package Name:**

```typescript
await NostrSignerModule.setPackageName('com.example.signerapp');
```

**Check if Signer is Installed:**

```typescript
const isInstalled = await NostrSignerModule.isExternalSignerInstalled('com.example.signerapp');
console.log('Signer Installed:', isInstalled.installed);
```

**Get Public Key:**

```typescript
try {
  const result = await NostrSignerModule.getPublicKey('com.example.signerapp');
  console.log('Public Key:', result.npub);
} catch (error) {
  console.error('Failed to get public key:', error);
}
```

**Sign Event:**

```typescript
try {
  const eventJson = '{"content": "Hello, Nostr!"}';
  const eventId = 'event123';
  const npub = 'npub1example...';

  const result = await NostrSignerModule.signEvent('com.example.signerapp', eventJson, eventId, npub);
  console.log('Signature:', result.signature);
} catch (error) {
  console.error('Failed to sign event:', error);
}
```

**Encryption & Decryption:**

```typescript
try {
  const plainText = "Hello, World!";
  const id = "unique_id";
  const pubKey = "recipientPublicKey";

  // NIP-04 Encrypt
  const encryptResult = await NostrSignerModule.nip04Encrypt('com.example.signerapp', plainText, id, pubKey, npub);
  console.log('Encrypted:', encryptResult.result);

  // NIP-04 Decrypt
  const decryptResult = await NostrSignerModule.nip04Decrypt('com.example.signerapp', encryptResult.result, id, pubKey, npub);
  console.log('Decrypted:', decryptResult.result);
} catch (error) {
  console.error('Encryption/Decryption failed:', error);
}
```

**Retrieve Installed Signer Apps:**

```typescript
try {
  const apps = await NostrSignerModule.getInstalledSignerApps();
  apps.forEach(app => {
    console.log(`Name: ${app.name}, Package: ${app.packageName}`);
  });
} catch (error) {
  console.error('Error fetching signer apps:', error);
}
```

**Decrypt Zap Event:**

```typescript
try {
  const eventJson = '{"content": "Zap event"}';
  const id = "zap123";
  const result = await NostrSignerModule.decryptZapEvent('com.example.signerapp', eventJson, id, npub);
  console.log('Decrypted Zap Event:', result.result);
} catch (error) {
  console.error('Failed to decrypt Zap event:', error);
}
```

**Get Relays:**

```typescript
try {
  const id = "relayRequest123";
  const result = await NostrSignerModule.getRelays('com.example.signerapp', id, npub);
  console.log('Relays:', result.result);
} catch (error) {
  console.error('Failed to get relays:', error);
}
```

## API Reference

### Methods

#### `setPackageName(packageName: string): Promise<void>`

Sets the package name of the external signer app.

#### `isExternalSignerInstalled(packageName: string): Promise<{ installed: boolean }>`

Checks if an external signer app is installed.

#### `getInstalledSignerApps(): Promise<SignerAppInfo[]>`

Retrieves a list of installed signer apps.

#### `getPublicKey(packageName: string): Promise<{ npub: string; package: string }>`

Retrieves the user's public key from the specified signer app.

#### `signEvent(packageName: string, eventJson: string, eventId: string, npub: string): Promise<{ signature: string; id: string; event: string }>`

Signs a Nostr event.

#### `nip04Encrypt(packageName: string, plainText: string, id: string, pubkey: string, npub: string): Promise<{ result: string }>`

Encrypts a message using NIP-04.

#### `nip04Decrypt(packageName: string, encryptedText: string, id: string, pubkey: string, npub: string): Promise<{ result: string }>`

Decrypts a message using NIP-04.

#### `nip44Encrypt(packageName: string, plainText: string, id: string, pubkey: string, npub: string): Promise<{ result: string }>`

Encrypts a message using NIP-44.

#### `nip44Decrypt(packageName: string, encryptedText: string, id: string, pubkey: string, npub: string): Promise<{ result: string }>`

Decrypts a message using NIP-44.

#### `decryptZapEvent(packageName: string, eventJson: string, id: string, npub: string): Promise<{ result: string }>`

Decrypts a Zap event.

#### `getRelays(packageName: string, id: string, npub: string): Promise<{ result: string }>`

Retrieves relay information.

### Types

#### `SignerAppInfo`

Represents information about an installed signer app.

```typescript
interface SignerAppInfo {
  name: string;
  packageName: string;
  iconData: string;
  iconUrl: string;
}
```

## NIP-55 Compliance

This module fully implements key aspects of the [NIP-55](https://github.com/nostr-protocol/nips/blob/master/55.md) specification, enabling secure and standardized signing of Nostr events via external signer apps.

## Contributing

Contributions are welcome! Please follow the standard [contributing guidelines](CONTRIBUTING.md) for this project.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

**Note**: For any issues or questions, please open an issue on the [GitHub repository](https://github.com/chebizarro/NostrSignerModule/issues).
