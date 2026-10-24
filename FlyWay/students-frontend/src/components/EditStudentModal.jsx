import { useState, useEffect } from 'react';

/**
 * Modal de modification des informations d'un étudiant.
 */
function EditStudentModal({ student, onSave, onCancel }) {
  const [form, setForm] = useState({ ...student });

  // Sync si l'étudiant sélectionné change
  useEffect(() => {
    setForm({ ...student });
  }, [student]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.firstName || !form.lastName || !form.email || !form.age || !form.filiere) {
      alert('Veuillez remplir tous les champs.');
      return;
    }
    onSave({ ...form, age: parseInt(form.age, 10) });
  };

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>✏️ Modifier l'étudiant</h2>
          <button className="modal-close" onClick={onCancel}>✕</button>
        </div>

        <form onSubmit={handleSubmit} className="student-form">
          <div className="form-row">
            <div className="form-group">
              <label>Prénom *</label>
              <input
                name="firstName"
                value={form.firstName}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label>Nom *</label>
              <input
                name="lastName"
                value={form.lastName}
                onChange={handleChange}
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
            <button type="submit" className="btn btn-warning">
              💾 Enregistrer
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default EditStudentModal;

