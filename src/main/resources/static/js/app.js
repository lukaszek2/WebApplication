/* ===========================================================
   app.js — router SPA + wszystkie widoki EduTutor
   =========================================================== */
const appEl = document.getElementById("app");
const navbar = document.getElementById("navbar");

/* ---------- Helpers ---------- */
const esc = (s) => String(s == null ? "" : s)
  .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
  .replace(/"/g, "&quot;");
const $ = (sel, root = document) => root.querySelector(sel);
const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));

function toast(msg, type = "") {
  const c = document.getElementById("toast-container");
  const t = document.createElement("div");
  t.className = "toast " + type;
  t.textContent = msg;
  c.appendChild(t);
  setTimeout(() => { t.style.opacity = "0"; setTimeout(() => t.remove(), 300); }, 3200);
}
const ok  = (m) => toast(m, "ok");
const err = (m) => toast(m, "err");

function spinner() { appEl.innerHTML = '<div class="spinner"></div>'; }
function fmtDate(s) { if (!s) return ""; const d = new Date(s); return isNaN(d) ? "" : d.toLocaleDateString("pl-PL"); }
function roleBadge(role) {
  const map = { STUDENT: "blue", TEACHER: "green", ADMIN: "amber" };
  return `<span class="badge ${map[role] || "gray"}">${esc(role)}</span>`;
}
const RES_ICON = { FILE: "📄", VIDEO: "▶", LINK: "🔗", NOTE: "📝" };

/* ---------- Modal ---------- */
function modal(title, bodyHtml, onMount) {
  const root = document.getElementById("modal-root");
  root.innerHTML = `
    <div class="modal-bg" id="modalBg">
      <div class="modal">
        <h2>${esc(title)}</h2>
        <div id="modalBody">${bodyHtml}</div>
      </div>
    </div>`;
  $("#modalBg").addEventListener("mousedown", (e) => { if (e.target.id === "modalBg") closeModal(); });
  if (onMount) onMount($("#modalBody"));
}
function closeModal() { document.getElementById("modal-root").innerHTML = ""; }

/* ---------- Navbar ---------- */
function renderNav() {
  if (!Auth.isLoggedIn) { navbar.style.display = "none"; return; }
  navbar.style.display = "block";
  const links = [["#/catalog", "Katalog"]];
  links.push(["#/dashboard", "Panel"], ["#/my-courses", "Moje kursy"]);
  if (Auth.isTeacher()) {
    links.push(["#/teacher", "Nauczyciel"], ["#/teacher/courses", "Zarządzaj kursami"],
               ["#/teacher/categories", "Kategorie"]);
  }
  if (Auth.isAdmin()) links.push(["#/admin/users", "Użytkownicy"], ["#/admin/stats", "Statystyki"], ["#/admin/logs", "Logi"]);
  const hash = location.hash || "#/catalog";
  $("#navLinks").innerHTML = links.map(([h, t]) =>
    `<a href="${h}" class="${hash.startsWith(h) ? "active" : ""}">${t}</a>`).join("");
  $("#navUser").innerHTML = `
    ${roleBadge(Auth.role)}
    <a href="#/profile" title="Profil">👤</a>
    <button class="btn ghost sm" id="logoutBtn">Wyloguj</button>`;
  $("#logoutBtn").onclick = () => { Auth.clear(); location.hash = "#/login"; };
  navbar.classList.remove("open");
}

/* ===========================================================
   ROUTER
   =========================================================== */
const routes = [
  [/^#\/login$/,                     viewLogin,            { public: true }],
  [/^#\/register$/,                  viewRegister,         { public: true }],
  [/^#\/catalog$/,                   viewCatalog],
  [/^#\/courses\/(\d+)$/,            viewCourseDetail],
  [/^#\/dashboard$/,                 viewDashboard],
  [/^#\/my-courses$/,                viewMyCourses],
  [/^#\/profile$/,                   viewProfile],
  [/^#\/teacher$/,                   viewTeacherDashboard, { role: "TEACHER" }],
  [/^#\/teacher\/courses$/,          viewTeacherCourses,   { role: "TEACHER" }],
  [/^#\/teacher\/courses\/(\d+)$/,   viewCourseEditor,     { role: "TEACHER" }],
  [/^#\/teacher\/courses\/(\d+)\/students$/, viewCourseStudents, { role: "TEACHER" }],
  [/^#\/teacher\/categories$/,       viewTeacherCategories,{ role: "TEACHER" }],
  [/^#\/admin\/users$/,              viewAdminUsers,       { role: "ADMIN" }],
  [/^#\/admin\/stats$/,              viewAdminStats,       { role: "ADMIN" }],
  [/^#\/admin\/logs$/,               viewAdminLogs,        { role: "ADMIN" }],
];

async function router() {
  const hash = location.hash || "#/catalog";
  let match = null, view = null, opts = {};
  for (const [re, fn, o] of routes) {
    const m = hash.match(re);
    if (m) { match = m; view = fn; opts = o || {}; break; }
  }
  if (!view) { view = viewCatalog; match = []; }

  // Strażnicy dostępu
  if (!opts.public && !Auth.isLoggedIn) { location.hash = "#/login"; return; }
  if (opts.role === "TEACHER" && !Auth.isTeacher()) { err("Brak uprawnień (wymagana rola nauczyciela)"); location.hash = "#/dashboard"; return; }
  if (opts.role === "ADMIN" && !Auth.isAdmin()) { err("Brak uprawnień (wymagana rola administratora)"); location.hash = "#/dashboard"; return; }

  renderNav();
  spinner();
  try {
    await view(...match.slice(1));
  } catch (e) {
    appEl.innerHTML = `<div class="container"><div class="empty">
      <div class="icon">⚠️</div><h2>Coś poszło nie tak</h2>
      <p class="muted">${esc(e.message || "Nieznany błąd")}</p></div></div>`;
  }
}
window.addEventListener("hashchange", router);
window.addEventListener("load", () => {
  if (!location.hash) location.hash = Auth.isLoggedIn ? "#/dashboard" : "#/login";
  else router();
});

/* ===========================================================
   AUTH VIEWS
   =========================================================== */
async function viewLogin() {
  appEl.innerHTML = `
  <div class="auth-wrap"><div class="card auth-card">
    <div class="brand"><span class="logo">ET</span> EduTutor</div>
    <p class="muted" style="text-align:center;margin-top:0">System zarządzania nauczaniem</p>
    <div class="field"><label>Email</label><input id="email" type="email" placeholder="ty@przyklad.pl" autocomplete="username"></div>
    <div class="field"><label>Hasło</label><input id="password" type="password" autocomplete="current-password"></div>
    <button class="btn" id="loginBtn" style="width:100%">Zaloguj się</button>
    <div class="auth-switch">Nie masz konta? <a href="#/register">Zarejestruj się</a></div>
  </div></div>`;
  const submit = async () => {
    const email = $("#email").value.trim(), password = $("#password").value;
    if (!email || !password) return err("Podaj email i hasło");
    const btn = $("#loginBtn"); btn.disabled = true; btn.textContent = "Logowanie…";
    try {
      const r = await api.post("/auth/login", { email, password });
      Auth.set(r.token, r.role);
      ok("Zalogowano");
      location.hash = "#/dashboard";
    } catch (e) { err(e.message); btn.disabled = false; btn.textContent = "Zaloguj się"; }
  };
  $("#loginBtn").onclick = submit;
  $("#password").addEventListener("keydown", (e) => { if (e.key === "Enter") submit(); });
}

async function viewRegister() {
  appEl.innerHTML = `
  <div class="auth-wrap"><div class="card auth-card">
    <div class="brand"><span class="logo">ET</span> EduTutor</div>
    <p class="muted" style="text-align:center;margin-top:0">Rejestracja konta studenta</p>
    <div class="field"><label>Imię i nazwisko</label><input id="name" type="text"></div>
    <div class="field"><label>Email</label><input id="email" type="email"></div>
    <div class="field"><label>Hasło (min. 6 znaków)</label><input id="password" type="password"></div>
    <button class="btn" id="regBtn" style="width:100%">Utwórz konto</button>
    <div class="auth-switch">Masz już konto? <a href="#/login">Zaloguj się</a></div>
  </div></div>`;
  $("#regBtn").onclick = async () => {
    const name = $("#name").value.trim(), email = $("#email").value.trim(), password = $("#password").value;
    if (!name || !email || password.length < 6) return err("Wypełnij pola; hasło min. 6 znaków");
    const btn = $("#regBtn"); btn.disabled = true; btn.textContent = "Tworzenie…";
    try {
      await api.post("/auth/register", { name, email, password });
      ok("Konto utworzone — zaloguj się");
      location.hash = "#/login";
    } catch (e) { err(e.message); btn.disabled = false; btn.textContent = "Utwórz konto"; }
  };
}

/* ===========================================================
   PUBLIC CATALOG
   =========================================================== */
function courseCard(c) {
  const init = (c.title || "?").charAt(0).toUpperCase();
  return `<div class="card hover course-card" data-id="${c.id}">
    <div class="cover">${esc(init)}</div>
    <h3>${esc(c.title)}</h3>
    <div class="desc">${esc(c.description || "Brak opisu")}</div>
    <div class="course-meta">
      ${c.categoryName ? `<span class="badge gray">${esc(c.categoryName)}</span>` : ""}
      <span>👤 ${esc(c.teacherName || "")}</span>
    </div>
  </div>`;
}
async function viewCatalog() {
  const [courses, cats] = await Promise.all([api.get("/courses"), api.get("/categories")]);
  appEl.innerHTML = `<div class="container">
    <div class="page-head"><div><h1>Katalog kursów</h1>
      <p class="muted">Opublikowane kursy dostępne w systemie</p></div></div>
    ${cats.length ? `<div class="row" style="margin-bottom:18px">
      <span class="badge blue" data-cat="" style="cursor:pointer">Wszystkie</span>
      ${cats.map(c => `<span class="badge gray" data-cat="${c.id}" style="cursor:pointer">${esc(c.name)}</span>`).join("")}
    </div>` : ""}
    <div class="grid cols-2" id="courseGrid">
      ${courses.length ? courses.map(courseCard).join("")
        : `<div class="empty"><div class="icon">📭</div><p>Brak opublikowanych kursów</p></div>`}
    </div></div>`;
  const grid = $("#courseGrid");
  const all = courses;
  $$("#courseGrid .course-card").forEach(card =>
    card.onclick = () => location.hash = "#/courses/" + card.dataset.id);
  $$("[data-cat]").forEach(b => b.onclick = () => {
    $$("[data-cat]").forEach(x => x.className = "badge gray"); b.className = "badge blue";
    const id = b.dataset.cat;
    const filtered = id ? all.filter(c => String(c.categoryId) === id) : all;
    grid.innerHTML = filtered.length ? filtered.map(courseCard).join("")
      : `<div class="empty"><p>Brak kursów w tej kategorii</p></div>`;
    $$("#courseGrid .course-card").forEach(card =>
      card.onclick = () => location.hash = "#/courses/" + card.dataset.id);
  });
}
/* ===========================================================
   STUDENT — dashboard, course detail, my courses, profile
   =========================================================== */
async function viewDashboard() {
  const d = await api.get("/dashboard");
  let teacherTiles = "";
  if (Auth.isTeacher()) {
    try {
      const t = await api.get("/teacher/dashboard");
      teacherTiles = `
        <div class="stat"><div class="value">${t.totalCourses ?? 0}</div><div class="label">Twoje kursy (nauczyciel)</div></div>
        <div class="stat"><div class="value">${t.totalStudents ?? 0}</div><div class="label">Zapisani studenci</div></div>`;
    } catch {}
  }
  appEl.innerHTML = `<div class="container">
    <div class="page-head"><div><h1>Witaj, ${esc(d.name)}!</h1>
      <p class="muted">Twój panel ${roleBadge(d.role)}</p></div></div>
    <div class="grid cols-4">
      <div class="stat"><div class="value">${d.enrolledCourses ?? 0}</div><div class="label">Kursy, na które jesteś zapisany</div></div>
      ${teacherTiles}
    </div>
    <h2>Szybki dostęp</h2>
    <div class="btn-row">
      <a class="btn" href="#/catalog">Przeglądaj katalog</a>
      <a class="btn ghost" href="#/my-courses">Moje kursy</a>
      ${Auth.isTeacher() ? '<a class="btn ghost" href="#/teacher">Panel nauczyciela</a>' : ""}
      ${Auth.isAdmin() ? '<a class="btn ghost" href="#/admin/stats">Statystyki systemu</a>' : ""}
    </div></div>`;
}

async function viewMyCourses() {
  const courses = await api.get("/my-courses");
  appEl.innerHTML = `<div class="container">
    <div class="page-head"><h1>Moje kursy</h1></div>
    <div class="grid cols-2" id="grid">
      ${courses.length ? courses.map(courseCard).join("")
        : `<div class="empty"><div class="icon">🎓</div><p>Nie jesteś jeszcze zapisany na żaden kurs.</p>
           <a class="btn" href="#/catalog">Przeglądaj katalog</a></div>`}
    </div></div>`;
  $$("#grid .course-card").forEach(card => card.onclick = () => location.hash = "#/courses/" + card.dataset.id);
}

async function viewCourseDetail(id) {
  const course = await api.get("/courses/" + id);
  // postęp studenta liczymy lokalnie na podstawie oznaczeń w tej sesji
  const sections = course.sections || [];
  const totalRes = sections.reduce((n, s) => n + (s.resources ? s.resources.length : 0), 0);
  appEl.innerHTML = `<div class="container">
    <a href="#/catalog" class="muted">← Katalog</a>
    <div class="page-head" style="margin-top:8px"><div>
      <h1>${esc(course.title)}</h1>
      <p class="muted">${course.categoryName ? esc(course.categoryName) + " · " : ""}Prowadzący: ${esc(course.teacherName)}
        · ${course.status === "PUBLISHED" ? '<span class="badge green">Opublikowany</span>' : '<span class="badge gray">Szkic</span>'}</p>
    </div></div>
    <div class="card"><p>${esc(course.description || "Brak opisu.")}</p>
      <p class="muted" style="margin:0">${sections.length} sekcji · ${totalRes} zasobów</p></div>
    <h2>Materiały</h2>
    <div id="sections">
      ${sections.length ? sections.map(sectionBlock).join("")
        : `<div class="empty"><p>Ten kurs nie ma jeszcze materiałów.</p></div>`}
    </div></div>`;

  $$(".resource-item").forEach(item => {
    const btn = $(".complete-btn", item);
    if (btn) btn.onclick = async (e) => {
      e.stopPropagation();
      try {
        await api.post("/progress/" + item.dataset.rid + "/complete");
        item.classList.add("done");
        btn.outerHTML = '<span class="badge green">✓ Ukończono</span>';
        ok("Oznaczono jako ukończone");
      } catch (er) { err(er.message); }
    };
    const open = $(".open-btn", item);
    if (open) open.onclick = (e) => { e.stopPropagation(); openResource(item.dataset.rdata); };
  });
}

function sectionBlock(s) {
  const resources = (s.resources || []).slice().sort((a, b) => (a.orderIndex || 0) - (b.orderIndex || 0));
  return `<div class="section-block">
    <div class="section-head"><span>${esc(s.title)}</span><span class="muted">${resources.length} zasobów</span></div>
    ${resources.length ? resources.map(resourceRow).join("")
      : `<div class="resource-item"><span class="muted">Brak zasobów w tej sekcji</span></div>`}
  </div>`;
}
function resourceRow(r) {
  const data = encodeURIComponent(JSON.stringify(r));
  return `<div class="resource-item" data-rid="${r.id}" data-rdata="${data}">
    <div class="ricon ${esc(r.type)}">${RES_ICON[r.type] || "•"}</div>
    <div class="rtitle">${esc(r.title)} <span class="muted" style="font-weight:400">· ${esc(r.type)}</span></div>
    ${(r.type === "LINK" || r.type === "VIDEO" || r.type === "NOTE" || r.type === "FILE")
      ? '<button class="btn ghost sm open-btn">Otwórz</button>' : ""}
    ${Auth.is("STUDENT") || Auth.isAdmin()
      ? '<button class="btn sm complete-btn">Oznacz ukończone</button>' : ""}
  </div>`;
}
function openResource(encoded) {
  const r = JSON.parse(decodeURIComponent(encoded));
  let body = "";
  if (r.type === "NOTE") body = `<div class="card" style="white-space:pre-wrap">${esc(r.content || "")}</div>`;
  else if (r.type === "LINK" || r.type === "VIDEO") body = `<p><a href="${esc(r.url)}" target="_blank" rel="noopener">${esc(r.url)}</a></p>`;
  else if (r.type === "FILE") {
    const fileName = (r.filePath || "").replace(/^.*[\\/]/, "");
    const url = "/" + (r.filePath || "").replace(/^\.\//, "");
    body = `<p><a class="btn" href="${esc(url)}" target="_blank" download="${esc(fileName)}">⬇ Pobierz plik</a></p>
      <p class="muted" style="font-size:.85rem">${esc(fileName)}</p>`;
  }
  modal(r.title, body + `<div class="modal-actions"><button class="btn ghost" onclick="closeModal()">Zamknij</button></div>`);
}

async function viewProfile() {
  const p = await api.get("/profile");
  appEl.innerHTML = `<div class="container" style="max-width:560px">
    <div class="page-head"><h1>Profil</h1></div>
    <div class="card">
      <div class="field"><label>Imię i nazwisko</label><input id="pname" value="${esc(p.name)}"></div>
      <div class="field"><label>Email</label><input value="${esc(p.email)}" disabled></div>
      <div class="field"><label>Rola</label><div>${roleBadge(p.role)}</div></div>
      <div class="field"><label>Status konta</label><div>${p.isActive
        ? '<span class="badge green">Aktywne</span>' : '<span class="badge red">Zawieszone</span>'}</div></div>
      <button class="btn" id="saveProfile">Zapisz zmiany</button>
    </div></div>`;
  $("#saveProfile").onclick = async () => {
    const name = $("#pname").value.trim();
    if (!name) return err("Imię nie może być puste");
    try { await api.put("/profile", { name }); ok("Zapisano profil"); renderNav(); }
    catch (e) { err(e.message); }
  };
}

/* ===========================================================
   TEACHER — dashboard z wykresami (Chart.js)
   =========================================================== */
let _charts = [];
function destroyCharts() { _charts.forEach(c => c.destroy()); _charts = []; }

async function viewTeacherDashboard() {
  destroyCharts();
  const [dash, analytics] = await Promise.all([
    api.get("/teacher/dashboard"), api.get("/teacher/analytics")]);
  appEl.innerHTML = `<div class="container">
    <div class="page-head"><div><h1>Panel nauczyciela</h1>
      <p class="muted">Statystyki i analityka Twoich kursów</p></div>
      <a class="btn" href="#/teacher/courses">Zarządzaj kursami</a></div>
    <div class="grid cols-4">
      <div class="stat"><div class="value">${dash.totalCourses ?? 0}</div><div class="label">Kursy</div></div>
      <div class="stat"><div class="value">${dash.totalStudents ?? 0}</div><div class="label">Studenci</div></div>
    </div>
    <div class="grid cols-2" style="margin-top:18px">
      <div class="card"><h3>Zapisy wg kursu</h3><div class="chart-box"><canvas id="chEnroll"></canvas></div></div>
      <div class="card"><h3>Aktywność (30 dni)</h3><div class="chart-box"><canvas id="chTimeline"></canvas></div></div>
      <div class="card" style="grid-column:1/-1"><h3>Wskaźnik ukończenia kursów (%)</h3>
        <div class="chart-box"><canvas id="chCompletion"></canvas></div></div>
    </div></div>`;

  const C = { primary: "#3a5bbf", accent: "#11a6a6", grid: "#e3e8f0" };

  const enr = analytics.enrollmentChart || [];
  _charts.push(new Chart($("#chEnroll"), {
    type: "bar",
    data: { labels: enr.map(e => e.title),
      datasets: [{ label: "Zapisy", data: enr.map(e => e.enrollments), backgroundColor: C.primary, borderRadius: 6 }] },
    options: chartOpts(C, true) }));

  const tl = analytics.activityTimeline || {};
  const tlKeys = Object.keys(tl).sort();
  _charts.push(new Chart($("#chTimeline"), {
    type: "line",
    data: { labels: tlKeys.map(k => k.slice(5)),
      datasets: [{ label: "Ukończone zasoby", data: tlKeys.map(k => tl[k]),
        borderColor: C.accent, backgroundColor: "rgba(17,166,166,.15)", fill: true, tension: .3 }] },
    options: chartOpts(C, false) }));

  const cmp = analytics.completionRates || [];
  _charts.push(new Chart($("#chCompletion"), {
    type: "bar",
    data: { labels: cmp.map(c => c.title),
      datasets: [{ label: "Ukończenie %", data: cmp.map(c => c.completionRate),
        backgroundColor: C.accent, borderRadius: 6 }] },
    options: { ...chartOpts(C, true), scales: { y: { beginAtZero: true, max: 100, grid: { color: C.grid } }, x: { grid: { display: false } } } } }));

  if (!enr.length && !tlKeys.length && !cmp.length) {
    $("#chEnroll").parentElement.parentElement.parentElement.insertAdjacentHTML("afterbegin",
      '<p class="muted">Brak danych — utwórz kursy i zapisz studentów, aby zobaczyć analitykę.</p>');
  }
}
function chartOpts(C, bar) {
  return { responsive: true, maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true, grid: { color: C.grid }, ticks: { precision: 0 } },
              x: { grid: { display: false } } } };
}

/* ===========================================================
   TEACHER — zarządzanie kursami
   =========================================================== */
async function viewTeacherCourses() {
  const [courses, cats] = await Promise.all([api.get("/teacher/courses"), api.get("/teacher/categories")]);
  appEl.innerHTML = `<div class="container">
    <div class="page-head"><h1>Zarządzaj kursami</h1>
      <button class="btn" id="newCourse">+ Nowy kurs</button></div>
    ${courses.length ? `<table class="tbl"><thead><tr>
        <th>Tytuł</th><th>Kategoria</th><th>Status</th><th>Utworzono</th><th></th></tr></thead><tbody>
        ${courses.map(c => `<tr>
          <td><strong>${esc(c.title)}</strong></td>
          <td>${esc(c.categoryName || "—")}</td>
          <td>${c.status === "PUBLISHED" ? '<span class="badge green">Opublikowany</span>' : '<span class="badge gray">Szkic</span>'}</td>
          <td class="muted">${fmtDate(c.createdAt)}</td>
          <td><div class="btn-row">
            <a class="btn ghost sm" href="#/teacher/courses/${c.id}">Edytuj</a>
            <a class="btn ghost sm" href="#/teacher/courses/${c.id}/students">Studenci</a>
            ${c.status !== "PUBLISHED" ? `<button class="btn success sm pub" data-id="${c.id}">Publikuj</button>` : ""}
            <button class="btn danger sm del" data-id="${c.id}">Usuń</button>
          </div></td></tr>`).join("")}
      </tbody></table>`
      : `<div class="empty"><div class="icon">📚</div><p>Nie masz jeszcze kursów.</p></div>`}
    </div>`;
  $("#newCourse").onclick = () => courseFormModal(null, cats, () => router());
  $$(".pub").forEach(b => b.onclick = async () => {
    try { await api.post(`/teacher/courses/${b.dataset.id}/publish`); ok("Kurs opublikowany"); router(); }
    catch (e) { err(e.message); } });
  $$(".del").forEach(b => b.onclick = () => confirmModal("Usunąć ten kurs wraz z sekcjami i zasobami?", async () => {
    try { await api.del(`/teacher/courses/${b.dataset.id}`); ok("Usunięto kurs"); router(); }
    catch (e) { err(e.message); } }));
}

function courseFormModal(course, cats, done) {
  const isEdit = !!course;
  modal(isEdit ? "Edytuj kurs" : "Nowy kurs", `
    <div class="field"><label>Tytuł *</label><input id="ctitle" value="${esc(course?.title || "")}"></div>
    <div class="field"><label>Opis</label><textarea id="cdesc">${esc(course?.description || "")}</textarea></div>
    <div class="field"><label>Kategoria</label><select id="ccat">
      <option value="">— brak —</option>
      ${cats.map(c => `<option value="${c.id}" ${course?.categoryId === c.id ? "selected" : ""}>${esc(c.name)}</option>`).join("")}
    </select></div>
    <div class="modal-actions">
      <button class="btn ghost" onclick="closeModal()">Anuluj</button>
      <button class="btn" id="saveCourse">Zapisz</button></div>`,
    () => {
      $("#saveCourse").onclick = async () => {
        const body = { title: $("#ctitle").value.trim(), description: $("#cdesc").value,
          categoryId: $("#ccat").value ? Number($("#ccat").value) : null };
        if (!body.title) return err("Tytuł jest wymagany");
        try {
          if (isEdit) await api.put("/teacher/courses/" + course.id, body);
          else await api.post("/teacher/courses", body);
          ok("Zapisano"); closeModal(); done();
        } catch (e) { err(e.message); }
      };
    });
}

/* ===========================================================
   TEACHER — edytor kursu (sekcje + zasoby)
   =========================================================== */
async function viewCourseEditor(id) {
  const [course, cats] = await Promise.all([api.get("/teacher/courses/" + id), api.get("/teacher/categories")]);
  const sections = (course.sections || []).slice().sort((a, b) => (a.orderIndex || 0) - (b.orderIndex || 0));
  appEl.innerHTML = `<div class="container">
    <a href="#/teacher/courses" class="muted">← Kursy</a>
    <div class="page-head" style="margin-top:8px"><div>
      <h1>${esc(course.title)}</h1>
      <p class="muted">${course.status === "PUBLISHED" ? '<span class="badge green">Opublikowany</span>' : '<span class="badge gray">Szkic</span>'}</p>
    </div><div class="btn-row">
      <button class="btn ghost" id="editCourse">Edytuj kurs</button>
      <button class="btn" id="addSection">+ Sekcja</button></div></div>
    <div id="sectionsWrap">
      ${sections.length ? sections.map(teacherSectionBlock).join("")
        : `<div class="empty"><p>Brak sekcji. Dodaj pierwszą sekcję, aby zacząć.</p></div>`}
    </div></div>`;

  $("#editCourse").onclick = () => courseFormModal(course, cats, () => router());
  $("#addSection").onclick = () => sectionFormModal(id, null, () => router());

  $$(".sec-edit").forEach(b => b.onclick = () => sectionFormModal(id,
    { id: b.dataset.sid, title: b.dataset.title, orderIndex: b.dataset.order }, () => router()));
  $$(".sec-del").forEach(b => b.onclick = () => confirmModal("Usunąć sekcję i jej zasoby?", async () => {
    try { await api.del(`/teacher/courses/${id}/sections/${b.dataset.sid}`); ok("Usunięto sekcję"); router(); }
    catch (e) { err(e.message); } }));
  $$(".add-res").forEach(b => b.onclick = () => resourceFormModal(b.dataset.sid, null, () => router()));
  $$(".res-edit").forEach(b => b.onclick = () =>
    resourceFormModal(null, JSON.parse(decodeURIComponent(b.dataset.r)), () => router()));
  $$(".res-del").forEach(b => b.onclick = () => confirmModal("Usunąć ten zasób?", async () => {
    try { await api.del("/teacher/resources/" + b.dataset.rid); ok("Usunięto zasób"); router(); }
    catch (e) { err(e.message); } }));
}

function teacherSectionBlock(s) {
  const resources = (s.resources || []).slice().sort((a, b) => (a.orderIndex || 0) - (b.orderIndex || 0));
  return `<div class="section-block">
    <div class="section-head">
      <span>${esc(s.title)}</span>
      <div class="btn-row">
        <button class="btn ghost sm add-res" data-sid="${s.id}">+ Zasób</button>
        <button class="btn ghost sm sec-edit" data-sid="${s.id}" data-title="${esc(s.title)}" data-order="${s.orderIndex || 0}">Edytuj</button>
        <button class="btn danger sm sec-del" data-sid="${s.id}">Usuń</button>
      </div></div>
    ${resources.length ? resources.map(r => {
      const data = encodeURIComponent(JSON.stringify({ ...r, sectionId: s.id }));
      return `<div class="resource-item">
        <div class="ricon ${esc(r.type)}">${RES_ICON[r.type] || "•"}</div>
        <div class="rtitle">${esc(r.title)} <span class="muted" style="font-weight:400">· ${esc(r.type)}</span></div>
        <button class="btn ghost sm res-edit" data-r="${data}">Edytuj</button>
        <button class="btn danger sm res-del" data-rid="${r.id}">Usuń</button>
      </div>`; }).join("")
      : `<div class="resource-item"><span class="muted">Brak zasobów</span></div>`}
  </div>`;
}

function sectionFormModal(courseId, section, done) {
  const isEdit = !!section;
  modal(isEdit ? "Edytuj sekcję" : "Nowa sekcja", `
    <div class="field"><label>Tytuł *</label><input id="stitle" value="${esc(section?.title || "")}"></div>
    <div class="field"><label>Kolejność</label><input id="sorder" type="number" value="${section?.orderIndex || 0}"></div>
    <div class="modal-actions"><button class="btn ghost" onclick="closeModal()">Anuluj</button>
      <button class="btn" id="saveSection">Zapisz</button></div>`,
    () => { $("#saveSection").onclick = async () => {
      const body = { title: $("#stitle").value.trim(), orderIndex: Number($("#sorder").value) || 0 };
      if (!body.title) return err("Tytuł jest wymagany");
      try {
        if (isEdit) await api.put(`/teacher/courses/${courseId}/sections/${section.id}`, body);
        else await api.post(`/teacher/courses/${courseId}/sections`, body);
        ok("Zapisano sekcję"); closeModal(); done();
      } catch (e) { err(e.message); }
    }; });
}

function resourceFormModal(sectionId, resource, done) {
  const isEdit = !!resource;
  const t = resource?.type || "NOTE";
  modal(isEdit ? "Edytuj zasób" : "Nowy zasób", `
    <div class="field"><label>Tytuł *</label><input id="rtitle" value="${esc(resource?.title || "")}"></div>
    <div class="field"><label>Typ</label><select id="rtype">
      ${["NOTE", "LINK", "VIDEO", "FILE"].map(x => `<option ${x === t ? "selected" : ""}>${x}</option>`).join("")}
    </select></div>
    <div class="field" id="fUrl"><label>URL</label><input id="rurl" value="${esc(resource?.url || "")}"></div>
    <div class="field" id="fNote"><label>Treść notatki</label><textarea id="rcontent">${esc(resource?.content || "")}</textarea></div>
    <div class="field" id="fFile"><label>Plik</label><input id="rfile" type="file">
      <p class="muted" id="fPath" style="font-size:.8rem">${resource?.filePath ? "Obecny: " + esc(resource.filePath) : ""}</p></div>
    <div class="field"><label>Kolejność</label><input id="rorder" type="number" value="${resource?.orderIndex || 0}"></div>
    <div class="modal-actions"><button class="btn ghost" onclick="closeModal()">Anuluj</button>
      <button class="btn" id="saveRes">Zapisz</button></div>`,
    (root) => {
      const sync = () => {
        const v = $("#rtype").value;
        $("#fUrl", root).style.display  = (v === "LINK" || v === "VIDEO") ? "block" : "none";
        $("#fNote", root).style.display = (v === "NOTE") ? "block" : "none";
        $("#fFile", root).style.display = (v === "FILE") ? "block" : "none";
      };
      sync(); $("#rtype").onchange = sync;
      $("#saveRes").onclick = async () => {
        const type = $("#rtype").value;
        const body = { title: $("#rtitle").value.trim(), type, orderIndex: Number($("#rorder").value) || 0,
          url: $("#rurl").value || null, content: $("#rcontent").value || null,
          filePath: resource?.filePath || null };
        if (!body.title) return err("Tytuł jest wymagany");
        try {
          if (type === "FILE") {
            const f = $("#rfile").files[0];
            if (f) {
              const fd = new FormData(); fd.append("file", f);
              const up = await api.upload("/teacher/resources/upload", fd);
              body.filePath = up.filePath;
            }
          }
          if (isEdit) await api.put("/teacher/resources/" + resource.id, body);
          else await api.post(`/teacher/sections/${sectionId}/resources`, body);
          ok("Zapisano zasób"); closeModal(); done();
        } catch (e) { err(e.message); }
      };
    });
}

/* ===========================================================
   TEACHER — studenci kursu
   =========================================================== */
async function viewCourseStudents(id) {
  const [course, students] = await Promise.all([
    api.get("/teacher/courses/" + id), api.get(`/teacher/courses/${id}/students`)]);
  appEl.innerHTML = `<div class="container">
    <a href="#/teacher/courses" class="muted">← Kursy</a>
    <div class="page-head" style="margin-top:8px"><div><h1>Studenci — ${esc(course.title)}</h1>
      <p class="muted">${students.length} zapisanych</p></div>
      <button class="btn" id="enroll">+ Zapisz studenta</button></div>
    ${students.length ? `<table class="tbl"><thead><tr><th>ID</th><th>Imię</th><th>Email</th><th>Status</th><th></th></tr></thead><tbody>
      ${students.map(s => `<tr><td>${s.id}</td><td>${esc(s.name)}</td><td>${esc(s.email)}</td>
        <td>${s.isActive ? '<span class="badge green">Aktywny</span>' : '<span class="badge red">Zawieszony</span>'}</td>
        <td><button class="btn danger sm unenroll" data-sid="${s.id}">Wypisz</button></td></tr>`).join("")}
      </tbody></table>`
      : `<div class="empty"><div class="icon">👥</div><p>Brak zapisanych studentów.</p></div>`}
    </div>`;
  $("#enroll").onclick = () => modal("Zapisz studenta", `
    <div class="field"><label>ID studenta</label><input id="sid" type="number" placeholder="np. 5">
      <p class="muted" style="font-size:.8rem">Podaj ID konta studenta (widoczne w panelu administratora w zakładce Użytkownicy).</p></div>
    <div class="modal-actions"><button class="btn ghost" onclick="closeModal()">Anuluj</button>
      <button class="btn" id="doEnroll">Zapisz</button></div>`,
    () => { $("#doEnroll").onclick = async () => {
      const sid = $("#sid").value;
      if (!sid) return err("Podaj ID studenta");
      try { await api.post(`/teacher/courses/${id}/students/${sid}`); ok("Zapisano studenta"); closeModal(); router(); }
      catch (e) { err(e.message); }
    }; });
  $$(".unenroll").forEach(b => b.onclick = () => confirmModal("Wypisać tego studenta z kursu?", async () => {
    try { await api.del(`/teacher/courses/${id}/students/${b.dataset.sid}`); ok("Wypisano"); router(); }
    catch (e) { err(e.message); } }));
}

/* ===========================================================
   TEACHER — kategorie
   =========================================================== */
async function viewTeacherCategories() {
  const cats = await api.get("/teacher/categories");
  appEl.innerHTML = `<div class="container">
    <div class="page-head"><h1>Kategorie</h1><button class="btn" id="newCat">+ Nowa kategoria</button></div>
    ${cats.length ? `<table class="tbl"><thead><tr><th>Nazwa</th><th>Opis</th><th>Ikona</th><th></th></tr></thead><tbody>
      ${cats.map(c => `<tr><td><strong>${esc(c.name)}</strong></td><td class="muted">${esc(c.description || "—")}</td>
        <td>${esc(c.icon || "")}</td>
        <td><div class="btn-row">
          <button class="btn ghost sm cedit" data-c="${encodeURIComponent(JSON.stringify(c))}">Edytuj</button>
          <button class="btn danger sm cdel" data-id="${c.id}">Usuń</button></div></td></tr>`).join("")}
      </tbody></table>`
      : `<div class="empty"><div class="icon">🏷️</div><p>Brak kategorii.</p></div>`}
    </div>`;
  $("#newCat").onclick = () => categoryFormModal(null, () => router());
  $$(".cedit").forEach(b => b.onclick = () => categoryFormModal(JSON.parse(decodeURIComponent(b.dataset.c)), () => router()));
  $$(".cdel").forEach(b => b.onclick = () => confirmModal("Usunąć kategorię?", async () => {
    try { await api.del("/teacher/categories/" + b.dataset.id); ok("Usunięto"); router(); }
    catch (e) { err(e.message); } }));
}
function categoryFormModal(cat, done) {
  const isEdit = !!cat;
  modal(isEdit ? "Edytuj kategorię" : "Nowa kategoria", `
    <div class="field"><label>Nazwa *</label><input id="cname" value="${esc(cat?.name || "")}"></div>
    <div class="field"><label>Opis</label><input id="cdesc" value="${esc(cat?.description || "")}"></div>
    <div class="field"><label>Ikona (emoji, opcjonalnie)</label><input id="cicon" value="${esc(cat?.icon || "")}"></div>
    <div class="modal-actions"><button class="btn ghost" onclick="closeModal()">Anuluj</button>
      <button class="btn" id="saveCat">Zapisz</button></div>`,
    () => { $("#saveCat").onclick = async () => {
      const body = { name: $("#cname").value.trim(), description: $("#cdesc").value, icon: $("#cicon").value };
      if (!body.name) return err("Nazwa jest wymagana");
      try {
        if (isEdit) await api.put("/teacher/categories/" + cat.id, body);
        else await api.post("/teacher/categories", body);
        ok("Zapisano"); closeModal(); done();
      } catch (e) { err(e.message); }
    }; });
}

/* ===========================================================
   ADMIN
   =========================================================== */
async function viewAdminUsers() {
  const users = await api.get("/admin/users");
  appEl.innerHTML = `<div class="container">
    <div class="page-head"><div><h1>Użytkownicy</h1><p class="muted">${users.length} kont</p></div></div>
    <table class="tbl"><thead><tr><th>ID</th><th>Imię</th><th>Email</th><th>Rola</th><th>Status</th><th>Akcje</th></tr></thead><tbody>
      ${users.map(u => `<tr>
        <td>${u.id}</td><td><strong>${esc(u.name)}</strong></td><td>${esc(u.email)}</td>
        <td>${roleBadge(u.role)}</td>
        <td>${u.isActive ? '<span class="badge green">Aktywny</span>' : '<span class="badge red">Zawieszony</span>'}</td>
        <td><div class="btn-row">
          <select class="role-sel" data-id="${u.id}" style="padding:4px 8px;border-radius:6px;border:1px solid var(--border)">
            ${["STUDENT", "TEACHER", "ADMIN"].map(r => `<option ${r === u.role ? "selected" : ""}>${r}</option>`).join("")}
          </select>
          <button class="btn ghost sm suspend" data-id="${u.id}">${u.isActive ? "Zawieś" : "Aktywuj"}</button>
          <button class="btn danger sm udel" data-id="${u.id}">Usuń</button>
        </div></td></tr>`).join("")}
      </tbody></table></div>`;
  $$(".role-sel").forEach(s => s.onchange = async () => {
    try { await api.put(`/admin/users/${s.dataset.id}/role`, { role: s.value }); ok("Zmieniono rolę"); }
    catch (e) { err(e.message); router(); } });
  $$(".suspend").forEach(b => b.onclick = async () => {
    try { await api.put(`/admin/users/${b.dataset.id}/suspend`); ok("Zmieniono status konta"); router(); }
    catch (e) { err(e.message); } });
  $$(".udel").forEach(b => b.onclick = () => confirmModal("Usunąć trwale to konto?", async () => {
    try { await api.del("/admin/users/" + b.dataset.id); ok("Usunięto konto"); router(); }
    catch (e) { err(e.message); } }));
}

async function viewAdminStats() {
  const s = await api.get("/admin/stats");
  appEl.innerHTML = `<div class="container">
    <div class="page-head"><h1>Statystyki systemu</h1></div>
    <div class="grid cols-4">
      <div class="stat"><div class="value">${s.totalUsers}</div><div class="label">Użytkownicy</div></div>
      <div class="stat"><div class="value">${s.totalCourses}</div><div class="label">Kursy</div></div>
      <div class="stat"><div class="value">${s.totalResources}</div><div class="label">Zasoby</div></div>
      <div class="stat"><div class="value">${s.monthlyActiveUsers}</div><div class="label">Aktywni (30 dni)</div></div>
    </div></div>`;
}

const LOG_ICON = {
  LOGIN: "🔑", LOGIN_FAILED: "⚠️", REGISTER: "👤",
  ROLE_CHANGE: "🔄", SUSPEND: "🚫", ACTIVATE: "✅", DELETE_USER: "🗑️"
};
async function viewAdminLogs() {
  const logs = await api.get("/admin/logs");
  appEl.innerHTML = `<div class="container">
    <div class="page-head"><div><h1>Logi systemowe</h1>
      <p class="muted">Ostatnie 100 zdarzeń systemowych</p></div></div>
    ${logs.length ? `<table class="tbl"><thead><tr>
        <th>Data</th><th>Akcja</th><th>Szczegóły</th><th>Użytkownik</th></tr></thead><tbody>
      ${logs.map(l => `<tr>
        <td class="muted" style="white-space:nowrap">${fmtDate(l.createdAt)}</td>
        <td><span class="badge gray">${esc(LOG_ICON[l.action] || "")} ${esc(l.action)}</span></td>
        <td>${esc(l.details || "—")}</td>
        <td class="muted">${esc(l.userEmail || "—")}</td>
      </tr>`).join("")}
      </tbody></table>`
    : `<div class="empty"><div class="icon">📋</div><p>Brak logów systemowych.</p></div>`}
  </div>`;
}

/* ---------- Confirm helper ---------- */
function confirmModal(message, onYes) {
  modal("Potwierdzenie", `<p>${esc(message)}</p>
    <div class="modal-actions"><button class="btn ghost" onclick="closeModal()">Anuluj</button>
      <button class="btn danger" id="cfYes">Tak</button></div>`,
    () => { $("#cfYes").onclick = () => { closeModal(); onYes(); }; });
}
