const STATUS_LABELS = {
  created: "Оформлен",
  confirmed: "Подтверждён",
  delivering: "В пути",
  delivered: "Доставлен",
  cancelled: "Отменён",
};

const els = {
  loginPanel: document.getElementById("loginPanel"),
  ordersPanel: document.getElementById("ordersPanel"),
  passwordInput: document.getElementById("passwordInput"),
  loginBtn: document.getElementById("loginBtn"),
  logoutBtn: document.getElementById("logoutBtn"),
  refreshBtn: document.getElementById("refreshBtn"),
  searchInput: document.getElementById("searchInput"),
  ordersBody: document.getElementById("ordersBody"),
  emptyNote: document.getElementById("emptyNote"),
  error: document.getElementById("error"),
};

let searchTimer = null;
let refreshTimer = null;

// Сессия хранится в HttpOnly-cookie, которую браузер шлёт сам.
async function api(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
  });
  const body = await response.json().catch(() => null);
  if (!response.ok) {
    const error = new Error(body?.error || `HTTP ${response.status}`);
    error.status = response.status;
    throw error;
  }
  return body;
}

async function loadOrders() {
  const query = els.searchInput.value.trim();
  const orders = await api(`/orders/all?q=${encodeURIComponent(query)}`);
  renderOrders(orders);
}

function renderOrders(orders) {
  els.ordersBody.innerHTML = "";
  els.emptyNote.style.display = orders.length ? "none" : "block";

  for (const order of orders) {
    const row = document.createElement("tr");

    row.appendChild(cell(`№ ${order.number}`));
    row.appendChild(cell(formatDate(order.createdAtMillis)));
    row.appendChild(cell(order.phone || "—"));
    row.appendChild(cell(order.addressText || "—"));

    const itemsCell = cell("");
    itemsCell.className = "items";
    itemsCell.textContent = order.items
      .map((item) => `${item.name} ×${item.qty}`)
      .join(", ");
    row.appendChild(itemsCell);

    const totalCell = cell(`${order.total} ₽`);
    totalCell.className = "total";
    totalCell.title = `Товары ${order.subtotal} ₽ + доставка ${order.delivery} ₽`;
    row.appendChild(totalCell);

    const statusCell = cell("");
    const select = document.createElement("select");
    select.className = `status ${order.status}`;
    for (const [value, label] of Object.entries(STATUS_LABELS)) {
      const option = document.createElement("option");
      option.value = value;
      option.textContent = label;
      option.selected = value === order.status;
      select.appendChild(option);
    }
    select.addEventListener("change", async () => {
      select.disabled = true;
      try {
        await api(`/orders/${order.id}/status`, {
          method: "PATCH",
          body: JSON.stringify({ status: select.value }),
        });
        select.className = `status ${select.value}`;
      } catch (error) {
        alert(`Не удалось обновить статус: ${error.message}`);
        select.value = order.status;
      } finally {
        select.disabled = false;
      }
    });
    statusCell.appendChild(select);
    row.appendChild(statusCell);

    els.ordersBody.appendChild(row);
  }
}

function cell(text) {
  const td = document.createElement("td");
  td.textContent = text;
  return td;
}

function formatDate(millis) {
  return new Date(millis).toLocaleString("ru-RU", {
    day: "numeric",
    month: "short",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function showOrders() {
  els.loginPanel.style.display = "none";
  els.ordersPanel.style.display = "block";
  loadOrders().catch(handleApiError);
  clearInterval(refreshTimer);
  refreshTimer = setInterval(() => loadOrders().catch(() => {}), 15000);
}

function showLogin(message) {
  clearInterval(refreshTimer);
  els.ordersPanel.style.display = "none";
  els.loginPanel.style.display = "block";
  if (message) {
    els.error.textContent = message;
    els.error.style.display = "block";
  } else {
    els.error.style.display = "none";
  }
}

function handleApiError(error) {
  if (error.status === 401) {
    showLogin();
  } else {
    showLogin(`Ошибка: ${error.message}`);
  }
}

els.loginBtn.addEventListener("click", async () => {
  els.error.style.display = "none";
  try {
    await api("/orders/admin/login", {
      method: "POST",
      body: JSON.stringify({ password: els.passwordInput.value }),
    });
    els.passwordInput.value = "";
    showOrders();
  } catch (error) {
    const message = {
      WRONG_PASSWORD: "Неверный пароль",
      TOO_MANY_ATTEMPTS: "Слишком много попыток. Подождите минуту",
    }[error.message] || `Ошибка: ${error.message}`;
    showLogin(message);
  }
});
els.passwordInput.addEventListener("keydown", (event) => {
  if (event.key === "Enter") els.loginBtn.click();
});

els.logoutBtn.addEventListener("click", async () => {
  await api("/orders/admin/logout", { method: "POST" }).catch(() => {});
  showLogin();
});

els.refreshBtn.addEventListener("click", () => loadOrders().catch(handleApiError));

els.searchInput.addEventListener("input", () => {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => loadOrders().catch(handleApiError), 300);
});

// Если cookie-сессия ещё жива — сразу показываем заказы, иначе форму входа.
api("/orders/all?q=")
  .then((orders) => {
    showOrders();
    renderOrders(orders);
  })
  .catch(() => showLogin());
