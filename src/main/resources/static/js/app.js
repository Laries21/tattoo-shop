/* ============================================================
   Clarity Tattoo — app.js
   ============================================================ */

document.addEventListener('DOMContentLoaded', function () {

  // ── 1. Custom Cursor — Tattoo Needle ─────────────────────
  var needle = document.createElement('div');
  var dot    = document.createElement('div');
  var ring   = document.createElement('div');
  needle.className = 'cursor-needle';
  dot.className    = 'cursor-dot';
  ring.className   = 'cursor-ring';

  // Build SVG needle without template literals (max compatibility)
  var svgNS = 'http://www.w3.org/2000/svg';
  var svg = document.createElementNS(svgNS, 'svg');
  svg.setAttribute('width', '28'); svg.setAttribute('height', '28');
  svg.setAttribute('viewBox', '0 0 28 28');
  var g = document.createElementNS(svgNS, 'g');
  g.setAttribute('transform', 'rotate(-45 14 14)');

  function mkRect(x,y,w,h,rx,fill,stroke,sw) {
    var r = document.createElementNS(svgNS,'rect');
    r.setAttribute('x',x); r.setAttribute('y',y);
    r.setAttribute('width',w); r.setAttribute('height',h);
    r.setAttribute('rx',rx); r.setAttribute('fill',fill);
    r.setAttribute('stroke',stroke); r.setAttribute('stroke-width',sw);
    return r;
  }
  function mkLine(x1,y1,x2,y2,stroke,sw,op) {
    var l = document.createElementNS(svgNS,'line');
    l.setAttribute('x1',x1); l.setAttribute('y1',y1);
    l.setAttribute('x2',x2); l.setAttribute('y2',y2);
    l.setAttribute('stroke',stroke); l.setAttribute('stroke-width',sw);
    l.setAttribute('opacity',op); return l;
  }
  function mkPoly(pts,fill) {
    var p = document.createElementNS(svgNS,'polygon');
    p.setAttribute('points',pts); p.setAttribute('fill',fill); return p;
  }
  function mkCircle(cx,cy,r,fill) {
    var c = document.createElementNS(svgNS,'circle');
    c.setAttribute('cx',cx); c.setAttribute('cy',cy);
    c.setAttribute('r',r); c.setAttribute('fill',fill); return c;
  }

  g.appendChild(mkRect(11,1,6,9,2,'#1e293b','#e11d48',1.2));
  g.appendChild(mkLine(11,4,17,4,'#e11d48',0.7,0.7));
  g.appendChild(mkLine(11,6,17,6,'#e11d48',0.7,0.7));
  g.appendChild(mkLine(11,8,17,8,'#e11d48',0.7,0.7));
  g.appendChild(mkRect(12,10,4,11,1,'#0f172a','#e11d48',1));
  g.appendChild(mkPoly('12,21 16,21 14,27','#e11d48'));
  g.appendChild(mkCircle(14,27,1.2,'#e11d48'));
  svg.appendChild(g);
  needle.appendChild(svg);

  document.body.appendChild(needle);
  document.body.appendChild(dot);
  document.body.appendChild(ring);

  var mouseX = 0, mouseY = 0, dotX = 0, dotY = 0, ringX = 0, ringY = 0;

  document.addEventListener('mousemove', function (e) {
    mouseX = e.clientX; mouseY = e.clientY;
    needle.style.left = (mouseX - 4) + 'px';
    needle.style.top  = (mouseY - 4) + 'px';
  });

  (function animateCursor() {
    dotX  += (mouseX - dotX)  * 0.25;
    dotY  += (mouseY - dotY)  * 0.25;
    ringX += (mouseX - ringX) * 0.12;
    ringY += (mouseY - ringY) * 0.12;
    dot.style.left  = dotX  + 'px'; dot.style.top  = dotY  + 'px';
    ring.style.left = ringX + 'px'; ring.style.top = ringY + 'px';
    requestAnimationFrame(animateCursor);
  })();

  function addHover(el) {
    el.addEventListener('mouseenter', function () {
      needle.classList.add('hovered'); dot.classList.add('hovered'); ring.classList.add('hovered');
    });
    el.addEventListener('mouseleave', function () {
      needle.classList.remove('hovered'); dot.classList.remove('hovered'); ring.classList.remove('hovered');
    });
  }
  document.querySelectorAll('a, button, .btn, .card, .service-item, input, textarea, select').forEach(addHover);

  // ── 2. Scroll Progress Bar ────────────────────────────────
  var progressBar = document.createElement('div');
  progressBar.className = 'scroll-progress';
  document.body.prepend(progressBar);
  window.addEventListener('scroll', function () {
    var pct = window.scrollY / (document.documentElement.scrollHeight - window.innerHeight) * 100;
    progressBar.style.width = Math.min(pct, 100) + '%';
  });

  // ── 3. Floating tattoo symbols ────────────────────────────
  var floatContainer = document.querySelector('.bg-floats');
  if (floatContainer) {
    var symbols = ['✦','⚡','◈','✧','◆','⬡','✒','⚔','⬟','◇','✴','⚜'];
    for (var i = 0; i < 18; i++) {
      var icon = document.createElement('div');
      icon.className = 'float-icon';
      icon.textContent = symbols[i % symbols.length];
      icon.style.left = (Math.random() * 100) + 'vw';
      icon.style.animationDuration = (14 + Math.random() * 18) + 's';
      icon.style.animationDelay    = (Math.random() * 12) + 's';
      icon.style.fontSize = (0.8 + Math.random() * 1.2) + 'rem';
      floatContainer.appendChild(icon);
    }
  }

  // ── 3b. Glowing orbs ─────────────────────────────────────
  var orbs = [{w:400,h:400,top:'10%',left:'-5%',dur:'8s'},{w:300,h:300,top:'60%',right:'-3%',dur:'11s'},{w:200,h:200,top:'30%',left:'50%',dur:'9s'}];
  orbs.forEach(function(o) {
    var orb = document.createElement('div');
    orb.className = 'glow-orb';
    orb.style.width = o.w+'px'; orb.style.height = o.h+'px'; orb.style.top = o.top;
    if (o.left)  orb.style.left  = o.left;
    if (o.right) orb.style.right = o.right;
    orb.style.animationDuration = o.dur;
    orb.style.animationDelay = (Math.random()*4)+'s';
    document.body.appendChild(orb);
  });

  // ── 3c. Needle trace lines ────────────────────────────────
  for (var t = 0; t < 3; t++) {
    var trace = document.createElement('div');
    trace.className = 'needle-trace';
    trace.style.height = (60 + Math.random()*120)+'px';
    trace.style.top    = (20 + Math.random()*60)+'%';
    trace.style.animationDuration = (12 + Math.random()*10)+'s';
    trace.style.animationDelay    = (Math.random()*8)+'s';
    document.body.appendChild(trace);
  }

  // ── 4. Hero Particles ─────────────────────────────────────
  var heroParticles = document.querySelector('.hero-particles');
  if (heroParticles) {
    for (var p = 0; p < 25; p++) {
      var particle = document.createElement('div');
      particle.className = 'particle';
      particle.style.left = (Math.random()*100)+'%';
      particle.style.bottom = '0';
      particle.style.animationDuration = (4 + Math.random()*8)+'s';
      particle.style.animationDelay    = (Math.random()*10)+'s';
      var sz = (1 + Math.random()*3)+'px';
      particle.style.width = sz; particle.style.height = sz;
      heroParticles.appendChild(particle);
    }
  }

  // ── 5. Scroll Reveal ──────────────────────────────────────
  // Add reveal class to grid cards first
  document.querySelectorAll('.grid-cards .card:not(.reveal)').forEach(function(card, i) {
    card.classList.add('reveal');
    card.style.transitionDelay = (i * 60) + 'ms';
  });

  var revealObs = new IntersectionObserver(function(entries) {
    entries.forEach(function(entry) {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
        revealObs.unobserve(entry.target);
      }
    });
  }, { threshold: 0.08 });

  document.querySelectorAll('.reveal').forEach(function(el) { revealObs.observe(el); });

  // ── 6. Hamburger Menu ─────────────────────────────────────
  var hamburger = document.querySelector('.hamburger');
  var navLinks  = document.querySelector('.nav-links');
  if (hamburger && navLinks) {
    hamburger.addEventListener('click', function() { navLinks.classList.toggle('nav-open'); });
    navLinks.querySelectorAll('a').forEach(function(a) {
      a.addEventListener('click', function() { navLinks.classList.remove('nav-open'); });
    });
  }

  // ── 7. Phone Validation ───────────────────────────────────
  document.querySelectorAll('input[name="phone"]').forEach(function(input) {
    var err = document.createElement('span');
    err.className = 'form-error'; err.style.display = 'none';
    err.textContent = 'Phone number must be exactly 10 digits.';
    input.parentNode.appendChild(err);
    input.addEventListener('input', function() {
      input.value = input.value.replace(/[^0-9]/g,'');
      var ok = /^\d{10}$/.test(input.value);
      err.style.display = (input.value.length > 0 && !ok) ? 'block' : 'none';
      input.setCustomValidity(ok || input.value.length === 0 ? '' : 'Must be 10 digits');
    });
    var form = input.closest('form');
    if (form) form.addEventListener('submit', function(e) {
      if (!/^\d{10}$/.test(input.value)) { e.preventDefault(); err.style.display='block'; input.focus(); }
    });
  });

  // ── 8. Custom Delete Confirmation Modal ──────────────────
  function showConfirm(message, onYes) {
    var overlay = document.createElement('div');
    overlay.className = 'confirm-overlay';
    overlay.innerHTML =
      '<div class="confirm-box">' +
        '<h3>⚠️ Confirm Delete</h3>' +
        '<p>' + message + '</p>' +
        '<div class="confirm-actions">' +
          '<button class="btn btn-danger btn-sm" id="confirmYes">Yes, Delete</button>' +
          '<button class="btn btn-outline btn-sm" id="confirmNo">Cancel</button>' +
        '</div>' +
      '</div>';
    document.body.appendChild(overlay);

    document.getElementById('confirmYes').addEventListener('click', function() {
      document.body.removeChild(overlay);
      onYes();
    });
    document.getElementById('confirmNo').addEventListener('click', function() {
      document.body.removeChild(overlay);
    });
  }

  document.querySelectorAll('form.delete-form').forEach(function(form) {
    form.addEventListener('submit', function(e) {
      e.preventDefault();
      showConfirm('This action cannot be undone.', function() {
        form.submit();
      });
    });
  });

  // ── 9. Video Carousel ─────────────────────────────────────
  var track = document.querySelector('.video-track');
  if (track) {
    var cards = track.querySelectorAll('.video-card');
    var dots  = document.querySelectorAll('.carousel-dot');
    var prev  = document.querySelector('.carousel-btn.prev');
    var next  = document.querySelector('.carousel-btn.next');
    var cur = 0;
    function visCount() { return window.innerWidth <= 768 ? 1 : window.innerWidth <= 1024 ? 2 : 3; }
    function maxSlide() { return Math.max(0, cards.length - visCount()); }
    function goTo(n) {
      cur = Math.max(0, Math.min(n, maxSlide()));
      if (cards.length > 0) track.style.transform = 'translateX(-'+(cur*(cards[0].offsetWidth+24))+'px)';
      dots.forEach(function(d,i){ d.classList.toggle('active', i===cur); });
    }
    if (prev) prev.addEventListener('click', function(){ goTo(cur-1); });
    if (next) next.addEventListener('click', function(){ goTo(cur+1); });
    dots.forEach(function(d,i){ d.addEventListener('click', function(){ goTo(i); }); });
    setInterval(function(){ goTo(cur >= maxSlide() ? 0 : cur+1); }, 5000);
    window.addEventListener('resize', function(){ goTo(0); });
  }

  // ── 10. Alert auto-dismiss ────────────────────────────────
  document.querySelectorAll('.alert-success').forEach(function(a) {
    setTimeout(function() {
      a.style.transition = 'opacity 0.5s'; a.style.opacity = '0';
      setTimeout(function(){ a.remove(); }, 500);
    }, 4000);
  });

  // ── 11. Navbar scroll shadow ──────────────────────────────
  var navbar = document.querySelector('.navbar');
  if (navbar) {
    window.addEventListener('scroll', function() {
      navbar.style.boxShadow = window.scrollY > 20 ? '0 4px 24px rgba(0,0,0,0.4)' : 'none';
    });
  }

  // ── 12. Admin anchor scroll ───────────────────────────────
  if (window.location.hash) {
    setTimeout(function() {
      var el = document.querySelector(window.location.hash);
      if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 300);
  }

});
