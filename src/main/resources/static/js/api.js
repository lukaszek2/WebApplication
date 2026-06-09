/* ===========================================================
   api.js — cienka warstwa nad fetch() z obsługą JWT
   =========================================================== */
const Auth = {
  get token() { return localStorage.getItem("edututor_token"); },
  get role()  { return localStorage.getItem("edututor_role"); },
  set(token, role) {
    localStorage.setItem("edututor_token", token);
    localStorage.setItem("edututor_role", role);
  },
  clear() {
    localStorage.removeItem("edututor_token");
    localStorage.removeItem("edututor_role");
  },
  get isLoggedIn() { return !!this.token; },
  is(role) { return this.role === role; },
  isTeacher() { return this.role === "TEACHER" || this.role === "ADMIN"; },
  isAdmin()   { return this.role === "ADMIN"; },
};

class ApiError extends Error {
  constructor(status, message) { super(message); this.status = status; }
}

async function apiRequest(method, path, body, isForm) {
  const headers = {};
  if (Auth.token) headers["Authorization"] = "Bearer " + Auth.token;
  let payload = undefined;
  if (body !== undefined && body !== null) {
    if (isForm) {
      payload = body; // FormData — niech przeglądarka ustawi boundary
    } else {
      headers["Content-Type"] = "application/json";
      payload = JSON.stringify(body);
    }
  }
  let res;
  try {
    res = await fetch("/api" + path, { method, headers, body: payload });
  } catch (e) {
    throw new ApiError(0, "Brak połączenia z serwerem");
  }
  if (res.status === 401) {
    // token wygasł / brak autoryzacji
    if (Auth.isLoggedIn) { Auth.clear(); location.hash = "#/login"; }
    throw new ApiError(401, "Sesja wygasła – zaloguj się ponownie");
  }
  if (res.status === 204) return null;
  const text = await res.text();
  let data = null;
  if (text) { try { data = JSON.parse(text); } catch { data = text; } }
  if (!res.ok) {
    const msg = (data && data.message) ? data.message : ("Błąd " + res.status);
    throw new ApiError(res.status, msg);
  }
  return data;
}

const api = {
  get:  (p)      => apiRequest("GET", p),
  post: (p, b)   => apiRequest("POST", p, b),
  put:  (p, b)   => apiRequest("PUT", p, b),
  del:  (p)      => apiRequest("DELETE", p),
  upload: (p, formData) => apiRequest("POST", p, formData, true),
};
