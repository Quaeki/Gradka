// ui.jsx — shared primitives: icons, placeholders, phone frame, nav

// ───────── ICONS (simple stroke, 24×24) ─────────
const Icon = {
  home: (c='currentColor') => (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M3 10.5 12 3l9 7.5V20a1 1 0 0 1-1 1h-5v-6h-6v6H4a1 1 0 0 1-1-1v-9.5Z"/>
    </svg>
  ),
  grid: (c='currentColor') => (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <rect x="3.5" y="3.5" width="7" height="7" rx="1.5"/>
      <rect x="13.5" y="3.5" width="7" height="7" rx="1.5"/>
      <rect x="3.5" y="13.5" width="7" height="7" rx="1.5"/>
      <rect x="13.5" y="13.5" width="7" height="7" rx="1.5"/>
    </svg>
  ),
  heart: (c='currentColor', fill='none') => (
    <svg width="24" height="24" viewBox="0 0 24 24" fill={fill} stroke={c} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 20s-7-4.35-7-10a4 4 0 0 1 7-2.65A4 4 0 0 1 19 10c0 5.65-7 10-7 10Z"/>
    </svg>
  ),
  bag: (c='currentColor') => (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M5 8h14l-1 11a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 8Z"/>
      <path d="M9 8V6a3 3 0 0 1 6 0v2"/>
    </svg>
  ),
  user: (c='currentColor') => (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="8" r="4"/>
      <path d="M4 21c1.5-4 4.5-6 8-6s6.5 2 8 6"/>
    </svg>
  ),
  search: (c='currentColor') => (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="11" cy="11" r="7"/>
      <path d="m20 20-4-4"/>
    </svg>
  ),
  barcode: (c='currentColor') => (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M4 6v12M7 6v12M10 6v12M14 6v12M17 6v12M20 6v12"/>
    </svg>
  ),
  plus: (c='currentColor', s=20) => (
    <svg width={s} height={s} viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="2.2" strokeLinecap="round"><path d="M12 5v14M5 12h14"/></svg>
  ),
  minus: (c='currentColor', s=20) => (
    <svg width={s} height={s} viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="2.2" strokeLinecap="round"><path d="M5 12h14"/></svg>
  ),
  back: (c='currentColor') => (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
      <path d="M15 19l-7-7 7-7"/>
    </svg>
  ),
  close: (c='currentColor') => (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.8" strokeLinecap="round"><path d="M6 6l12 12M18 6 6 18"/></svg>
  ),
  chevron: (c='currentColor', s=18) => (
    <svg width={s} height={s} viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><path d="M9 6l6 6-6 6"/></svg>
  ),
  pin: (c='currentColor') => (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 22s7-7.5 7-13a7 7 0 1 0-14 0c0 5.5 7 13 7 13Z"/><circle cx="12" cy="9" r="2.5"/>
    </svg>
  ),
  clock: (c='currentColor') => (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.6" strokeLinecap="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>
  ),
  filter: (c='currentColor') => (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.6" strokeLinecap="round"><path d="M4 6h16M7 12h10M10 18h4"/></svg>
  ),
  mic: (c='currentColor') => (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <rect x="9" y="3" width="6" height="12" rx="3"/><path d="M5 11a7 7 0 0 0 14 0M12 18v3"/>
    </svg>
  ),
  check: (c='currentColor', s=20) => (
    <svg width={s} height={s} viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M5 12l4 4 10-10"/></svg>
  ),
  delivery: (c='currentColor') => (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M3 7h11v10H3zM14 10h4l3 3v4h-7"/><circle cx="7" cy="18" r="2"/><circle cx="17" cy="18" r="2"/>
    </svg>
  ),
  leaf: (c='currentColor', s=22) => (
    <svg width={s} height={s} viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M20 4c-8 0-14 5-14 12 0 2 1 4 2 4 7 0 12-6 12-16Z"/><path d="M6 20c3-6 7-10 13-13"/>
    </svg>
  ),
  more: (c='currentColor') => (
    <svg width="22" height="22" viewBox="0 0 24 24" fill={c}><circle cx="5" cy="12" r="1.6"/><circle cx="12" cy="12" r="1.6"/><circle cx="19" cy="12" r="1.6"/></svg>
  ),
  repeat: (c='currentColor') => (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M4 9V7a2 2 0 0 1 2-2h12l-3-3M20 15v2a2 2 0 0 1-2 2H6l3 3"/>
    </svg>
  ),
};

// ───────── PRODUCT PLACEHOLDER ─────────
// Striped tile with monospace label. Per default-aesthetic guidance.
function ProductPlaceholder({ hue = 120, size = 120, label = '' }) {
  // subtle toned background per product; horizontal stripes
  const bg   = `oklch(0.94 0.03 ${hue})`;
  const ink  = `oklch(0.42 0.07 ${hue})`;
  const line = `oklch(0.88 0.04 ${hue})`;
  return (
    <div style={{
      width: '100%', aspectRatio: size ? undefined : '1/1',
      height: size, background: bg, borderRadius: 12,
      position: 'relative', overflow: 'hidden', flexShrink: 0,
    }}>
      <svg width="100%" height="100%" preserveAspectRatio="none" style={{ position: 'absolute', inset: 0 }}>
        <defs>
          <pattern id={`s${hue}`} width="8" height="8" patternUnits="userSpaceOnUse" patternTransform="rotate(0)">
            <line x1="0" y1="4" x2="8" y2="4" stroke={line} strokeWidth="1"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill={`url(#s${hue})`}/>
      </svg>
      {label && (
        <div style={{
          position: 'absolute', left: 10, bottom: 8, right: 10,
          fontFamily: 'JetBrains Mono, monospace', fontSize: 9,
          color: ink, letterSpacing: '0.04em', textTransform: 'lowercase',
          textShadow: `0 0 8px ${bg}`,
        }}>{label}</div>
      )}
    </div>
  );
}

// ───────── CHIP ─────────
function Chip({ active, onClick, children, hue }) {
  return (
    <button
      onClick={onClick}
      className="btn"
      style={{
        flexShrink: 0,
        padding: '8px 14px',
        borderRadius: 999,
        border: `1px solid ${active ? 'var(--ink)' : 'var(--line)'}`,
        background: active ? 'var(--ink)' : 'var(--surface)',
        color: active ? 'var(--bg)' : 'var(--ink)',
        fontSize: 13,
        fontWeight: 500,
        fontFamily: 'inherit',
        cursor: 'pointer',
        whiteSpace: 'nowrap',
      }}>
      {children}
    </button>
  );
}

// ───────── STEPPER ─────────
function Stepper({ qty, onAdd, onSub, compact=false }) {
  if (qty === 0) {
    return (
      <button className="btn" onClick={onAdd} style={{
        width: compact ? 32 : 36, height: compact ? 32 : 36, borderRadius: 12,
        background: 'var(--ink)', color: 'var(--bg)', border: 0,
        display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
      }}>{Icon.plus('currentColor', compact ? 16 : 18)}</button>
    );
  }
  return (
    <div style={{
      display: 'flex', alignItems: 'center', height: compact ? 32 : 36,
      background: 'var(--ink)', color: 'var(--bg)',
      borderRadius: 12, padding: '0 4px', gap: 2,
    }}>
      <button onClick={onSub} className="btn" style={{
        width: compact ? 24 : 28, height: compact ? 24 : 28, borderRadius: 8,
        border: 0, background: 'transparent', color: 'inherit',
        display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
      }}>{Icon.minus('currentColor', 16)}</button>
      <div className="mono" style={{ minWidth: compact ? 22 : 26, textAlign: 'center', fontSize: 13, fontWeight: 600 }}>{qty}</div>
      <button onClick={onAdd} className="btn" style={{
        width: compact ? 24 : 28, height: compact ? 24 : 28, borderRadius: 8,
        border: 0, background: 'transparent', color: 'inherit',
        display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
      }}>{Icon.plus('currentColor', 16)}</button>
    </div>
  );
}

// ───────── PHONE FRAME (custom, brand-tuned) ─────────
function Phone({ children, dark=false, label, notchColor, style }) {
  const frameColor = dark ? '#2a2a27' : '#d6d2c6';
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', ...style }}>
      {label && <div className="canvas-label"><b>{label.title}</b>{label.sub}</div>}
      <div
        data-theme={dark ? 'dark' : 'light'}
        style={{
          width: 390, height: 820,
          borderRadius: 44,
          padding: 8,
          background: frameColor,
          boxShadow: dark
            ? '0 30px 70px rgba(0,0,0,0.5), 0 6px 20px rgba(0,0,0,0.35)'
            : '0 30px 70px rgba(26,26,23,0.18), 0 6px 20px rgba(26,26,23,0.08)',
        }}>
        <div style={{
          width: '100%', height: '100%',
          background: 'var(--bg)',
          borderRadius: 36,
          overflow: 'hidden',
          position: 'relative',
          display: 'flex', flexDirection: 'column',
        }}>
          {/* status bar */}
          <div style={{
            height: 36, padding: '10px 22px 0',
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            fontSize: 13, fontWeight: 600, color: 'var(--ink)',
            position: 'relative',
            flexShrink: 0,
          }}>
            <span className="mono" style={{ letterSpacing: '0.02em' }}>9:41</span>
            {/* camera */}
            <div style={{
              position: 'absolute', left: '50%', top: 10, transform: 'translateX(-50%)',
              width: 20, height: 20, borderRadius: 10,
              background: '#1a1a17',
            }} />
            <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
              <svg width="15" height="11" viewBox="0 0 15 11"><path d="M1.5 6.5 3 8l3-3 6.5 6.5" fill="none" stroke="currentColor" strokeWidth="1.3" strokeLinecap="round"/></svg>
              <svg width="14" height="10" viewBox="0 0 14 10"><path d="M1 8l0-1M4 8l0-3M7 8l0-5M10 8l0-7M13 8l0-8" stroke="currentColor" strokeWidth="1.3" strokeLinecap="round"/></svg>
              <svg width="22" height="10" viewBox="0 0 22 10"><rect x="0.5" y="0.5" width="18" height="9" rx="2.5" fill="none" stroke="currentColor" strokeWidth="0.9"/><rect x="2" y="2" width="12" height="6" rx="1" fill="currentColor"/><rect x="19.5" y="3" width="2" height="4" rx="1" fill="currentColor"/></svg>
            </div>
          </div>

          {/* screen content */}
          <div className="screen-scroll" style={{ flex: 1, overflow: 'auto', position: 'relative' }}>
            {children}
          </div>

          {/* gesture nav */}
          <div style={{ height: 24, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
            <div style={{ width: 120, height: 4, borderRadius: 2, background: 'var(--ink)', opacity: 0.35 }}/>
          </div>
        </div>
      </div>
    </div>
  );
}

// ───────── BOTTOM NAV ─────────
function BottomNav({ route, setRoute, cartCount }) {
  const items = [
    { id: 'home',     label: 'Главная',   icon: Icon.home },
    { id: 'catalog',  label: 'Каталог',   icon: Icon.grid },
    { id: 'fav',      label: 'Избранное', icon: Icon.heart },
    { id: 'cart',     label: 'Корзина',   icon: Icon.bag,  badge: cartCount },
    { id: 'profile',  label: 'Профиль',   icon: Icon.user },
  ];
  return (
    <div style={{
      background: 'var(--surface)',
      borderTop: '1px solid var(--line)',
      padding: '8px 6px 6px',
      display: 'flex',
      justifyContent: 'space-around',
    }}>
      {items.map(it => {
        const active = route === it.id;
        const color = active ? 'var(--ink)' : 'var(--ink-3)';
        return (
          <button key={it.id} onClick={() => setRoute(it.id)} className="btn" style={{
            flex: 1, border: 0, background: 'transparent', cursor: 'pointer',
            display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2,
            padding: '4px 2px', position: 'relative',
            fontFamily: 'inherit',
          }}>
            <div style={{ position: 'relative' }}>
              {it.icon(color)}
              {it.badge > 0 && (
                <div className="mono" style={{
                  position: 'absolute', top: -4, right: -8,
                  minWidth: 16, height: 16, padding: '0 4px',
                  background: 'var(--accent)', color: 'var(--accent-ink)',
                  borderRadius: 8, fontSize: 10, fontWeight: 700,
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                }}>{it.badge}</div>
              )}
            </div>
            <span style={{ fontSize: 10, color, fontWeight: active ? 600 : 500, letterSpacing: '-0.01em' }}>{it.label}</span>
          </button>
        );
      })}
    </div>
  );
}

// ───────── LOGO ─────────
function Logo({ size = 20, color }) {
  const c = color || 'var(--ink)';
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8, color: c }}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
        <circle cx="12" cy="12" r="10" stroke={c} strokeWidth="1.6"/>
        <path d="M8 15c0-4 2-7 6-7-1 4-2 5-6 7Z" fill={c}/>
      </svg>
      <span className="serif" style={{ fontSize: size * 0.95, fontWeight: 500, letterSpacing: '-0.02em' }}>грядка</span>
    </div>
  );
}

Object.assign(window, { Icon, ProductPlaceholder, Chip, Stepper, Phone, BottomNav, Logo });
