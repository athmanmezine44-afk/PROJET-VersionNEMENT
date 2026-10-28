import { useCallback, useEffect, useState } from "react";
import "./App.css";
import * as api from "./api";
import StatsCards from "./components/StatsCards";
import StudentForm from "./components/StudentForm";

const SORT_OPTIONS = [
  { value: "id:asc", label: "Ordre d'ajout" },
  { value: "nom:asc", label: "Nom (A → Z)" },
  { value: "nom:desc", label: "Nom (Z → A)" },
  { value: "moyenne:desc", label: "Moyenne (décroissante)" },
  { value: "moyenne:asc", label: "Moyenne (croissante)" },
  { value: "age:asc", label: "Âge (croissant)" },
];

export default function App() {
  const [students, setStudents] = useState([]);
  const [stats, setStats] = useState(null);
  const [filieres, setFilieres] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState("");

  // Filtres envoyés au backend
  const [search, setSearch] = useState("");
  const [q, setQ] = useState(""); // valeur « débouncée » de la recherche
  const [filiere, setFiliere] = useState("");
  const [sort, setSort] = useState("id:asc");

  // Formulaire
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});

  const [message, setMessage] = useState("");
  const [actionError, setActionError] = useState("");

  // Attend 300 ms après la dernière frappe avant d'interroger l'API
  useEffect(() => {
    const t = setTimeout(() => setQ(search), 300);
    return () => clearTimeout(t);
  }, [search]);

  const loadStudents = useCallback(async () => {
    const [sortBy, order] = sort.split(":");
    try {
      setStudents(await api.listStudents({ q, filiere, sortBy, order }));
      setLoadError("");
    } catch (e) {
      setLoadError(e.message);
    } finally {
      setLoading(false);
    }
  }, [q, filiere, sort]);

  const loadSummary = useCallback(async () => {
    try {
      const [s, f] = await Promise.all([api.getStats(), api.getFilieres()]);
      setStats(s);
      setFilieres(f);
      // Si la filière sélectionnée n'existe plus (dernier étudiant supprimé), on retire le filtre
      setFiliere((current) => (current && !f.includes(current) ? "" : current));
    } catch {
      /* l'erreur est déjà affichée par loadStudents */
    }
  }, []);

  useEffect(() => { loadStudents(); }, [loadStudents]);
  useEffect(() => { loadSummary(); }, [loadSummary]);

  // Les messages de succès disparaissent seuls
  useEffect(() => {
    if (!message) return;
    const t = setTimeout(() => setMessage(""), 4000);
    return () => clearTimeout(t);
  }, [message]);

  function openForm(student = null) {
    setEditing(student);
    setFieldErrors({});
    setActionError("");
    setFormOpen(true);
  }

  function closeForm() {
    setFormOpen(false);
    setEditing(null);
    setFieldErrors({});
  }

  async function saveStudent(data) {
    setSaving(true);
    setActionError("");
    setFieldErrors({});
    try {
      if (editing) await api.updateStudent(editing.id, data);
      else await api.createStudent(data);
      setMessage(editing ? "Étudiant modifié avec succès." : "Étudiant ajouté avec succès.");
      closeForm();
      await Promise.all([loadStudents(), loadSummary()]);
    } catch (e) {
      setFieldErrors(e.fieldErrors ?? {});
      setActionError(Object.keys(e.fieldErrors ?? {}).length ? "" : e.message);
    } finally {
      setSaving(false);
    }
  }

  async function removeStudent(student) {
    if (!window.confirm(`Voulez-vous vraiment supprimer ${student.prenom} ${student.nom} ?`)) return;
    setMessage("");
    setActionError("");
    try {
      await api.deleteStudent(student.id);
      setMessage("Étudiant supprimé avec succès.");
      await Promise.all([loadStudents(), loadSummary()]);
    } catch (e) {
      setActionError("Suppression impossible : " + e.message);
    }
  }

  const filtersActive = q !== "" || filiere !== "";

  return (
    <main className="container">
      <header className="page-header">
        <div>
          <p className="eyebrow">ESPACE ADMINISTRATION</p>
          <h1>Gestion des étudiants</h1>
          <p className="subtitle">Consultez et gérez la liste des étudiants.</p>
        </div>
        <button className="btn btn-primary" onClick={() => (formOpen ? closeForm() : openForm())}>
          <span className="btn-icon">{formOpen ? "×" : "+"}</span>
          {formOpen ? "Fermer" : "Ajouter un étudiant"}
        </button>
      </header>

      <StatsCards stats={stats} />

      {formOpen && (
        <StudentForm
          key={editing?.id ?? "new"}
          student={editing}
          saving={saving}
          fieldErrors={fieldErrors}
          onSubmit={saveStudent}
          onCancel={closeForm}
        />
      )}

      {message && <div className="notice success">{message}</div>}
      {actionError && <div className="notice error">{actionError}</div>}
      {loadError && (
        <div className="notice error">
          {loadError} <button className="link-btn" onClick={loadStudents}>Réessayer</button>
        </div>
      )}
      {loading && <p className="info">Chargement des étudiants…</p>}

      {!loading && !loadError && (
        <section className="card">
          <div className="table-top">
            <div><h2>Liste des étudiants</h2><p>Les étudiants enregistrés dans le système</p></div>
            <span className="total">{students.length} étudiant{students.length !== 1 ? "s" : ""}</span>
          </div>

          <div className="toolbar">
            <input
              type="search"
              className="toolbar-search"
              placeholder="Rechercher par nom, prénom ou email…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              aria-label="Rechercher"
            />
            <select value={filiere} onChange={(e) => setFiliere(e.target.value)} aria-label="Filtrer par filière">
              <option value="">Toutes les filières</option>
              {filieres.map((f) => <option key={f} value={f}>{f}</option>)}
            </select>
            <select value={sort} onChange={(e) => setSort(e.target.value)} aria-label="Trier">
              {SORT_OPTIONS.map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
            </select>
          </div>

          <div className="table-scroll">
            <table>
              <thead><tr><th>ID</th><th>Nom complet</th><th>Email</th><th>Filière</th><th>Âge</th><th>Moyenne</th><th>Actions</th></tr></thead>
              <tbody>
                {students.map((s) => (
                  <tr key={s.id}>
                    <td className="id-cell">#{s.id}</td>
                    <td><div className="student-name">{s.prenom} {s.nom}</div></td>
                    <td>{s.email}</td>
                    <td><span className="badge">{s.filiere}</span></td>
                    <td>{s.age} ans</td>
                    <td><span className={`grade ${s.moyenne >= 10 ? "grade-good" : "grade-low"}`}>{Number(s.moyenne).toFixed(2)}/20</span></td>
                    <td className="actions-cell">
                      <button className="btn-edit" onClick={() => openForm(s)} aria-label={`Modifier ${s.prenom} ${s.nom}`} title="Modifier">✎ Modifier</button>
                      <button className="btn-delete" onClick={() => removeStudent(s)} aria-label={`Supprimer ${s.prenom} ${s.nom}`} title="Supprimer">🗑 Supprimer</button>
                    </td>
                  </tr>
                ))}
                {students.length === 0 && (
                  <tr><td colSpan="7" className="empty">
                    {filtersActive ? "Aucun étudiant ne correspond à votre recherche." : "Aucun étudiant enregistré. Cliquez sur « Ajouter un étudiant »."}
                  </td></tr>
                )}
              </tbody>
            </table>
          </div>
          <footer className="card-footer">Gestion des étudiants <span>•</span> {students.length} résultat(s)</footer>
        </section>
      )}
    </main>
  );
}
