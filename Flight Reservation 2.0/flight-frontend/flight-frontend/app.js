// Shared frontend helper + API wiring
const API_BASE = "http://localhost:8080"; // same origin. If backend at different URL set like 'http://localhost:8080'
const TOKEN_KEY = "flight_token";

// Decode JWT to extract payload
function parseJwt(token) {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
}

// Redirect based on user role
async function redirectByRole(token) {
  const decoded = parseJwt(token);
  if (decoded && decoded.role && decoded.role.toLowerCase() === "admin") {
    location.href = "admin.html";
  } else {
    location.href = "flights.html";
  }
}

async function apiFetch(path, opts = {}) {
  const headers = opts.headers || {};
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) headers["Authorization"] = "Bearer " + token;
  headers["Content-Type"] = headers["Content-Type"] || "application/json";
  const res = await fetch(API_BASE + path, { ...opts, headers });
  if (res.ok) {
    const ct = res.headers.get("content-type") || "";
    if (ct.includes("application/json")) return res.json();
    return res.text();
  } else {
    let errText = await res.text();
    try {
      const j = JSON.parse(errText);
      if (j.error) errText = j.error;
    } catch (e) {}
    const err = new Error(errText || "Request failed: " + res.status);
    err.status = res.status;
    throw err;
  }
}

async function apiGet(path) {
  return apiFetch(path, { method: "GET" });
}
async function apiPost(path, body) {
  return apiFetch(path, { method: "POST", body: JSON.stringify(body) });
}
async function apiPut(path, body) {
  return apiFetch(path, { method: "PUT", body: JSON.stringify(body) });
}
async function apiDelete(path) {
  return apiFetch(path, { method: "DELETE" });
}

// Auth helpers
async function apiLogin(username, password) {
  const r = await apiPost("/auth/login", { username, password });
  if (r && r.token) {
    localStorage.setItem(TOKEN_KEY, r.token);
    return r;
  }
  throw new Error("Login failed");
}

async function apiRegister(username, email, password) {
  const r = await apiPost("/auth/register", { username, email, password });
  if (r && r.token) {
    localStorage.setItem(TOKEN_KEY, r.token);
    return r;
  }
  throw new Error("Register failed");
}

function logout() {
  localStorage.removeItem(TOKEN_KEY);
  location.href = "index.html";
}

function ensureAuth() {
  if (!localStorage.getItem(TOKEN_KEY)) {
    location.href = "index.html";
    throw new Error("Not authenticated");
  }
}

// small helpers
function escapeHtml(s) {
  if (s === null || s === undefined) return "";
  return String(s).replace(
    /[&<>"']/g,
    (c) =>
      ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[
        c
      ])
  );
}

function formatDate(iso) {
  if (!iso) return "";
  const d = new Date(iso);
  if (isNaN(d)) return iso;
  return d.toLocaleString();
}

// convert ISO to local datetime-local input value
function toLocalInput(iso) {
  const d = new Date(iso);
  const pad = (n) => String(n).padStart(2, "0");
  const yyyy = d.getFullYear();
  const mm = pad(d.getMonth() + 1);
  const dd = pad(d.getDate());
  const hh = pad(d.getHours());
  const min = pad(d.getMinutes());
  return `${yyyy}-${mm}-${dd}T${hh}:${min}`;
}
// Open/Close panel
const settingsPanel = document.getElementById("settingsPanel");
const openSettings = document.getElementById("openSettings");
const closeSettings = document.getElementById("closeSettings");

document.addEventListener("click", e => {
  if (e.target.id === "openSettings") settingsPanel.classList.add("show");
  if (e.target.id === "closeSettings") settingsPanel.classList.remove("show");
});

if (closeSettings) closeSettings.onclick = () => settingsPanel.classList.remove("show");

// Switch pages inside panel
document.querySelectorAll(".settings-list li[data-panel]").forEach(item => {
  item.addEventListener("click", () => {
    document.querySelectorAll(".settings-list li").forEach(i => i.classList.remove("active"));
    item.classList.add("active");

    document.querySelectorAll(".settings-page").forEach(p => p.classList.remove("active"));
    document.getElementById(item.dataset.panel).classList.add("active");
  });
});

// Dark Mode toggle (shared)
const darkToggle = document.getElementById("darkToggle");
if (darkToggle) {
  darkToggle.addEventListener("click", () => {
    document.body.classList.toggle("dark");
    localStorage.setItem("darkMode", document.body.classList.contains("dark"));
  });

  if (localStorage.getItem("darkMode") === "true") {
    document.body.classList.add("dark");
  }
}

// Logout action
const logoutAction = document.getElementById("logoutAction");
if (logoutAction) logoutAction.onclick = () => {
  localStorage.removeItem("token");
  location.href = "index.html";
};

// SUB PANEL HANDLING
document.querySelectorAll("[data-sub]").forEach(item => {
  item.addEventListener("click", () => {
    document.querySelectorAll(".settings-page").forEach(p => p.classList.remove("active"));
    document.getElementById(item.dataset.sub).classList.add("active");
  });
});

document.querySelectorAll("[data-back]").forEach(btn => {
  btn.addEventListener("click", () => {
    document.querySelectorAll(".settings-page").forEach(p => p.classList.remove("active"));
    document.getElementById(btn.dataset.back).classList.add("active");
  });
});

// DELETE ACCOUNT ACTION (real logic depends on backend)
const deleteAccountBtn = document.getElementById("deleteAccount");
if (deleteAccountBtn) {
  deleteAccountBtn.addEventListener("click", () => {
    if (confirm("Are you sure you want to delete your account?")) {
      // API will depend on your backend
      apiPost("/users/delete", {}).then(() => {
        localStorage.removeItem("token");
        location.href = "index.html";
      });
    }
  });
}

const LANG = {
  en: {
    settings: "Settings",
    accounts: "Accounts",
    profile: "Profile Settings",
    addAccount: "Add Account",
    darkMode: "Dark Mode",
    timeLang: "Time & Language",
    logout: "Log Out"
  },
  hi: {
    settings: "सेटिंग्स",
    accounts: "खाते",
    profile: "प्रोफ़ाइल सेटिंग्स",
    addAccount: "नया खाता जोड़ें",
    darkMode: "डार्क मोड",
    timeLang: "समय और भाषा",
    logout: "लॉग आउट"
  },
  bn: {
    settings: "সেটিংস",
    accounts: "অ্যাকাউন্ট",
    profile: "প্রোফাইল সেটিংস",
    addAccount: "অ্যাকাউন্ট যোগ করুন",
    darkMode: "ডার্ক মোড",
    timeLang: "সময় ও ভাষা",
    logout: "লগ আউট"
  }
};

function applyLanguage(lang) {
  localStorage.setItem("lang", lang);

  // Update UI Texts
  document.querySelector(".settings-header span").textContent = LANG[lang].settings;
  document.querySelector('[data-panel="acc"]').textContent = LANG[lang].accounts;
  document.querySelector('[data-sub="profile"]').textContent = LANG[lang].profile;
  document.querySelector('[data-sub="add"]').textContent = LANG[lang].addAccount;
  document.querySelector('[data-panel="dark"]').textContent = LANG[lang].darkMode;
  document.querySelector('[data-panel="time"]').textContent = LANG[lang].timeLang;
  document.getElementById("logoutAction").textContent = LANG[lang].logout;
}

// Handle Change Language button
const langSelect = document.getElementById("langSelect");
const changeLang = document.getElementById("changeLang");

if (changeLang) {
  changeLang.onclick = () => {
    applyLanguage(langSelect.value);
  };
}

// Load saved language on page load
const savedLang = localStorage.getItem("lang") || "en";
applyLanguage(savedLang);
if (langSelect) langSelect.value = savedLang;
function updateClock() {
  const format = localStorage.getItem("timeFormat") || "12";
  const now = new Date();

  let options = { hour: "numeric", minute: "numeric", second: "numeric" };
  options.hour12 = (format === "12");

  const timeBox = document.getElementById("liveTime");
  if (timeBox) timeBox.textContent = now.toLocaleTimeString(undefined, options);
}

setInterval(updateClock, 1000);

const timeFormat = document.getElementById("timeFormat");
const applyFormat = document.getElementById("applyFormat");

if (applyFormat) {
  applyFormat.onclick = () => {
    localStorage.setItem("timeFormat", timeFormat.value);
    updateClock();
  };
}

const savedFormat = localStorage.getItem("timeFormat") || "12";
if (timeFormat) timeFormat.value = savedFormat;
updateClock();

