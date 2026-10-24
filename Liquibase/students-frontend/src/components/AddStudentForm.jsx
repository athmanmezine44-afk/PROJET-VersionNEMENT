import { useState } from 'react';

/**
 * Formulaire d'ajout d'un nouvel étudiant.
 */
function AddStudentForm({ onSubmit, onCancel }) {
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    age: '',
    filiere: '',
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.firstName || !form.lastName || !form.email || !form.age || !form.filiere) {
      alert('Veuillez remplir tous les champs.');
      return;
    }
    onSubmit({ ...form, age: parseInt(form.age, 10) });
  };

  return (
    <div className="card form-card">
      <h2>Ajouter un étudiant</h2>
      <form onSubmit={handleSubmit} className="student-form">
        <div className="form-row">
          <div className="form-group">
            <label>Prénom *</label>
            <input
              name="firstName"
              value={form.firstName}
              onChange={handleChange}
              placeholder="Alice"
              required
            />
          </div>
          <div className="form-group">
            <label>Nom *</label>
            <input
              name="lastName"
              value={form.lastName}
              onChange={handleChange}
              placeholder="Martin"
              required
            />
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Email *</label>
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="alice.martin@etud.fr"
              required
            />
          </div>
          <div className="form-group">
            <label>Âge *</label>
            <input
              type="number"
              name="age"
              value={form.age}
              onChange={handleChange}
              placeholder="21"
              min="16"
              max="99"
              required
            />
          </div>
        </div>
        <div className="form-group">
          <label>Filière *</label>
          <select name="filiere" value={form.filiere} onChange={handleChange} required>
            <option value="">-- Choisir une filière --</option>
            <option value="Informatique">Informatique</option>
            <option value="Mathématiques">Mathématiques</option>
            <option value="Physique">Physique</option>
            <option value="Chimie">Chimie</option>
            <option value="Biologie">Biologie</option>
            <option value="Économie">Économie</option>
          </select>
        </div>
        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={onCancel}>
            Annuler
          </button>
          <button type="submit" className="btn btn-success">
            ✓ Enregistrer
          </button>
        </div>
      </form>
    </div>
  );
}

export default AddStudentForm;

