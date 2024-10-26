/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, {useState} from 'react';
import {Alert, Button, SafeAreaView, StyleSheet, Text} from 'react-native';

import NostrSignerModule from './NostrSignerModule';

function App(): React.JSX.Element {
  const [publicKey, setPublicKey] = useState<string>('');
  const [signature, setSignature] = useState<string>('');

  const packageName = 'com.example.signerapp'; // Replace with actual signer app package name

  const getPublicKey = async () => {
    try {
      const result = await NostrSignerModule.getPublicKey(packageName);
      setPublicKey(result.npub);
      Alert.alert('Public Key', `npub: ${result.npub}`);
    } catch (error) {
      console.error(error);
      Alert.alert('Error', (error as Error).message || 'Failed to get public key');
    }
  };

  const signEvent = async () => {
    try {
      const eventJson = '{"content": "Hello Nostr"}';
      const eventId = 'event123';
      const npub = publicKey; // Use the retrieved public key

      if (!npub) {
        Alert.alert('Error', 'Please get the public key first.');
        return;
      }

      const result = await NostrSignerModule.signEvent(
        packageName,
        eventJson,
        eventId,
        npub,
      );
      setSignature(result.signature);
      Alert.alert('Sign Event', `Signature: ${result.signature}`);
    } catch (error) {
      console.error(error);
      Alert.alert('Error', (error as Error).message || 'Failed to sign event');
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <Button title="Get Public Key" onPress={getPublicKey} />
      <Text style={styles.text}>Public Key: {publicKey}</Text>
      <Button title="Sign Event" onPress={signEvent} />
      <Text style={styles.text}>Signature: {signature}</Text>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
  },
  text: {
    marginVertical: 16,
  },
});

export default App;
