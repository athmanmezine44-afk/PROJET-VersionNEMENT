// Couche d'accès à l'API : tous les appels réseau passent par ici.
const BASE_URL = (import.meta.env.VITE_API_URL ?? "http://localhost:8080") + "/api/students";

export class ApiError extends Error {
  constructor(message, status = 0, fieldErrors = {}) {
    super(message);
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

async function request(path = "", options = {}) {
  let res;
  try {
    res = await fetch(BASE_URL + path, {
      headers: { "Content-Type": "application/json" },
      ...options,
    });
  } catch {
    throw new ApiError("Impossible de contacter l'API. Vérifiez que le backend est lancé.");
  }
  if (res.status === 204) return null;
  const body = await res.json().catch(() => null);
  if (!res.ok) {
    throw new ApiError(body?.message ?? `Erreur ${res.status}`, res.status, body?.fieldErrors ?? {});
  }
  return body;
}

export function listStudents({ q = "", filiere = "", sortBy = "id", order = "asc" } = {}) {
  const params = new URLSearchParams({ q, filiere, sortBy, order });
  return request("?" + params);
}

export const getStats = () => request("/stats");
export const getFilieres = () => request("/filieres");
export const createStudent = (data) => request("", { method: "POST", body: JSON.stringify(data) });
export const updateStudent = (id, data) =>
  request(`/${id}`, { method: "PUT", body: JSON.stringify(data) });
export const deleteStudent = (id) => request(`/${id}`, { method: "DELETE" });
