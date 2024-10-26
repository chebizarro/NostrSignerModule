import { NativeModules } from 'react-native';

type NostrSignerModuleType = {
  getPublicKey(packageName: string): Promise<{ npub: string }>;
  signEvent(
    packageName: string,
    eventJson: string,
    eventId: string,
    npub: string
  ): Promise<{ signature: string }>;
};

const { NostrSignerModule } = NativeModules;

export default NostrSignerModule as NostrSignerModuleType;
