// screens-shop.jsx — Home variants, Catalog, Search, Product, Categories

// ─── Small shared bits ───
function SectionTitle({ children, action, onAction }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 20px', margin: '28px 0 14px' }}>
      <div className="serif" style={{ fontSize: 22, fontWeight: 500, letterSpacing: '-0.02em', color: 'var(--ink)' }}>{children}</div>
      {action && <button onClick={onAction} className="btn" style={{ border: 0, background: 'transparent', color: 'var(--ink-2)', fontSize: 13, cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 2, fontFamily: 'inherit' }}>{action}{Icon.chevron('currentColor', 14)}</button>}
    </div>
  );
}

function DeliveryBadge({ compact }) {
  return (
    <div style={{
      display: 'inline-flex', alignItems: 'center', gap: 6,
      padding: compact ? '4px 8px' : '6px 10px',
      background: 'var(--accent-soft)',
      color: 'var(--accent-deep)',
      borderRadius: 999, fontSize: 12, fontWeight: 500,
    }}>
      <span style={{ width: 6, height: 6, borderRadius: 3, background: 'var(--accent)' }}/>
      Доставим за 40 мин
    </div>
  );
}

// ─── Product card (grid cell) ───
function ProductCard({ p, qty, onAdd, onSub, onOpen, onFav, isFav }) {
  return (
    <div className="screen-fade" style={{
      background: 'var(--surface)', borderRadius: 18, padding: 12,
      border: '1px solid var(--line)',
      display: 'flex', flexDirection: 'column', gap: 8,
      position: 'relative',
    }}>
      <div style={{ position: 'relative' }} onClick={onOpen}>
        <ProductPlaceholder hue={p.hue} size={120} label={p.cat}/>
        <button onClick={(e) => { e.stopPropagation(); onFav(); }} className="btn" style={{
          position: 'absolute', top: 6, right: 6,
          width: 30, height: 30, borderRadius: 15, border: 0,
          background: 'rgba(255,255,255,0.9)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          cursor: 'pointer', backdropFilter: 'blur(8px)',
        }}>
          {Icon.heart(isFav ? 'var(--danger)' : 'var(--ink-2)', isFav ? 'var(--danger)' : 'none')}
        </button>
        {p.badge && (
          <div style={{
            position: 'absolute', left: 6, top: 6,
            padding: '3px 7px', borderRadius: 6,
            background: p.badge.startsWith('-') ? 'var(--danger)' : 'var(--ink)',
            color: '#fff', fontSize: 10, fontWeight: 700, letterSpacing: '0.02em',
            textTransform: 'uppercase',
          }}>{p.badge}</div>
        )}
      </div>
      <div onClick={onOpen} style={{ cursor: 'pointer' }}>
        <div style={{ fontSize: 13, fontWeight: 500, color: 'var(--ink)', lineHeight: 1.25 }}>{p.name}</div>
        <div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 2 }}>{p.subtitle}</div>
      </div>
      <div style={{ display: 'flex', alignItems: 'flex-end', justifyContent: 'space-between', marginTop: 'auto' }}>
        <div>
          <div className="mono" style={{ fontSize: 16, fontWeight: 600, color: 'var(--ink)' }}>{p.price} ₽</div>
          <div style={{ fontSize: 10, color: 'var(--ink-3)' }}>{p.unit}</div>
        </div>
        <Stepper qty={qty} onAdd={onAdd} onSub={onSub} compact/>
      </div>
    </div>
  );
}

// ─── Product list row (horizontal) ───
function ProductRow({ p, qty, onAdd, onSub, onOpen }) {
  return (
    <div className="screen-fade" style={{
      background: 'var(--surface)', borderRadius: 16, padding: 10,
      border: '1px solid var(--line)', display: 'flex', gap: 12, alignItems: 'center',
    }}>
      <div onClick={onOpen} style={{ width: 72, height: 72, flexShrink: 0 }}>
        <ProductPlaceholder hue={p.hue} size={72} label=""/>
      </div>
      <div style={{ flex: 1, minWidth: 0 }} onClick={onOpen}>
        <div style={{ fontSize: 14, fontWeight: 500, color: 'var(--ink)' }}>{p.name}</div>
        <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 2 }}>{p.subtitle} · {p.unit}</div>
        <div className="mono" style={{ fontSize: 15, fontWeight: 600, color: 'var(--ink)', marginTop: 6 }}>{p.price} ₽</div>
      </div>
      <Stepper qty={qty} onAdd={onAdd} onSub={onSub} compact/>
    </div>
  );
}

// ─── Promo hero ───
function PromoHero({ hue, badge, title, subtitle, cta, onClick }) {
  return (
    <div onClick={onClick} className="btn" style={{
      margin: '0 16px', borderRadius: 22,
      background: `oklch(0.93 0.04 ${hue})`,
      padding: 20, position: 'relative', overflow: 'hidden',
      cursor: 'pointer', minHeight: 150,
      display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
    }}>
      <svg style={{ position: 'absolute', right: -30, bottom: -30, opacity: 0.3 }} width="180" height="180" viewBox="0 0 180 180">
        <circle cx="90" cy="90" r="75" fill="none" stroke={`oklch(0.5 0.08 ${hue})`} strokeWidth="1"/>
        <circle cx="90" cy="90" r="55" fill="none" stroke={`oklch(0.5 0.08 ${hue})`} strokeWidth="1"/>
        <circle cx="90" cy="90" r="35" fill="none" stroke={`oklch(0.5 0.08 ${hue})`} strokeWidth="1"/>
      </svg>
      <div style={{ position: 'relative', zIndex: 1 }}>
        <div style={{ display: 'inline-block', padding: '3px 9px', background: 'var(--ink)', color: 'var(--bg)', borderRadius: 6, fontSize: 10, fontWeight: 700, letterSpacing: '0.04em', textTransform: 'uppercase' }}>{badge}</div>
        <div className="serif" style={{ fontSize: 26, fontWeight: 500, color: `oklch(0.2 0.05 ${hue})`, marginTop: 10, lineHeight: 1.1, letterSpacing: '-0.02em', maxWidth: 240 }}>{title}</div>
        <div style={{ fontSize: 13, color: `oklch(0.35 0.06 ${hue})`, marginTop: 6, maxWidth: 220 }}>{subtitle}</div>
      </div>
      <div style={{
        display: 'inline-flex', alignItems: 'center', gap: 6, alignSelf: 'flex-start',
        padding: '8px 14px', background: 'var(--ink)', color: 'var(--bg)',
        borderRadius: 999, fontSize: 13, fontWeight: 500, position: 'relative', zIndex: 1, marginTop: 14,
      }}>{cta}{Icon.chevron('currentColor', 14)}</div>
    </div>
  );
}

// ─── HOME (variant A – editorial, generous whitespace) ───
function HomeA({ app }) {
  const featured = PRODUCTS.slice(0, 6);
  const deals = PRODUCTS.filter(p => p.badge && p.badge.startsWith('-'));
  return (
    <>
      {/* Header */}
      <div style={{ padding: '14px 20px 8px' }}>
        <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
          <div>
            <div style={{ fontSize: 11, color: 'var(--ink-3)', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Доставка сюда</div>
            <button className="btn" onClick={() => app.setRoute('address')} style={{
              display: 'flex', alignItems: 'center', gap: 4, border: 0, background: 'transparent',
              padding: 0, marginTop: 2, cursor: 'pointer', color: 'var(--ink)', fontSize: 16, fontWeight: 500, fontFamily: 'inherit',
            }}>{Icon.pin()} ул. Лесная, 14 {Icon.chevron('var(--ink-3)', 16)}</button>
          </div>
          <button className="btn" onClick={() => app.setRoute('profile')} style={{
            width: 40, height: 40, borderRadius: 20, border: '1px solid var(--line)',
            background: 'var(--surface)', cursor: 'pointer',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>{Icon.user()}</button>
        </div>

        {/* Search */}
        <button onClick={() => app.setRoute('search')} className="btn" style={{
          marginTop: 14, width: '100%', height: 48, borderRadius: 14,
          background: 'var(--surface-2)', border: '1px solid transparent',
          display: 'flex', alignItems: 'center', gap: 10, padding: '0 14px',
          cursor: 'pointer', color: 'var(--ink-3)', fontSize: 14, fontFamily: 'inherit',
        }}>
          {Icon.search('var(--ink-3)')}
          <span style={{ flex: 1, textAlign: 'left' }}>Молоко, хлеб, яблоки…</span>
          <div style={{ width: 1, height: 18, background: 'var(--line-2)' }}/>
          <span style={{ color: 'var(--ink)', display: 'flex', alignItems: 'center' }} onClick={(e) => { e.stopPropagation(); app.setRoute('scan'); }}>{Icon.barcode('var(--ink)')}</span>
        </button>
      </div>

      {/* Hero promo */}
      <div style={{ marginTop: 14 }}>
        <PromoHero
          hue={125}
          badge="Сезон"
          title="Первая зелень — уже на грядке"
          subtitle="Подборка от тепличного хозяйства «Юг» — с доставкой сегодня"
          cta="Смотреть"
          onClick={() => app.setRoute('catalog')}
        />
      </div>

      {/* Categories strip */}
      <SectionTitle action="Все" onAction={() => app.setRoute('catalog')}>Категории</SectionTitle>
      <div className="chip-scroll" style={{ padding: '0 16px 4px' }}>
        {CATEGORIES.slice(1).map(c => (
          <button key={c.id} className="btn" onClick={() => { app.setCatFilter(c.id); app.setRoute('catalog'); }} style={{
            flexShrink: 0, border: 0, background: 'transparent', padding: 0, cursor: 'pointer',
            width: 76, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6,
          }}>
            <div style={{
              width: 64, height: 64, borderRadius: 18,
              background: `oklch(0.94 0.04 ${c.hue})`,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              <div style={{ width: 28, height: 28, borderRadius: '50%', background: `oklch(0.6 0.1 ${c.hue})`, opacity: 0.4 }}/>
            </div>
            <div style={{ fontSize: 12, color: 'var(--ink)', fontWeight: 500 }}>{c.label}</div>
          </button>
        ))}
      </div>

      {/* Featured grid */}
      <SectionTitle action="Все" onAction={() => app.setRoute('catalog')}>Рекомендуем</SectionTitle>
      <div style={{ padding: '0 16px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        {featured.map(p => (
          <ProductCard key={p.id} p={p}
            qty={app.cart[p.id] || 0}
            onAdd={() => app.addToCart(p.id)}
            onSub={() => app.subFromCart(p.id)}
            onOpen={() => app.openProduct(p.id)}
            onFav={() => app.toggleFav(p.id)}
            isFav={app.favs.has(p.id)}/>
        ))}
      </div>

      {/* Deals banner */}
      <SectionTitle>Скидки недели</SectionTitle>
      <div style={{ display: 'flex', gap: 10, overflowX: 'auto', padding: '0 16px 4px', scrollbarWidth: 'none' }}>
        {deals.map(p => (
          <div key={p.id} style={{ flexShrink: 0, width: 160 }}>
            <ProductCard p={p}
              qty={app.cart[p.id] || 0}
              onAdd={() => app.addToCart(p.id)}
              onSub={() => app.subFromCart(p.id)}
              onOpen={() => app.openProduct(p.id)}
              onFav={() => app.toggleFav(p.id)}
              isFav={app.favs.has(p.id)}/>
          </div>
        ))}
      </div>

      {/* Recipes */}
      <SectionTitle action="Все" onAction={() => app.setRoute('recipes')}>Рецепты недели</SectionTitle>
      <div style={{ padding: '0 16px 28px', display: 'flex', flexDirection: 'column', gap: 10 }}>
        {RECIPES.slice(0, 2).map(r => (
          <button key={r.id} onClick={() => app.setRoute('recipes')} className="btn" style={{
            display: 'flex', gap: 12, border: '1px solid var(--line)', borderRadius: 16,
            background: 'var(--surface)', padding: 10, cursor: 'pointer', fontFamily: 'inherit', textAlign: 'left',
          }}>
            <div style={{ width: 80, height: 80, flexShrink: 0 }}>
              <ProductPlaceholder hue={r.hue} size={80} label="recipe"/>
            </div>
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <div style={{ fontSize: 14, fontWeight: 500, color: 'var(--ink)', lineHeight: 1.3 }}>{r.title}</div>
                <div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 4, display: 'flex', gap: 8 }}>
                  <span style={{ display: 'inline-flex', alignItems: 'center', gap: 3 }}>{Icon.clock('var(--ink-3)')} {r.time}</span>
                  <span>·</span>
                  <span>{r.items} ингредиентов</span>
                </div>
              </div>
              <div style={{ display: 'inline-flex', alignItems: 'center', gap: 4, fontSize: 12, fontWeight: 500, color: 'var(--accent-deep)' }}>
                В корзину одной кнопкой {Icon.chevron('currentColor', 12)}
              </div>
            </div>
          </button>
        ))}
      </div>
    </>
  );
}

// ─── HOME (variant B – dense/compact – market-style) ───
function HomeB({ app }) {
  return (
    <>
      <div style={{ padding: '12px 16px 0' }}>
        <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
          <button onClick={() => app.setRoute('address')} className="btn" style={{
            flex: 1, height: 42, borderRadius: 12, border: '1px solid var(--line)',
            background: 'var(--surface)', padding: '0 12px', display: 'flex', alignItems: 'center', gap: 8,
            cursor: 'pointer', fontFamily: 'inherit',
          }}>
            {Icon.pin('var(--ink-2)')}
            <div style={{ textAlign: 'left', flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 10, color: 'var(--ink-3)' }}>40 мин · бесплатно</div>
              <div style={{ fontSize: 13, fontWeight: 500, color: 'var(--ink)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>ул. Лесная, 14</div>
            </div>
            {Icon.chevron('var(--ink-3)', 14)}
          </button>
          <button onClick={() => app.setRoute('profile')} className="btn" style={{ width: 42, height: 42, borderRadius: 12, border: '1px solid var(--line)', background: 'var(--surface)', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{Icon.user()}</button>
        </div>

        <button onClick={() => app.setRoute('search')} className="btn" style={{
          marginTop: 10, width: '100%', height: 44, borderRadius: 12,
          background: 'var(--surface-2)', border: 0,
          display: 'flex', alignItems: 'center', gap: 10, padding: '0 12px',
          cursor: 'pointer', color: 'var(--ink-3)', fontSize: 14, fontFamily: 'inherit',
        }}>
          {Icon.search('var(--ink-3)')}<span style={{ flex: 1, textAlign: 'left' }}>Найти что угодно</span>{Icon.barcode('var(--ink)')}
        </button>
      </div>

      {/* Dense category grid */}
      <div style={{ padding: '14px 16px 0', display: 'grid', gridTemplateColumns: '1fr 1fr 1fr 1fr', gap: 8 }}>
        {CATEGORIES.slice(1).map(c => (
          <button key={c.id} className="btn" onClick={() => { app.setCatFilter(c.id); app.setRoute('catalog'); }} style={{
            border: 0, background: `oklch(0.95 0.035 ${c.hue})`, borderRadius: 12, padding: '10px 6px',
            cursor: 'pointer', display: 'flex', flexDirection: 'column', gap: 6, alignItems: 'center',
            fontFamily: 'inherit',
          }}>
            <div style={{ width: 30, height: 30, borderRadius: 8, background: `oklch(0.6 0.1 ${c.hue})`, opacity: 0.4 }}/>
            <div style={{ fontSize: 11, color: `oklch(0.25 0.07 ${c.hue})`, fontWeight: 500 }}>{c.label}</div>
          </button>
        ))}
      </div>

      {/* Quick promo strip */}
      <div style={{ display: 'flex', gap: 10, padding: '16px 16px 0', overflowX: 'auto', scrollbarWidth: 'none' }}>
        {[
          { hue: 125, badge: '-20%', t: 'Зелень и салаты' },
          { hue: 28,  badge: 'Новое', t: 'Хлеб на закваске' },
          { hue: 48,  badge: 'Хит',   t: 'Молочная полка' },
        ].map((x, i) => (
          <div key={i} style={{
            flexShrink: 0, width: 180, height: 100, borderRadius: 14,
            background: `oklch(0.93 0.04 ${x.hue})`, padding: 14, position: 'relative',
            display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
          }}>
            <div style={{ fontSize: 10, fontWeight: 700, letterSpacing: '0.04em', textTransform: 'uppercase', color: `oklch(0.35 0.08 ${x.hue})` }}>{x.badge}</div>
            <div className="serif" style={{ fontSize: 17, fontWeight: 500, color: `oklch(0.2 0.05 ${x.hue})`, lineHeight: 1.1, letterSpacing: '-0.01em' }}>{x.t}</div>
          </div>
        ))}
      </div>

      {/* Popular - 3-column dense grid */}
      <SectionTitle action="Все" onAction={() => app.setRoute('catalog')}>Популярное</SectionTitle>
      <div style={{ padding: '0 16px 28px', display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8 }}>
        {PRODUCTS.slice(0, 9).map(p => (
          <div key={p.id} style={{ background: 'var(--surface)', borderRadius: 14, padding: 8, border: '1px solid var(--line)', display: 'flex', flexDirection: 'column', gap: 6 }}>
            <div onClick={() => app.openProduct(p.id)}><ProductPlaceholder hue={p.hue} size={70} label=""/></div>
            <div className="mono" style={{ fontSize: 13, fontWeight: 600, color: 'var(--ink)' }}>{p.price} ₽</div>
            <div style={{ fontSize: 11, color: 'var(--ink)', lineHeight: 1.2, flex: 1 }}>{p.name}</div>
            <Stepper qty={app.cart[p.id] || 0} onAdd={() => app.addToCart(p.id)} onSub={() => app.subFromCart(p.id)} compact/>
          </div>
        ))}
      </div>
    </>
  );
}

// ─── HOME (variant C – editorial magazine) ───
function HomeC({ app }) {
  const hero = PRODUCTS[4];
  return (
    <>
      {/* Minimal header */}
      <div style={{ padding: '14px 20px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Logo size={22}/>
        <div style={{ display: 'flex', gap: 8 }}>
          <button onClick={() => app.setRoute('search')} className="btn" style={{ width: 38, height: 38, borderRadius: 19, border: '1px solid var(--line)', background: 'var(--surface)', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{Icon.search()}</button>
          <button onClick={() => app.setRoute('profile')} className="btn" style={{ width: 38, height: 38, borderRadius: 19, border: '1px solid var(--line)', background: 'var(--surface)', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{Icon.user()}</button>
        </div>
      </div>

      {/* Editorial hero */}
      <div style={{ padding: '28px 20px 10px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 11, color: 'var(--ink-3)', textTransform: 'uppercase', letterSpacing: '0.1em', fontWeight: 600 }}>
          <span style={{ width: 20, height: 1, background: 'var(--ink-3)' }}/>
          Сезон №14 · Апрель
        </div>
        <div className="serif" style={{ fontSize: 38, fontWeight: 400, color: 'var(--ink)', lineHeight: 1.0, letterSpacing: '-0.03em', marginTop: 12 }}>
          С грядки <i style={{ color: 'var(--accent-deep)' }}>прямо</i><br/>на стол — за 40&nbsp;минут.
        </div>
        <div style={{ fontSize: 14, color: 'var(--ink-2)', marginTop: 14, lineHeight: 1.5, maxWidth: 300 }}>
          Отбираем у 32 фермеров средней полосы. Без холодных складов — только то, что собрано сегодня.
        </div>
        <button onClick={() => app.setRoute('address')} className="btn" style={{
          marginTop: 18, display: 'inline-flex', alignItems: 'center', gap: 8,
          padding: '12px 20px', borderRadius: 999,
          background: 'var(--ink)', color: 'var(--bg)', border: 0,
          fontFamily: 'inherit', fontSize: 14, fontWeight: 500, cursor: 'pointer',
        }}>{Icon.pin('currentColor')} Доставить на Лесную, 14</button>
      </div>

      {/* Featured product spread */}
      <div onClick={() => app.openProduct(hero.id)} style={{
        margin: '28px 20px 0', borderRadius: 20, overflow: 'hidden', cursor: 'pointer',
        background: `oklch(0.94 0.04 ${hero.hue})`, padding: 20,
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
          <div>
            <div style={{ fontSize: 10, fontWeight: 700, letterSpacing: '0.08em', textTransform: 'uppercase', color: `oklch(0.35 0.08 ${hero.hue})` }}>Фермер недели</div>
            <div className="serif" style={{ fontSize: 22, fontWeight: 500, color: `oklch(0.2 0.05 ${hero.hue})`, marginTop: 6, letterSpacing: '-0.02em', lineHeight: 1.1 }}>Теплицы Юга<br/>Бакинский розовый</div>
          </div>
          <div className="mono" style={{ fontSize: 24, fontWeight: 600, color: `oklch(0.2 0.05 ${hero.hue})` }}>{hero.price}<span style={{ fontSize: 14, marginLeft: 2 }}>₽</span></div>
        </div>
        <div style={{ height: 140, borderRadius: 14, overflow: 'hidden' }}>
          <ProductPlaceholder hue={hero.hue} size={140} label="pomidor · baku"/>
        </div>
      </div>

      <SectionTitle action="Весь каталог" onAction={() => app.setRoute('catalog')}>Сегодня на грядке</SectionTitle>
      <div style={{ padding: '0 16px 28px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        {PRODUCTS.slice(5, 9).map(p => (
          <ProductCard key={p.id} p={p}
            qty={app.cart[p.id] || 0}
            onAdd={() => app.addToCart(p.id)}
            onSub={() => app.subFromCart(p.id)}
            onOpen={() => app.openProduct(p.id)}
            onFav={() => app.toggleFav(p.id)}
            isFav={app.favs.has(p.id)}/>
        ))}
      </div>
    </>
  );
}

// ─── CATALOG ───
function CatalogScreen({ app }) {
  const active = app.catFilter;
  const list = active === 'all' ? PRODUCTS : PRODUCTS.filter(p => p.cat === active);
  return (
    <>
      <div style={{ padding: '14px 20px 10px', background: 'var(--surface)', borderBottom: '1px solid var(--line)', position: 'sticky', top: 0, zIndex: 5 }}>
        <div className="serif" style={{ fontSize: 24, fontWeight: 500, letterSpacing: '-0.02em' }}>Каталог</div>
      </div>
      <div className="chip-scroll" style={{ padding: '14px 16px 4px', background: 'var(--surface)', borderBottom: '1px solid var(--line)' }}>
        {CATEGORIES.map(c => (
          <Chip key={c.id} active={active === c.id} onClick={() => app.setCatFilter(c.id)}>{c.label}</Chip>
        ))}
      </div>
      <div style={{ padding: '14px 16px 28px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        {list.map(p => (
          <ProductCard key={p.id} p={p}
            qty={app.cart[p.id] || 0}
            onAdd={() => app.addToCart(p.id)}
            onSub={() => app.subFromCart(p.id)}
            onOpen={() => app.openProduct(p.id)}
            onFav={() => app.toggleFav(p.id)}
            isFav={app.favs.has(p.id)}/>
        ))}
      </div>
    </>
  );
}

// ─── SEARCH ───
function SearchScreen({ app }) {
  const [q, setQ] = React.useState('');
  const results = q.length > 0 ? PRODUCTS.filter(p => p.name.toLowerCase().includes(q.toLowerCase())) : [];
  const suggestions = ['Молоко', 'Яблоки', 'Хлеб ржаной', 'Авокадо', 'Лосось'];
  const recent = ['творог', 'базилик', 'говядина'];
  return (
    <>
      <div style={{ padding: '14px 16px 10px', display: 'flex', gap: 10, alignItems: 'center' }}>
        <button onClick={() => app.setRoute('home')} className="btn" style={{ border: 0, background: 'transparent', cursor: 'pointer', padding: 4 }}>{Icon.back()}</button>
        <div style={{ flex: 1, height: 44, borderRadius: 12, background: 'var(--surface-2)', display: 'flex', alignItems: 'center', gap: 10, padding: '0 12px' }}>
          {Icon.search('var(--ink-3)')}
          <input autoFocus value={q} onChange={e => setQ(e.target.value)} placeholder="Молоко, хлеб, яблоки…" style={{
            flex: 1, border: 0, outline: 0, background: 'transparent', fontSize: 15,
            fontFamily: 'inherit', color: 'var(--ink)',
          }}/>
          {q && <button onClick={() => setQ('')} style={{ border: 0, background: 'transparent', cursor: 'pointer', padding: 0 }}>{Icon.close('var(--ink-3)')}</button>}
          {!q && Icon.mic('var(--ink-3)')}
        </div>
      </div>

      {q.length === 0 ? (
        <div style={{ padding: '10px 20px' }}>
          <div style={{ fontSize: 11, color: 'var(--ink-3)', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600, marginBottom: 10, marginTop: 8 }}>Недавно искали</div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
            {recent.map(r => (
              <button key={r} onClick={() => setQ(r)} className="btn" style={{
                border: '1px solid var(--line)', background: 'var(--surface)', borderRadius: 999,
                padding: '7px 12px', fontSize: 13, color: 'var(--ink-2)', cursor: 'pointer', fontFamily: 'inherit',
              }}>{r}</button>
            ))}
          </div>
          <div style={{ fontSize: 11, color: 'var(--ink-3)', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600, margin: '28px 0 10px' }}>Популярные запросы</div>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            {suggestions.map(s => (
              <button key={s} onClick={() => setQ(s)} className="btn" style={{
                padding: '14px 0', textAlign: 'left', border: 0, borderBottom: '1px solid var(--line)',
                background: 'transparent', fontSize: 15, color: 'var(--ink)', cursor: 'pointer',
                display: 'flex', alignItems: 'center', gap: 12, fontFamily: 'inherit',
              }}>
                {Icon.search('var(--ink-3)')}<span style={{ flex: 1 }}>{s}</span>{Icon.chevron('var(--ink-3)', 14)}
              </button>
            ))}
          </div>
        </div>
      ) : (
        <div style={{ padding: '10px 16px 28px', display: 'flex', flexDirection: 'column', gap: 10 }}>
          <div style={{ fontSize: 13, color: 'var(--ink-3)', padding: '0 4px' }}>Найдено: {results.length}</div>
          {results.map(p => (
            <ProductRow key={p.id} p={p}
              qty={app.cart[p.id] || 0}
              onAdd={() => app.addToCart(p.id)}
              onSub={() => app.subFromCart(p.id)}
              onOpen={() => app.openProduct(p.id)}/>
          ))}
          {results.length === 0 && (
            <div style={{ padding: 40, textAlign: 'center', color: 'var(--ink-3)' }}>Ничего не нашли. Попробуйте другой запрос.</div>
          )}
        </div>
      )}
    </>
  );
}

// ─── PRODUCT ───
function ProductScreen({ app }) {
  const p = PRODUCTS.find(x => x.id === app.productId);
  if (!p) return null;
  const qty = app.cart[p.id] || 0;
  const isFav = app.favs.has(p.id);
  return (
    <>
      <div style={{ padding: '12px 16px', display: 'flex', justifyContent: 'space-between', position: 'absolute', top: 0, left: 0, right: 0, zIndex: 5 }}>
        <button onClick={() => app.back()} className="btn" style={{ width: 40, height: 40, borderRadius: 20, background: 'rgba(255,255,255,0.85)', border: 0, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', backdropFilter: 'blur(8px)' }}>{Icon.back()}</button>
        <div style={{ display: 'flex', gap: 8 }}>
          <button onClick={() => app.toggleFav(p.id)} className="btn" style={{ width: 40, height: 40, borderRadius: 20, background: 'rgba(255,255,255,0.85)', border: 0, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', backdropFilter: 'blur(8px)' }}>{Icon.heart(isFav ? 'var(--danger)' : 'var(--ink)', isFav ? 'var(--danger)' : 'none')}</button>
          <button className="btn" style={{ width: 40, height: 40, borderRadius: 20, background: 'rgba(255,255,255,0.85)', border: 0, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', backdropFilter: 'blur(8px)' }}>{Icon.more()}</button>
        </div>
      </div>

      <div style={{ height: 340, background: `oklch(0.94 0.04 ${p.hue})`, display: 'flex', alignItems: 'center', justifyContent: 'center', position: 'relative' }}>
        <div style={{ width: 220, height: 220 }}>
          <ProductPlaceholder hue={p.hue} size={220} label={p.cat}/>
        </div>
        {/* dots */}
        <div style={{ position: 'absolute', bottom: 18, left: '50%', transform: 'translateX(-50%)', display: 'flex', gap: 6 }}>
          {[0,1,2].map(i => <div key={i} style={{ width: i===0?24:6, height: 6, borderRadius: 3, background: i===0 ? 'var(--ink)' : 'rgba(0,0,0,0.2)' }}/>)}
        </div>
      </div>

      <div style={{ padding: '22px 20px 10px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
          {Icon.leaf('var(--accent-deep)', 16)}
          <div style={{ fontSize: 12, color: 'var(--accent-deep)', fontWeight: 500 }}>{p.farm}</div>
        </div>
        <div className="serif" style={{ fontSize: 28, fontWeight: 500, letterSpacing: '-0.02em', color: 'var(--ink)', marginTop: 8, lineHeight: 1.15 }}>{p.name}</div>
        <div style={{ fontSize: 14, color: 'var(--ink-2)', marginTop: 4 }}>{p.subtitle}</div>

        <div style={{ display: 'flex', alignItems: 'baseline', gap: 10, marginTop: 18 }}>
          <div className="mono" style={{ fontSize: 30, fontWeight: 600, color: 'var(--ink)' }}>{p.price} ₽</div>
          <div style={{ fontSize: 13, color: 'var(--ink-3)' }}>/ {p.unit}</div>
        </div>

        {/* meta */}
        <div style={{ marginTop: 22, borderRadius: 14, background: 'var(--surface-2)', padding: 4, display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 1 }}>
          {[
            { t: 'Свежесть', v: 'Сегодня' },
            { t: 'Страна',   v: 'Россия' },
            { t: 'Хранить',  v: '+4°C' },
          ].map((m, i) => (
            <div key={i} style={{ padding: '12px 10px', background: 'var(--surface-2)', textAlign: 'center' }}>
              <div style={{ fontSize: 10, color: 'var(--ink-3)', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{m.t}</div>
              <div style={{ fontSize: 13, color: 'var(--ink)', fontWeight: 500, marginTop: 4 }}>{m.v}</div>
            </div>
          ))}
        </div>

        <div style={{ fontSize: 11, color: 'var(--ink-3)', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600, marginTop: 24 }}>Описание</div>
        <div style={{ fontSize: 14, color: 'var(--ink-2)', marginTop: 8, lineHeight: 1.55 }}>
          Собрано сегодня утром. Без ГМО и химических удобрений. Хранится до 5 дней при температуре от 0 до +6°C.
        </div>

        {/* Subscribe */}
        <div style={{ marginTop: 22, padding: 14, borderRadius: 14, border: '1px dashed var(--line-2)', display: 'flex', gap: 10, alignItems: 'center' }}>
          {Icon.repeat('var(--accent-deep)')}
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 13, fontWeight: 500, color: 'var(--ink)' }}>Подписаться на регулярную доставку</div>
            <div style={{ fontSize: 11, color: 'var(--ink-3)', marginTop: 2 }}>Еженедельно · скидка 10%</div>
          </div>
          <div style={{ width: 36, height: 22, borderRadius: 11, background: 'var(--surface-3)', padding: 2, flexShrink: 0 }}>
            <div style={{ width: 18, height: 18, borderRadius: 9, background: 'var(--surface)' }}/>
          </div>
        </div>
      </div>

      {/* Bottom action bar */}
      <div style={{ position: 'sticky', bottom: 0, padding: '14px 16px', background: 'var(--bg)', borderTop: '1px solid var(--line)', display: 'flex', gap: 10 }}>
        {qty === 0 ? (
          <button onClick={() => app.addToCart(p.id)} className="btn" style={{
            flex: 1, height: 54, borderRadius: 16, border: 0,
            background: 'var(--ink)', color: 'var(--bg)', fontFamily: 'inherit',
            fontSize: 15, fontWeight: 500, cursor: 'pointer',
            display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 10,
          }}>{Icon.plus('currentColor', 18)} В корзину · {p.price} ₽</button>
        ) : (
          <>
            <div style={{
              flex: 1, height: 54, borderRadius: 16, background: 'var(--ink)', color: 'var(--bg)',
              display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 6px',
            }}>
              <button onClick={() => app.subFromCart(p.id)} className="btn" style={{ width: 42, height: 42, border: 0, background: 'transparent', color: 'inherit', borderRadius: 10, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{Icon.minus('currentColor', 20)}</button>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: 11, opacity: 0.6 }}>В корзине</div>
                <div className="mono" style={{ fontSize: 15, fontWeight: 600 }}>{qty} · {qty * p.price} ₽</div>
              </div>
              <button onClick={() => app.addToCart(p.id)} className="btn" style={{ width: 42, height: 42, border: 0, background: 'transparent', color: 'inherit', borderRadius: 10, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{Icon.plus('currentColor', 20)}</button>
            </div>
            <button onClick={() => app.setRoute('cart')} className="btn" style={{
              width: 54, height: 54, borderRadius: 16, border: 0,
              background: 'var(--accent)', color: 'var(--accent-ink)', cursor: 'pointer',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>{Icon.bag('currentColor')}</button>
          </>
        )}
      </div>
    </>
  );
}

Object.assign(window, { HomeA, HomeB, HomeC, CatalogScreen, SearchScreen, ProductScreen, ProductCard, ProductRow, SectionTitle, DeliveryBadge, PromoHero });
