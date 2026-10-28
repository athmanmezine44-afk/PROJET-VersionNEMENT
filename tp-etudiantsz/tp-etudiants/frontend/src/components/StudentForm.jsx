import { useState } from "react";

const emptyForm = { nom: "", prenom: "", email: "", filiere: "", age: "", moyenne: "" };

function Field({ label, name, error, children }) {
  return (
    <label className={error ? "has-error" : ""}>
      {label}
      {children}
      {error && <span className="field-error">{error}</span>}
    </label>
  );
}

/** Formulaire d'ajout ET de modification (selon que `student` est fourni). */
export default function StudentForm({ student, saving, fieldErrors, onSubmit, onCancel }) {
  const [form, setForm] = useState(student ?? emptyForm);
  const editing = Boolean(student);

  function change(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  function submit(e) {
    e.preventDefault();
    onSubmit({ ...form, age: Number(form.age), moyenne: Number(form.moyenne) });
  }

  return (
    <section className="form-card">
      <div className="form-heading">
        <div>
          <h2>{editing ? `Modifier ${student.prenom} ${student.nom}` : "Nouvel étudiant"}</h2>
          <p>Remplissez les informations ci-dessous.</p>
        </div>
      </div>
      <form onSubmit={submit}>
        <div className="form-grid">
          <Field label="Nom" error={fieldErrors.nom}>
            <input name="nom" value={form.nom} onChange={change} required placeholder="Ex. Alaoui" />
          </Field>
          <Field label="Prénom" error={fieldErrors.prenom}>
            <input name="prenom" value={form.prenom} onChange={change} required placeholder="Ex. Yassine" />
          </Field>
          <Field label="Email" error={fieldErrors.email}>
            <input name="email" type="email" value={form.email} onChange={change} required placeholder="nom@etu.ma" />
          </Field>
          <Field label="Filière" error={fieldErrors.filiere}>
            <input name="filiere" value={form.filiere} onChange={change} required placeholder="Ex. Génie Informatique" />
          </Field>
          <Field label="Âge" error={fieldErrors.age}>
            <input name="age" type="number" min="16" max="100" value={form.age} onChange={change} required />
          </Field>
          <Field label="Moyenne" error={fieldErrors.moyenne}>
            <input name="moyenne" type="number" min="0" max="20" step="0.01" value={form.moyenne} onChange={change} required />
          </Field>
        </div>
        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={onCancel}>Annuler</button>
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? "Enregistrement…" : editing ? "Enregistrer les modifications" : "Enregistrer l'étudiant"}
          </button>
        </div>
      </form>
    </section>
  );
}
