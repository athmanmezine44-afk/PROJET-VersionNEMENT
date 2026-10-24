import { useState, useEffect } from 'react';
import StudentTable from './components/StudentTable';
import AddStudentForm from './components/AddStudentForm';
import EditStudentModal from './components/EditStudentModal';
import './App.css';

const API_URL = 'http://localhost:8081/api/students';

function App() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [editingStudent, setEditingStudent] = useState(null); // étudiant en cours de modification

  const fetchStudents = async () => {
    try {
      setLoading(true);
      const response = await fetch(API_URL);
      if (!response.ok) throw new Error(`Erreur HTTP : ${response.status}`);
      const data = await response.json();
      setStudents(data);
      setError(null);
    } catch (err) {
      setError('Impossible de charger les étudiants. Vérifiez que le backend Spring Boot est démarré sur le port 8081.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStudents();
  }, []);

  const handleAddStudent = async (student) => {
    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(student),
      });
      if (!response.ok) throw new Error('Erreur lors de l\'ajout');
      await fetchStudents();
      setShowForm(false);
    } catch (err) {
      alert('Erreur lors de l\'ajout de l\'étudiant.');
      console.error(err);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Supprimer cet étudiant ?')) return;
    try {
      const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
      if (!response.ok) throw new Error('Erreur lors de la suppression');
      setStudents((prev) => prev.filter((s) => s.id !== id));
    } catch (err) {
      alert('Erreur lors de la suppression.');
      console.error(err);
    }
  };

  const handleEditSave = async (updated) => {
    try {
      const response = await fetch(`${API_URL}/${updated.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updated),
      });
      if (!response.ok) throw new Error('Erreur lors de la modification');
      const saved = await response.json();
      setStudents((prev) => prev.map((s) => (s.id === saved.id ? saved : s)));
      setEditingStudent(null);
    } catch (err) {
      alert('Erreur lors de la modification.');
      console.error(err);
    }
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <div className="header-content">
          <div>
            <h1>🎓 Gestion des Étudiants</h1>
            <p className="subtitle">Application Spring Boot + React.js</p>
          </div>
          <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>
            {showForm ? '✕ Annuler' : '+ Ajouter un étudiant'}
          </button>
        </div>
      </header>

      <main className="main-content">
        {showForm && (
          <AddStudentForm
            onSubmit={handleAddStudent}
            onCancel={() => setShowForm(false)}
          />
        )}

        <div className="card">
          <div className="card-header">
            <h2>Liste des étudiants</h2>
            <span className="badge">{students.length} étudiant{students.length !== 1 ? 's' : ''}</span>
          </div>

          {loading && (
            <div className="status-msg loading">
              <span className="spinner">⏳</span> Chargement…
            </div>
          )}
          {error && (
            <div className="status-msg error">
              ⚠️ {error}
            </div>
          )}
          {!loading && !error && (
            <StudentTable
              students={students}
              onDelete={handleDelete}
              onEdit={setEditingStudent}
            />
          )}
        </div>

        {editingStudent && (
          <EditStudentModal
            student={editingStudent}
            onSave={handleEditSave}
            onCancel={() => setEditingStudent(null)}
          />
        )}
      </main>
    </div>
  );
}

export default App;
