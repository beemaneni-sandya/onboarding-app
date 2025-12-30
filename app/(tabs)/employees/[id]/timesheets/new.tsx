// @ts-nocheck
import React, { useState } from 'react';
import { Alert, Platform, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { useLocalSearchParams, useRouter } from 'expo-router';

const API_BASE = Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api';

export default function NewTimesheet() {
  const params = useLocalSearchParams();
  const employeeId = params.id as string;
  const router = useRouter();

  const [date, setDate] = useState('');
  const [hours, setHours] = useState('8');
  const [description, setDescription] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const onSubmit = () => {
    if (!employeeId) return Alert.alert('Error', 'Missing employee id');
    if (!hours || isNaN(Number(hours))) return Alert.alert('Validation', 'Please enter valid hours');

    setSubmitting(true);
    const paramsObj: Record<string, string> = { hours: String(Number(hours)), description };
    if (date) paramsObj.date = date;

    const formBody = new URLSearchParams(paramsObj).toString();

    fetch(`${API_BASE}/employees/${employeeId}/timesheets`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: formBody,
    })
      .then((res) => {
        if (!res.ok) throw new Error('Failed to save');
        return res.json();
      })
      .then(() => {
        router.back();
      })
      .catch((e) => Alert.alert('Error', String(e)))
      .finally(() => setSubmitting(false));
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Add Timesheet</Text>

      <Text style={styles.label}>Date (YYYY-MM-DD)</Text>
      <TextInput style={styles.input} value={date} onChangeText={setDate} placeholder="2025-10-24" />

      <Text style={styles.label}>Hours</Text>
      <TextInput style={styles.input} value={hours} onChangeText={setHours} keyboardType="numeric" />

      <Text style={styles.label}>Description</Text>
      <TextInput style={[styles.input, { height: 80 }]} value={description} onChangeText={setDescription} multiline />

      <View style={styles.buttons}>
        <TouchableOpacity style={styles.cancel} onPress={() => router.back()} disabled={submitting}>
          <Text style={styles.cancelText}>Cancel</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.save} onPress={onSubmit} disabled={submitting}>
          <Text style={styles.saveText}>{submitting ? 'Saving...' : 'Save'}</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, backgroundColor: '#fff' },
  title: { fontSize: 22, fontWeight: '700', marginBottom: 12 },
  label: { fontSize: 14, marginTop: 8, marginBottom: 4 },
  input: { borderWidth: 1, borderColor: '#ddd', padding: 8, borderRadius: 6 },
  buttons: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 16 },
  cancel: { backgroundColor: '#ddd', paddingVertical: 10, paddingHorizontal: 14, borderRadius: 6 },
  cancelText: { color: '#333', fontWeight: '600' },
  save: { backgroundColor: '#0a7ea4', paddingVertical: 10, paddingHorizontal: 14, borderRadius: 6 },
  saveText: { color: '#fff', fontWeight: '600' },
});
