// @ts-nocheck
import React, { useEffect, useState } from 'react';
import { ActivityIndicator, FlatList, Platform, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { Link } from 'expo-router';

const API_BASE = Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api';

export default function EmployeesScreen() {
  const [loading, setLoading] = useState(true);
  const [employees, setEmployees] = useState<any[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch(`${API_BASE}/employees`)
      .then((res) => {
        if (!res.ok) throw new Error('Failed to fetch');
        return res.json();
      })
      .then((data) => setEmployees(data))
      .catch((e) => setError(String(e)))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <ActivityIndicator style={{ flex: 1 }} />;

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Employees</Text>
      {error ? <Text style={styles.error}>{error}</Text> : null}
      <FlatList
        data={employees}
        keyExtractor={(item) => String(item.id)}
        renderItem={({ item }) => (
          <View style={styles.row}>
            <View style={{ flex: 1 }}>
              <Text style={styles.name}>{item.firstName} {item.lastName}</Text>
              <Text style={styles.small}>{item.email}</Text>
            </View>
            <View style={styles.actions}>
              <Link href={`/employees/${item.id}/timesheets`} asChild>
                <TouchableOpacity style={styles.btn}>
                  <Text style={styles.btnText}>Timesheets</Text>
                </TouchableOpacity>
              </Link>
            </View>
          </View>
        )}
      />
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
  btn: { backgroundColor: '#0a7ea4', paddingVertical: 8, paddingHorizontal: 10, borderRadius: 6 },
  btnText: { color: '#fff', fontWeight: '600' },
  error: { color: 'red', marginBottom: 8 },
});
