// @ts-nocheck
import React, { useEffect, useState } from 'react';
import { ActivityIndicator, Alert, FlatList, Platform, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { Link, useLocalSearchParams } from 'expo-router';

const API_BASE = Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api';

export default function EmployeeTimesheets() {
  const params = useLocalSearchParams();
  const employeeId = params.id as string;

  const [loading, setLoading] = useState(true);
  const [timesheets, setTimesheets] = useState<any[]>([]);
  const [error, setError] = useState<string | null>(null);

  const fetchTimesheets = () => {
    setLoading(true);
    fetch(`${API_BASE}/employees/${employeeId}/timesheets`)
      .then((res) => {
        if (!res.ok) throw new Error('Failed to fetch timesheets');
        return res.json();
      })
      .then((data) => setTimesheets(data))
      .catch((e) => setError(String(e)))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    if (!employeeId) return;
    fetchTimesheets();
  }, [employeeId]);

  const onDelete = (id: number) => {
    Alert.alert('Delete', 'Delete this timesheet?', [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Delete',
        style: 'destructive',
        onPress: () => {
          fetch(`${API_BASE}/timesheets/${id}`, { method: 'DELETE' })
            .then((res) => {
              if (res.status === 204) {
                fetchTimesheets();
              } else {
                throw new Error('Failed to delete');
              }
            })
            .catch((e) => Alert.alert('Error', String(e)));
        },
      },
    ]);
  };

  if (loading) return <ActivityIndicator style={{ flex: 1 }} />;

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Timesheets</Text>
      {error ? <Text style={styles.error}>{error}</Text> : null}

      <FlatList
        data={timesheets}
        keyExtractor={(item) => String(item.id)}
        renderItem={({ item }) => (
          <View style={styles.row}>
            <View style={{ flex: 1 }}>
              <Text style={styles.name}>{item.date} — {item.hours}h</Text>
              <Text style={styles.small}>{item.description}</Text>
            </View>
            <View style={styles.actions}>
              <TouchableOpacity style={styles.deleteBtn} onPress={() => onDelete(item.id)}>
                <Text style={styles.deleteText}>Delete</Text>
              </TouchableOpacity>
            </View>
          </View>
        )}
        ListEmptyComponent={<Text style={{ color: '#666', marginTop: 8 }}>No timesheets yet.</Text>}
      />

      <View style={styles.footer}>
        <Link href="/employees" asChild>
          <TouchableOpacity style={styles.backBtn}><Text style={styles.backText}>Back</Text></TouchableOpacity>
        </Link>
        <Link href={`/employees/${employeeId}/timesheets/new`} asChild>
          <TouchableOpacity style={styles.addBtn}><Text style={styles.addText}>Add Timesheet</Text></TouchableOpacity>
        </Link>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, backgroundColor: '#fff' },
  title: { fontSize: 22, fontWeight: '700', marginBottom: 12 },
  row: { flexDirection: 'row', paddingVertical: 12, borderBottomWidth: 1, borderColor: '#eee', alignItems: 'center' },
  name: { fontSize: 16, fontWeight: '600' },
  small: { color: '#555' },
  actions: { marginLeft: 8 },
  deleteBtn: { backgroundColor: '#ff4d4f', paddingVertical: 6, paddingHorizontal: 10, borderRadius: 6 },
  deleteText: { color: '#fff', fontWeight: '600' },
  footer: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 12 },
  addBtn: { backgroundColor: '#0a7ea4', paddingVertical: 10, paddingHorizontal: 14, borderRadius: 6 },
  addText: { color: '#fff', fontWeight: '600' },
  backBtn: { backgroundColor: '#ddd', paddingVertical: 10, paddingHorizontal: 14, borderRadius: 6 },
  backText: { color: '#333', fontWeight: '600' },
  error: { color: 'red', marginBottom: 8 },
});
