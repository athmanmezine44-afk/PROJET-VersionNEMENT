export default function StatsCards({ stats }) {
  if (!stats) return null;
  const items = [
    { label: "Étudiants", value: stats.total },
    { label: "Moyenne générale", value: `${stats.moyenneGenerale.toFixed(2)}/20` },
    { label: "Meilleure moyenne", value: `${stats.meilleureMoyenne.toFixed(2)}/20` },
    { label: "Taux de réussite", value: `${stats.tauxReussite.toFixed(0)} %` },
  ];
  return (
    <section className="stats" aria-label="Statistiques">
      {items.map((item) => (
        <div className="stat-card" key={item.label}>
          <span className="stat-label">{item.label}</span>
          <strong className="stat-value">{item.value}</strong>
        </div>
      ))}
    </section>
  );
}
