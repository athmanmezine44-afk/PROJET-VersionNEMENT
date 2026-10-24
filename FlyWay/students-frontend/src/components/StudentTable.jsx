/**
 * Tableau stylé affichant la liste des étudiants.
 */
function StudentTable({ students, onDelete, onEdit }) {
  if (students.length === 0) {
    return <p className="empty-msg">Aucun étudiant enregistré.</p>;
  }

  return (
    <div className="table-wrapper">
      <table className="student-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Prénom</th>
            <th>Nom</th>
            <th>Email</th>
            <th>Âge</th>
            <th>Filière</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {students.map((student, index) => (
            <tr key={student.id} className={index % 2 === 0 ? 'row-even' : 'row-odd'}>
              <td className="id-cell">{student.id}</td>
              <td>{student.firstName}</td>
              <td><strong>{student.lastName}</strong></td>
              <td>
                <a href={`mailto:${student.email}`} className="email-link">
                  {student.email}
                </a>
              </td>
              <td className="age-cell">{student.age} ans</td>
              <td>
                <span className="filiere-badge">{student.filiere}</span>
              </td>
              <td>
                <div className="action-btns">
                  <button
                    className="btn btn-warning btn-sm"
                    onClick={() => onEdit(student)}
                    title="Modifier"
                  >
                    ✏️
                  </button>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => onDelete(student.id)}
                    title="Supprimer"
                  >
                    🗑️
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default StudentTable;

