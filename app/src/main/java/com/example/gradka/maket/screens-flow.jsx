// screens-flow.jsx — Cart, Checkout, Tracking, Onboarding, Address, Favs, Profile, Recipes, Orders

// ─── ONBOARDING ───
function OnboardingScreen({ app }) {
  const [step, setStep] = React.useState(0);
  const steps = [
    { badge: '01', title: 'С грядки — прямо на стол', body: '32 фермера средней полосы. Никаких посредников и холодных складов.', hue: 125 },
    { badge: '02', title: 'Доставим за 40 минут',     body: 'Курьер привезёт заказ в удобное время — даже если это сегодня вечером.', hue: 95 },
    { badge: '03', title: 'Любимое — в пару тапов',   body: 'Сохраняйте списки, повторяйте заказ и подписывайтесь на регулярные продукты.', hue: 48 },
  ];
  const s = steps[step];
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', padding: '10px 24px 24px', background: 'var(--bg)' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: 10 }}>
        <Logo size={22}/>
        <button onClick={() => app.setRoute('home')} className="btn" style={{ border: 0, background: 'transparent', fontSize: 13, color: 'var(--ink-3)', cursor: 'pointer', fontFamily: 'inherit' }}>Пропустить</button>
      </div>
      <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <div style={{ width: 240, height: 240, borderRadius: 36, overflow: 'hidden', background: `oklch(0.94 0.04 ${s.hue})`, position: 'relative', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <ProductPlaceholder hue={s.hue} size={240} label=""/>
          <div style={{ position: 'absolute' }}>
            <svg width="120" height="120" viewBox="0 0 120 120">
              <circle cx="60" cy="60" r="52" fill="none" stroke={`oklch(0.5 0.1 ${s.hue})`} strokeWidth="1"/>
              <circle cx="60" cy="60" r="34" fill="none" stroke={`oklch(0.5 0.1 ${s.hue})`} strokeWidth="1"/>
              <circle cx="60" cy="60" r="16" fill={`oklch(0.55 0.12 ${s.hue})`}/>
            </svg>
          </div>
        </div>
      </div>
      <div>
        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: '0.12em', textTransform: 'uppercase', color: 'var(--accent-deep)' }}>Шаг {s.badge}</div>
        <div className="serif" style={{ fontSize: 34, fontWeight: 500, color: 'var(--ink)', letterSpacing: '-0.025em', lineHeight: 1.05, marginTop: 10 }}>{s.title}</div>
        <div style={{ fontSize: 15, color: 'var(--ink-2)', lineHeight: 1.55, marginTop: 14 }}>{s.body}</div>
        <div style={{ display: 'flex', gap: 6, marginTop: 24 }}>
          {steps.map((_, i) => <div key={i} style={{ height: 3, flex: i === step ? 2 : 1, borderRadius: 2, background: i === step ? 'var(--ink)' : 'var(--line-2)' }}/>)}
        </div>
        <div style={{ display: 'flex', gap: 10, marginTop: 18 }}>
          {step > 0 && <button onClick={() => setStep(step - 1)} className="btn" style={{ height: 54, padding: '0 20px', borderRadius: 16, border: '1px solid var(--line-2)', background: 'transparent', fontSize: 15, cursor: 'pointer', fontFamily: 'inherit', color: 'var(--ink)' }}>Назад</button>}
          <button onClick={() => step < 2 ? setStep(step + 1) : app.setRoute('home')} className="btn" style={{ flex: 1, height: 54, borderRadius: 16, border: 0, background: 'var(--ink)', color: 'var(--bg)', fontFamily: 'inherit', fontSize: 15, fontWeight: 500, cursor: 'pointer' }}>{step < 2 ? 'Дальше' : 'Начать покупки'}</button>
        </div>
      </div>
    </div>
  );
}

// ─── CART ───
function CartScreen({ app }) {
  const items = Object.entries(app.cart).filter(([, q]) => q > 0).map(([id, q]) => ({ ...PRODUCTS.find(p => p.id === id), q }));
  const subtotal = items.reduce((s, x) => s + x.price * x.q, 0);
  const delivery = subtotal > 1500 ? 0 : 149;
  const total = subtotal + delivery;
  if (items.length === 0) return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%', padding: 40, textAlign: 'center' }}>
      <div style={{ width: 100, height: 100, borderRadius: 50, background: 'var(--surface-2)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 20 }}>{Icon.bag('var(--ink-3)')}</div>
      <div className="serif" style={{ fontSize: 26, fontWeight: 500, letterSpacing: '-0.02em' }}>Корзина пуста</div>
      <div style={{ fontSize: 14, color: 'var(--ink-2)', marginTop: 8, maxWidth: 240, lineHeight: 1.5 }}>Добавьте что-нибудь вкусное из каталога</div>
      <button onClick={() => app.setRoute('catalog')} className="btn" style={{ marginTop: 20, padding: '12px 24px', borderRadius: 999, background: 'var(--ink)', color: 'var(--bg)', border: 0, fontFamily: 'inherit', fontSize: 14, fontWeight: 500, cursor: 'pointer' }}>К каталогу</button>
    </div>
  );
  return (
    <>
      <div style={{ padding: '14px 20px 10px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div className="serif" style={{ fontSize: 24, fontWeight: 500, letterSpacing: '-0.02em' }}>Корзина</div>
        <div style={{ fontSize: 13, color: 'var(--ink-3)' }}>{items.length} товаров</div>
      </div>
      {delivery > 0 && (
        <div style={{ margin: '0 16px 12px', padding: '12px 14px', background: 'var(--accent-soft)', borderRadius: 12, color: 'var(--accent-deep)', fontSize: 13 }}>
          До бесплатной доставки: <b className="mono">{1500 - subtotal} ₽</b>
          <div style={{ height: 4, borderRadius: 2, background: 'rgba(0,0,0,0.08)', marginTop: 8 }}><div style={{ height: '100%', width: `${Math.min(100, subtotal / 15)}%`, background: 'var(--accent)', borderRadius: 2 }}/></div>
        </div>
      )}
      <div style={{ padding: '0 16px', display: 'flex', flexDirection: 'column', gap: 10 }}>
        {items.map(p => (
          <div key={p.id} style={{ display: 'flex', gap: 12, alignItems: 'center', padding: 10, background: 'var(--surface)', borderRadius: 14, border: '1px solid var(--line)' }}>
            <div style={{ width: 64, height: 64, flexShrink: 0 }} onClick={() => app.openProduct(p.id)}><ProductPlaceholder hue={p.hue} size={64} label=""/></div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 14, fontWeight: 500 }}>{p.name}</div>
              <div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 2 }}>{p.unit}</div>
              <div className="mono" style={{ fontSize: 14, fontWeight: 600, marginTop: 6 }}>{p.price * p.q} ₽</div>
            </div>
            <Stepper qty={p.q} onAdd={() => app.addToCart(p.id)} onSub={() => app.subFromCart(p.id)} compact/>
          </div>
        ))}
      </div>
      <div style={{ padding: '20px 20px 10px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', padding: '5px 0', fontSize: 14, color: 'var(--ink-2)' }}><span>Товары</span><span className="mono">{subtotal} ₽</span></div>
        <div style={{ display: 'flex', justifyContent: 'space-between', padding: '5px 0', fontSize: 14, color: 'var(--ink-2)' }}><span>Доставка</span><span className="mono">{delivery === 0 ? 'Бесплатно' : `${delivery} ₽`}</span></div>
        <div style={{ height: 1, background: 'var(--line)', margin: '10px 0' }}/>
        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 16, fontWeight: 600 }}><span>Итого</span><span className="mono">{total} ₽</span></div>
      </div>
      <div style={{ position: 'sticky', bottom: 0, padding: '12px 16px 14px', background: 'var(--bg)', borderTop: '1px solid var(--line)' }}>
        <button onClick={() => app.setRoute('checkout')} className="btn" style={{ width: '100%', height: 54, borderRadius: 16, border: 0, background: 'var(--ink)', color: 'var(--bg)', fontFamily: 'inherit', fontSize: 15, fontWeight: 500, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 20px' }}>
          <span>Оформить заказ</span><span className="mono">{total} ₽ →</span>
        </button>
      </div>
    </>
  );
}

// ─── CHECKOUT ───
function CheckoutScreen({ app }) {
  const items = Object.entries(app.cart).filter(([, q]) => q > 0).map(([id, q]) => ({ ...PRODUCTS.find(p => p.id === id), q }));
  const subtotal = items.reduce((s, x) => s + x.price * x.q, 0);
  const delivery = subtotal > 1500 ? 0 : 149;
  const total = subtotal + delivery;
  const [pay, setPay] = React.useState('card');
  const [slot, setSlot] = React.useState(0);
  const slots = ['Сегодня · 19:00–20:00', 'Сегодня · 20:30–21:30', 'Завтра · 08:00–09:00'];
  return (
    <>
      <div style={{ padding: '14px 20px 10px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <button onClick={() => app.setRoute('cart')} className="btn" style={{ border: 0, background: 'transparent', cursor: 'pointer', padding: 4 }}>{Icon.back()}</button>
        <div className="serif" style={{ fontSize: 22, fontWeight: 500, letterSpacing: '-0.02em' }}>Оформление</div>
      </div>
      <div style={{ padding: '0 16px', display: 'flex', flexDirection: 'column', gap: 20 }}>
        {/* address */}
        <div>
          <div style={{ fontSize: 11, color: 'var(--ink-3)', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600, marginBottom: 8 }}>Адрес доставки</div>
          <button onClick={() => app.setRoute('address')} className="btn" style={{ width: '100%', padding: 14, borderRadius: 14, border: '1px solid var(--line)', background: 'var(--surface)', display: 'flex', alignItems: 'center', gap: 12, cursor: 'pointer', fontFamily: 'inherit', textAlign: 'left' }}>
            <div style={{ width: 38, height: 38, borderRadius: 10, background: 'var(--accent-soft)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--accent-deep)', flexShrink: 0 }}>{Icon.pin()}</div>
            <div style={{ flex: 1 }}><div style={{ fontSize: 14, fontWeight: 500 }}>ул. Лесная, 14, кв. 47</div><div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 2 }}>Код домофона 47В</div></div>
            {Icon.chevron('var(--ink-3)')}
          </button>
        </div>
        {/* time */}
        <div>
          <div style={{ fontSize: 11, color: 'var(--ink-3)', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600, marginBottom: 8 }}>Время доставки</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {slots.map((s, i) => (
              <button key={i} onClick={() => setSlot(i)} className="btn" style={{ padding: '13px 16px', borderRadius: 12, border: `1.5px solid ${slot === i ? 'var(--ink)' : 'var(--line)'}`, background: 'var(--surface)', display: 'flex', alignItems: 'center', gap: 12, fontFamily: 'inherit', cursor: 'pointer', fontSize: 14, color: 'var(--ink)', textAlign: 'left' }}>
                <div style={{ width: 18, height: 18, borderRadius: 9, border: `2px solid ${slot === i ? 'var(--ink)' : 'var(--line-2)'}`, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>{slot === i && <div style={{ width: 8, height: 8, borderRadius: 4, background: 'var(--ink)' }}/>}</div>
                <span style={{ flex: 1 }}>{s}</span>
              </button>
            ))}
          </div>
        </div>
        {/* payment */}
        <div>
          <div style={{ fontSize: 11, color: 'var(--ink-3)', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600, marginBottom: 8 }}>Оплата</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {[['card','Карта •• 4821','Visa'], ['sbp','СБП','Система быстрых платежей'], ['cash','Наличными курьеру','Сдача с 3 000 ₽']].map(([id, lab, sub]) => (
              <button key={id} onClick={() => setPay(id)} className="btn" style={{ padding: '13px 16px', borderRadius: 12, border: `1.5px solid ${pay === id ? 'var(--ink)' : 'var(--line)'}`, background: 'var(--surface)', display: 'flex', alignItems: 'center', gap: 12, fontFamily: 'inherit', cursor: 'pointer', textAlign: 'left' }}>
                <div style={{ width: 18, height: 18, borderRadius: 9, border: `2px solid ${pay === id ? 'var(--ink)' : 'var(--line-2)'}`, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>{pay === id && <div style={{ width: 8, height: 8, borderRadius: 4, background: 'var(--ink)' }}/>}</div>
                <div><div style={{ fontSize: 14, fontWeight: 500, color: 'var(--ink)' }}>{lab}</div><div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 2 }}>{sub}</div></div>
              </button>
            ))}
          </div>
        </div>
        {/* summary */}
        <div style={{ paddingBottom: 90 }}>
          <div style={{ height: 1, background: 'var(--line)', marginBottom: 12 }}/>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 14, color: 'var(--ink-2)', marginBottom: 6 }}><span>Товары ({items.length})</span><span className="mono">{subtotal} ₽</span></div>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 14, color: 'var(--ink-2)', marginBottom: 10 }}><span>Доставка</span><span className="mono">{delivery === 0 ? 'Бесплатно' : `${delivery} ₽`}</span></div>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 16, fontWeight: 600 }}><span>К оплате</span><span className="mono">{total} ₽</span></div>
        </div>
      </div>
      <div style={{ position: 'sticky', bottom: 0, padding: '12px 16px 14px', background: 'var(--bg)', borderTop: '1px solid var(--line)' }}>
        <button onClick={() => app.placeOrder()} className="btn" style={{ width: '100%', height: 54, borderRadius: 16, border: 0, background: 'var(--ink)', color: 'var(--bg)', fontFamily: 'inherit', fontSize: 15, fontWeight: 500, cursor: 'pointer' }}>Оплатить {total} ₽</button>
      </div>
    </>
  );
}

// ─── TRACKING ───
function TrackingScreen({ app }) {
  const steps = [
    { t: 'Принят',   time: '19:02', done: true },
    { t: 'Собран',   time: '19:18', done: true },
    { t: 'В пути',   time: '19:24', done: true, active: true },
    { t: 'У дверей', time: '~19:55', done: false },
  ];
  return (
    <>
      <div style={{ height: 340, background: '#e8e4db', position: 'relative', overflow: 'hidden', flexShrink: 0 }}>
        <svg width="100%" height="100%" viewBox="0 0 374 340" preserveAspectRatio="xMidYMid slice">
          <defs><pattern id="gmap" width="28" height="28" patternUnits="userSpaceOnUse"><path d="M28 0H0V28" fill="none" stroke="#d6d2c6" strokeWidth="0.5"/></pattern></defs>
          <rect width="374" height="340" fill="url(#gmap)"/>
          <path d="M0 200 Q100 170 200 195 T374 180" stroke="#c8c2b2" strokeWidth="22" fill="none" opacity="0.55"/>
          <path d="M120 0 L120 200 L270 240 L270 340" stroke="#c8c2b2" strokeWidth="16" fill="none" opacity="0.4"/>
          <rect x="20" y="220" width="80" height="70" rx="8" fill="#c9dac1" opacity="0.65"/>
          <circle cx="60" cy="260" r="12" fill="#9ab38f" opacity="0.6"/>
          <path d="M90 290 Q150 250 200 230 T320 80" stroke="oklch(0.63 0.12 125)" strokeWidth="3" strokeDasharray="7 5" fill="none"/>
          <circle cx="200" cy="228" r="18" fill="oklch(0.63 0.12 125)" opacity="0.25"/>
          <circle cx="200" cy="228" r="11" fill="oklch(0.63 0.12 125)"/>
          <circle cx="200" cy="228" r="4" fill="#fff"/>
          <circle cx="320" cy="80" r="9" fill="#1a1a17"/>
          <circle cx="320" cy="80" r="16" fill="none" stroke="#1a1a17" strokeWidth="1.5"/>
        </svg>
        <button onClick={() => app.back()} className="btn" style={{ position: 'absolute', top: 16, left: 16, width: 40, height: 40, borderRadius: 20, background: 'rgba(255,255,255,0.92)', border: 0, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{Icon.back()}</button>
      </div>
      <div style={{ padding: '20px 20px 14px', background: 'var(--bg)', borderTopLeftRadius: 24, borderTopRightRadius: 24, marginTop: -20, position: 'relative', zIndex: 2 }}>
        <div style={{ width: 36, height: 4, borderRadius: 2, background: 'var(--line-2)', margin: '0 auto 18px' }}/>
        <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
          <div><div style={{ fontSize: 11, color: 'var(--accent-deep)', fontWeight: 700, letterSpacing: '0.08em', textTransform: 'uppercase' }}>В пути</div><div className="serif" style={{ fontSize: 24, fontWeight: 500, letterSpacing: '-0.02em', marginTop: 4 }}>Прибудет через 31 мин</div></div>
          <div className="mono" style={{ fontSize: 26, fontWeight: 600, color: 'var(--accent-deep)' }}>~19:55</div>
        </div>
        <div style={{ marginTop: 16, padding: 12, borderRadius: 14, background: 'var(--surface)', border: '1px solid var(--line)', display: 'flex', alignItems: 'center', gap: 12 }}>
          <div style={{ width: 42, height: 42, borderRadius: 21, background: 'var(--accent-soft)', color: 'var(--accent-deep)', fontWeight: 700, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>АП</div>
          <div style={{ flex: 1 }}><div style={{ fontSize: 14, fontWeight: 500 }}>Александр П.</div><div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 2 }}>Курьер · электровелосипед</div></div>
          <button className="btn" style={{ width: 40, height: 40, borderRadius: 20, border: '1px solid var(--line)', background: 'var(--surface)', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6"><path d="M3 5a2 2 0 0 1 2-2h3l2 5-3 2a14 14 0 0 0 7 7l2-3 5 2v3a2 2 0 0 1-2 2c-9 0-16-7-16-16Z"/></svg>
          </button>
        </div>
        <div style={{ marginTop: 20, display: 'flex', flexDirection: 'column', gap: 14 }}>
          {steps.map((s, i) => (
            <div key={i} style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <div style={{ width: 22, height: 22, borderRadius: 11, border: `2px solid ${s.done ? 'var(--accent)' : 'var(--line-2)'}`, background: s.done ? 'var(--accent)' : 'var(--surface)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{s.done && Icon.check('var(--accent-ink)', 12)}</div>
                {i < steps.length - 1 && <div style={{ width: 2, height: 18, background: s.done ? 'var(--accent)' : 'var(--line-2)', marginTop: 2 }}/>}
              </div>
              <div style={{ paddingBottom: 4 }}>
                <div style={{ fontSize: 14, fontWeight: s.active ? 600 : 500, color: 'var(--ink)' }}>{s.t}</div>
                <div className="mono" style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 1 }}>{s.time}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}

// ─── ADDRESS ───
function AddressScreen({ app }) {
  const ADDRESSES = window.ADDRESSES;
  return (
    <>
      <div style={{ padding: '14px 20px 10px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <button onClick={() => app.back()} className="btn" style={{ border: 0, background: 'transparent', cursor: 'pointer', padding: 4 }}>{Icon.back()}</button>
        <div className="serif" style={{ fontSize: 22, fontWeight: 500, letterSpacing: '-0.02em' }}>Адрес доставки</div>
      </div>
      <div style={{ margin: '8px 16px', height: 190, borderRadius: 16, overflow: 'hidden', background: '#e8e4db', position: 'relative' }}>
        <svg width="100%" height="100%" viewBox="0 0 358 190" preserveAspectRatio="xMidYMid slice">
          <defs><pattern id="g2" width="22" height="22" patternUnits="userSpaceOnUse"><path d="M22 0H0V22" stroke="#d6d2c6" strokeWidth="0.5" fill="none"/></pattern></defs>
          <rect width="358" height="190" fill="url(#g2)"/>
          <path d="M0 100 Q160 75 358 115" stroke="#c8c2b2" strokeWidth="18" fill="none" opacity="0.5"/>
          <path d="M180 0 L180 190" stroke="#c8c2b2" strokeWidth="12" fill="none" opacity="0.35"/>
          <rect x="22" y="115" width="85" height="55" rx="6" fill="#c9dac1" opacity="0.6"/>
        </svg>
        <div style={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -100%)' }}>
          <svg width="32" height="42" viewBox="0 0 32 42"><path d="M16 0a14 14 0 0 0-14 14c0 10 14 28 14 28s14-18 14-28A14 14 0 0 0 16 0Z" fill="#1a1a17"/><circle cx="16" cy="14" r="5" fill="#fff"/></svg>
        </div>
      </div>
      <div style={{ padding: '10px 16px' }}>
        <div style={{ padding: 14, borderRadius: 14, background: 'var(--surface)', border: '1px solid var(--line)' }}>
          <div style={{ fontSize: 16, fontWeight: 500 }}>ул. Лесная, 14</div>
          <div style={{ fontSize: 13, color: 'var(--ink-3)', marginTop: 4 }}>Москва · 101000</div>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, marginTop: 10 }}>
          {[['Квартира', '47'], ['Подъезд', '3'], ['Этаж', '6'], ['Домофон', '47В']].map(([k, v]) => (
            <div key={k} style={{ padding: 12, borderRadius: 12, background: 'var(--surface-2)' }}>
              <div style={{ fontSize: 11, color: 'var(--ink-3)' }}>{k}</div>
              <div style={{ fontSize: 15, fontWeight: 500, marginTop: 4 }}>{v}</div>
            </div>
          ))}
        </div>
        <div style={{ fontSize: 11, color: 'var(--ink-3)', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600, margin: '20px 0 10px' }}>Мои адреса</div>
        {ADDRESSES.map(a => (
          <div key={a.id} style={{ padding: 14, borderRadius: 12, background: 'var(--surface)', border: `1px solid ${a.primary ? 'var(--ink)' : 'var(--line)'}`, display: 'flex', gap: 12, alignItems: 'center', marginBottom: 8 }}>
            <div style={{ width: 36, height: 36, borderRadius: 10, background: 'var(--surface-2)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{Icon.pin()}</div>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 14, fontWeight: 500, display: 'flex', gap: 8, alignItems: 'center' }}>{a.label}{a.primary && <span style={{ fontSize: 10, padding: '2px 6px', borderRadius: 4, background: 'var(--ink)', color: 'var(--bg)', fontWeight: 700 }}>ОСНОВНОЙ</span>}</div>
              <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 2 }}>{a.text}</div>
            </div>
          </div>
        ))}
        <button className="btn" style={{ width: '100%', padding: '12px 16px', borderRadius: 12, border: '1px dashed var(--line-2)', background: 'transparent', color: 'var(--ink)', fontSize: 14, cursor: 'pointer', fontFamily: 'inherit', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6 }}>{Icon.plus('currentColor', 16)} Добавить адрес</button>
        <div style={{ height: 90 }}/>
      </div>
      <div style={{ position: 'sticky', bottom: 0, padding: '12px 16px 14px', background: 'var(--bg)', borderTop: '1px solid var(--line)' }}>
        <button onClick={() => app.back()} className="btn" style={{ width: '100%', height: 54, borderRadius: 16, border: 0, background: 'var(--ink)', color: 'var(--bg)', fontFamily: 'inherit', fontSize: 15, fontWeight: 500, cursor: 'pointer' }}>Сохранить</button>
      </div>
    </>
  );
}

// ─── FAVORITES ───
function FavScreen({ app }) {
  const list = PRODUCTS.filter(p => app.favs.has(p.id));
  return (
    <>
      <div style={{ padding: '14px 20px 4px' }}>
        <div className="serif" style={{ fontSize: 24, fontWeight: 500, letterSpacing: '-0.02em' }}>Избранное</div>
        <div style={{ fontSize: 13, color: 'var(--ink-3)', marginTop: 2 }}>{list.length} товаров</div>
      </div>
      {list.length === 0 ? (
        <div style={{ padding: 40, textAlign: 'center' }}>
          <div style={{ width: 80, height: 80, borderRadius: 40, background: 'var(--surface-2)', margin: '0 auto 16px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{Icon.heart('var(--ink-3)')}</div>
          <div style={{ fontSize: 14, color: 'var(--ink-2)', lineHeight: 1.5 }}>Нажимайте на сердечко в карточке, чтобы сохранять товары</div>
        </div>
      ) : (
        <div style={{ padding: '14px 16px 28px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          {list.map(p => <ProductCard key={p.id} p={p} qty={app.cart[p.id]||0} onAdd={() => app.addToCart(p.id)} onSub={() => app.subFromCart(p.id)} onOpen={() => app.openProduct(p.id)} onFav={() => app.toggleFav(p.id)} isFav/>)}
        </div>
      )}
      <div style={{ padding: '0 20px 28px' }}>
        <div style={{ fontSize: 11, color: 'var(--ink-3)', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600, marginBottom: 10 }}>Мои списки</div>
        {[['Еженедельная закупка', 12, 125], ['Завтраки', 6, 48], ['Гости в субботу', 18, 28]].map(([t, n, h], i) => (
          <button key={i} className="btn" style={{ width: '100%', padding: 12, marginBottom: 8, borderRadius: 12, background: 'var(--surface)', border: '1px solid var(--line)', display: 'flex', alignItems: 'center', gap: 12, cursor: 'pointer', fontFamily: 'inherit' }}>
            <div style={{ width: 38, height: 38, borderRadius: 10, background: `oklch(0.94 0.04 ${h})` }}/>
            <div style={{ flex: 1, textAlign: 'left' }}><div style={{ fontSize: 14, fontWeight: 500 }}>{t}</div><div style={{ fontSize: 11, color: 'var(--ink-3)' }}>{n} товаров</div></div>
            {Icon.chevron('var(--ink-3)')}
          </button>
        ))}
      </div>
    </>
  );
}

// ─── PROFILE ───
function ProfileScreen({ app }) {
  return (
    <>
      <div style={{ padding: '14px 20px 10px' }}><div className="serif" style={{ fontSize: 24, fontWeight: 500, letterSpacing: '-0.02em' }}>Профиль</div></div>
      <div style={{ padding: '0 16px' }}>
        <div style={{ padding: 16, borderRadius: 18, background: 'var(--surface)', border: '1px solid var(--line)', display: 'flex', alignItems: 'center', gap: 14, marginBottom: 10 }}>
          <div style={{ width: 54, height: 54, borderRadius: 27, background: 'var(--accent-soft)', color: 'var(--accent-deep)', fontSize: 20, fontWeight: 700, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>АР</div>
          <div style={{ flex: 1 }}><div style={{ fontSize: 17, fontWeight: 500 }}>Анна Р.</div><div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 2 }}>+7 916 204-18-47</div></div>
          <button className="btn" style={{ width: 34, height: 34, borderRadius: 17, border: '1px solid var(--line)', background: 'transparent', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"><path d="M4 20h4L19 9l-4-4L4 16z"/></svg>
          </button>
        </div>
        {/* loyalty card */}
        <div style={{ padding: 16, borderRadius: 18, background: 'var(--ink)', color: 'var(--bg)', marginBottom: 16 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div><div style={{ fontSize: 11, opacity: 0.55, textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Уровень · Серебро</div><div className="serif" style={{ fontSize: 24, fontWeight: 500, marginTop: 6, letterSpacing: '-0.02em' }}>1 248 баллов</div></div>
            <div style={{ padding: '5px 10px', borderRadius: 999, background: 'rgba(255,255,255,0.1)', fontSize: 11, fontWeight: 500 }}>Кешбек 3%</div>
          </div>
          <div style={{ marginTop: 14 }}><div style={{ fontSize: 11, opacity: 0.6 }}>До золота: 752 балла</div><div style={{ height: 4, borderRadius: 2, background: 'rgba(255,255,255,0.15)', marginTop: 6, overflow: 'hidden' }}><div style={{ width: '62%', height: '100%', background: 'var(--accent)', borderRadius: 2 }}/></div></div>
        </div>
        {/* menu sections */}
        {[
          [
            { label: 'Мои заказы', sub: '3 активных', route: 'orders', icon: () => Icon.bag() },
            { label: 'Адреса', sub: '2 сохранённых', route: 'address', icon: () => Icon.pin() },
            { label: 'Подписки', sub: 'Молоко · еженедельно', icon: () => Icon.repeat() },
          ],
          [
            { label: 'Промокоды', sub: '2 активных', icon: () => <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6"><path d="M20 12a2 2 0 0 0 0-4V4H4v4a2 2 0 0 1 0 4 2 2 0 0 1 0 4v4h16v-4a2 2 0 0 0 0-4Z"/></svg> },
            { label: 'Уведомления', sub: 'Вкл', icon: () => <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6"><path d="M6 8a6 6 0 1 1 12 0c0 7 3 9 3 9H3s3-2 3-9M10 21h4"/></svg> },
            { label: 'Поддержка', sub: 'Написать в чат', icon: () => <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg> },
          ],
        ].map((grp, gi) => (
          <div key={gi} style={{ marginBottom: 12, borderRadius: 16, background: 'var(--surface)', border: '1px solid var(--line)', overflow: 'hidden' }}>
            {grp.map((it, i) => (
              <button key={i} onClick={() => it.route && app.setRoute(it.route)} className="btn" style={{ width: '100%', padding: '13px 16px', border: 0, background: 'transparent', borderBottom: i < grp.length - 1 ? '1px solid var(--line)' : 0, display: 'flex', alignItems: 'center', gap: 14, cursor: 'pointer', fontFamily: 'inherit', color: 'var(--ink)', textAlign: 'left' }}>
                <div style={{ color: 'var(--ink-2)' }}>{it.icon()}</div>
                <div style={{ flex: 1 }}><div style={{ fontSize: 14, fontWeight: 500 }}>{it.label}</div><div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 1 }}>{it.sub}</div></div>
                {Icon.chevron('var(--ink-3)', 16)}
              </button>
            ))}
          </div>
        ))}
        <button className="btn" style={{ width: '100%', padding: 14, textAlign: 'center', background: 'transparent', border: 0, color: 'var(--danger)', fontSize: 13, cursor: 'pointer', fontFamily: 'inherit' }}>Выйти из аккаунта</button>
        <div style={{ height: 20 }}/>
      </div>
    </>
  );
}

// ─── ORDERS ───
function OrdersScreen({ app }) {
  return (
    <>
      <div style={{ padding: '14px 20px 10px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <button onClick={() => app.back()} className="btn" style={{ border: 0, background: 'transparent', cursor: 'pointer', padding: 4 }}>{Icon.back()}</button>
        <div className="serif" style={{ fontSize: 22, fontWeight: 500, letterSpacing: '-0.02em' }}>Мои заказы</div>
      </div>
      <div style={{ padding: '10px 16px 28px', display: 'flex', flexDirection: 'column', gap: 12 }}>
        {ORDERS.map(o => (
          <button key={o.id} onClick={() => app.setRoute('tracking')} className="btn" style={{ width: '100%', padding: 16, borderRadius: 16, background: 'var(--surface)', border: '1px solid var(--line)', cursor: 'pointer', fontFamily: 'inherit', textAlign: 'left' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div>
                <div style={{ fontSize: 13, color: 'var(--ink-3)' }}>{o.date} · {o.n}</div>
                <div style={{ fontSize: 15, fontWeight: 500, marginTop: 4 }}>{o.items} товаров</div>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 4 }}>
                <span style={{ fontSize: 11, padding: '3px 8px', borderRadius: 6, background: o.status === 'В пути' ? 'var(--accent-soft)' : 'var(--surface-2)', color: o.status === 'В пути' ? 'var(--accent-deep)' : 'var(--ink-3)', fontWeight: 600 }}>{o.status}</span>
                <span className="mono" style={{ fontSize: 14, fontWeight: 600, color: 'var(--ink)' }}>{o.total} ₽</span>
              </div>
            </div>
            {o.status === 'В пути' && (
              <div style={{ marginTop: 12, padding: '10px 12px', borderRadius: 10, background: 'var(--surface-2)', display: 'flex', alignItems: 'center', gap: 8, fontSize: 13, color: 'var(--ink-2)' }}>
                {Icon.delivery('var(--accent-deep)')} <span>Прибудет через <b>31 мин</b></span>
              </div>
            )}
          </button>
        ))}
      </div>
    </>
  );
}

// ─── RECIPES ───
function RecipesScreen({ app }) {
  return (
    <>
      <div style={{ padding: '14px 20px 10px' }}><div className="serif" style={{ fontSize: 24, fontWeight: 500, letterSpacing: '-0.02em' }}>Рецепты</div><div style={{ fontSize: 13, color: 'var(--ink-3)', marginTop: 2 }}>Ингредиенты в корзину в 1 клик</div></div>
      <div style={{ padding: '10px 16px 28px', display: 'flex', flexDirection: 'column', gap: 12 }}>
        {RECIPES.map(r => (
          <div key={r.id} style={{ borderRadius: 18, background: 'var(--surface)', border: '1px solid var(--line)', overflow: 'hidden' }}>
            <div style={{ height: 140 }}><ProductPlaceholder hue={r.hue} size={140} label="recipe"/></div>
            <div style={{ padding: '14px 16px 16px' }}>
              <div style={{ fontSize: 16, fontWeight: 500, letterSpacing: '-0.01em' }}>{r.title}</div>
              <div style={{ display: 'flex', gap: 14, marginTop: 6, fontSize: 12, color: 'var(--ink-3)' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>{Icon.clock('var(--ink-3)')} {r.time}</span>
                <span>{r.items} ингредиентов</span>
              </div>
              <button onClick={() => {
                PRODUCTS.slice(0, r.items > PRODUCTS.length ? PRODUCTS.length : r.items).forEach(p => app.addToCart(p.id));
                app.setRoute('cart');
              }} className="btn" style={{ marginTop: 12, width: '100%', height: 42, borderRadius: 12, border: 0, background: 'var(--ink)', color: 'var(--bg)', fontFamily: 'inherit', fontSize: 14, fontWeight: 500, cursor: 'pointer' }}>Добавить все в корзину</button>
            </div>
          </div>
        ))}
      </div>
    </>
  );
}

// ─── SUCCESS / CONFIRMATION ───
function SuccessScreen({ app }) {
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '40px 28px', textAlign: 'center' }}>
      <div style={{ width: 100, height: 100, borderRadius: 50, background: 'var(--accent-soft)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 24 }}>
        <div style={{ width: 60, height: 60, borderRadius: 30, background: 'var(--accent)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{Icon.check('var(--accent-ink)', 28)}</div>
      </div>
      <div className="serif" style={{ fontSize: 30, fontWeight: 500, letterSpacing: '-0.025em', lineHeight: 1.05 }}>Заказ принят!</div>
      <div style={{ fontSize: 15, color: 'var(--ink-2)', marginTop: 12, lineHeight: 1.55, maxWidth: 260 }}>Курьер соберёт его прямо сейчас. Ждём вас через 40 минут.</div>
      <button onClick={() => app.setRoute('tracking')} className="btn" style={{ marginTop: 28, width: '100%', height: 54, borderRadius: 16, border: 0, background: 'var(--ink)', color: 'var(--bg)', fontFamily: 'inherit', fontSize: 15, fontWeight: 500, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 10 }}>{Icon.delivery('currentColor')} Следить за заказом</button>
      <button onClick={() => app.setRoute('home')} className="btn" style={{ marginTop: 10, width: '100%', height: 48, borderRadius: 16, border: '1px solid var(--line)', background: 'transparent', color: 'var(--ink)', fontFamily: 'inherit', fontSize: 14, cursor: 'pointer' }}>На главную</button>
    </div>
  );
}

Object.assign(window, { OnboardingScreen, CartScreen, CheckoutScreen, TrackingScreen, AddressScreen, FavScreen, ProfileScreen, OrdersScreen, RecipesScreen, SuccessScreen });
