import { NativeModules } from 'react-native';

/**
 * Interface representing a Signer Application's information.
 */
interface SignerAppInfo {
  name: string;
  packageName: string;
  iconData: string;
  iconUrl: string;
}

/**
 * Type definition for the NostrSignerModule native module.
 */
type NostrSignerModuleType = {
  /**
   * Retrieves the public key associated with the signer.
   * @param packageName - The package name of the signer application.
   * @returns A promise resolving to an object containing the public key (`npub`) and package name.
   */
  getPublicKey(packageName: string): Promise<{ npub: string; package: string }>;

  /**
   * Signs an event with the provided event JSON, event ID, and public key.
   * @param packageName - The package name of the signer application.
   * @param eventJson - The JSON string of the event to sign.
   * @param eventId - The ID of the event.
   * @param npub - The public key (`npub`) of the signer.
   * @returns A promise resolving to an object containing the signature, event ID, and signed event JSON.
   */
  signEvent(
    packageName: string,
    eventJson: string,
    eventId: string,
    npub: string,
  ): Promise<{ signature: string; id: string; event: string }>;

  /**
   * Encrypts plaintext using NIP-04 protocol.
   * @param packageName - The package name of the signer application.
   * @param plainText - The plaintext message to encrypt.
   * @param id - An identifier for the encryption operation.
   * @param pubkey - The recipient's public key.
   * @param npub - The signer's public key (`npub`).
   * @returns A promise resolving to an object containing the encrypted result.
   */
  nip04Encrypt(
    packageName: string,
    plainText: string,
    id: string,
    pubkey: string,
    npub: string,
  ): Promise<{ result: string }>;

  /**
   * Decrypts encrypted text using NIP-04 protocol.
   * @param packageName - The package name of the signer application.
   * @param encryptedText - The encrypted text to decrypt.
   * @param id - An identifier for the decryption operation.
   * @param pubkey - The recipient's public key.
   * @param npub - The signer's public key (`npub`).
   * @returns A promise resolving to an object containing the decrypted result.
   */
  nip04Decrypt(
    packageName: string,
    encryptedText: string,
    id: string,
    pubkey: string,
    npub: string,
  ): Promise<{ result: string }>;

  /**
   * Encrypts plaintext using NIP-44 protocol.
   * @param packageName - The package name of the signer application.
   * @param plainText - The plaintext message to encrypt.
   * @param id - An identifier for the encryption operation.
   * @param pubkey - The recipient's public key.
   * @param npub - The signer's public key (`npub`).
   * @returns A promise resolving to an object containing the encrypted result.
   */
  nip44Encrypt(
    packageName: string,
    plainText: string,
    id: string,
    pubkey: string,
    npub: string,
  ): Promise<{ result: string }>;

  /**
   * Decrypts encrypted text using NIP-44 protocol.
   * @param packageName - The package name of the signer application.
   * @param encryptedText - The encrypted text to decrypt.
   * @param id - An identifier for the decryption operation.
   * @param pubkey - The recipient's public key.
   * @param npub - The signer's public key (`npub`).
   * @returns A promise resolving to an object containing the decrypted result.
   */
  nip44Decrypt(
    packageName: string,
    encryptedText: string,
    id: string,
    pubkey: string,
    npub: string,
  ): Promise<{ result: string }>;

  /**
   * Retrieves a list of installed signer applications.
   * @returns A promise resolving to an array of `SignerAppInfo` objects.
   */
  getInstalledSignerApps(): Promise<SignerAppInfo[]>;

  /**
   * Sets the default package name for the signer application.
   * @param packageName - The package name to set as default.
   * @returns A promise resolving when the package name is set.
   */
  setPackageName(packageName: string): Promise<void>;

  /**
   * Decrypts a Zap event.
   * @param packageName - The package name of the signer application.
   * @param eventJson - The JSON string of the Zap event to decrypt.
   * @param id - An identifier for the decryption operation.
   * @param npub - The signer's public key (`npub`).
   * @returns A promise resolving to an object containing the decrypted event.
   */
  decryptZapEvent(
    packageName: string,
    eventJson: string,
    id: string,
    npub: string,
  ): Promise<{ result: string }>;

  /**
   * Retrieves relay information.
   * @param packageName - The package name of the signer application.
   * @param id - An identifier for the relay retrieval operation.
   * @param npub - The signer's public key (`npub`).
   * @returns A promise resolving to an object containing relay information.
   */
  getRelays(
    packageName: string,
    id: string,
    npub: string,
  ): Promise<{ result: string }>;

  /**
   * Checks if an external signer application is installed.
   * @param packageName - The package name of the signer application to check.
   * @returns A promise resolving to an object indicating if the signer is installed.
   */
  isExternalSignerInstalled(
    packageName: string,
  ): Promise<{ installed: boolean }>;
};

const { NostrSignerModule } = NativeModules;

export default NostrSignerModule as NostrSignerModuleType;
