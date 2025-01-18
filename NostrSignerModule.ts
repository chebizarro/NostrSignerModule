import {NativeModules} from 'react-native';

type NostrSignerModuleType = {
  getPublicKey(packageName: string): Promise<{npub: string}>;
  signEvent(
    packageName: string,
    eventJson: string,
    eventId: string,
    npub: string,
  ): Promise<{signature: string}>;
  nip04Encrypt(
    packageName: string,
    plainText: string,
    id: string,
    pubkey: string,
    npub: string,
  ): Promise<{result: string}>;
  nip04Decrypt(
    packageName: string,
    encryptedText: string,
    id: string,
    pubkey: string,
    npub: string,
  ): Promise<{result: string}>;
  nip44Encrypt(
    packageName: string,
    plainText: string,
    id: string,
    pubkey: string,
    npub: string,
  ): Promise<{result: string}>;
  nip44Decrypt(
    packageName: string,
    encryptedText: string,
    id: string,
    pubkey: string,
    npub: string,
  ): Promise<{result: string}>;
};

const {NostrSignerModule} = NativeModules;

export default NostrSignerModule as NostrSignerModuleType;
